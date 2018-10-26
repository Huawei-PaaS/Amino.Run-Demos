package sapphire.appexamples.hankstodo.glue;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.example.hankstodo.R;

import java.util.ArrayList;

import sapphire.appexamples.hankstodo.device.TodoActivity;

public class CreateToDo extends Activity {
    private ArrayList<String> todos;
    private ArrayAdapter<String> adapter;
    private GridView todoItems;
    private String todoMangName;

    static {
        new StartApp().execute(Configuration.hostAddress);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_home);

        todoItems = (GridView) findViewById(R.id.grid_view);

        todos = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this,
                R.layout.grid_fmt, todos);
        todoItems.setAdapter(adapter);

        new FetchAllToDoList().execute();
        setupGridViewListener();
    }

    private void setupGridViewListener() {
        todoItems.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(CreateToDo.this, ToDoItems.class);
                        String todoName = todoItems.getItemAtPosition(position).toString();
                        intent.putExtra("value", todoName);
                        startActivity(intent);
                    }
                });
    }

    public void onAddToDo(View v) {
        EditText newItem = (EditText) findViewById(R.id.todo_name);
        todoMangName = newItem.getText().toString();
        if (todoMangName.matches("")) {
            Toast.makeText(this, "Field is empty!!", Toast.LENGTH_SHORT).show();
        } else {
            adapter.add(todoMangName);
            new CreateToDoList().execute(todoMangName);
            newItem.setText("");
        }
    }

    public void onDeleteToDo(View v) {
        EditText newItem = (EditText) findViewById(R.id.todo_name);
        todoMangName = newItem.getText().toString();
        int pos = todos.indexOf(todoMangName);
        if(pos != -1) {
            todos.remove(pos);
            adapter.notifyDataSetChanged();
            new DeleteToDoList().execute(todoMangName);
        } else {
            Toast.makeText(CreateToDo.this, "Could Not Find List: " + todoMangName,
                    Toast.LENGTH_SHORT).show();
        }
        newItem.setText("");
    }

    private class CreateToDoList extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            String response = null;
            TodoActivity.createNewToDoList(params[0]);
            return  response;
        }
    }

    private class DeleteToDoList extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            TodoActivity.removeToDo(params[0]);
            return null;
        }
    }

    private static class StartApp extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            String response = null;
            try {
                TodoActivity.setObject(params);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return response;
        }
    }

    private class FetchAllToDoList extends AsyncTask<Void, Void, String>{
        protected String doInBackground(Void... params) {
            String response = null;
            final ArrayList<String> allLists = TodoActivity.fetchToDoLists();
            if(allLists != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.addAll(allLists);
                    }
                });
            }
            return response;
        }
    }
}
