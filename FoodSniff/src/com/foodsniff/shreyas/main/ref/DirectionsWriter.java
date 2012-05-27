package com.foodsniff.shreyas.main.ref;

import java.util.ArrayList;
import java.util.HashMap;

import com.foodsniff.shreyas.main.overlay.DirectionWriterOverlay;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class DirectionsWriter {
	
	/**
	 * Directions/Route writing class. Basically gets the source and destination and writes the optimized path. 
	 * Uses:Google Directions API. Refer API for more details for how route is determined.
	 * */

	public void getDirections(MapView getmapView, DirectionWriterOverlay getRouteOverlay, 
			GeoPoint getSourceLoc, GeoPoint getTargetLoc, 
			String getTransportType) {
		/*
		 * Gets the directions as per the type selected. 
		 * 1. getSourceLoc: Starting Location(User's current location) 
		 * 2. getTargetLoc: Target Location(In App's case, it would be the targeted food place). 
		 * 3. getTransportType: Mode of transport: Biking/Public Transport
		 * 4. getmapView: MapView Object which stores the locations tracked via LocationMarker
		 * 5. getRouteOverlay: OverLay Object which will draw the route selected.
		 */
		final String DIRECTIONS_URL = "https://maps.googleapis.com/maps/api/directions/json?";
		final String PUBLIC_TRANSPORT_URL = "https://maps.google.com/?";
		
		StringBuilder urlLink = new StringBuilder();
		
		if (getTransportType.equals("p")){
			urlLink.append(PUBLIC_TRANSPORT_URL)
				   .append("saddr=")
				   .append(getSourceLoc.getLatitudeE6() / 1E6).append(",")
				   .append(getSourceLoc.getLongitudeE6() / 1E6)
				   .append("&daddr=")
				   .append(getTargetLoc.getLatitudeE6() / 1E6).append(",")
				   .append(getTargetLoc.getLongitudeE6() / 1E6)
				   .append("&dirflg=r");
			
			writeRouteforPT(urlLink.toString());
			
		} else {
			urlLink.append(DIRECTIONS_URL)
				.append("origin=")
				.append(getSourceLoc.getLatitudeE6() / 1E6).append(",")
				.append(getSourceLoc.getLongitudeE6() / 1E6)
				.append("&destination=")
				.append(getTargetLoc.getLatitudeE6() / 1E6).append(",")
				.append(getTargetLoc.getLongitudeE6() / 1E6)
				.append(getTransportType)
				.append("&waypoints=optimize:true&sensor=false");

			JSONParser getDirectURL = new JSONParser();

			// Call the Route writing algorithm
			writeRouteOnMap(getDirectURL.getDirections(urlLink.toString()),
					getmapView, getRouteOverlay);

		}
	}

	private void writeRouteforPT(String getURLforPT) {
		/*Uri uri = Uri.parse(getURLforPT);
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
		startActivity(intent);*/
		// TODO V2
		
	}

	protected void writeRouteOnMap(Direction getDirectDetails,
			MapView getmapView, DirectionWriterOverlay getRouteOverlay) {
		/*
		 * 1. Get the Step wise location and the length and time required to
		 * reach the co-ordinates. 
		 * 2. Put each step onto the map with necessary
		 * length markers. 
		 * 3. Accumulate the result and display to the user.
		 */
		
		String[] getStartLoc = getDirectDetails.routeMap.get("legsStartLoc").split(",");
		
		ArrayList<HashMap<String, String>> getSteps = getDirectDetails.stepsMap;

		GeoPoint gStartPoint = new GeoPoint((int) (Double.valueOf(getStartLoc[0].trim()).doubleValue() * 1E6), 
				(int) (Double.valueOf(getStartLoc[1].trim()).doubleValue() * 1E6));
		GeoPoint gStepPoint;
		String[] getStepLoc = new String[2];

		getRouteOverlay.addDirectionPathParam(getSteps, gStartPoint);
		
		// Setting Zoom Level based on the total number of points found.
		int minLatitude = Integer.MAX_VALUE;
		int maxLatitude = Integer.MIN_VALUE;
		int minLongitude = Integer.MAX_VALUE;
		int maxLongitude = Integer.MIN_VALUE;

		// Deliberately done for each tapped destination to be checked 
		// as the Java memory buffer would contain previous values.
		minLatitude = Math.min(gStartPoint.getLatitudeE6(), minLatitude);
		maxLatitude = Math.max(gStartPoint.getLatitudeE6(), maxLatitude);
		minLongitude = Math.min(gStartPoint.getLongitudeE6(), minLongitude);
		maxLongitude = Math.max(gStartPoint.getLongitudeE6(), maxLongitude);
		
		for (int i = 0; i < getSteps.size(); i++){
			getStepLoc = getSteps.get(i).get("stepsEndLoc").split(",");
			gStepPoint = new GeoPoint((int) (Double.valueOf(getStepLoc[0].trim())
					.doubleValue() * 1E6), (int) (Double.valueOf(
							getStepLoc[1].trim()).doubleValue() * 1E6));
			
			// Zoom Tuning
			maxLatitude = Math.max(gStepPoint.getLatitudeE6(), maxLatitude);
			minLatitude = Math.min(gStepPoint.getLatitudeE6(), minLatitude);
			maxLongitude = Math.max(gStepPoint.getLongitudeE6(), maxLongitude);
			minLongitude = Math.min(gStepPoint.getLongitudeE6(), minLongitude);

		}
		getmapView.getController().zoomToSpan(Math.abs(maxLatitude - minLatitude), 
                Math.abs(maxLongitude - minLongitude));
		getmapView.getController().animateTo(new GeoPoint((maxLatitude + minLatitude)/2,
							  (maxLongitude + minLongitude)/2 )); 


		// Draws the Route Map. calls draw() function. Do NOT CHANGE THE FLOW!
		getmapView.getOverlays().add(getRouteOverlay);
		getmapView.setBuiltInZoomControls(true);
		getmapView.postInvalidate();

	}

}
