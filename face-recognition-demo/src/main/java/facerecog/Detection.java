package facerecog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * This class does face detection locally by forking "detection.py" process.
 */
public class Detection {
    private OutputStream out2;
    private BufferedReader in2;

    public Detection(String targetType) {
        /* targetType = "display": for screen, "file": write to file */
        String cwd = System.getProperty("user.dir");
        String home = System.getProperty("user.home");
        String cmd = home + "/.virtualenvs/cv/bin/python3"; // if deployed on host system with opencv installed
        //String cmd = "/usr/local/bin/python"; // if deployed in container
        String path = cwd + "/src/main/python/";

        // fork detection process
        ProcessBuilder ps2 = new ProcessBuilder(cmd, path + "detection.py", targetType);
        ps2.redirectErrorStream(true);
        Process pr2 = null;
        try {
            pr2 = ps2.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        out2 = pr2.getOutputStream();
        in2 = new BufferedReader(new InputStreamReader(pr2.getInputStream()));
    }

    public String processFrame(String frame) throws IOException, InterruptedException {
        out2.write((frame+"\n").getBytes());
        out2.flush();
        return in2.readLine();
    }
}
