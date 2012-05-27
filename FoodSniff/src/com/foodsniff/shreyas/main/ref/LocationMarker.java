package com.foodsniff.shreyas.main.ref;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.foodsniff.shreyas.main.MapCallActivity;
import com.foodsniff.shreyas.main.R;
import com.foodsniff.shreyas.main.overlay.DirectionWriterOverlay;
import com.foodsniff.shreyas.main.overlay.LocationMarkerOverlay;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class LocationMarker {
	/** 
	 * Uses Google Places API.
	 * 
	 * Location Marker Class has the following features.
	 * 1. Initiates the Context referring to. Here, it is our MapView Object into consideration.
	 * All the points have to be written onto the same MapView Object as the Source Location.
	 * 
	 * 2. Gets the radius up to which places have to be found out from the source location. 
	 * Thus, requires both of these parameters to run successfully.
	 * 
	 * 3. Creates the URL which is a HTTP request onto servers. And the response is a JSON file.
	 * 
	 * 4. Parses the JSON File and returns the table containing necessary information like Place Name, 
	 * Place Location Co-Ordinates, Place Ratings etc. See the JSONParser class for a detailed list. 
	 * 
	 * Note: The data is comprehensive enough to store it on the location marker to know more about it.
	 * And, the Google Places API JSON file returns a maximum of 20 places for every value.
	 * 
	 * 5. Uses the Table created and then writes the location onto the same MapView Object.
	 * 
	 * */
	
	// Fill in the API key you want to use.
    String appApiKey;
	Context locContext;
	GeoPoint getDestPoint;
	DirectionWriterOverlay setRouteOverlay = new DirectionWriterOverlay();
	
	// Get the context via Constructor and set the API Key
	public LocationMarker(Context getLocContext, String getapiKey){
		this.locContext = getLocContext;
		this.appApiKey = getapiKey;
	}
	
	/**
	 * Radius Tracker based on input parameters.
	 * This creates the basic setup required for the MapView Object for location determination 
	 * within a confined radius and Writing the directions.
	 * 
	 * Uses: Google Places API. Refer the API details on http://developers.google.com for more information.
	 * */
	public void radiusTracker(MapView getmapView, GeoPoint getSourceLoc,
							String getfoodtype,
							String getPlaceType,
							String getTransportType,
							int getRadius) {
		/**
		 * Get the URL link of the parameters passed.
		 * 
		 * */
		final String PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
		
		JSONParser getLocURL = new JSONParser();
		ArrayList<HashMap<String, String>> placeValuesMap;
		int setMarkerid = R.drawable.destinationmarker;
		
		LocationMarkerOverlay nearPlcOverlay = new LocationMarkerOverlay(locContext
				.getResources().getDrawable(setMarkerid), locContext, setMarkerid);
		// Add the source, the transport Mode and the route Overlay so that the direction is located.
		nearPlcOverlay.addParams(getSourceLoc,getTransportType,setRouteOverlay);

		// Get the details from the user search.
		StringBuilder places = new StringBuilder();
		places.append(PLACES_SEARCH_URL)
				.append("key=")
				.append(appApiKey)
				.append("&location=")
				.append((getSourceLoc.getLatitudeE6() / 1E6) + ","
						+ (getSourceLoc.getLongitudeE6() / 1E6))
				.append("&radius=").append(getRadius)
				.append("&sensor=false").append("&types=")
				.append(getPlaceType);
		
		if(!getfoodtype.toLowerCase().equals("all")){
			places.append("&keyword=").append(getfoodtype);
		}
		

		// Call the JSON Parser to parse the output and get the location details
		placeValuesMap = getLocURL.getLocations(places.toString());
		

		/*
		 * Get the status value for the first element of the ArrayList.(Status
		 * is added for every HashMap as it is common for the entire JSON file.)
		 * 
		 */
		boolean isFileValid = checkStatus(placeValuesMap.get(0).get("status"));

		// Status OK, then populate results.
		if (isFileValid) {
			displayLocations(locContext, getmapView, nearPlcOverlay, placeValuesMap, getSourceLoc);
		} else {
			// Exit out of the Map Activity. Throws relevant messages.
			((MapCallActivity) locContext).finish();
		}
	}
	
	protected void displayLocations(Context context, 
			MapView getmapview, 
			LocationMarkerOverlay getnearPlcOverlay, 
			ArrayList<HashMap<String, String>> getValuesMap, 
			GeoPoint getSource) {
			
		/**
		 * 1. Get the HashMap of the List of Locations. 
		 * 2. Find the Location in that HashMap. 
		 * 3. Split the String value of the location. 
		 * 4. Get the Location coordinates. 
		 * 5. Add this to the Map using overlays.
		 * 6. Set the automatic Zoom to cover all the places.
		 * 7. Populate results.
		 **/
		
		String[] getTargetLoc = new String[2];
		GeoPoint getTargetPoint;
		String getPlaceDetail = "Test";

		// Setting Zoom Level based on the total number of points found.
		int minLatitude = Math.min(getSource.getLatitudeE6(),Integer.MAX_VALUE);
		int maxLatitude = Math.max(getSource.getLatitudeE6(),Integer.MIN_VALUE);
		int minLongitude = Math.min(getSource.getLongitudeE6(),Integer.MAX_VALUE);
		int maxLongitude = Math.max(getSource.getLongitudeE6(),Integer.MIN_VALUE);
		
		// Get the number of elements in the array. Equivalent to number of locations.
		int noOfPlaces = getValuesMap.size();
		
		// Get the Target Places to be traced onto the map.
		OverlayItem setNearPlaces[] = new OverlayItem[noOfPlaces];
		
		try {
			for (int j = 0; j < noOfPlaces; j++) {

				/** 
				 * Location co-ordinates. Can extract other info also based on need.
				 * */				
				
				getTargetLoc = getValuesMap.get(j).get("loc").split(","); // 1.// 2.// 3.
				getPlaceDetail = getValuesMap.get(j).get("name")
						/*+ "\nRating = " + getValuesMap.get(j).get("rating")*/;

				getTargetPoint = new GeoPoint((int) (Double.valueOf(getTargetLoc[0].trim()).doubleValue() * 1E6),
						(int) (Double.valueOf(getTargetLoc[1].trim()).doubleValue() * 1E6)); // 4.

				setNearPlaces[j] = new OverlayItem(getTargetPoint, getPlaceDetail, null);
				// getnearPlcOverlay.addLocForMarker(context, getTargetPoint, getNameAndRatings);
				getnearPlcOverlay.addOverlay(setNearPlaces[j]);

				// Zoom Tuning
				maxLatitude = Math.max(getTargetPoint.getLatitudeE6(),
						maxLatitude);
				minLatitude = Math.min(getTargetPoint.getLatitudeE6(),
						minLatitude);
				maxLongitude = Math.max(getTargetPoint.getLongitudeE6(),
						maxLongitude);
				minLongitude = Math.min(getTargetPoint.getLongitudeE6(),
						minLongitude);

			}
			
			getmapview.getController().zoomToSpan(Math.abs(maxLatitude - minLatitude), 
					                              Math.abs(maxLongitude - minLongitude));
			getmapview.getController().animateTo(new GeoPoint((maxLatitude + minLatitude)/2,
															  (maxLongitude + minLongitude)/2 )); 

			getmapview.getOverlays().add(getnearPlcOverlay);// 5.
		
            //Added symbols will be displayed when map is redrawn so force redraw now
			getmapview.postInvalidate();
			getmapview.setBuiltInZoomControls(true);
			
		} catch (Exception e) {
			Log.e("displayLocations", "Error in Location Determination.");
			e.printStackTrace();
		}
	}

	
	private boolean checkStatus(String getStatus) {
		boolean isValid = true;
		if (getStatus.equals("ZERO_RESULTS")) {
			Toast.makeText(locContext,
					"No Results found. Please try again for a different set.",
					Toast.LENGTH_LONG).show();
			isValid = false;
		} else if (getStatus.equals("UNKNOWN_ERROR")) {
			Toast.makeText(locContext, "Oops! Server-Side Error. Try again.",
					Toast.LENGTH_LONG).show();
			isValid = false;
		} else if (getStatus.equals("OVER_QUERY_LIMIT")) {
			Toast.makeText(
					locContext,
					"The app has reached maximum queries for the day! Try again tomorrow.",
					Toast.LENGTH_LONG).show();
			isValid = false;
		} else if (getStatus.equals("REQUEST_DENIED")) {
			Toast.makeText(locContext,
					"Sorry! You've no permissions to access.",
					Toast.LENGTH_LONG).show();
			isValid = false;
		} else if (getStatus.equals("INVALID_REQUEST")) {
			Toast.makeText(locContext, "Invalid Request. Try again.",
					Toast.LENGTH_LONG).show();
			isValid = false;
		}
		return isValid;
	}
	
}
