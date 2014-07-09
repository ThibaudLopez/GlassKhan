package com.immortaldevs.khanapitestapp;

import java.util.Map;

import android.util.Log;

import com.google.gdata.client.authn.oauth.OAuthHelper;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.google.gdata.client.authn.oauth.OAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthParameters.OAuthType;
import com.google.gdata.client.authn.oauth.OAuthUtil;

public class KhanAcademyConnector {
	
	private static final String TAG = "KhanAcademyConnector";
	
	//API REQs

	public static final String CONSUMER_KEY = "bsXqdcLSH3TJVcgv";
	public static final String CONSUMER_SECRET = "sZZtpfAt6dTK6jZX";
	public static final String REQ_URL = "http://www.khanacademy.org/api/auth/request_token";
	public static final String ACCESS_URL = "http://www.khanacademy.org/api/auth/access_token";
	public static final String AUTHORIZE_URL = "http://www.khanacademy.org/api/auth/authorize";//?token=%s";
	public static final String VERSION = "1.0";
	public static final String SIGNATURE = "blah";
	public static final String SIGNATURE_METHOD = "HMAC-SHA1";
	public static final String CALLBACK = "http://www.khanacademy.org/api/auth/default_callback";
	
	public static final String OAUTH_CONSUMER_KEY = "oauth_consumer_key";
	public static final String OAUTH_TOKEN = "oauth_token";
	public static final String OAUTH_TOKEN_SECRET = "oauth_token_secret";
	public static final String OAUTH_VERSION = "oauth_version";
	public static final String OAUTH_TIMESTAMP = "oauth_timestamp";
	public static final String OAUTH_NONCE = "oauth_nonce";
	public static final String OAUTH_SIGNATURE = "oauth_signature";
	public static final String OAUTH_SIGNATURE_METHOD = "oauth_signature_method";
	public static final String OAUTH_CALLBACK = "oauth_callback";
	
	//SAMPLE API CALL INFO
	
	//public static final String SAMPLE_URL = "http://www.khanacademy.org/api/v1/exercises";//count-to-100";
	//public static final String SAMPLE_URL = "http://www.khanacademy.org/api/v1/user/exercises";
	//public static final String SAMPLE_URL = "http://www.khanacademy.org/api/v1/user/exercises/progress_changes";
	public static final String SAMPLE_URL = "http://www.khanacademy.org/api/v1/user";
	
	public String createRequestTokenUrl() throws Exception {
		
		OAuthHmacSha1Signer oAuthSigner = new OAuthHmacSha1Signer();
		OAuthParameters oAuthParams = new OAuthParameters();
		
		oAuthParams.setOAuthConsumerKey(CONSUMER_KEY);
		oAuthParams.setOAuthConsumerSecret(CONSUMER_SECRET);
		oAuthParams.setOAuthNonce(OAuthUtil.getNonce());
		oAuthParams.setOAuthTimestamp(OAuthUtil.getTimestamp());
		oAuthParams.setOAuthType(OAuthType.TWO_LEGGED_OAUTH);
		oAuthParams.setOAuthSignatureMethod(SIGNATURE_METHOD);
		oAuthParams.setOAuthConsumerSecret(CONSUMER_SECRET);
		oAuthParams.addCustomBaseParameter(OAUTH_VERSION, VERSION);
		
		String baseString = OAuthUtil.getSignatureBaseString(REQ_URL, "GET", oAuthParams.getBaseParameters());
		
		Log.d(TAG, "baseString: " + baseString);
		
		String signature = oAuthSigner.getSignature(baseString, oAuthParams);
		
		Log.d(TAG, "signature: " + signature);
		
		oAuthParams.addCustomBaseParameter(OAUTH_SIGNATURE, signature);

		OAuthHelper oAuthHelper = new OAuthHelper(REQ_URL, AUTHORIZE_URL, ACCESS_URL, null, oAuthSigner);
		
		String reqUrl = oAuthHelper.getOAuthUrl(REQ_URL, "GET", oAuthParams).toString();
		Log.d(TAG, "oauth request page: " + reqUrl);
		
		return reqUrl;
	}

