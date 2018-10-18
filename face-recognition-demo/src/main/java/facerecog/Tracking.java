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
//        String home = System.getProperty("user.home");
//        String cmd = home + "/.virtualenvs/cv/bin/python";
        String cmd = "/usr/local/bin/python";
        String path = cwd + "/src/main/java/facerecog/";

//        String outputType = "display"; // "display": for screen, "file": write to file

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
        //System.out.println("got frame from generator: " + frame);
        line2 = in2.readLine();
        //System.out.println("got frame for recog: " + line2);

        if ( !( line2.equals("done") || line2.equals("next") )) {
            //System.out.println("Sending frame to recog module now");
            bbox_list_str = recog.processFrame(frame);
            //System.out.println("Got bbox_list_str: " + bbox_list_str);

            out2.write((bbox_list_str+"\n").getBytes());
            out2.flush();
        }

    }
}
