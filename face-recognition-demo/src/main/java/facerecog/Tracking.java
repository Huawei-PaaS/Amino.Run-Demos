package facerecog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class Tracking  {
    private OutputStream out2;
    private BufferedReader in2;
    private Recognition recog;
    private String line2;
    private String bbox_list_str;

    public Tracking(Recognition recog, String targetType) {
        /* targetType =  "display": for screen, "file": write to file */
        String cwd = System.getProperty("user.dir");
        // String home = System.getProperty("user.home");
        // String cmd = home + "/.virtualenvs/cv/bin/python";
        String cmd = "/usr/local/bin/python";
        String path = cwd + "/src/main/python/";

        // String outputType = "display"; // "display": for screen, "file": write to file

        ProcessBuilder ps2 = new ProcessBuilder(cmd, path + "tracking.py", targetType);
        ps2.redirectErrorStream(true);
        Process pr2 = null;
        try {
            pr2 = ps2.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        out2 = pr2.getOutputStream();
        in2 = new BufferedReader(new InputStreamReader(pr2.getInputStream()));
        this.recog = recog;
    }

    public void processFrame(String frame) throws IOException, InterruptedException {
        out2.write((frame+"\n").getBytes());
        out2.flush();
        line2 = in2.readLine();

        if ( !( line2.equals("done") || line2.equals("next") )) {
            bbox_list_str = recog.processFrame(frame);
            out2.write((bbox_list_str+"\n").getBytes());
            out2.flush();
        }
    }
}
