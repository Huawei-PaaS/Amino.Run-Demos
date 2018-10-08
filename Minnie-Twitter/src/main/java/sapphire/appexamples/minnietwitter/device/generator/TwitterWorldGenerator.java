package sapphire.appexamples.minnietwitter.device.generator;

import java.net.InetSocketAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

import sapphire.appexamples.minnietwitter.app.TagManager;
import sapphire.appexamples.minnietwitter.app.Timeline;
import sapphire.appexamples.minnietwitter.app.Tweet;
import sapphire.appexamples.minnietwitter.app.TweetContainer;
import sapphire.appexamples.minnietwitter.app.TwitterManager;
import sapphire.appexamples.minnietwitter.app.UserManager;
import sapphire.appexamples.minnietwitter.app.User;
import sapphire.common.SapphireObjectID;
import sapphire.kernel.server.KernelServer;
import sapphire.kernel.server.KernelServerImpl;
import sapphire.oms.OMSServer;

public class TwitterWorldGenerator {
	public static UserManager userManager;

	public static void setObject(String[] args){
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));
			OMSServer server = (OMSServer) registry.lookup("SapphireOMS");

			KernelServer nodeServer = new KernelServerImpl(new InetSocketAddress(args[2], Integer.parseInt(args[3])), new InetSocketAddress(args[0], Integer.parseInt(args[1])));

            /* Get Twitter and User Manager */
			SapphireObjectID sapphireObjId = server.createSapphireObject("sapphire.appexamples.minnietwitter.app.TwitterManager");
			TwitterManager tm = (TwitterManager) server.acquireSapphireObjectStub(sapphireObjId);

			/* To set a name to sapphire object. It is required to set the name if the object has to be shared */
			server.setSapphireObjectName(sapphireObjId, "MyTwitterManager");

			userManager = tm.getUserManager();
			TagManager tagManager = tm.getTagManager();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static User registerUser(String username, String password){
		User user = null;
		user = userManager.getUser(username);
		if(user == null){
			try {
				user = userManager.addUser(username, password);
				System.out.println("Added new user: "+username);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return user;
		}
		else {
			return null;
		}
	}

	public static User newLogin(String user, String pswd){
		User user0 = null;
		try{
			user0 = userManager.login(user, pswd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user0;
	}

	private static boolean executeMinnieTwitterDemo(String name, String message) {
		boolean tweetStatus = false;
		try {
			User u = userManager.getUser(name);
			Timeline t = u.getTimeline();

			try {
				t.tweet(message);
				tweetStatus = true;
			} catch(Exception e) {
				System.out.print(", Failed ");
			}
			System.out.println("\n@user" + " tweeted: " + message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tweetStatus;
	}

	public static ArrayList<String> allTweets(String name) {
		ArrayList<String> allTweets = new ArrayList<String>();
		List<Tweet> myTweets;
		User u = userManager.getUser(name);
		Timeline t = u.getTimeline();

		try {
			List<TweetContainer> tweetList = t.getTweetsList();
			int size = tweetList.size();
			myTweets = t.getTweets(0, size);
			for (int i = 0; i < myTweets.size(); i++) {
				Tweet t1 = myTweets.get(i);
				String text = t1.getText();
				allTweets.add(text);
			}
			for (String n : allTweets) {
				System.out.println(n);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return allTweets;
	}

	public static boolean main(String name, String message) {
		boolean status = executeMinnieTwitterDemo(name, message);
		return  status;
	}
}