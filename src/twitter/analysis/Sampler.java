package twitter.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twitter.data.User;
import twitter.data.UserLoader;
import twitter.utils.IOUtils;
import twitter.utils.Logger;

public class Sampler {

	public static final String SAMPLE_DATA_PATH = UserLoader.PATH + "sampled-normal-users.obj";

	public static void sample() {
		final Map<Long, User> allUsers = UserLoader.loadAllUsers();
		final Map<Long, User> spammers = UserLoader.loadSpammers();

		Sampler sampler = new Sampler(allUsers);
		sampler.addFilter(new SampleFilter() {

			@Override
			public boolean accept(User u) {
				return !spammers.containsKey(u.getId());
			}
		});
		sampler.addFilter(new SampleFilter() {

			@Override
			public boolean accept(User u) {
				return UserLoader.loadTweet(u.getId()).size() != 0;
			}
		});

		Map<Long, User> sampledUsers = sampler.sample(spammers.size());
		for (User u : sampledUsers.values()) {
			u.setTweets(UserLoader.loadTweet(u.getId()));
		}
		IOUtils.saveObject(sampledUsers, SAMPLE_DATA_PATH);
	}

	public static void fillSampledUsersOldDegree() {
		Map<Long, User> sampledUsers = loadSampledUsers();
		Map<Long, Integer[]> oldDegree = UserLoader.loadOldDegree();
		int i = 0;
		for (User u : sampledUsers.values()) {
			if (i % 100 == 0) {
				Logger.info("Filled " + i + " users...");
			}
			if (oldDegree.containsKey(u.getId())) {
				u.setOldInDegree(oldDegree.get(u.getId())[0]);
				u.setOldOutDegree(oldDegree.get(u.getId())[1]);
			}
			i++;
		}
		IOUtils.saveObject(sampledUsers, SAMPLE_DATA_PATH);
	}

	public static Map<Long, User> loadSampledUsers() {
		return (Map<Long, User>) IOUtils.loadObject(SAMPLE_DATA_PATH);
	}

	public Sampler(Map<Long, User> allUsers) {
		this.allUsers = new ArrayList<>(allUsers.values());
		this.filters = new ArrayList<>();
	}

	public void addFilter(SampleFilter filter) {
		filters.add(filter);
	}

	public Map<Long, User> sample(int size) {
		Map<Long, User> result = new HashMap<>();

		Collections.shuffle(allUsers);

		for (int i = 0; i < allUsers.size() && result.size() < size; i++) {
			User u = allUsers.get(i);
			boolean accept = true;
			for (SampleFilter filter : filters) {
				accept &= filter.accept(u);
			}

			if (accept) {
				result.put(u.getId(), u);
				if (result.size() % 100 == 0) {
					Logger.info("Sampled " + result.size() + " users...");
				}
			}
		}

		return result;
	}

	public static interface SampleFilter {

		boolean accept(User u);
	}

	private List<User> allUsers;
	private List<SampleFilter> filters;
}
