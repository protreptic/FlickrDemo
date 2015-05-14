package skbkonturcontest.flickr.fragment.base;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.javaprotrepticon.android.androidutils.Fonts;

import skbkonturcontest.flickr.R;
import skbkonturcontest.flickr.storage.Storage;
import android.accounts.Account;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

public abstract class BaseEntityListFragment<T> extends Fragment implements OnRefreshListener {
	
	protected Account mAccount;
	
	protected Typeface mRobotoCondensedBold;
	protected Typeface mRobotoCondensedRegular;
	protected Typeface mRobotoCondensedLight;
	
	protected RecyclerView mRecyclerView;
	private RecyclerView.Adapter<?> mRecyclerViewAdapter;
	
	private SwipeRefreshLayout mSwipeRefreshWidget;
	
	protected List<T> mEntityList = new ArrayList<T>();
	
	private Handler mHandler = new Handler();
	
    private final Runnable mRefreshDone = new Runnable() {

        @Override
        public void run() {
            mSwipeRefreshWidget.setRefreshing(false);
        }

    };
    
    private final Runnable mRefreshBegin = new Runnable() {

        @Override
        public void run() {
            mSwipeRefreshWidget.setRefreshing(true);
        }

    };
	
    protected Calendar mCalendar = Calendar.getInstance(new Locale("ru", "RU"));
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.recyclerview, container, false);
	}
	
	@Override
	public void onRefresh() {
		refreshData();
	}
	
	protected abstract RecyclerView.Adapter<?> createAdapter();
	protected abstract void refreshData();
	protected abstract Class<T> getType();
	
	public abstract class DefaultAdapter extends RecyclerView.Adapter<DefaultViewHolder> {
		
		@Override
		public int getItemCount() {
			return mEntityList.size();
		}
		
		@Override
		public DefaultViewHolder onCreateViewHolder(ViewGroup parent, int position) {
			return new DefaultViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.place_item_v2, parent, false));
		}
		
	}
	
	public abstract class PhotoAdapter extends RecyclerView.Adapter<PhotoViewHolder> {
		
		private int lastPosition = -1;
		
		@Override
		public int getItemCount() {
			return mEntityList.size();
		}
		
		@Override
		public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int position) {
			return new PhotoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item_v2, parent, false));
		}
		
		public void setAnimation(View viewToAnimate, int position) {
	        if (position > lastPosition) {
	            Animation animation = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
	            viewToAnimate.startAnimation(animation);
	            lastPosition = position;
	        }
	    }
		
	}
	
    public abstract class DataLoader extends AsyncTask<Void, Void, Void> {
    	
    	protected Storage mStorage;
    	protected Dao<T, String> mDao;
    	protected QueryBuilder<T, String> mQueryBuilder;
    	
    	@SuppressWarnings("unchecked")
		@Override
    	protected void onPreExecute() {
    		mIsLoading = true;
    		
    		mHandler.removeCallbacks(mRefreshDone);
            mHandler.removeCallbacks(mRefreshBegin);
            mHandler.postDelayed(mRefreshBegin, 250);
    		
    		mEntityList.clear();
    		mRecyclerViewAdapter.notifyDataSetChanged();
    		
    		Class<T> type = getType();
    		
    		mStorage = new Storage(getActivity());
    		mDao = (Dao<T, String>) mStorage.createDao(type);
    		mQueryBuilder = mDao.queryBuilder();
    	}
    	
		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mStorage.closeConnection();
			
			mRecyclerViewAdapter.notifyDataSetChanged();
			
			mHandler.removeCallbacks(mRefreshBegin);
            mHandler.removeCallbacks(mRefreshDone);
            mHandler.postDelayed(mRefreshDone, 10);
			
			mIsLoading = false;
		}
    	
    }
    
    protected String mQueryText = "%%";
    
    private boolean mIsLoading;
    
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
				
		inflater.inflate(R.menu.base_entity_list_fragment, menu);
		
		MenuItem menuItem = menu.findItem(R.id.actionSearch2);
		menuItem.setIcon(getResources().getDrawable(R.drawable.search));
		 
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.actionSearch2));
	    searchView.setOnQueryTextListener(new OnQueryTextListener() {
			
	    	private boolean makeQuery(String query) {
	    		boolean result = false;
	    		
				if (query.length() >= 3) {
					mQueryText = "%" + query + "%";
					
					refreshData();
					
					result = true;
				}
				if (query.isEmpty()) {
					mQueryText = "%%";
					
					refreshData();
					
					result = true;
				}
				
				return result;
	    	}
	    	
			@Override
			public boolean onQueryTextSubmit(String query) {
				return mIsLoading ? false : makeQuery(query);
			}
			
			@Override
			public boolean onQueryTextChange(String query) {
				return mIsLoading ? false : makeQuery(query);
			}
		});
	}
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true); 
	}
	
	private int columns() {
		int columns = 2;
		
		Configuration config = getResources().getConfiguration();
		
		int screenSize = config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
		
		if (screenSize == Configuration.SCREENLAYOUT_SIZE_SMALL) {
		    if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
		    	columns = 2;
		    } else {
		    	columns = 1;
		    }
		} 
		if (screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
		    if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
		    	columns = 2;
		    } else {
		    	columns = 1;
		    }
		} 
		if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE) {
		    if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
		    	columns = 3;
		    } else {
		    	columns = 2;
		    }
		} 
		if (screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
		    if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
		    	columns = 4;
		    } else {
		    	columns = 2;
		    }
		} 
		
		return columns;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mRobotoCondensedBold = Fonts.get(getActivity()).getTypeface("RobotoCondensed-Bold");
		mRobotoCondensedRegular = Fonts.get(getActivity()).getTypeface("RobotoCondensed-Regular");
		mRobotoCondensedLight = Fonts.get(getActivity()).getTypeface("RobotoCondensed-Light");
		
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), columns());
        layoutManager.setReverseLayout(false);
		
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.cardList);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setVerticalScrollBarEnabled(true); 
        
		mRecyclerViewAdapter = createAdapter();
		mRecyclerView.setAdapter(mRecyclerViewAdapter); 
		
        mSwipeRefreshWidget = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh_widget);
        mSwipeRefreshWidget.setOnRefreshListener(this); 
        
        //refreshData();
	}
	
	public static class DefaultViewHolder extends RecyclerView.ViewHolder {
		
		public TextView title;
		public TextView subtitle;
		
		public ImageView imageView;
		
		public DefaultViewHolder(View itemView) {
			super(itemView); 
			
			title = (TextView) itemView.findViewById(R.id.title);
			subtitle = (TextView) itemView.findViewById(R.id.subtitle);
			
			imageView = (ImageView) itemView.findViewById(R.id.userAvatar); 
		}
		
	}
	
	public static class PhotoViewHolder extends RecyclerView.ViewHolder {
		
		public TextView title;
		public TextView userName;
		public TextView views;
		
		public ImageView picture;
		
		public PhotoViewHolder(View itemView) {
			super(itemView); 
			
			title = (TextView) itemView.findViewById(R.id.title);
			userName = (TextView) itemView.findViewById(R.id.userName);
			views = (TextView) itemView.findViewById(R.id.views);
			
			picture = (ImageView) itemView.findViewById(R.id.picture); 
		}
		
	}

}