	public String createAccessToken(String reqToken, String reqTokenSecret) throws Exception {
		
		OAuthHmacSha1Signer oAuthSigner = new OAuthHmacSha1Signer();
		OAuthParameters oAuthParams = new OAuthParameters();
		
		oAuthParams.setOAuthConsumerKey(CONSUMER_KEY);
		oAuthParams.setOAuthConsumerSecret(CONSUMER_SECRET);
		oAuthParams.setOAuthToken(reqToken);
		oAuthParams.setOAuthTokenSecret(reqTokenSecret);
		oAuthParams.setOAuthNonce(OAuthUtil.getNonce());
		oAuthParams.setOAuthTimestamp(OAuthUtil.getTimestamp());
		oAuthParams.setOAuthType(OAuthType.TWO_LEGGED_OAUTH);
		oAuthParams.setOAuthSignatureMethod(SIGNATURE_METHOD);
		oAuthParams.setOAuthConsumerSecret(CONSUMER_SECRET);
		oAuthParams.addCustomBaseParameter(OAUTH_VERSION, VERSION);
		
		String baseString = OAuthUtil.getSignatureBaseString(ACCESS_URL, "GET", oAuthParams.getBaseParameters());
		
		Log.d(TAG, "baseString: " + baseString);
		
		String signature = oAuthSigner.getSignature(baseString, oAuthParams);
		
		Log.d(TAG, "signature: " + signature);
		
		oAuthParams.addCustomBaseParameter(OAUTH_SIGNATURE, signature);
		
		OAuthHelper oAuthHelper = new OAuthHelper(REQ_URL, AUTHORIZE_URL, ACCESS_URL, null, oAuthSigner);
		
		String accessUrl = oAuthHelper.getOAuthUrl(ACCESS_URL, "GET", oAuthParams).toString();
		Log.d(TAG, "oauth access page: " + accessUrl);
		
		return accessUrl;
		
	}
	
	public String makeSampleApiCall(String accessToken, String accessTokenSecret) throws Exception {
		
		OAuthHmacSha1Signer oAuthSigner = new OAuthHmacSha1Signer();
		OAuthParameters oAuthParams = new OAuthParameters();
		
		oAuthParams.setOAuthConsumerKey(CONSUMER_KEY);
		oAuthParams.setOAuthConsumerSecret(CONSUMER_SECRET);
		oAuthParams.setOAuthToken(accessToken);
		oAuthParams.setOAuthTokenSecret(accessTokenSecret);
		oAuthParams.setOAuthNonce(OAuthUtil.getNonce());
		oAuthParams.setOAuthTimestamp(OAuthUtil.getTimestamp());
		oAuthParams.setOAuthType(OAuthType.TWO_LEGGED_OAUTH);
		oAuthParams.setOAuthSignatureMethod(SIGNATURE_METHOD);
		oAuthParams.addCustomBaseParameter(OAUTH_VERSION, VERSION);
		
		oAuthParams.addCustomBaseParameter("email", "RRG3rd@gmail.com");
		
		//oAuthParams.addExtraParameter("exercise_name", "count-to-100");
		//oAuthParams.addExtraParameter("userid", "ryanj.mccormick86@gmail.com");
		//oAuthParams.addExtraParameter("userid", "RRG3rd@gmail.com");
		//oAuthParams.addExtraParameter("userid", "thibaud.lopez.schneider@gmail.com");
		
		String baseString = OAuthUtil.getSignatureBaseString(SAMPLE_URL, "GET", oAuthParams.getBaseParameters());
		
		Log.d(TAG, "baseString: " + baseString);
		
		String signature = oAuthSigner.getSignature(baseString, oAuthParams);
		
		Log.d(TAG, "signature: " + signature);
		
		oAuthParams.addCustomBaseParameter(OAUTH_SIGNATURE, signature);
		
		OAuthHelper oAuthHelper = new OAuthHelper(REQ_URL, AUTHORIZE_URL, ACCESS_URL, null, oAuthSigner);
		
		String accessUrl = oAuthHelper.getOAuthUrl(SAMPLE_URL, "GET", oAuthParams).toString();
		Log.d(TAG, "oauth call page: " + accessUrl);
		
		return accessUrl;
		
	}
	
	private String buildAuthHeaderString(OAuthParameters params) {
		StringBuffer buffer = new StringBuffer();
		int cnt = 0;
		buffer.append("OAuth ");
		Map<String, String> paramMap = params.getBaseParameters();
		Object[] paramNames = paramMap.keySet().toArray();
		for (Object paramName : paramNames) {
			String value = paramMap.get((String) paramName);
			buffer.append(paramName + "=\"" + OAuthUtil.encode(value) + "\"");
			cnt++;
			if (paramNames.length > cnt) {
				buffer.append(",");
			}

		}
		return buffer.toString();
	}
	
}
