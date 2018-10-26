package sapphire.appexamples.hankstodo.app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import sapphire.app.DMSpec;
import sapphire.app.Language;
import sapphire.app.SapphireObject;
import static sapphire.runtime.Sapphire.*;

import sapphire.app.SapphireObjectSpec;
import sapphire.policy.dht.DHTKey;
import sapphire.policy.dht.DHTPolicy;

public class TodoListManager implements SapphireObject{
    LinkedHashMap<DHTKey, TodoList> todoLists = new LinkedHashMap<DHTKey, TodoList>();

	public TodoList newTodoList(String name) {
		TodoList t = todoLists.get(new DHTKey(name));
		if (t == null) {

			SapphireObjectSpec spec = SapphireObjectSpec.newBuilder()
					.setLang(Language.java)
					.setJavaClassName(TodoList.class.getName()).addDMSpec(
							DMSpec.newBuilder()
									.setName(DHTPolicy.class.getName())
									.create())
					.create();

			t = (TodoList) new_(spec, name);
			todoLists.put(new DHTKey(name), t);
			System.out.println("Created new list");
			System.out.println("This managers lists" + todoLists.toString());
		}
		return t;
	}

	public ArrayList<String> getAllTodoLists() {
		if(todoLists.isEmpty() == false) {
			ArrayList<String> todoList = new ArrayList();
			Iterator itr = todoLists.entrySet().iterator();
			while(itr.hasNext()) {
				Map.Entry entry = (Map.Entry) itr.next();
				todoList.add(((DHTKey)entry.getKey()).getIdentifier());
			}
			return todoList;
		} else {
			return null;
		}
	}

	public void deleteTodoList(String name) {
		todoLists.remove(new DHTKey(name));
		System.out.println("ToDoList Deleted");
	}
}
