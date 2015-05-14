package skbkonturcontest.flickr.fragment;

import java.sql.SQLException;
import java.util.Locale;

import org.javaprotrepticon.util.gmsurl.GmsUrl;
import org.javaprotrepticon.util.gmsurl.GmsUrl.MapType;
import org.javaprotrepticon.util.gmsurl.GmsUrl.ScaleType;
import org.javaprotrepticon.util.gmsurl.GmsUrl.ZoomLevel;

import skbkonturcontest.flickr.R;
import skbkonturcontest.flickr.fragment.base.BaseEntityListFragment_v2;
import skbkonturcontest.flickr.storage.Storage;
import skbkonturcontest.flickr.storage.model.Place;
import skbkonturcontest.flickr.util.FlickrUtils;
import skbkonturcontest.flickr.util.TextUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.RequestContext;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.places.PlacesList;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.Where;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TopPlacesListFragment extends BaseEntityListFragment_v2<Place> {

	@Override
	protected Adapter<?> createAdapter() {
		return new DefaultAdapter() {
			
			@Override
			public void onBindViewHolder(DefaultViewHolder holder, int position) {
				final Place place = mEntityList.get(position);
				
				holder.imageView.setImageDrawable(null);
				holder.imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Bundle arguments = new Bundle();
						arguments.putString("woe_id", place.getWoeid());
						arguments.putBoolean("initialGeopoint", true); 
						arguments.putDouble("latitude", place.getLatitude());
						arguments.putDouble("longitude", place.getLongitude());
						
						Fragment fragment = new MapFragment();
						fragment.setArguments(arguments); 
						
			            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
			            fragmentTransaction.replace(R.id.content_frame, fragment);
			            fragmentTransaction.addToBackStack(null);
			            fragmentTransaction.commit();	
					}
				});
				
				holder.title.setText(place.getName());
				holder.title.setTypeface(mRobotoCondensedBold);
				holder.title.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Bundle arguments = new Bundle();
						arguments.putString("place_id", place.getId());
						arguments.putInt("place_type", placeType);
						
						Fragment fragment = new TopPlacesListFragment();
						fragment.setArguments(arguments); 
						
			            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
			            fragmentTransaction.replace(R.id.content_frame, fragment);
			            fragmentTransaction.addToBackStack(null);
			            fragmentTransaction.commit();	
					}
				});
				
				holder.subtitle.setText(TextUtils.formatPhotoCount(place.getPhotoCount()));
				holder.subtitle.setTypeface(mRobotoCondensedRegular);
				
				holder.itemView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						Bundle arguments = new Bundle();
						arguments.putString("woe_id", place.getWoeid());
						
						Fragment fragment = new PhotosInPlaceFragment();
						fragment.setArguments(arguments); 
						
			            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
			            fragmentTransaction.replace(R.id.content_frame, fragment);
			            fragmentTransaction.addToBackStack(null);
			            fragmentTransaction.commit();	
					}
				});
				
				String url = new GmsUrl.Builder()
					.setCenter(place.getLatitude(), place.getLongitude())
					.setZoom(ZoomLevel.ZOOM_8) 
					.setSize(320, 150)
					.setScale(ScaleType.SCALE_1) 
					.setMapType(MapType.TERRAIN)
					.setSensor(false) 
					.setLocale(Locale.getDefault()) 
					.setKey("AIzaSyD4xUFmBuqp4iA1XcM4czq2GmaqMK3i3o4")
					.build();   
				
				ImageLoader imageLoader = ImageLoader.getInstance();
				imageLoader.displayImage(url, holder.imageView);
			} 
			
		};
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
			default: {
				super.onOptionsItemSelected(item);
			}
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private class PlaceTypeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 5;
		}

		public Integer getPlaceTypeId(int position) {
			int result = 0;
			 
			switch (position) {
				case 0: {
					result = com.googlecode.flickrjandroid.places.Place.TYPE_CONTINENT;
				} break;
				case 1: {
					result = com.googlecode.flickrjandroid.places.Place.TYPE_COUNTRY;
				} break;
				case 2: {
					result = com.googlecode.flickrjandroid.places.Place.TYPE_REGION;
				} break;
				case 3: {
					result = com.googlecode.flickrjandroid.places.Place.TYPE_LOCALITY;
				} break;
				case 4: {
					result = com.googlecode.flickrjandroid.places.Place.TYPE_NEIGHBOURHOOD;
				} break;
			}
			
			return result;
		}
		
		@Override
		public Object getItem(int position) {
			String result = "";
			
			switch (position) {
				case 0: {
					result = "CONTINENT";
				} break;
				case 1: {
					result = "COUNTRY";
				} break;
				case 2: {
					result = "REGION";
				} break;
				case 3: {
					result = "LOCALITY";
				} break;
				case 4: {
					result = "NEIGHBOURHOOD";
				} break;
			}
			
			return result;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View arg1, ViewGroup arg2) {
			TextView textView = new TextView(getActivity());
			textView.setText((String) getItem(position)); 
			textView.setPadding(15, 15, 15, 15); 
			textView.setTypeface(mRobotoCondensedBold);
			textView.setBackgroundResource(R.drawable.card_selector); 
 			textView.setTextColor(getResources().getColor(R.color.flickr1)); 
 			textView.setTextSize(24); 
 			
			return textView;
		}
		
	}
	
	private Integer placeType = com.googlecode.flickrjandroid.places.Place.TYPE_CONTINENT;
	
	@Override
	protected void refreshData() {
		new DataLoader() {
			
			@Override
			protected Void doInBackground(Void... params) {
				try {
					Where<Place, String> where = mQueryBuilder.where();
					where.like("name", mQueryText).and().eq("placeType", placeType);
					
					if (placeId != null) {
						where.and().eq("parentId", placeId); 
					}
					
					mQueryBuilder.orderBy("name", true);  
					
					mEntityList.addAll(mQueryBuilder.query());
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				return null;
			}
			
		}.execute();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		final DrawerLayout drawerLayout = (DrawerLayout) getView().findViewById(R.id.drawer_layout);
		
        final ListView listView = (ListView) getView().findViewById(R.id.listView1);
        listView.setAdapter(new PlaceTypeAdapter()); 
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE); 
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				placeType = ((PlaceTypeAdapter) listView.getAdapter()).getPlaceTypeId(arg2);
				
				drawerLayout.closeDrawers();
				
				new LoadPhotostreamTask().execute(FlickrUtils.getOAuthToken(getActivity()));
			}
		});
        
        TextView button1 = (TextView) getView().findViewById(R.id.signInButton);
        button1.setTypeface(mRobotoCondensedBold); 
        button1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {

			}		
		});
        
        TextView button2 = (TextView) getView().findViewById(R.id.TextView01);
        button2.setTypeface(mRobotoCondensedBold);
        button2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
			}
		});
        
        if (getArguments() != null) {
        	placeId = getArguments().getString("place_id");
        	placeType  = getArguments().getInt("place_type");
        	
			switch (placeType) {
				case com.googlecode.flickrjandroid.places.Place.TYPE_CONTINENT: {
					placeType = com.googlecode.flickrjandroid.places.Place.TYPE_COUNTRY;
				} break;
				case com.googlecode.flickrjandroid.places.Place.TYPE_COUNTRY: {
					placeType = com.googlecode.flickrjandroid.places.Place.TYPE_REGION;
				} break;
				case com.googlecode.flickrjandroid.places.Place.TYPE_REGION: {
					placeType = com.googlecode.flickrjandroid.places.Place.TYPE_LOCALITY;
				} break;
				case com.googlecode.flickrjandroid.places.Place.TYPE_LOCALITY: {
					placeType = com.googlecode.flickrjandroid.places.Place.TYPE_NEIGHBOURHOOD;
				} break;
				case com.googlecode.flickrjandroid.places.Place.TYPE_NEIGHBOURHOOD: {
					placeType = com.googlecode.flickrjandroid.places.Place.TYPE_NEIGHBOURHOOD;
				} break;
			}
        }
        
		OAuth oauth = FlickrUtils.getOAuthToken(getActivity());
		
		if (mEntityList.isEmpty()) {
			new LoadPhotostreamTask().execute(oauth);
		}
	}
	
	@Override
	protected Class<Place> getType() {
		return Place.class;
	}
	
	private String placeId;
	
	public class LoadPhotostreamTask extends AsyncTask<OAuth, Void, PlacesList> {

		public Flickr getFlickrAuthed(String token, String secret) {
			Flickr flick = FlickrUtils.getInstance();
			
            RequestContext requestContext = RequestContext.getRequestContext();
            
            OAuth auth = new OAuth();
            auth.setToken(new OAuthToken(token, secret));
            requestContext.setOAuth(auth);
            
            return flick;
	    }
		
        @Override
        protected PlacesList doInBackground(OAuth... arg0) {
        	OAuthToken token = arg0[0].getToken();
            Flickr flick = getFlickrAuthed(token.getOauthToken(), token.getOauthTokenSecret());
            
            try {
            	PlacesList places = flick.getPlacesInterface().getTopPlacesList(placeType, null, placeId, null);
            	
            	Storage storage = new Storage(getActivity());
            	
            	@SuppressWarnings("unchecked")
				Dao<Place, String> dao = (Dao<Place, String>) storage.createDao(Place.class);
            	
            	//dao.delete(dao.queryForAll());
            	
            	for (com.googlecode.flickrjandroid.places.Place place : places) {
            		Place newPlace = new Place();
            		newPlace.setId(place.getPlaceId());
            		newPlace.setParentId(placeId); 
            		newPlace.setName(place.getName()); 
            		newPlace.setWoeid(place.getWoeId());
            		newPlace.setPhotoCount(place.getPhotoCount());
            		newPlace.setPlaceType(placeType); 
            		newPlace.setUrl(place.getPlaceUrl()); 
            		newPlace.setLatitude(place.getLatitude()); 
            		newPlace.setLongitude(place.getLongitude()); 
            		
            		dao.createOrUpdate(newPlace);
				}
            	
            	storage.closeConnection();
            } catch (Exception e) {
            	e.printStackTrace();
            }
            
            return null;
        }

        protected void onPostExecute(PlacesList result) {
        	refreshData();
        }
        
	}
	
}