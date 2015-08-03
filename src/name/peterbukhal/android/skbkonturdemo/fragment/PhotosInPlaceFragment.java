package name.peterbukhal.android.skbkonturdemo.fragment;

import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

import name.peterbukhal.android.skbkonturdemo.fragment.base.BaseEntityListFragment;
import name.peterbukhal.android.skbkonturdemo.storage.Storage;
import name.peterbukhal.android.skbkonturdemo.storage.model.Photo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.SearchParameters;
import com.j256.ormlite.dao.Dao;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PhotosInPlaceFragment extends BaseEntityListFragment<Photo> {

	@Override
	protected Adapter<?> createAdapter() {
		return new PhotoAdapter() {
			
			@Override
			public void onBindViewHolder(PhotoViewHolder holder, int position) {
				final Photo photo = mEntityList.get(position);
				
				holder.picture.setImageDrawable(null);
				
				if (!TextUtils.isEmpty(photo.getTitle())) { 
					holder.title.setText(photo.getTitle().length() > 64 ? photo.getTitle().substring(0, 64) + "..." : photo.getTitle());
					holder.title.setTypeface(mRobotoCondensedBold);
					holder.title.setVisibility(View.VISIBLE); 
				} else {
					holder.title.setVisibility(View.GONE); 
				}
				
				holder.userName.setText(photo.getOwnerName());
				holder.userName.setTypeface(mRobotoCondensedRegular);
				
				holder.views.setText(name.peterbukhal.android.skbkonturdemo.util.TextUtils.formatPhotoCount(photo.getViews()));
				holder.views.setTypeface(mRobotoCondensedRegular);
				
//				if (!TextUtils.isEmpty(photo.getDescription())) { 
//					holder.description.setText(photo.getDescription().length() > 180 ? photo.getDescription().substring(0, 180) + "..." : photo.getDescription());
//					holder.description.setTypeface(mRobotoCondensedRegular);
//					holder.description.setVisibility(View.VISIBLE); 
//				} else {
//					holder.description.setVisibility(View.GONE); 
//				}
					
				final String url = String.format("https://farm%s.staticflickr.com/%s/%s_%s_n.jpg", photo.getFarm(), photo.getServer(), photo.getId(), photo.getSecret());
				
				ImageLoader imageLoader = ImageLoader.getInstance();
				imageLoader.displayImage(url, holder.picture);
				
				holder.itemView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						
					}
				});
			}  
			
		};
	}
	
	@Override
	protected void refreshData() {
		new DataLoader() {
			
			@Override
			protected Void doInBackground(Void... params) {
				try {
					mEntityList.addAll(mQueryBuilder.where().eq("woeid", woeid).query());
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
			woeid = getArguments().getString("woe_id");
			
			new LoadPhotosInPlaceTask().execute();
		}
	}
	
	private String woeid;
	
	@Override
	protected Class<Photo> getType() {
		return Photo.class;
	}
	
	public class LoadPhotosInPlaceTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            Flickr flick = new Flickr("153f33703432b91f607afa5dc195c23d", "da16906b3ce5d5e2");
            
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

        protected void onPostExecute(Void result) {
        	refreshData();
        }
        
	}
	
}
