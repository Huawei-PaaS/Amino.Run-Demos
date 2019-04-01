package application;

import amino.run.app.MicroServiceSpec;
import amino.run.common.MicroServiceID;
import amino.run.policy.DefaultPolicy;
import facerecog.FrameGenerator;
import facerecog.Detection;
import facerecog.Recognition;
import facerecog.Tracking;
import amino.run.app.DMSpec;
import amino.run.kernel.server.KernelServer;
import amino.run.kernel.server.KernelServerImpl;
import amino.run.app.Language;
import amino.run.app.Registry;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.rmi.registry.LocateRegistry;
import java.util.logging.Logger;

public class DemoAppStart {
    private static final Logger logger = Logger.getLogger(DemoAppStart.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException {
        String frame, resp;
        java.rmi.registry.Registry registry;
        String sourceType = args[5]; // "camera": for onboard camera, "video": for video file
        FrameGenerator frameGenerator = null;
        if ( sourceType.equalsIgnoreCase("camera") || sourceType.equalsIgnoreCase("video") ) {
            frameGenerator = new FrameGenerator(sourceType);
        } else {
            logger.severe("Incorrect source specified, use either \"camera\" or \"video\"");
            return;
        }

        try {

            // refer to gradle.properties file for the sequence of arguments
            registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));
            Registry server = (Registry) registry.lookup("io.amino.run.oms");

            // "10.0.2.15", "22345" - Kernel server
            KernelServer nodeServer = new KernelServerImpl(
                    new InetSocketAddress(args[2], Integer.parseInt(args[3])),
                    new InetSocketAddress(args[0], Integer.parseInt(args[1])));

            if (args.length == 7) {
                if (args[4].equalsIgnoreCase("detection")) {
                    /* detection runs locally */
                    Detection detect = new Detection(args[6]);

                    int i = 0;
                    while ((frame = frameGenerator.getFrame()) != null) {
                        /* ignore first few frames to allow for camera warm up */
                        if (i < 3) {
                            i++;
                            continue;
                        }
                        resp = detect.processFrame(frame);
                    }
                }
                else if (args[4].equalsIgnoreCase("tracking")) {
                    /* recog is a remote object that has handles to the iostream of recognition.py running on server */
                    MicroServiceSpec spec = MicroServiceSpec.newBuilder()
                            .setLang(Language.java)
                            .setJavaClassName("facerecog.Recognition").addDMSpec(
                                    DMSpec.newBuilder()
                                    .setName(DefaultPolicy.class.getName())
                                    .create())
                            .create();

                    MicroServiceID sapphireObjId = server.create(spec.toString());
                    Recognition recog = (Recognition)server.acquireStub(sapphireObjId);
                    Tracking track = new Tracking(recog, args[6]);

                    int i = 0;
                    while ((frame = frameGenerator.getFrame()) != null) {
                        /* ignore first few frames to allow for camera warm up */
                        if (i < 3) {
                            i++;
                            continue;
                        }
                        track.processFrame(frame);
                    }
                }
                else {
                    logger.severe("Incorrect input: please input oms_ip, oms_port, kernel_android_ip, " +
                            "kernel_server_port, detection/tracking, camera/video, display/file");
                }
            } else {
                logger.severe("Incorrect input: please input oms_ip, oms_port, kernel_android_ip, " +
                        "kernel_server_port, detection/tracking, camera/video, display/file");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}