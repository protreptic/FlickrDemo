package skbkonturcontest.flickr.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import skbkonturcontest.flickr.storage.Storage;
import skbkonturcontest.flickr.storage.model.Photo;
import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.RequestContext;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.SearchParameters;
import com.googlecode.flickrjandroid.places.PlacesList;
import com.j256.ormlite.dao.Dao;

public class LocationUtils {
	
	private static LocationUtils sInstance;
	
	private Context mContext;
	private GoogleMap mMap;

	private ClusterManager<AbstractMarker> mClusterManager;
	
	public synchronized static LocationUtils get(Context context, GoogleMap map, String woeid) {
		if (sInstance == null) {
			sInstance = new LocationUtils(context, map, woeid);
		}
		
		return sInstance;
	}
	
	public synchronized static void destroy() {
		if (sInstance != null) {
			sInstance = null;
		}
	}
	
	private LocationUtils(Context context, GoogleMap map, String woeid) {
		mContext = context; 
		mMap = map;
		this.woeid = woeid;
	} 
	
	public void moveCameraToLocation(double latitude, double longitude, int zoom) {
		CameraPosition cameraPosition = CameraPosition.fromLatLngZoom(new LatLng(latitude, longitude), zoom);
		
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}
	
	public void showShop(Photo photo) {
		CameraPosition cameraPosition = CameraPosition.fromLatLngZoom(new LatLng(photo.getLatitude(), photo.getLongitude()), 19);
		
		Collection<Marker> storeMarkers = mClusterManager.getMarkerCollection().getMarkers();
		
		for (Marker marker : storeMarkers) {
			LatLng position =  marker.getPosition();
			
			if (photo.getLatitude() == position.latitude && photo.getLongitude() == position.longitude) {
				marker.showInfoWindow(); break;
			} 
		}
		
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}
	
	public void moveToPoint(double latitude, double longitude) {
		mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
	}
	
	public void updateOverlays() {	
		mMap.clear();
		
		new LoadPsrGeodata().execute();
	}
	
	private String woeid;
	
	private class LoadPsrGeodata extends AsyncTask<Void, Void, Void> {

		private List<PhotoMarker> mPhotoMarkers;
		
		@Override
		protected Void doInBackground(Void... params) {
			mPhotoMarkers = getPhotoMarkers();
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			addPhotoMarkers();
		}
		
	    private List<Photo> getPhotos() {
	    	List<Photo> photos = new ArrayList<Photo>();
	    	
	    	Storage storage = new Storage(mContext);
	    	
			try {
				@SuppressWarnings("unchecked")
				Dao<Photo, String> dao = (Dao<Photo, String>) storage.createDao(Photo.class);
				
				photos.addAll(dao.queryBuilder().where().eq("woeid", woeid).query());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			storage.closeConnection();
			
	    	return photos;
	    }
		
		private List<PhotoMarker> getPhotoMarkers() {
			List<PhotoMarker> psrMarkers = new ArrayList<PhotoMarker>();
			
			for (Photo photo : getPhotos()) {
				if (photo.getLatitude() == 0 || photo.getLongitude() == 0) continue;
				
				psrMarkers.add(new PhotoMarker(photo));
			}
			
			return psrMarkers;
		}
	    
		private void addPhotoMarkers() {
			for (PhotoMarker photoMarker : mPhotoMarkers) {
				mMap.addMarker(photoMarker.getMarker());
			}
		}
		
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
            	Set<String> extras = new TreeSet<String>();
            	extras.add("original_format");
            	extras.add("description");
            	extras.add("date_upload");
            	extras.add("owner_name");
            	extras.add("geo");
            	extras.add("views");
            	
            	SearchParameters searchParameters = new SearchParameters();
            	//searchParameters.setPlaceId(placeid);
            	searchParameters.setWoeId(woeid); 
            	searchParameters.setExtras(extras);
            	//searchParameters.setAccuracy(3); 
            	
            	PhotoList photoList = flick.getPhotosInterface().search(searchParameters, 50, 1);
            	
            	Storage storage = new Storage(mContext);
            	
            	@SuppressWarnings("unchecked")
				Dao<Photo, String> dao = (Dao<Photo, String>) storage.createDao(Photo.class);
            	
            	for (com.googlecode.flickrjandroid.photos.Photo photo: photoList) {
            		Photo newPhoto = new Photo();
            		newPhoto.setId(photo.getId());
            		newPhoto.setFarm(photo.getFarm()); 
            		newPhoto.setServer(photo.getServer()); 
            		newPhoto.setSecret(photo.getSecret()); 
            		newPhoto.setTitle(photo.getTitle()); 
            		newPhoto.setDescription(photo.getDescription()); 
            		newPhoto.setOwnerName(photo.getOwner().getUsername()); 
            		newPhoto.setWoeid(woeid); 
            		newPhoto.setLatitude((double) photo.getGeoData().getLatitude());
            		newPhoto.setLongitude((double) photo.getGeoData().getLongitude());
            		newPhoto.setViews(photo.getViews());
            		
            		dao.createOrUpdate(newPhoto);
				}
            	
            	storage.closeConnection();
            } catch (Exception e) {
            	e.printStackTrace();
            }
            
            return null;
        }

        protected void onPostExecute(PlacesList result) {
        	updateOverlays();
        }
        
	}
	
	
}
