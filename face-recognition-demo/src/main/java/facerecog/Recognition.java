package facerecog;

import amino.run.app.MicroService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

// TODO: Explicitly specify where to run this task based on some logic.
public class Recognition implements MicroService {
    transient OutputStream out3;
    transient BufferedReader in3;
    transient ProcessBuilder ps;

    public Recognition() {}

    public String processFrame(String frame) throws IOException, InterruptedException {
        if (ps == null) {
            initRecognitionProcess();
        }
        System.out.println("<Server> Recognition microservice received new frame");
        String bbox_list_str;
        out3.write((frame+"\n").getBytes());
        out3.flush();
        bbox_list_str = in3.readLine();
        return bbox_list_str;
    }

    private void initRecognitionProcess() {
        String cwd = System.getProperty("user.dir");
        String home = System.getProperty("user.home");
        String cmd = home + "/.virtualenvs/cv/bin/python3";
        //String cmd = "/usr/local/bin/python";
        String path = cwd + "/src/main/python/";

        ps = new ProcessBuilder(cmd, path + "recognition.py");
        ps.redirectErrorStream(true);
        Process pr3 = null;
        try {
            pr3 = ps.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        in3 = new BufferedReader(new InputStreamReader(pr3.getInputStream()));
        out3 = pr3.getOutputStream();
    }

}
