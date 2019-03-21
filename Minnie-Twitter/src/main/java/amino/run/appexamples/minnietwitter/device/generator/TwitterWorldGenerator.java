package amino.run.appexamples.minnietwitter.device.generator;

import java.net.InetSocketAddress;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import amino.run.app.Language;
import amino.run.app.MicroServiceSpec;
import amino.run.app.Registry;
import amino.run.appexamples.minnietwitter.app.TagManager;
import amino.run.appexamples.minnietwitter.app.Timeline;
import amino.run.appexamples.minnietwitter.app.Tweet;
import amino.run.appexamples.minnietwitter.app.TweetContainer;
import amino.run.appexamples.minnietwitter.app.TwitterManager;
import amino.run.appexamples.minnietwitter.app.UserManager;
import amino.run.appexamples.minnietwitter.app.User;
import amino.run.common.MicroServiceID;
import amino.run.kernel.server.KernelServer;
import amino.run.kernel.server.KernelServerImpl;

public class TwitterWorldGenerator {
	private static final Logger logger = Logger.getLogger(TwitterWorldGenerator.class.getName());
	public static UserManager userManager;

	public static void setObject(String[] args){
		java.rmi.registry.Registry registry;
		try {
			registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));
			Registry server = (Registry) registry.lookup("io.amino.run.oms");

			KernelServer nodeServer = new KernelServerImpl(new InetSocketAddress(args[2], Integer.parseInt(args[3])), new InetSocketAddress(args[0], Integer.parseInt(args[1])));

			/* Creating the Spec */

			MicroServiceSpec spec = MicroServiceSpec.newBuilder()
					.setLang(Language.java)
					.setJavaClassName("amino.run.appexamples.minnietwitter.app.TwitterManager")
					.create();

			/* Get Twitter and User Manager */
			MicroServiceID microServiceId = server.create(spec.toString());
			TwitterManager tm = (TwitterManager) server.acquireStub(microServiceId);

			/* To set a name to sapphire object. It is required to set the name if the object has to be shared */
			server.setName(microServiceId, "MyTwitterManager");

                        /* Attach to sapphire object is to get reference to shared sapphire object. Generally it
                        is not done in the same thread which creates sapphire object. In this example,
                        Twitter manager sapphire object is created just above in same thread. Below attach call
                        has no significance. It is just used to show the usage of API. */
			TwitterManager tmAttached =
					(TwitterManager) server.attachTo("MyTwitterManager");

                        /* Detach from the shared sapphire object. It is necessary to explicitly call detach to
                        un-reference the sapphire object. This call is not required here if attach call was not
                        made above */
			server.detachFrom("MyTwitterManager");

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
				e.printStackTrace();
				return tweetStatus;
			}
			logger.info("@user " + name + " tweeted: " + message);
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
			for(Tweet tw : myTweets){
				allTweets.add(tw.getText());
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
