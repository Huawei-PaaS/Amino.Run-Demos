package sapphire.appexamples.hankstodo.app;

import java.util.Hashtable;
import java.util.Map;

import sapphire.app.DMSpec;
import sapphire.app.Language;
import sapphire.app.SapphireObject;
import static sapphire.runtime.Sapphire.*;

import sapphire.app.SapphireObjectSpec;
import sapphire.policy.cache.CacheLeasePolicy;
import sapphire.policy.dht.DHTKey;

public class TodoListManager implements SapphireObject{
    Map<DHTKey, TodoList> todoLists = new Hashtable<DHTKey, TodoList>();

	public TodoList newTodoList(String name) {
		TodoList t = todoLists.get(new DHTKey(name));
		if (t == null) {

			SapphireObjectSpec spec = SapphireObjectSpec.newBuilder()
					.setLang(Language.java)
					.setJavaClassName(TodoList.class.getName()).addDMSpec(
							DMSpec.newBuilder()
									.setName(CacheLeasePolicy.class.getName())
									.create())
					.create();


			t = (TodoList) new_(spec, name);
			todoLists.put(new DHTKey(name), t);
		}
		System.out.println("Created new list");
		System.out.println("This managers lists" + todoLists.toString());
		return t;
	}
}
