package com.google.glassware;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.mortbay.util.ajax.JSON;


class KhanAcademyApi {
	static String baseUri = "https://www.khanacademy.org";
	static String baseApiUri = "http://api-explorer.khanacademy.org";
	/*
	 * Decorator to return the coach's roster of students for the coach's specified credentials.
	 * Khan Academy is unfortunately missing an API to get the roster, so we have to login and do screen scraping.
	 */
	public static List<String> getRoster(String identifier, String password) {
		try {
			// login to Khan Academy
			String fkey = "1.0_glasshackaton";
			Executor executor = Executor.newInstance();
			HttpResponse response = executor.execute(Request.Post(baseUri + "/login").addHeader("Cookie", "fkey=" + fkey).bodyForm(Form.form().add("fkey", fkey).add("identifier", identifier).add("password", password).build())).returnResponse();
			// remember the session id for the next request
			String sessionId = response.getFirstHeader("Set-Cookie").getValue();
			// get the roster
			String html = executor.execute(Request.Get(baseUri + "/coach/roster/?listId=allStudents").addHeader("Cookie", sessionId)).returnContent().asString();
			// extract the usernames
			Pattern p = Pattern.compile("<a href=\"/profile/(.*?)/\""); // parse the HTML with regular expressions; we can't use XPath because this HTML is not a well-formed XML
			Matcher m = p.matcher(html);
			List<String> roster = new ArrayList<String>();
			while (m.find()) {
				roster.add(m.group(1));
			}
			return roster;
		} catch (Exception e) {
			return null;
		}
	}
	/*
	 * PENDING - shamefully simulating OAuth to khanacademy.org; hackatons go fast!
	 */
	public static String OAuth() {
		String session = "v9qefoyB4QXciAiwKWAetyODomU=";
		String oauth_token_string = "UydvYXV0aF90b2tlbl9zZWNyZXQ9NmVtaEpIeUgzWm1QWTVBOSZvYXV0aF90b2tlbj10NjY1MTY1MDcyNTM4MDA5NicKcDEKLg==";
		String request_token_string = "UydvYXV0aF90b2tlbl9zZWNyZXQ9dnhSWUJMTmFzWGc5VjlZQiZvYXV0aF90b2tlbj10NTU5OTE5MDc4MzA5ODg4MCcKcDEKLg==";
		return "session=\"" + session + "?oauth_token_string=" + oauth_token_string + "&request_token_string=" + request_token_string + "\";";
	}
	/*
	 * Adapter for the Khan Academy API to get information about the specified user name.
	 */
	public static Map<?,?> getUser(String username) {
		try {
			String json = Request.Get(baseApiUri + "/api/v1/user?username=" + username)
					.addHeader("X-Requested-With", "XMLHttpRequest")
					.addHeader("Cookie", OAuth()).execute().returnContent().asString();
			Map<?,?> map = (Map<?,?>)JSON.parse(json);
			String response = (String)map.get("response");
			return (Map<?,?>)JSON.parse(response);
		} catch (Exception e) {
			return null;
		}
	}
	/*
	 * Adapter for the Khan Academy API to get the list of exercises for the specified user name.
	 */
	public static String[] getExercises(String username) {
		// /api/v1/user/exercises
		// PENDING: unfortunately, although it's a valid API request,
		// the Khan Academy server is currently returning
		// "500 Internal Server Error [...] the server is overloaded"
		return new String[] { "making-ten-2", "count-from-any-number", "teen-numbers-2", "counting-objects" };
	}
	/*
	 * Adapter for the Khan Academy API to get information about the specified user name and exercise name.
	 */
	public static Map<?,?> getExercise(String username, String exercise_name) {
		try {
			String json = Request.Get(baseApiUri + "/api/v1/user/exercises/" + exercise_name + "?username=" + username)
					.addHeader("X-Requested-With", "XMLHttpRequest")
					.addHeader("Cookie", OAuth()).execute().returnContent().asString();
			Map<?,?> map = (Map<?,?>)JSON.parse(json);
			String response = (String)map.get("response");
			return (Map<?,?>)JSON.parse(response);
		} catch (Exception e) {
			return null;
		}
	}
}
