package sapphire.appexamples.minnietwitter.glue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.minnietwitter.R;

public class UserTimelineActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_timeline);

        // Get the message from the intent
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        TextView result = (TextView ) findViewById(R.id.listTweetView);
        result.setText(message);
    }
}
