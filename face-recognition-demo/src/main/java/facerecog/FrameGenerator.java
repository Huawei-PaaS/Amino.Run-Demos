package facerecog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FrameGenerator {
    private BufferedReader in1;

    public FrameGenerator(String sourceType) {
        String cwd = System.getProperty("user.dir");
        // String home = System.getProperty("user.home");
        // String cmd = home + "/.virtualenvs/cv/bin/python";
        String cmd = "/usr/local/bin/python";
        String path = cwd + "/src/main/python/";

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

    public String getFrame() throws IOException, InterruptedException {
        return in1.readLine();
    }
}
