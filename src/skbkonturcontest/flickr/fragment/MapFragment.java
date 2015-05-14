package skbkonturcontest.flickr.fragment;

import skbkonturcontest.flickr.util.LocationUtils;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {
	
	private LocationUtils mLocationHelper;
	
	@Override
	public void onMapReady(GoogleMap googleMap) {
		Bundle arguments = getArguments();
		
		googleMap.setMyLocationEnabled(true);
		googleMap.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
			
			@Override
			public void onMyLocationChange(Location location) {
				if (location != null) {
					getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 7f));
					getMap().setOnMyLocationChangeListener(null); 
				}
			}
		});
		
		mLocationHelper = LocationUtils.get(getActivity(), googleMap, woeid);
		
		if (arguments.getBoolean("initialGeopoint")) { 
			googleMap.setOnMyLocationChangeListener(null); 
			
			double latitude = arguments.getDouble("latitude");
			double longitude = arguments.getDouble("longitude");
			
			mLocationHelper.moveCameraToLocation(latitude, longitude, 10); 
		}
		
		mLocationHelper.updateOverlays();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Bundle arguments = getArguments();
		
		woeid = arguments.getString("woe_id");
		
		getMapAsync(this); 
	}	
	
	@Override
	public void onDetach() {
		super.onDetach();
		
		LocationUtils.destroy();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true); 
	}
	
	public void updateMap() {
		if (mLocationHelper != null) 
			mLocationHelper.updateOverlays();
	}
	
	private String woeid;

}
