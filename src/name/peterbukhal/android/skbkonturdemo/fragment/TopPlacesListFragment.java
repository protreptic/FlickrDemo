package name.peterbukhal.android.skbkonturdemo.fragment;

import java.sql.SQLException;
import java.util.Locale;

import name.peterbukhal.android.skbkonturdemo.R;
import name.peterbukhal.android.skbkonturdemo.fragment.base.BaseEntityListFragment_v2;
import name.peterbukhal.android.skbkonturdemo.storage.Storage;
import name.peterbukhal.android.skbkonturdemo.storage.model.Place;

import org.javaprotrepticon.util.gmsurl.GmsUrl;
import org.javaprotrepticon.util.gmsurl.GmsUrl.MapType;
import org.javaprotrepticon.util.gmsurl.GmsUrl.ScaleType;
import org.javaprotrepticon.util.gmsurl.GmsUrl.ZoomLevel;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.View;
import android.view.View.OnClickListener;

import com.googlecode.flickrjandroid.Flickr;
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
				
				holder.subtitle.setText(place.getPhotoCount() != null ? "" + place.getPhotoCount() : "-");
				holder.subtitle.setTypeface(mRobotoCondensedBold);
				
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
		
		if (mEntityList.isEmpty()) {
			new LoadTopPlacesListTask().execute();
		}
	}
	
	@Override
	protected Class<Place> getType() {
		return Place.class;
	}
	
	public class LoadTopPlacesListTask extends AsyncTask<Void, Void, PlacesList> {

        @Override
        protected PlacesList doInBackground(Void... arg0) {
            Flickr flick = new Flickr("153f33703432b91f607afa5dc195c23d", "da16906b3ce5d5e2");
            
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