package amino.run.appexamples.minnietwitter.app;

import amino.run.app.DMSpec;
import amino.run.app.Language;
import amino.run.app.MicroServiceSpec;
import amino.run.app.MicroService;
import amino.run.common.MicroServiceCreationException;
import amino.run.policy.atleastoncerpc.AtLeastOnceRPCPolicy;

import java.util.logging.Logger;

import static amino.run.runtime.MicroService.delete_;
import static amino.run.runtime.MicroService.new_;

public class TwitterManager implements MicroService {
	private static final Logger logger = Logger.getLogger(TwitterManager.class.getName());
	private UserManager userManager;
	private TagManager tagManager;
	
	public TwitterManager() {
		MicroServiceSpec tagManagerSpec,userManagerSpec;

		tagManagerSpec = MicroServiceSpec.newBuilder()
				.setLang(Language.java)
				.setJavaClassName(TagManager.class.getName())
				.create();
	    userManagerSpec = MicroServiceSpec.newBuilder()
				.setLang(Language.java)
				.setJavaClassName(UserManager.class.getName())
				.addDMSpec(
						DMSpec.newBuilder()
								.setName(AtLeastOnceRPCPolicy.class.getName())
								.create())
				.create();
		try {
			tagManager = (TagManager) new_(tagManagerSpec);
			userManager = (UserManager) new_(userManagerSpec, tagManager);
		} catch (MicroServiceCreationException e) {
			logger.warning("Creating MicroService failed" + e.toString());
		}
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public TagManager getTagManager() {
		return tagManager;
	}

	public void deInitialize() {
		delete_(tagManager);
		delete_(userManager);
	}
}
