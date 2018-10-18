package sapphire.appexamples.hankstodo.glue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.hankstodo.R;

public class CreateToDo extends Activity {
    private EditText nameToDo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_todo);
    }

    public void onAddList(View v) {
        nameToDo = (EditText) findViewById(R.id.todo_name);
        String listName = nameToDo.getText().toString();
        Intent intent = new Intent(CreateToDo.this, ToDoItems.class);
        Bundle b = new Bundle();
        b.putString("todoname", listName);
        intent.putExtras(b);
        startActivity(intent);
    }
}
