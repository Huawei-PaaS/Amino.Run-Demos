package sapphire.appexamples.hankstodo.device;

import java.net.InetSocketAddress;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;

import amino.run.app.Language;
import amino.run.app.MicroServiceSpec;
import amino.run.app.Registry;
import amino.run.common.MicroServiceID;
import amino.run.kernel.server.KernelServer;
import amino.run.kernel.server.KernelServerImpl;
import sapphire.appexamples.hankstodo.app.TodoList;
import sapphire.appexamples.hankstodo.app.TodoListManager;

import static java.lang.Thread.sleep;

public class TodoActivity {
	public static TodoListManager tlm;

	public static void setObject(String[] args){
		java.rmi.registry.Registry registry;
		try {
			registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));
			Registry server = (Registry) registry.lookup("SapphireOMS");

			KernelServer nodeServer = new KernelServerImpl(new InetSocketAddress(args[2], Integer.parseInt(args[3])), new InetSocketAddress(args[0], Integer.parseInt(args[1])));

			MicroServiceSpec spec = MicroServiceSpec.newBuilder()
					.setLang(Language.java)
					.setJavaClassName("sapphire.appexamples.hankstodo.app.TodoListManager")
					.create();

			MicroServiceID microServiceId = server.create(spec.toString());
			tlm = (TodoListManager)server.acquireStub(microServiceId);
			System.out.println("Received tlm: " + tlm);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void createNewToDoList(String listName) {
		TodoList tl = null;
		try {
			tl = tlm.newTodoList(listName);
			// Consensus policy needs some time after creating new Sapphire object; otherwise,
			// leader election may fail.
			sleep(7000);
			System.out.println("Received tl: " + tl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addTaskItem(String listName, String item) {
		TodoList tl = null;
		try {
			tl = tlm.newTodoList(listName);
			String outcome = tl.addToDo(listName, item);
			System.out.println(outcome);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void removeTaskItem(String listName, String itemName) {
		TodoList tl = null;
		try {
			tl = tlm.newTodoList(listName);
			tl.removeToDo(listName, itemName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String fetchToDoItems(String listName) {
		TodoList tl;
		String myItems = null;
		try {
			tl = tlm.newTodoList(listName);
			myItems = tl.getToDo(listName);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return myItems;
	}

	public static ArrayList<String> fetchToDoLists() {
		return tlm.getAllTodoLists();
	}

	public static void removeToDo(String listName) {
		tlm.deleteTodoList(listName);
	}
}
