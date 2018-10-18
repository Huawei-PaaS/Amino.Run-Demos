package sapphire.appexamples.hankstodo.device;

import java.net.InetSocketAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import sapphire.app.DMSpec;
import sapphire.app.Language;
import sapphire.app.SapphireObjectSpec;
import sapphire.appexamples.hankstodo.app.TodoList;
import sapphire.appexamples.hankstodo.app.TodoListManager;
import sapphire.common.SapphireObjectID;
import sapphire.kernel.server.KernelServer;
import sapphire.kernel.server.KernelServerImpl;
import sapphire.oms.OMSServer;
import sapphire.policy.dht.DHTPolicy;


public class TodoActivity {
	public static TodoList tl;
	public static TodoListManager tlm;

	public static void setObject(String[] args){
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));
			OMSServer server = (OMSServer) registry.lookup("SapphireOMS");

			KernelServer nodeServer = new KernelServerImpl(new InetSocketAddress(args[2], Integer.parseInt(args[3])), new InetSocketAddress(args[0], Integer.parseInt(args[1])));

			SapphireObjectSpec spec = SapphireObjectSpec.newBuilder()
					.setLang(Language.java)
					.setJavaClassName("sapphire.appexamples.hankstodo.app.TodoListManager").addDMSpec(
					DMSpec.newBuilder()
							.setName(DHTPolicy.class.getName())
							.create())
					.create();


			SapphireObjectID sapphireObjId = server.createSapphireObject(spec.toString());
			tlm = (TodoListManager)server.acquireSapphireObjectStub(sapphireObjId);
			System.out.println("Received tlm: " + tlm);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void createNewToDoList(String listName) {
		tl = tlm.newTodoList(listName);
		System.out.println("Received tl1: " + tl);
	}

	public static void addTaskItem(String item) {
		String outcome = tl.addToDo(item);
		System.out.println(outcome);
	}

	public static void removeTaskItem(int pos) {
		tl.removeToDo(pos);
	}
}
