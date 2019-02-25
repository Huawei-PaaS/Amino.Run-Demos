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
import sapphire.common.SapphireObjectCreationException;
import sapphire.policy.dht.DHTPolicy;
import sapphire.policy.replication.ConsensusRSMPolicy;

public class TodoListManager implements SapphireObject{
    LinkedHashMap<String, TodoList> todoLists = new LinkedHashMap<>();

	public TodoListManager() {
		System.out.println("Instantiating TodoListManager...");
	}

	public void doSomething(String input) {
		System.out.println("Input received: " + input);
	}

	public TodoList newTodoList(String id) throws SapphireObjectCreationException {
		TodoList t = todoLists.get(id);
		if (t == null) {
			SapphireObjectSpec spec;
			spec = SapphireObjectSpec.newBuilder()
					.setLang(Language.java)
					.setJavaClassName(TodoList.class.getName())
					.addDMSpec(
							DMSpec.newBuilder()
									.setName(DHTPolicy.class.getName())
									.create())
					.addDMSpec(
							DMSpec.newBuilder()
									.setName(ConsensusRSMPolicy.class.getName())
									.create())
					.create();

			t = (TodoList) new_(spec, id);
			todoLists.put(id, t);
			System.out.println("Created new Todo list");
		} else {
			System.out.println("ToDoList for ID: "+ id + " already exists.");
		}
		System.out.println("This managers lists" + todoLists.toString());
		return t;
	}

	public ArrayList<String> getAllTodoLists() {
		if(!todoLists.isEmpty()) {
			ArrayList<String> todoList = new ArrayList();
			Iterator<Map.Entry<String, TodoList>> itr = todoLists.entrySet().iterator();
			while(itr.hasNext()) {
				Map.Entry<String, TodoList> entry = itr.next();
				todoList.add(entry.getKey());
			}
			return todoList;
		} else {
			return null;
		}
	}

	public TodoList getToDoList(String id) {
		TodoList t = todoLists.get(id);
		if (t == null) {
			return null;
		}
		return t;
	}

	public void deleteTodoList(String name) {
		TodoList t = todoLists.remove(name);
		if (t != null) {
			delete_(t);
		}
        System.out.println("ToDoList Deleted");
	}
}
