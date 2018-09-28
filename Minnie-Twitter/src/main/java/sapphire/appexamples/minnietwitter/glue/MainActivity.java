package sapphire.appexamples.minnietwitter.glue;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.minnietwitter.R;

import sapphire.appexamples.minnietwitter.device.generator.TwitterWorldGenerator;

public class MainActivity extends Activity {
    public String message;
    public final static String EXTRA_MESSAGE="com.example.root1.minnietwitter.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, UserTimelineActivity.class);
        EditText editText = (EditText) findViewById(R.id.tweetBody);
        message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
        new GenerateWorld().execute(Configuration.hostAddress);
    }

    private class GenerateWorld extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            String response = null;
            try {
                TwitterWorldGenerator.main(params, message);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return response;
        }
    }

    public void clear(View v) {
        EditText editText = (EditText) findViewById(R.id.tweetBody);
        editText.setText("");
    }
}
