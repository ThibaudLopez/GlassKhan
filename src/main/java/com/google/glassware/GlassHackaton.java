package com.google.glassware;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.mirror.model.NotificationConfig;
import com.google.api.services.mirror.model.TimelineItem;

public class GlassHackaton {
	  private static final Logger LOG = Logger.getLogger(NewUserBootstrapper.class.getSimpleName());
	  String[] colors = { "BlueViolet", "Crimson", "DarkGreen", "ForestGreen", "DeepSkyBlue", "LawnGreen", "OrangeRed" };
	/*
	 * Start
	 */
	public GlassHackaton(Credential credential) throws IOException {
		// get the coach's roster
		String identifier = "identifier@khanacademy";
		String password = "*******";
		List<String> roster = KhanAcademyApi.getRoster(identifier, password);
		if (roster == null) {
			System.err.println("Couldn't get roster.");
			return;
		}
		// get each user's information
		for (String username: roster) {
			System.out.println("username: " + username);
			Map<?,?> user = KhanAcademyApi.getUser(username);
			if (user == null) {
				System.err.println("Couldn't get user " + username);
				continue;
			}
			String nickname = (String)user.get("nickname");
			System.out.println("nickname: " + nickname);
			// get the user's exercises
			String[] exercises = KhanAcademyApi.getExercises(username);
			// get each exercise's level
			for (String exercise_name: exercises) {
				Map<?,?> exercise = KhanAcademyApi.getExercise(username, exercise_name);
				if (exercise == null) {
					System.err.println("Couldn't get exercise " + exercise_name);
					continue;
				}
				Map<?,?> exercise_model = (Map<?,?>)exercise.get("exercise_model");
				String translated_title = (String)exercise_model.get("translated_title");					
				Map<?,?> exercise_states = (Map<?,?>)exercise.get("exercise_states");
				Boolean struggling = (Boolean)exercise_states.get("struggling");
				if (struggling) {
					int color1 = (int)(Math.random()*colors.length);
					int color2 = (int)(Math.random()*colors.length);
					String html = "<article><section><p class=\"text-auto-size\"><strong style=\"color:" + colors[color1] + "\">" + nickname + "</strong> is struggling with <strong style=\"color:" + colors[color2] +"\">" + translated_title + "</strong></p></section></article>";
					// insert timeline card here
				    TimelineItem timelineItem = new TimelineItem();
				    timelineItem.setHtml(html);
				    timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));
				    TimelineItem insertedItem = MirrorClient.insertTimelineItem(credential, timelineItem);
				    LOG.info("GlassHackaton inserted message " + insertedItem.getId());
				}
			}
		}
	}

}
