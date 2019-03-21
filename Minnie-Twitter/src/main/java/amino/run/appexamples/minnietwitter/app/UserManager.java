package amino.run.appexamples.minnietwitter.app;

import java.security.MessageDigest;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

import amino.run.app.Language;
import amino.run.app.MicroServiceSpec;
import amino.run.app.MicroService;
import amino.run.common.MicroServiceCreationException;
import amino.run.policy.dht.DHTKey;
import static amino.run.runtime.MicroService.*;

public class UserManager implements MicroService {
	private static final Logger logger = Logger.getLogger(UserManager.class.getName());
	Map<DHTKey, User> users;
	private TagManager tm;

	public UserManager(TagManager tm) {
		this.tm = tm;
		this.users = new Hashtable<DHTKey, User>();
	}

	public User addUser(String username, String passwd) {

		MicroServiceSpec userSpec;
		User user = null;

		userSpec = MicroServiceSpec.newBuilder()
				.setLang(Language.java)
				.setJavaClassName(User.class.getName())
				.create();
		try {
			user = (User) new_(userSpec, new UserInfo(username, passwd), tm);
		} catch (MicroServiceCreationException e) {
			logger.warning("Creating MicroService failed" + e.toString());
		}
		user.initialize(user);
		users.put(new DHTKey(username), user);

		return user;
	}

	public void deleteUser(String username) {
		User user = users.remove(new DHTKey(username));
		user.deInitialize();
		delete_(user);
	}

	public User getUser(String username) {
		// TODO: check
		return users.get(new DHTKey(username));
	}

	// TODO: throws exception; test
	public User login(String username, String passwd) {
		User u = users.get(new DHTKey(username));

		if (u == null)
			return null;

		// check password
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] pass = md.digest(passwd.getBytes("UTF-8"));
			if (!checkPasswords(pass, u.getUserInfo().getPassword()))
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: throw exception
		}
		return u;
	}

	private boolean checkPasswords(byte[] p1, byte[] p2) {
		if (p1.length != p2.length)
			return false;

		for (int i = 0; i < p1.length; i++) {
			int loperand = (p1[i] & 0xff);
			int roperand = (p2[i] & 0xff);
			if (loperand != roperand) {
				return false;
			}
		}
		return true;
	}

}
