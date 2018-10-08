package sapphire.appexamples.minnietwitter.glue;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.minnietwitter.R;

import java.util.ArrayList;
import java.util.HashMap;

import sapphire.appexamples.minnietwitter.device.generator.TwitterWorldGenerator;

public class MyTweets extends Activity {
    String userOne;
    ListView listView;
    SessionManagement session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_tweets);
        session = new SessionManagement(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        userOne = user.get(SessionManagement.KEY_NAME);
        listView = (ListView) findViewById(R.id.list);
        new GetTweets().execute();
    }

    public class GetTweets extends AsyncTask<Void,Void,ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            ArrayList<String> tweetList = TwitterWorldGenerator.allTweets(userOne);
            return tweetList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> tweetList) {
            String tweets[]=tweetList.toArray(new String[tweetList.size()]);
            CustomList adapter = new
                    CustomList(MyTweets.this, tweets);
            listView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

