package com.foodsniff.shreyas.main;

import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.foodsniff.shreyas.main.overlay.LocationMarkerOverlay;
import com.foodsniff.shreyas.main.ref.LocationMarker;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * Second Activity which shows the locations and the paths to reach them.
 * 
 * */

public class MapCallActivity extends MapActivity {

	private static final int MAXIMUM_DISTANCE = 50000;

	/**
	 * Get the API Key from Google Code. 
	 * (Link:
	 * https://developers.google.com/maps/documentation/javascript/tutorial#api_key) 
	 * NOTE: This is different from the one generated using keytool!
	 * */
	private static final String API_KEY = "<yourGoogleCodeAPIKey>";
	MapView mapView;
	/*
	 * String defaultLoc[] = { "1.296705", "103.773336" }; //String defaultLoc[]
	 * = { "1.304546", "103.76773" };
	 */

	double SourceLocLat;
	double SourceLocLon;

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_layout);

		// Getting values from previous activity
		Bundle valuesPassed = getIntent().getExtras();

		if (valuesPassed != null) {
			String[] MapList = valuesPassed
					.getStringArray("MapSearchListIntentKey");

			/**
			 * The below code is checked to parse based on the distance passed.
			 * Google Places API has a maximum radius limit of
			 * "MAXIMUM_DISTANCE" value.
			 * */
			if (Integer.valueOf(MapList[3]) > MAXIMUM_DISTANCE) {
				Toast.makeText(
						MapCallActivity.this,
						"Length of the Distance exceeds the maximum limit of "
								+ MAXIMUM_DISTANCE
								+ " meters. Sorry, FoodSniff cannot proceed!",
						Toast.LENGTH_LONG).show();
				finish();
			} else {

				// Get the user's location
				GeoPoint srcLoc = getCurrLocation();

				/**
				 * If the app is not able to track current location due to GPS
				 * problems and the cache has no location co-ordinates
				 * */
				if (srcLoc.getLatitudeE6() == 0 && srcLoc.getLongitudeE6() == 0) {
					Toast.makeText(
							MapCallActivity.this,
							"FoodSniff is not able to find your location. Check your GPS.",
							Toast.LENGTH_LONG).show();
					finish();
				} else {
					searchRestaurants(MapList[0].toString(),
							MapList[1].toString(), MapList[2].toString(),
							Integer.valueOf(MapList[3]), srcLoc, API_KEY);
				}
			}
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		MapController mc = mapView.getController();
		switch (keyCode) {
		case KeyEvent.KEYCODE_3:
			mc.zoomIn();
			break;
		case KeyEvent.KEYCODE_1:
			mc.zoomOut();
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void searchRestaurants(String foodtype, String placeType,
			String transType, int distance, GeoPoint getSrcLoc, String apiKey) {

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.invalidate();
		mapView.setBuiltInZoomControls(true);

		/**
		 * Display on the map. Includes your present location and the list of
		 * points within the confined radius.
		 * */

		LocationMarker getMyLocation = new LocationMarker(this, apiKey);
		List<Overlay> mapOverlays = mapView.getOverlays();
		int setMarkerID = R.drawable.sourcemarker;

		LocationMarkerOverlay setSrcLocOnMap = new LocationMarkerOverlay(this
				.getResources().getDrawable(setMarkerID), setMarkerID);

		OverlayItem sourceOverlayItem = new OverlayItem(getSrcLoc,
				"You are Here!", null);
		setSrcLocOnMap.addOverlay(sourceOverlayItem);
		mapOverlays.add(setSrcLocOnMap);

		/**
		 * Get the radius, FoodType and placeType. And then, plot the radius of
		 * location within the confined distance.
		 * */

		getMyLocation.radiusTracker(mapView, getSrcLoc, foodtype, placeType,
				transType, distance);

	}

	protected GeoPoint getCurrLocation() {

		GeoPoint getpresentLoc;
		Location location;
		LocationManager locationManager;

		// To get the current location of the phone for easy tracking.

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocListener locListener = new LocListener();
		String getProviderName = LocationManager.GPS_PROVIDER;

		// Get the user location as quickly as possible.
		// Note: This will consume power till the location is found!

		locationManager.requestLocationUpdates(getProviderName, 0, 0,
				locListener);

		if (SourceLocLat == 0.0d && SourceLocLon == 0.0d) {
			// Else get it from the last known location source.
			location = locationManager.getLastKnownLocation(getProviderName);

			if (location != null) {
				SourceLocLat = location.getLatitude();
				SourceLocLon = location.getLongitude();
			} else {
				getProviderName = LocationManager.NETWORK_PROVIDER;
				locationManager.requestLocationUpdates(getProviderName, 0, 0,
						locListener);
				if (SourceLocLat == 0.0d && SourceLocLon == 0.0d) {
					// Else get it from the last known location source.
					location = locationManager.getLastKnownLocation(getProviderName);

					if (location != null) {
						SourceLocLat = location.getLatitude();
						SourceLocLon = location.getLongitude();
					}
				}
			}
		}

		locationManager.removeUpdates(locListener);

		getpresentLoc = new GeoPoint((int) (SourceLocLat * 1E6),
				(int) (SourceLocLon * 1E6));
		return getpresentLoc;

	}

	public class LocListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				SourceLocLat = location.getLatitude();
				SourceLocLon = location.getLongitude();
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			if (provider.equals("OUT_OF_SERVICE")) {
				Toast.makeText(
						getApplicationContext(),
						"Your GPS Provider is out of Service. Please contact operator.",
						Toast.LENGTH_LONG).show();
			} else if (provider.equals("TEMPORARILY_UNAVAILABLE")) {
				Toast.makeText(
						getApplicationContext(),
						"Your GPS Provider is temporarily out of Service. Please contact operator or try in a few minutes.",
						Toast.LENGTH_LONG).show();
			} else if (provider.equals("AVAILABLE")) {
				Toast.makeText(getApplicationContext(),
						"Searching for your location..", Toast.LENGTH_LONG)
						.show();
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText(
					getApplicationContext(),
					"Either GPS is turned off/Network is down. Check your phone Settings or try contacting your operator.",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText(getApplicationContext(),
					"Provider enabled by the user. GPS turned on",
					Toast.LENGTH_LONG).show();
		}

	}

}
