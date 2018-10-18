package application;

import facerecog.FrameGenerator;
import facerecog.Detection;
import facerecog.Recognition;
import facerecog.Tracking;
import sapphire.common.SapphireObjectID;
import sapphire.kernel.server.KernelServer;
import sapphire.kernel.server.KernelServerImpl;
import sapphire.oms.OMSServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class DemoAppStart {

    public static void main(String[] args) throws IOException, InterruptedException {
        String frame, resp;
        Registry registry;
        String sourceType = args[5]; // "camera": for onboard camera, "video": for video file
        FrameGenerator frameGenerator = new FrameGenerator(sourceType);

        try {

            // refer to gradle.properties file for the sequence of arguments
            registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));
            OMSServer server = (OMSServer) registry.lookup("SapphireOMS");

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
                        // System.out.println("frame from generator: " + frame);
                        /* ignore first few frames to allow for camera warm up */
                        if (i < 3) {
                            i++;
                            continue;
                        }
                        resp = detect.processFrame(frame);
                        // System.out.println("response from detection: " + resp);
                    }
                }
                else if (args[4].equalsIgnoreCase("tracking")) {
                    /* recog is a remote object that has handles to the iostream of recognition.py running on server */
                    SapphireObjectID sapphireObjId = server.createSapphireObject("facerecog.Recognition");
                    Recognition recog = (Recognition)server.acquireSapphireObjectStub(sapphireObjId);
                    Tracking track = new Tracking(recog, args[6]);

                    int i = 0;
                    while ((frame = frameGenerator.getFrame()) != null) {
                        // System.out.println("frame from generator: " + frame);
                        /* ignore first few frames to allow for camera warm up */
                        if (i < 3) {
                            i++;
                            continue;
                        }
                        track.processFrame(frame);
                    }
                }
                else System.err.println("Incorrect input: please input oms_ip, oms_port, kernel_android_ip, " +
                                        "kernel_server_port, detection/tracking, camera/video, display/file");
            } else {
                System.err.println("Incorrect input: please input oms_ip, oms_port, kernel_android_ip, " +
                        "kernel_server_port, detection/tracking, camera/video, display/file");
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

}