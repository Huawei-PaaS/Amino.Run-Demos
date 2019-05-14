package facerecog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class forks frame_generator.py python process and provides interface for retrieving frames
 * from specified source (video or camera).
 */
public class FrameGenerator {
    private BufferedReader in1;

    public FrameGenerator(String sourceType) {
        String cwd = System.getProperty("user.dir");
        String home = System.getProperty("user.home");
        String cmd = home + "/.virtualenvs/cv/bin/python3"; // if deployed on host system with opencv installed
        //String cmd = "/usr/local/bin/python"; // if deployed in container
        String path = cwd + "/src/main/python/";

        // spin up new python process to retrieve frames
        ProcessBuilder ps1 = new ProcessBuilder(cmd, path + "frame_generator.py", sourceType);
        ps1.redirectErrorStream(true);
        Process pr1 = null;
        try{
            pr1 = ps1.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        in1 = new BufferedReader(new InputStreamReader(pr1.getInputStream()));
    }

    /**
     * Retrieve video frames from specified source.
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public String getFrame() throws IOException, InterruptedException {
        return in1.readLine();
    }
}
