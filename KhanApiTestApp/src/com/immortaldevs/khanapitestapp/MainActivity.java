package com.immortaldevs.khanapitestapp;

import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.immortaldevs.httpclienttestapp.R;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		
		WebView mWebView;
		ScrollView mApiCallResultScrollView;
		TextView mApiCallResultTextView;
		String mRequestUrl, mReqResultUrl, mAccessUrl;
		String mReqToken, mReqTokenSecret;
		String mAccessToken, mAccessTokenSecret;
		String mCallResponse;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			
			mWebView = (WebView) rootView.findViewById(R.id.webView);
			mWebView.getSettings().setJavaScriptEnabled(true);
			
			mApiCallResultScrollView = (ScrollView) rootView.findViewById(R.id.callScrollView);
			mApiCallResultTextView = (TextView) rootView.findViewById(R.id.callTextView);
			
			mWebView.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					
					Log.d(TAG, "URL Changed: " + url);
					
					if (url.startsWith(KhanAcademyConnector.CALLBACK)) {
						Log.d(TAG, "callback detected");
						mReqResultUrl = url;
						getReqResult();
						new OAuthAccessTask().execute();
					}
					
					return false;
				}
			});
			
			mWebView.setWebChromeClient(new WebChromeClient() {
				public void onProgressChanged(WebView webView, int progress) {
					/*if (progress == 100) {
						progressBar.setVisibility(View.INVISIBLE);
					} else {
						progressBar.setVisibility(View.VISIBLE);
						progressBar.setProgress(progress);
					}*/
				}
				
				public void onReceivedTitle (WebView webView, String title) {
					//titleTextView.setText(title);
				}
			});
			
			try {
				mRequestUrl = new KhanAcademyConnector().createRequestTokenUrl();
				mWebView.loadUrl(mRequestUrl);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			return rootView;
		}
		
		
		/*
		 * Simple AsyncTask to get the access tokens
		 */
		
		public class OAuthAccessTask extends AsyncTask<Void, Void, Void> {
			
			@Override
			protected Void doInBackground(Void... params) {
				
				try {
					mAccessUrl = new KhanAcademyConnector().createAccessToken(mReqToken, mReqTokenSecret);
					HttpClient httpClient = new DefaultHttpClient();
					HttpGet httpGet = new HttpGet(mAccessUrl);
					HttpResponse httpResponse = httpClient.execute(httpGet);
					
					String responseString = EntityUtils.toString(httpResponse.getEntity());
					Log.d(TAG, "response string: " + responseString);
					
					Map<String, String> responseMap = splitQuery(responseString);
					
					Log.d(TAG, "response string: " + responseString);
					
					mAccessToken = responseMap.get(KhanAcademyConnector.OAUTH_TOKEN);
					mAccessTokenSecret = responseMap.get(KhanAcademyConnector.OAUTH_TOKEN_SECRET);
					
					Log.d(TAG, "access token: " + mAccessToken);
					Log.d(TAG, "access token secret: " + mAccessTokenSecret);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				if (mAccessUrl != null) {
					Log.d(TAG, "access url: " + mAccessUrl);
					
					//	Do a test call to see if we get data back
					new OAuthCallTask(mAccessToken, mAccessTokenSecret).execute();
				}
			}

			
		}
		
		/*
		 * Sample call to the Khan Academy API
		 */
		
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
					Log.d(TAG, "call access url: " + accessUrl);
					HttpClient httpClient = new DefaultHttpClient();
					HttpGet httpGet = new HttpGet(accessUrl);
					//httpGet.getParams().setParameter("email", "RRG3rd@gmail.com");
					Log.d(TAG, "student exercise req: " + httpGet.getURI());
					HttpResponse httpResponse = httpClient.execute(httpGet);
					
					mCallResponse = EntityUtils.toString(httpResponse.getEntity());
					Log.d(TAG, "call response string: " + mCallResponse);
					
					//JSONObject responseObject = new JSONObject(responseString);
					
					//Log.d(TAG, "number of exercises: " + responseObject.getInt(name));
					
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				if (accessUrl != null) {
					mWebView.setVisibility(View.INVISIBLE);
					mApiCallResultScrollView.setVisibility(View.VISIBLE);
					mApiCallResultTextView.setText("API CALL RESPONSE:\n\n" + mCallResponse);
				}
			}

			
		}
		
		private void getReqResult() {
			
			Log.d(TAG, "request response url: " + mReqResultUrl);
			
			Uri reqResultUri = Uri.parse(mReqResultUrl);
			
			mReqToken = reqResultUri.getQueryParameter(KhanAcademyConnector.OAUTH_TOKEN);
			mReqTokenSecret = reqResultUri.getQueryParameter(KhanAcademyConnector.OAUTH_TOKEN_SECRET);
			
			Log.d(TAG, "request token: " + mReqToken);
			Log.d(TAG, "request token secret: " + mReqTokenSecret);
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

}
