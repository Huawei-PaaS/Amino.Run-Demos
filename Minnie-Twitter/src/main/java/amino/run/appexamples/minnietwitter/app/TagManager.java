package amino.run.appexamples.minnietwitter.app;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import amino.run.app.MicroService;
import amino.run.policy.dht.DHTKey;


public class TagManager implements MicroService {
	private static final Logger logger = Logger.getLogger(TagManager.class.getName());
	Map<DHTKey, Tag> tags = new Hashtable<DHTKey, Tag>();
	
	public TagManager() {
	}
	
	public void addTag(String label, Tweet t) {
		DHTKey newKey = new DHTKey(label);
		
		Tag tag = tags.get(newKey);
		
		if (tag == null) {
			tag = new Tag(label);
			tags.put(newKey, tag);
		}

		logger.info("Adding tag: " + label + " for tweet: " + t.getText());
		tag.addTweet(t);
	}
	
	public List<Tweet> getTweetsForTag(String label, int from, int to) {
		Tag tag = tags.get(new DHTKey(label));
		
		if (tag == null)
			return new ArrayList<Tweet>();
		
		return tag.getTweets(from, to);
	}
}
