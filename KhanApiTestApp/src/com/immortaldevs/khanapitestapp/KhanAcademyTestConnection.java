package com.immortaldevs.khanapitestapp;

import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.net.Uri;
import android.os.AsyncTask;

public class KhanAcademyTestConnection {
		
	String mReqResultUrl;
	String mReqToken, mReqTokenSecret;

	public KhanAcademyTestConnection() {
		// TODO Auto-generated constructor stub
	}
	
	public void CreateConnection() {
		String reqUrl = null;
		try {
			//	Creates the request URL, send the user here
			reqUrl = new KhanAcademyConnector().createRequestTokenUrl();
			
			//	
			getReqResultUrl();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public class OAuthReqTask extends AsyncTask<Void, Void, Void> {

		String reqUrl = null;
		
		@Override
		protected Void doInBackground(Void... params) {
			
			try {
				reqUrl = new KhanAcademyConnector().createRequestTokenUrl();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if (reqUrl != null) {
				//mWebView.loadUrl(reqUrl);
			}
		}

		
	}
	
	public class OAuthAccessTask extends AsyncTask<Void, Void, Void> {

		String reqToken = null;
		String reqTokenSecret = null;
		String accessUrl = null;
		String accessToken = null;
		String accessTokenSecret = null;
		
		public OAuthAccessTask(String reqToken, String reqTokenSecret) {
			this.reqToken = reqToken;
			this.reqTokenSecret = reqTokenSecret;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			
			try {
				accessUrl = new KhanAcademyConnector().createAccessToken(reqToken, reqTokenSecret);
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(accessUrl);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				
				String responseString = EntityUtils.toString(httpResponse.getEntity());
				
				Map<String, String> responseMap = splitQuery(responseString);
				
				accessToken = responseMap.get(KhanAcademyConnector.OAUTH_TOKEN);
				accessTokenSecret = responseMap.get(KhanAcademyConnector.OAUTH_TOKEN_SECRET);
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if (accessUrl != null) {
				new OAuthCallTask(accessToken, accessTokenSecret).execute();
			}
		}

		
	}
	
	public class OAuthCallTask extends AsyncTask<Void, Void, Void> {

		String accessToken = null;
		String accessTokenSecret = null;
		String accessUrl = null;
		
		public OAuthCallTask(String accessToken, String accessTokenSecret) {
			this.accessToken = accessToken;
			this.accessTokenSecret = accessTokenSecret;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			
			try {
				accessUrl = new KhanAcademyConnector().makeSampleApiCall(accessToken, accessTokenSecret);
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(accessUrl);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				
				String responseString = EntityUtils.toString(httpResponse.getEntity());
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if (accessUrl != null) {
				
			}
		}
		
		public class OAuthRequestAndAccess extends AsyncTask<Void, Void, Void> {
			
			String reqUrl = null;
			
			String reqToken = null;
			String reqTokenSecret = null;
			
			String accessUrl = null;
			String accessToken = null;
			String accessTokenSecret = null;

			
			@Override
			protected Void doInBackground(Void... params) {
				
				try {

					reqUrl = new KhanAcademyConnector().createRequestTokenUrl();
					

					accessUrl = new KhanAcademyConnector().createAccessToken(reqToken, reqTokenSecret);
					HttpClient httpClient = new DefaultHttpClient();
					HttpGet httpGet = new HttpGet(accessUrl);
					HttpResponse httpResponse = httpClient.execute(httpGet);
					
					String responseString = EntityUtils.toString(httpResponse.getEntity());
					
					Map<String, String> responseMap = splitQuery(responseString);
					
					accessToken = responseMap.get(KhanAcademyConnector.OAUTH_TOKEN);
					accessTokenSecret = responseMap.get(KhanAcademyConnector.OAUTH_TOKEN_SECRET);
					
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				if (accessUrl != null) {
					
				}
			}
		}

		
	}
	
	private void getReqResultUrl() {
		
		//mReqResultUrl = mWebView.getUrl();
		
		
		Uri reqResultUri = Uri.parse(mReqResultUrl);
		
		mReqToken = reqResultUri.getQueryParameter(KhanAcademyConnector.OAUTH_TOKEN);
		mReqTokenSecret = reqResultUri.getQueryParameter(KhanAcademyConnector.OAUTH_TOKEN_SECRET);
		
		new OAuthAccessTask(mReqToken, mReqTokenSecret).execute();
	}
	
	public static Map<String, String> splitQuery(String queryString) throws Exception {
	    Map<String, String> queryPairs = new LinkedHashMap<String, String>();
	    String[] pairs = queryString.split("&");
	    for (String pair : pairs) {
	        int i = pair.indexOf("=");
	        queryPairs.put(URLDecoder.decode(pair.substring(0, i), "UTF-8"), URLDecoder.decode(pair.substring(i + 1), "UTF-8"));
	    }
	    return queryPairs;
	}
}


