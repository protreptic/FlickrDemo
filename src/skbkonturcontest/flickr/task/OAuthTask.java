package skbkonturcontest.flickr.task;

import java.io.IOException;

import skbkonturcontest.flickr.util.FlickrUtils;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.auth.Permission;
import com.googlecode.flickrjandroid.oauth.OAuthToken;

public class OAuthTask extends AsyncTask<Void, Void, Uri> {

	private Context mContext;
	private ProgressDialog progressDialog;
	
	public OAuthTask(Context context) {
		mContext = context;
		
		progressDialog = new ProgressDialog(context);
		progressDialog.setTitle("");
		progressDialog.setMessage("Loading"); 
		progressDialog.setCancelable(false); 
	}
	
	@Override
	protected void onPreExecute() {
		progressDialog.show();
	}
	
    @Override
    protected Uri doInBackground(Void... params) {
    	Flickr flick = FlickrUtils.getInstance();
		
		try {
			OAuthToken token = flick.getOAuthInterface().getRequestToken(FlickrUtils.CALLBACK_SCHEME + "://oauth");
			
			FlickrUtils.saveOAuthToken(mContext, null, null, token.getOauthToken(), token.getOauthTokenSecret()); 
			
			return Uri.parse(flick.getOAuthInterface().buildAuthenticationUrl(Permission.WRITE, token).toString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FlickrException e) {
			e.printStackTrace();
		}
		
		return null;
    }
    
    @Override
    protected void onPostExecute(Uri uri) {
    	progressDialog.dismiss();
    	
    	Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    	
    	mContext.startActivity(intent);
    }
    
}
