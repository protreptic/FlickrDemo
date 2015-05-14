package skbkonturcontest.flickr.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;

public class FlickrUtils {
	
	public static final String CALLBACK_SCHEME = "flickrj-android-oauth";
	
	private static final String APP_KEY = "153f33703432b91f607afa5dc195c23d";
	private static final String APP_SECRET = "da16906b3ce5d5e2";
	
	private static Flickr sInstance;
	
	private FlickrUtils() {}
	
	public static Flickr getInstance() {
		if (sInstance == null) {
			sInstance = new Flickr(APP_KEY, APP_SECRET);
		}
		
		return sInstance;
	}
	
	public static final String PREFS_NAME = "flickrj-auth";
	public static final String KEY_OAUTH_TOKEN = "token"; 
	public static final String KEY_TOKEN_SECRET = "tokenSecret";
	public static final String KEY_USER_NAME = "userName";
	public static final String KEY_USER_ID = "userId";
	
	public static void saveOAuthToken(Context context, String userName, String userId, String token, String tokenSecret) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		
		Editor editor = sharedPreferences.edit();
		editor.putString(KEY_OAUTH_TOKEN, token);
		editor.putString(KEY_TOKEN_SECRET, tokenSecret);
		editor.putString(KEY_USER_NAME, userName);
		editor.putString(KEY_USER_ID, userId);
		editor.commit();
	}
	
	public static OAuth getOAuthToken(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		
		String token = sharedPreferences.getString(KEY_OAUTH_TOKEN, null);
		String tokenSecret = sharedPreferences.getString(KEY_TOKEN_SECRET, null);
		
		if (token == null && tokenSecret == null) {
			return null;
		}
		
		OAuthToken oauthToken = new OAuthToken();
		oauthToken.setOauthToken(token);
		oauthToken.setOauthTokenSecret(tokenSecret);
		
		OAuth oauth = new OAuth();
		
		oauth.setToken(oauthToken);
		
		String userName = sharedPreferences.getString(KEY_USER_NAME, null);
		String userId = sharedPreferences.getString(KEY_USER_ID, null);
		
		if (userId != null) {
           User user = new User();
           user.setUsername(userName);
           user.setId(userId);
           
           oauth.setUser(user);
		}
		
		return oauth;
	}
	
}
