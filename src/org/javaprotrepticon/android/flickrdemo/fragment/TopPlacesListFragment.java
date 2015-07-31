package org.javaprotrepticon.android.flickrdemo.fragment;

import java.sql.SQLException;
import java.util.Locale;

import org.javaprotrepticon.android.flickrdemo.R;
import org.javaprotrepticon.android.flickrdemo.fragment.base.BaseEntityListFragment_v2;
import org.javaprotrepticon.android.flickrdemo.storage.Storage;
import org.javaprotrepticon.android.flickrdemo.storage.model.Place;
import org.javaprotrepticon.android.flickrdemo.util.FlickrUtils;
import org.javaprotrepticon.util.gmsurl.GmsUrl;
import org.javaprotrepticon.util.gmsurl.GmsUrl.MapType;
import org.javaprotrepticon.util.gmsurl.GmsUrl.ScaleType;
import org.javaprotrepticon.util.gmsurl.GmsUrl.ZoomLevel;

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

import com.google.android.gms.plus.People.OrderBy;
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
				
				holder.title.setText(place.getName());
				holder.title.setTypeface(mRobotoCondensedBold);
				
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
	protected void refreshData() {
		new DataLoader() {
			
			@Override
			protected Void doInBackground(Void... params) {
				try {
					Where<Place, String> where = mQueryBuilder.where();
					where.like("name", mQueryText).and().eq("placeType", 7);
					
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
		
		OAuth oauth = FlickrUtils.getOAuthToken(getActivity());
		
		if (mEntityList.isEmpty()) {
			new LoadPhotostreamTask().execute(oauth);
		}
	}
	
	@Override
	protected Class<Place> getType() {
		return Place.class;
	}
	
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
            	PlacesList places = flick.getPlacesInterface().getTopPlacesList(7, null, null, null);
            	
            	Storage storage = new Storage(getActivity());
            	
            	@SuppressWarnings("unchecked")
				Dao<Place, String> dao = (Dao<Place, String>) storage.createDao(Place.class);
            	
            	dao.delete(dao.queryForAll());
            	
            	for (com.googlecode.flickrjandroid.places.Place place : places) {
            		Place newPlace = new Place();
            		newPlace.setId(place.getPlaceId());
            		newPlace.setName(place.getName()); 
            		newPlace.setWoeid(place.getWoeId());
            		newPlace.setPhotoCount(place.getPhotoCount());
            		newPlace.setPlaceType(7); 
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