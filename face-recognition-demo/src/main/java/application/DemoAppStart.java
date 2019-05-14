package application;

import com.google.devtools.common.options.OptionsParser;

import amino.run.app.MicroServiceSpec;
import amino.run.common.MicroServiceID;
import amino.run.policy.atleastoncerpc.AtLeastOnceRPCPolicy;
import facerecog.FrameGenerator;
import facerecog.Detection;
import facerecog.Recognition;
import facerecog.Tracking;
import amino.run.app.DMSpec;
import amino.run.kernel.server.KernelServerImpl;
import amino.run.app.Language;
import amino.run.app.Registry;

import java.net.InetSocketAddress;
import java.rmi.registry.LocateRegistry;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * This class orchestrates face recognition app. It forks two python child processes:
 *  1. FrameGenerator : It reads frames from source. Source can be video or camera
 *  2. Tracking: It detects frames supplied by {@link FrameGenerator} with human faces and calls recognition micro service for detecting them in frames.
 *
 */
public class DemoAppStart {
    private static final Logger logger = Logger.getLogger(DemoAppStart.class.getName());

    public static void main(String[] args) throws Exception {
        String frame;
        java.rmi.registry.Registry registry;

        OptionsParser parser = OptionsParser.newOptionsParser(DemoAppArgumentParser.class);
        try {
            // parse command line arguments for app
            parser.parse(args);
        } catch (Exception e) {
            System.out.println(e.getMessage() + System.lineSeparator() + System.lineSeparator() +
                    "Usage: "
                    + DemoAppStart.class.getSimpleName() + System.lineSeparator()
                    + parser.describeOptions(
                    Collections.<String, String>emptyMap(),
                    OptionsParser.HelpVerbosity.LONG));
            return;
        }

        DemoAppArgumentParser appArgs = parser.getOptions(DemoAppArgumentParser.class);

        // deploy frame generator
        String sourceType = appArgs.sourceType; // "camera": for onboard camera, "video": for video file
        FrameGenerator frameGenerator = null;
        if (sourceType.equalsIgnoreCase("camera") || sourceType.equalsIgnoreCase("video")) {
            frameGenerator = new FrameGenerator(sourceType);
        } else {
            logger.severe("Incorrect source specified, use either \"camera\" or \"video\"");
            return;
        }

        if (appArgs.inferenceType.equalsIgnoreCase("detection")) { // run face detection locally
            /* detection runs locally */
            Detection detect = new Detection(appArgs.targetType);

            int i = 0;
            while ((frame = frameGenerator.getFrame()) != null) {
                /* ignore first few frames to allow for camera warm up */
                if (i < 3) {
                    i++;
                    continue;
                }
                detect.processFrame(frame);
            }
            return;
        }

        if (appArgs.inferenceType.equalsIgnoreCase("tracking")) {
            MicroServiceID mid = null;

            registry = LocateRegistry.getRegistry(appArgs.omsIP, appArgs.omsPort);
            Registry server = (Registry) registry.lookup("io.amino.run.oms");
            if (appArgs.startKernelServer) { // start kernel server with app
                KernelServerImpl.main(new String[]{
                        "--kernel-server-ip", appArgs.kernelServerIP,
                        "--kernel-server-port", appArgs.kernelServerPort.toString(),
                        "--oms-ip", appArgs.omsIP,
                        "--oms-port", appArgs.omsPort.toString()
                });
            } else {
                // create kernel server instance for kernel client init
                new KernelServerImpl(
                        new InetSocketAddress(appArgs.kernelServerIP, appArgs.kernelServerPort),
                        new InetSocketAddress(appArgs.omsIP, appArgs.omsPort));
            }

            try {
                // Deploy Recognition micro service in Amino system and fork Tracking process to detect frames with faces in it
                // and eventually use Recognition micro service to identify human face in frames.
                MicroServiceSpec spec = MicroServiceSpec.newBuilder()
                        .setLang(Language.java)
                        .setJavaClassName("facerecog.Recognition").addDMSpec(
                                DMSpec.newBuilder()
                                        .setName(AtLeastOnceRPCPolicy.class.getName())
                                        .create())
                        .create();

                mid = server.create(spec.toString());
                /* recog is a remote object that has handles to the iostream of recognition.py running on server */
                Recognition recog = (Recognition) server.acquireStub(mid);
                Tracking track = new Tracking(recog, appArgs.targetType);

                int i = 0;
                while ((frame = frameGenerator.getFrame()) != null) {
                    /* ignore first few frames to allow for warm up */
                    if (i < 3) {
                        i++;
                        continue;
                    }
                    track.processFrame(frame);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            } finally {
                if (mid != null) {
                    server.delete(mid);
                }
            }
            return;
        }

    logger.severe("Incorrect input: please input correct inference type (detection/tracking)");

    }
}