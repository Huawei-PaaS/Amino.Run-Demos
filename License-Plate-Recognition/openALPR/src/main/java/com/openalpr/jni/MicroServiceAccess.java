package com.openalpr.jni;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.rmi.registry.LocateRegistry;
import java.util.logging.Logger;

import amino.run.app.DMSpec;
import amino.run.app.Registry;

import amino.run.app.Language;
import amino.run.app.MicroServiceSpec;
import amino.run.common.MicroServiceID;
import amino.run.kernel.server.KernelServer;
import amino.run.kernel.server.KernelServerImpl;
import amino.run.oms.OMSServerImpl;
import amino.run.policy.mobility.explicitmigration.ExplicitMigrationPolicy;


public class MicroServiceAccess
{
    private static final Logger logger = Logger.getLogger(MicroServiceAccess.class.getName());
    public AlprMicroService lr;
    public static long fileUploadTime; // file upload time.
    public static long processingTime; // image recognition processing time.

    private static Configuration.ProcessEntity previousEntity = Configuration.ProcessEntity.UNDECIDED;

    public void initialize() {

        try {
            String[] kernelArgs = new String[]{ KernelServerImpl.KERNEL_SERVER_IP_OPT,
                    Configuration.natDeviceKernelAddress[0],
                    KernelServerImpl.KERNEL_SERVER_PORT_OPT,
                    Configuration.natDeviceKernelAddress[1],
                    OMSServerImpl.OMS_IP_OPT,
                    Configuration.natOmsAddress[0],
                    OMSServerImpl.OMS_PORT_OPT,
                    Configuration.natOmsAddress[1],
                    KernelServerImpl.LABEL_OPT,
                    KernelServerImpl.REGION_KEY + "=" + Configuration.ProcessEntity.DEVICE.toString()
            };

            // Launch local Microservice kernel server.
            //TODO: Once running on device works, this can be replaced with
            // KernelServerImpl.main(kernelArgs);
            KernelServer nodeServer = new KernelServerImpl(new InetSocketAddress(kernelArgs[1], Integer.parseInt(kernelArgs[3])), new InetSocketAddress(kernelArgs[5], Integer.parseInt(kernelArgs[7])));
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Get license plate recognition result for the given image file in the device.
     * @param ANDROID_DATA_DIR
     * @param countryCode
     * @param region
     * @param imageFilePath
     * @param processEntity
     * @return JSON string of the process result.
     */
    public Results getResult(String ANDROID_DATA_DIR, String countryCode, String region, String imageFilePath, Configuration.ProcessEntity processEntity) {
        Results results = null;

        //TODO: Running on the device should be supported
        if(processEntity == Configuration.ProcessEntity.DEVICE) {
            logger.warning("Currently device is not supported. Please select SERVER");
            return null;
        }

        if (previousEntity == Configuration.ProcessEntity.UNDECIDED || lr == null) {
            try {
                java.rmi.registry.Registry registry;
                registry = LocateRegistry.getRegistry(Configuration.natOmsAddress[0], Integer.parseInt(Configuration.natOmsAddress[1]));
                Registry server = (Registry) registry.lookup("io.amino.run.oms");

                MicroServiceSpec spec =
                        MicroServiceSpec.newBuilder()
                                .setLang(Language.java)
                                .setJavaClassName(AlprMicroService.class.getName())
                                .addDMSpec(
                                        DMSpec.newBuilder()
                                                .setName(ExplicitMigrationPolicy.class.getName())
                                                .create())
                                .create();
                MicroServiceID microServiceId = server.create(spec.toString());
                lr = (AlprMicroService) server.acquireStub(microServiceId);
            }catch (Exception e){
                e.printStackTrace();
            }
            if (lr == null) {
                System.out.println("Failed to acquire stub for microservice.");
                return null;
            }
        } else if (previousEntity != processEntity) {
            if (processEntity == Configuration.ProcessEntity.SERVER) {
                lr.migrateTo(Configuration.getNatServerKernelAddress());
            } else {
                lr.migrateTo(Configuration.getNatDeviceKernelAddress());
            }
        }

        try {
            File file = new File(imageFilePath);

            String openAlprConfFile = ANDROID_DATA_DIR + File.separatorChar + "runtime_data" + File.separatorChar + "openalpr.conf";

            if (processEntity == Configuration.ProcessEntity.SERVER) {
                fileUploadTime = UploadFileToServer(file);
                if (fileUploadTime == -1) {
                    System.out.println("File upload failed for " + imageFilePath);
                    return null;
                };

                openAlprConfFile = Constants.OPEN_ALPR_CONF_FILE_LINUX;
            }

            final long startTime = System.currentTimeMillis();
            results = lr.recognizeImage
                    (countryCode, region, openAlprConfFile, imageFilePath, file.getName(), Constants.MAX_NUM_OF_PLATES, processEntity);

            processingTime = System.currentTimeMillis() - startTime;
            previousEntity = processEntity;
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return results;
    }

    /**
     * Upload a image file to server.
     * @param file
     * @return Image transfer time, -1 if it failed.
     */
    private long UploadFileToServer(File file) {
        final long startTime = System.currentTimeMillis();

        try {
            long fileLength = file.length();
            System.out.println("File length = " + fileLength);
            FileInputStream in = new FileInputStream(file);
            byte [] imageData = new byte[2048*1024];

            int mylen = in.read(imageData);
            System.out.println("Size of image = " + mylen);
            while (mylen > 0) {
                lr.saveImage(file.getName(), imageData, mylen);
                mylen = in.read(imageData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("File transfer completed. Took " + elapsedTime + " milliseconds.");

        return elapsedTime;
    }
}
