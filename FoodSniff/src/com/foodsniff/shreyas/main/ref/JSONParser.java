package com.foodsniff.shreyas.main.ref;

/**
 * The common class to parse the information sent by Google Places API and Google Directions API.
 * Parses the JSON file into necessary Tables for retrieving information.
 * Currently, location co-ordinates being important, they would be used in base classes but 
 * this information can be scaled up to display more information.
 * */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {
	
	public ArrayList<HashMap<String, String>> getLocations(String getLocationurl) {
		/*
		 * Called by LocationMarker Class. 
		 * Retrieves the Locations sent by Google Places.
		 * */
		
		ArrayList<HashMap<String, String>> retLocDetails = new ArrayList<HashMap<String, String>>();
		// Get the JSON output from the URL link sent. Refer to Google Places API for information.
		
		JSONObject json = getJSONfromURL(getLocationurl);

		try {
			/*
			 * To check if there are any positive results. And get values only if any matching location found.
			 * Note: This is Google Maps API JSON Specific parsing!*/
			
			String statusValue = json.getString("status");
			
			// Dump the JSON values into a single array.
			JSONArray results = json.getJSONArray("results");

			// Check if there are any results. If so, then it is added to the Valid Map list.
			if(results.length() != 0){
				for (int i = 0; i < results.length(); i++) {
					// Generate a Table for every Place.
					HashMap<String, String> validmap = new HashMap<String, String>();
					validmap.put("id", String.valueOf(i));

					// Get the Location co-ordinates
					JSONObject locationGeom = results.getJSONObject(i).getJSONObject("geometry");
					JSONObject locationCoOrd = locationGeom.getJSONObject("location");
					validmap.put("loc", 
							locationCoOrd.getString("lat").toString()
							+ "," + locationCoOrd.getString("lng").toString());

					validmap.put("locid", 
							results.getJSONObject(i).getString("id"));
					validmap.put("name", 
							results.getJSONObject(i).getString("name"));
					validmap.put("reference", 
							results.getJSONObject(i).getString("reference"));
					validmap.put("vicinity", 
							results.getJSONObject(i).getString("vicinity"));
					validmap.put("status", 
							statusValue);
					
					if (!results.getJSONObject(i).has("rating")) {
						validmap.put("rating", "N.A");
					} else {
						validmap.put("rating",
								results.getJSONObject(i).getString("rating") + "/5");
					}
					
					retLocDetails.add(validmap);

				}
			} else {
				HashMap<String, String> invalidmap = new HashMap<String, String>();
				invalidmap.put("loc",
						Double.toString(0.0) + "," + Double.toString(0.0));
				invalidmap.put("status", 
						statusValue);
				
				retLocDetails.add(invalidmap);
			}

		} catch (JSONException e) {
			Log.e("parseJSON.getLocations", "Error Reading Locations from JSON " + e.toString());
			e.printStackTrace();
		}
		
		return retLocDetails;

	}

	public Direction getDirections(String getDirectionurl) {
		/*
		 * Called by DirectionWriter Class. 
		 * Retrieves the Step-wise location co-ordinates to be written as routes on the map sent by Google Directions.
		 * */
		
		// Get the JSON File output as an object from the link. Refer Google Directions API for more info.
		JSONObject json = getJSONfromURL(getDirectionurl);
		Direction retDirection = new Direction();

		try {
			retDirection.routeMap = new HashMap<String,String>();
			retDirection.stepsMap = new ArrayList<HashMap<String, String>>();

			// Parsing Data as per Direction JSON File.
			retDirection.routeMap.put("status", 
					json.getString("status").toString());
			
			JSONArray routes = json.getJSONArray("routes");
			
			JSONObject bounds = routes.getJSONObject(0).getJSONObject("bounds");
			JSONObject northeast = bounds.getJSONObject("northeast");
			retDirection.routeMap.put("neLoc", 
					northeast.getString("lat").toString()
					+ "," + northeast.getString("lng").toString());

			JSONObject southwest = bounds.getJSONObject("southwest");
			retDirection.routeMap.put("swLoc", 
					southwest.getString("lat").toString()
					+ "," + southwest.getString("lng").toString());

			JSONArray legs = routes.getJSONObject(0).getJSONArray("legs");

			JSONObject distance = legs.getJSONObject(0).getJSONObject("distance");
			retDirection.routeMap.put("legsdistValues", 
					distance.getString("text").toString() 
					+ ":" + distance.getString("value").toString() + " meters.");

			JSONObject duration = legs.getJSONObject(0).getJSONObject("duration");
			retDirection.routeMap.put("legsdurValues", 
					duration.getString("text").toString() 
					+ ":" + duration.getString("value").toString()  + " seconds.");

			retDirection.routeMap.put("legsEndAddr",
					legs.getJSONObject(0).getString("end_address"));
			JSONObject end_location = legs.getJSONObject(0).getJSONObject("end_location");
			retDirection.routeMap.put("legsEndLoc", 
					end_location.getString("lat").toString()
					+ "," + end_location.getString("lng").toString());

			retDirection.routeMap.put("legsStartAddr", 
					legs.getJSONObject(0).getString("start_address"));
			JSONObject start_location = legs.getJSONObject(0).getJSONObject("start_location");
			retDirection.routeMap.put("legsStartLoc", 
					start_location.getString("lat").toString()
					+ "," + start_location.getString("lng").toString());

			JSONArray steps = legs.getJSONObject(0).getJSONArray("steps");

			for (int k = 0; k < steps.length(); k++) {
				HashMap<String, String> stepsDetailsMap = new HashMap<String, String>();

				stepsDetailsMap.put("stepsID", 
						String.valueOf(k));

				JSONObject stepsDist = steps.getJSONObject(k).getJSONObject("distance");
				stepsDetailsMap.put("stepsDist", 
						stepsDist.getString("text").toString()
						+ ":" + stepsDist.getString("value").toString() + " meters");

				JSONObject stepsDur = steps.getJSONObject(k).getJSONObject("duration");
				stepsDetailsMap.put("stepsDur", 
						stepsDur.getString("text").toString()
						+ ":" + stepsDur.getString("value").toString() + " seconds");

				JSONObject stepsEndLoc = steps.getJSONObject(k).getJSONObject("end_location");
				stepsDetailsMap.put("stepsEndLoc", 
						stepsEndLoc.getString("lat").toString()
						+ "," + stepsEndLoc.getString("lng").toString());

				stepsDetailsMap.put("stepsHTMLInstruct", 
						steps.getJSONObject(k).getString("html_instructions").toString().replaceAll("\\<.*?\\>", ""));

				JSONObject stepsPolyLine = steps.getJSONObject(k).getJSONObject("polyline");
				stepsDetailsMap.put("stepsPolyLine",
						stepsPolyLine.getString("points").toString());

				JSONObject stepsStartLoc = steps.getJSONObject(k).getJSONObject("start_location");
				stepsDetailsMap.put("stepsStartLoc",
						stepsStartLoc.getString("lat").toString() + ","
								+ stepsStartLoc.getString("lng").toString());

				stepsDetailsMap.put("stepsMode", 
						steps.getJSONObject(k).getString("travel_mode").toString());

				retDirection.stepsMap.add(stepsDetailsMap);
			}
			
			JSONArray warnings = routes.getJSONObject(0).getJSONArray("warnings");
			for(int l = 0; l < warnings.length(); l++){
				retDirection.routeMap.put("warnings", warnings.getString(l).toString());
			}

		} catch (JSONException e) {
			Log.e("parseJSON.getDirections", "Error Reading Directions from JSON " + e.toString());
			e.printStackTrace();
		}
		
		return retDirection;
	}
	
	public static JSONObject getJSONfromURL(String url) {

		InputStream is = null;
		String streamResult = "";
		JSONObject jArray = null;
		HttpResponse response = null;


		try {
			// Post it as a HTTP Request.
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			
			// Get the HTTP response. This will be a JSON output from the URL that is parsed.
			response = httpclient.execute(httppost);
			
			// Get Response Entity of the JSON Object
			HttpEntity entity = response.getEntity();
			if(entity != null){
				is = entity.getContent();
				
				// convert response to string
				streamResult= convertStreamToString(is);
                
                jArray = new JSONObject(streamResult);
			}
		} catch (IOException e) {
			Log.e("parseJSON.getJSONfromURL", "Error in http connection " + e.toString());
			e.printStackTrace();
		} catch (JSONException e) {
			Log.e("parseJSON.getJSONfromURL", "Error parsing data " + e.toString());
			e.printStackTrace();
		}
		return jArray;

	}

	private static String convertStreamToString(InputStream inpStream) throws IOException {
		/*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader;
		reader = new BufferedReader(new InputStreamReader(inpStream));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
        	Log.e("parseJSON.convertStreamToString", "Wrong Input");
            e.printStackTrace();
        } finally {
            try {
            	inpStream.close();
            } catch (IOException e) {
            	Log.e("parseJSON.convertStreamToString", "Failed to close Input");
                e.printStackTrace();
            }
        }
        return sb.toString();
	}
}
