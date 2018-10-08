package sapphire.appexamples.minnietwitter.glue;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.minnietwitter.R;

import java.util.HashMap;

import sapphire.appexamples.minnietwitter.device.generator.TwitterWorldGenerator;

public class HomeActivity extends Activity {
    SessionManagement session;
    public String userOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        session = new SessionManagement(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        userOne = user.get(SessionManagement.KEY_NAME);

        Button button = (Button) findViewById(R.id.btnShow);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MyTweets.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void sendMessage(View view) {
        EditText editText = (EditText) findViewById(R.id.tweetBody);
        String message = editText.getText().toString();
        new GenerateWorld().execute(userOne, message);
    }

    private class GenerateWorld extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            boolean twtStatus = false;
            try {
                twtStatus = TwitterWorldGenerator.main(params[0], params[1]);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return twtStatus;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean == true) {
                final TextView tv = (TextView) findViewById(R.id.currentTweet);
                tv.setText("Tweet Successful !!");
                tv.postDelayed(new Runnable() {
                    public void run() {
                        tv.setVisibility(View.INVISIBLE);
                    }
                }, 1000);
                EditText editText = (EditText) findViewById(R.id.tweetBody);
                editText.setText("");
                tv.setVisibility(View.VISIBLE);
            }
        }
    }

    public void clear(View v) {
        EditText editText = (EditText) findViewById(R.id.tweetBody);
        editText.setText("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            session.logoutUser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
