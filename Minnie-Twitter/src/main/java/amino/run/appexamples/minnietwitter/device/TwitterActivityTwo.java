package amino.run.appexamples.minnietwitter.device;

import java.net.InetSocketAddress;
import java.rmi.registry.LocateRegistry;
import java.util.List;
import java.util.logging.Logger;

import amino.run.app.Language;
import amino.run.app.Registry;
import amino.run.appexamples.minnietwitter.app.Timeline;
import amino.run.appexamples.minnietwitter.app.TagManager;
import amino.run.appexamples.minnietwitter.app.Tweet;
import amino.run.appexamples.minnietwitter.app.TwitterManager;
import amino.run.appexamples.minnietwitter.app.User;
import amino.run.appexamples.minnietwitter.app.UserManager;
import amino.run.app.MicroServiceSpec;
import amino.run.common.MicroServiceID;
import amino.run.kernel.server.KernelServer;
import amino.run.kernel.server.KernelServerImpl;

//TODO: Convert this into android application and make changes as per latest amino code and this is required as it
// covers multi-user scenario.
public class TwitterActivityTwo {
    private static final Logger logger = Logger.getLogger(TwitterActivityTwo.class.getName());
    /**
     * To execute this application, please pass in three parameters: <OMS-IP> <OMS-Port> <KernelServer-Port>
     *
     * @param args <ul>
     *             <li><code>args[0]</code>:</li> OMS server IP address
     *             <li><code>args[1]</code>:</li> OMS server Port number
     *             <li><code>args[2]</code>:</li> Kernel server Port number
     *             </ul>
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        java.rmi.registry.Registry registry;
        registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));
        Registry server = (Registry) registry.lookup("io.amino.run.oms");

        // This kernel server is a fake kernel server. It is _not_ registered in OMS. Therefore
        // there will be no Sapphire object on this server. The purpose of creating such a fake
        // kernel server is to construct a KernelClient (inside the KernelServer object) and to
        // configure GlobalKernelReferences.nodeServer properly.
        //
        // Since this is a fake kernel server, we do not have to use the real IP of this host.
        // We can use any IP address as long as it does not conflict with OMS IP and the IPs of
        // other kernel servers. To keep things simple, I hard coded it as "127.0.0.2".
        KernelServer nodeServer = new KernelServerImpl(new InetSocketAddress("127.0.0.2", Integer.parseInt(args[2])), new InetSocketAddress(args[0], Integer.parseInt(args[1])));

        /* Creating the Spec */

        MicroServiceSpec spec = MicroServiceSpec.newBuilder()
                .setLang(Language.java)
                .setJavaClassName("amino.run.appexamples.minnietwitter.app.TwitterManager")
                .create();

        MicroServiceID microServiceID = server.create(spec.toString());
        TwitterManager tm = (TwitterManager)server.acquireStub(microServiceID);
        logger.info("Received Twitter Manager Stub: " + tm);

        UserManager userManger = tm.getUserManager();
        TagManager tagManager = tm.getTagManager();

        User me = userManger.addUser("user1", "user1_password");
        Timeline myTimeline = me.getTimeline();
        User peer = userManger.getUser("user1");

        peer.addFollowing(me);
        me.addFollower(peer);

        Timeline peerTimeline = peer.getTimeline();
        peerTimeline.tweet("peer hello");
        List<Tweet> peerTweets = peerTimeline.getTweets(0, 1);
        myTimeline.retweet(peerTweets.get(0));

        List<Tweet> myTweets = myTimeline.getTweets(0, 1);
        logger.info("My tweet: " + myTweets.get(0).getText());

        tagManager.addTag("#goodlife", myTweets.get(0));
        List<Tweet> tweetsForTag = tagManager.getTweetsForTag("#goodlife", 0, 1);
        logger.info("Tweet for tag #goodlife: " + tweetsForTag.get(0).getText());

        logger.info("Retweets: " + peerTimeline.getRetweets(peerTweets.get(0).getId(), 0, 1).get(0));
        System.exit(0);
    }
}
