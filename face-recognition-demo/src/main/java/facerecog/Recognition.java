package facerecog;

import sapphire.app.SapphireObject;
import sapphire.policy.DefaultSapphirePolicy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

// TODO: Explicitly specify where to run this task based on some logic.
//public class Recognition implements SapphireObject<ExplicitMigrationPolicy> {
public class Recognition implements SapphireObject<DefaultSapphirePolicy> {
    transient OutputStream out3;
    transient BufferedReader in3;

    public Recognition() {
        String cwd = System.getProperty("user.dir");
//        String home = System.getProperty("user.home");
//        String cmd = home + "/.virtualenvs/cv/bin/python";
        String cmd = "/usr/local/bin/python";
        String path = cwd + "/src/main/java/facerecog/";

        ProcessBuilder ps3 = new ProcessBuilder(cmd, path + "recognition.py");
        ps3.redirectErrorStream(true);
        Process pr3 = null;
        try {
            pr3 = ps3.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        in3 = new BufferedReader(new InputStreamReader(pr3.getInputStream()));
        out3 = pr3.getOutputStream();
    }

    public String processFrame(String frame) throws IOException, InterruptedException {
        String bbox_list_str;
        out3.write((frame+"\n").getBytes());
        out3.flush();
        bbox_list_str = in3.readLine();
        return bbox_list_str;
    }

}