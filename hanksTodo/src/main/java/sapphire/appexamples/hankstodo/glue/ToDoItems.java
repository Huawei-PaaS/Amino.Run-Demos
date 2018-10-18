package sapphire.appexamples.hankstodo.glue;

import com.example.hankstodo.R;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import sapphire.appexamples.hankstodo.device.TodoActivity;

public class ToDoItems extends Activity {
	public final static String EXTRA_MESSAGE = "com.example.hankstodo.MESSAGE";
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;

    static {
        new StartApp().execute(Configuration.hostAddress);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_list);

        lvItems = (ListView) findViewById(R.id.lvItems);
        items = new ArrayList<String>();
        itemsAdapter = new ArrayAdapter<String>(this,
                R.layout.fmt_text, items);
        lvItems.setAdapter(itemsAdapter);

        Bundle b = getIntent().getExtras();
        String todoListName = b.getString("todoname");
        new CreateToDoList().execute(todoListName);

        setupListViewListener();
    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {
                        items.remove(pos);
                        itemsAdapter.notifyDataSetChanged();
                        new RemoveToDoListItem().execute(pos);
                        return true;
                    }

                });
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

    private class CreateToDoList extends AsyncTask<String, Void, String>{
        protected String doInBackground(String... params) {
            String response = null;
            TodoActivity.createNewToDoList(params[0]);
            return response;
        }
    }

    public void onAddItem(View v) {
        EditText newItem = (EditText) findViewById(R.id.todo_item);
        String itemText = newItem.getText().toString();
        itemsAdapter.add(itemText);
        newItem.setText("");
        new AddToDoListItem().execute(itemText);
    }

    private class AddToDoListItem extends AsyncTask<String, Void, String>{
        protected String doInBackground(String... params) {
            String response = null;
            TodoActivity.addTaskItem(params[0]);
            return response;
        }
    }

    private class RemoveToDoListItem extends AsyncTask<Integer, Void, String>{
        protected String doInBackground(Integer... params) {
            String response = null;
            TodoActivity.removeTaskItem(params[0]);
            return response;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
