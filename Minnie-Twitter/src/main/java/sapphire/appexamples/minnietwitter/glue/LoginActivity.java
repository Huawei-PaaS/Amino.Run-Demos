package sapphire.appexamples.minnietwitter.glue;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minnietwitter.R;

import sapphire.appexamples.minnietwitter.app.User;
import sapphire.appexamples.minnietwitter.device.generator.TwitterWorldGenerator;

public class LoginActivity extends Activity {
    EditText txtUsername, txtPassword;
    AlertDialogManager alert = new AlertDialogManager();
    SessionManagement session;
    public Intent intent;
    public User user;

    static {
        new StartApp().execute(Configuration.hostAddress);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SessionManagement(getApplicationContext());
        txtUsername = (EditText) findViewById(R.id.username);
        txtPassword = (EditText) findViewById(R.id.password);

        TextView registerScreen = (TextView) findViewById(R.id.link_to_register);
        registerScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    private static class StartApp extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            String response = null;
            try {
                TwitterWorldGenerator.setObject(params);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return response;
        }
    }

    public void userLogin(View view) {
        intent = new Intent(this, HomeActivity.class);
        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();
        intent.putExtra("username", username);
        if(username.matches("") || password.matches("")) {
            Toast.makeText(this, "Username, Password cannot be empty!!", Toast.LENGTH_SHORT).show();
        } else {
            new DoLogin().execute(username, password);
        }
    }

    private class DoLogin extends AsyncTask<String, Void, User> {
        protected User doInBackground(String... params) {
            try {
                user = TwitterWorldGenerator.newLogin(params[0], params[1]);
                if (user != null) {
                    session.createLoginSession(params[0], params[1]);
                    startActivity(intent);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return user;
        }
        @Override
        protected void onPostExecute(User user) {
            if (user == null) {
                alert.showAlertDialog(LoginActivity.this, "Login Attempt Failed", "Please register user.");
            }
        }
    }
}
