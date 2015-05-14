package skbkonturcontest.flickr.util;

import skbkonturcontest.flickr.R;
import skbkonturcontest.flickr.storage.model.Photo;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PhotoMarker extends AbstractMarker {

    public PhotoMarker(Photo photo) {
        super(photo.getLatitude(), photo.getLongitude());
        
        setMarker(new MarkerOptions()
        	.position(new LatLng(photo.getLatitude(), photo.getLongitude()))
            .title(photo.getTitle())
            .snippet(photo.getDescription()) 
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.compact_camera19)));
    }

	@Override
	public String toString() {
		return null;
	}

	@Override
	public MarkerOptions getMarker() {
		return marker;
	}
    
}