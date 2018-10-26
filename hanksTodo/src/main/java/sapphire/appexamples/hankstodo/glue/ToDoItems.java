package sapphire.appexamples.hankstodo.glue;

import com.example.hankstodo.R;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import sapphire.appexamples.hankstodo.device.TodoActivity;
import sapphire.appexamples.hankstodo.glue.ListAdapter.customButtonListener;

public class ToDoItems extends Activity implements customButtonListener {
	public final static String EXTRA_MESSAGE = "com.example.hankstodo.MESSAGE";
    private ArrayList<String> items;
    private ListAdapter adapter;
    private ListView lvItems;
    private String listName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_list);

        lvItems = (ListView) findViewById(R.id.list_todo);
        items = new ArrayList<String>();
        adapter = new ListAdapter(ToDoItems.this, items);
        adapter.setCustomButtonListner(ToDoItems.this);
        lvItems.setAdapter(adapter);

        Intent intent = getIntent();
        listName = (String) intent.getSerializableExtra("value");
        setTitle(listName + " List");

        new FetchToDoListItem().execute(listName);
    }

    @Override
    public void onButtonClickListner(int position, String value) {
        Toast.makeText(ToDoItems.this, "Item Removed: " + value,
                Toast.LENGTH_SHORT).show();
        items.remove(position);
        adapter.notifyDataSetChanged();
        new RemoveToDoListItem().execute(listName, value);
    }

    public void onAddItem(View v) {
        EditText newItem = (EditText) findViewById(R.id.todo_item);
        String itemText = newItem.getText().toString();
        if (itemText.matches("")) {
            Toast.makeText(this, "Field is empty!!", Toast.LENGTH_SHORT).show();
        } else {
            adapter.add(itemText);
            newItem.setText("");
            new AddToDoListItem().execute(listName, itemText);
        }
    }

    private class AddToDoListItem extends AsyncTask<String, Void, String>{
        protected String doInBackground(String... params) {
            String response = null;
            TodoActivity.addTaskItem(params[0], params[1]);
            return response;
        }
    }

    private class RemoveToDoListItem extends AsyncTask<Object, Void, String>{
        protected String doInBackground(Object... params) {
            String response = null;
            TodoActivity.removeTaskItem((String)params[0], (String)params[1]);
            return response;
        }
    }

    private class FetchToDoListItem extends AsyncTask<String, Void, String>{
        protected String doInBackground(String... params) {
            String response = null;
            final ArrayList<Object> allItems = TodoActivity.fetchToDoItems(params[0]);
            if(allItems.isEmpty() == false) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i=0; i<allItems.size(); i++) {
                            String itemName = (String)allItems.get(i);
                            adapter.add(itemName);
                        }
                    }
                });
            }
            return response;
        }
    }
}
