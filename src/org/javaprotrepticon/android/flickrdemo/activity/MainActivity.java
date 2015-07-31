package org.javaprotrepticon.android.flickrdemo.activity;

import java.io.IOException;

import org.javaprotrepticon.android.flickrdemo.R;
import org.javaprotrepticon.android.flickrdemo.fragment.TopPlacesListFragment;
import org.javaprotrepticon.android.flickrdemo.task.OAuthTask;
import org.javaprotrepticon.android.flickrdemo.util.FlickrUtils;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthInterface;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainActivity extends ActionBarActivity {

    @Override
    public void onBackPressed() {
    	if (mDrawerLayout.isDrawerOpen(mLeftDrawer)) {
    		mDrawerLayout.closeDrawer(mLeftDrawer); return;
    	}
    	
    	super.onBackPressed();
    }
	
	private DrawerLayout mDrawerLayout;
	private LinearLayout mLeftDrawer;
	private ActionBarDrawerToggle mDrawerToggle;
	
	public class DefaultDrawerListener implements DrawerLayout.DrawerListener {
		
        @Override
        public void onDrawerOpened(View drawerView) {
            mDrawerToggle.onDrawerOpened(drawerView);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            mDrawerToggle.onDrawerClosed(drawerView);
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            mDrawerToggle.onDrawerStateChanged(newState);
        }
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_activity);
		 
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerLayout.setDrawerListener(new DefaultDrawerListener());
        mDrawerLayout.setDrawerTitle(GravityCompat.START, getString(R.string.drawer_title));
        
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        
        mLeftDrawer = (LinearLayout) findViewById(R.id.left_drawer);
		
		OAuth oauth = FlickrUtils.getOAuthToken(getBaseContext());
		
        if (oauth == null || oauth.getUser() == null) {
        	new OAuthTask(this).execute();
        } else {
			Fragment fragment = new TopPlacesListFragment();
			
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            //fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();	
        }
        
        initToolbar();
        initImageLoader();
	}
	
	private void initImageLoader() {
        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
    	.cacheInMemory(true)
    	.cacheOnDisk(true)
    	.build();
    
	    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
	        .defaultDisplayImageOptions(displayImageOptions)
	        .build();
	    
	    ImageLoader imageLoader = ImageLoader.getInstance();
	    imageLoader.init(config);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.destroy();
	}
	
	private Toolbar mToolBar;
	
	private void initToolbar() {
		mToolBar = (Toolbar) findViewById(R.id.toolbar);
		mToolBar.setTitle("");
		mToolBar.setSubtitle("");
		//mToolBar.setBackgroundColor(getResources().getColor(R.color.flickr2));  
		
	    setSupportActionBar(mToolBar);
	}
	
	@Override
    protected void onNewIntent(Intent intent) {
		setIntent(intent);
    }
    
	@Override
	protected void onResume() {
        super.onResume();
        
        Intent intent = getIntent();
        String scheme = intent.getScheme();
        
        OAuth savedToken = FlickrUtils.getOAuthToken(getBaseContext());
        if (FlickrUtils.CALLBACK_SCHEME.equals(scheme) && (savedToken == null || savedToken.getUser() == null)) {
            Uri uri = intent.getData();
            String query = uri.getQuery();

            String[] data = query.split("&");
            if (data != null && data.length == 2) {
                String oauthToken = data[0].substring(data[0].indexOf("=") + 1);
                String oauthVerifier = data[1].substring(data[1].indexOf("=") + 1);
                
                OAuth oauth = FlickrUtils.getOAuthToken(getBaseContext());
                if (oauth != null && oauth.getToken() != null && oauth.getToken().getOauthTokenSecret() != null) {
                	new GetOAuthTokenTask().execute(oauthToken, oauth.getToken().getOauthTokenSecret(), oauthVerifier);
                }
            }
        }
	}
	
	public class GetOAuthTokenTask extends AsyncTask<String, Integer, OAuth> {

	    @Override
	    protected OAuth doInBackground(String... params) {
			String oauthToken = params[0];
			String oauthTokenSecret = params[1];
			String verifier = params[2];
			
			Flickr flick = FlickrUtils.getInstance();
			
			OAuthInterface oauthApi = flick.getOAuthInterface();
			
			try {
				return oauthApi.getAccessToken(oauthToken, oauthTokenSecret, verifier);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (FlickrException e) {
				e.printStackTrace();
			}
			
			return null;
	    }

	    @Override
	    protected void onPostExecute(OAuth result) {
	    	FlickrUtils.saveOAuthToken(getBaseContext(), result.getUser().getUsername(), result.getUser().getId(), result.getToken().getOauthToken(), result.getToken().getOauthTokenSecret()); 
	    	
			Fragment fragment = new TopPlacesListFragment();
			
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();	
	    }

	}
	
}
