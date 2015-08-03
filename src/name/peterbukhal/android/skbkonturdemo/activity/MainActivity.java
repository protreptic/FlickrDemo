package name.peterbukhal.android.skbkonturdemo.activity;

import name.peterbukhal.android.skbkonturdemo.R;
import name.peterbukhal.android.skbkonturdemo.fragment.TopPlacesListFragment;
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
