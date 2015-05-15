package skbkonturcontest.flickr.fragment;

import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

import skbkonturcontest.flickr.fragment.base.BaseEntityListFragment;
import skbkonturcontest.flickr.storage.Storage;
import skbkonturcontest.flickr.storage.model.Photo;
import skbkonturcontest.flickr.util.FlickrUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.RequestContext;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.places.PlacesList;
import com.j256.ormlite.dao.Dao;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PhotostreamFragment extends BaseEntityListFragment<Photo> {

	@Override
	protected Adapter<?> createAdapter() {
		return new PhotoAdapter() {
			
			@Override
			public void onBindViewHolder(PhotoViewHolder holder, int position) {
				final Photo photo = mEntityList.get(position);
				
				holder.picture.setImageDrawable(null);
				
				if (!TextUtils.isEmpty(photo.getTitle())) { 
					holder.title.setText(photo.getTitle().length() > 64 ? photo.getTitle().substring(1, 64) + "..." : photo.getTitle());
					holder.title.setTypeface(mRobotoCondensedBold);
					holder.title.setVisibility(View.VISIBLE); 
				} else {
					holder.title.setVisibility(View.GONE); 
				}
				
				holder.userName.setText(photo.getOwnerName());
				holder.userName.setTypeface(mRobotoCondensedRegular);
				
//				if (!TextUtils.isEmpty(photo.getDescription())) { 
//					holder.description.setText(photo.getDescription().length() > 200 ? photo.getDescription().substring(1, 200) + "..." : photo.getDescription());
//					holder.description.setTypeface(mRobotoCondensedRegular);
//					holder.description.setVisibility(View.VISIBLE); 
//				} else {
//					holder.description.setVisibility(View.GONE); 
//				}
					
				holder.itemView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						
					}
				});
				
				ImageLoader imageLoader = ImageLoader.getInstance();
				imageLoader.displayImage(String.format("https://farm%s.staticflickr.com/%s/%s_%s_n.jpg", photo.getFarm(), photo.getServer(), photo.getId(), photo.getSecret()), holder.picture);
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
	
	@Override
	protected void refreshData() {
		new DataLoader() {
			
			@Override
			protected Void doInBackground(Void... params) {
				try {
					mEntityList.addAll(mDao.queryForAll());
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
	protected Class<Photo> getType() {
		return Photo.class;
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
            	
            	PhotoList photoList = flick.getPeopleInterface().getPhotos(arg0[0].getUser().getId(), extras, 50, 1);
            	
            	Storage storage = new Storage(getActivity());
            	
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
            		
            		dao.createOrUpdate(newPhoto);
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
