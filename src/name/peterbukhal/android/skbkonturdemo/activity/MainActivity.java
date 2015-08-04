package name.peterbukhal.android.skbkonturdemo.activity;

import name.peterbukhal.android.skbkonturdemo.R;
import name.peterbukhal.android.skbkonturdemo.fragment.TopPlacesListFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_activity);
		 
		Fragment fragment = new TopPlacesListFragment();
		
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();	
        
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
		
	    setSupportActionBar(mToolBar);
	}
	
}
