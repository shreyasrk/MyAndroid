package com.foodsniff.shreyas.main.overlay;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class DirectionWriterOverlay extends Overlay {

	/**
	 * Overlay Object used to put the route on the same MapView object.
	 * This gets all the routes as per DirectionsWriter class, and indicates the path as of how to travel.
	 * 
	 * Note: Since Google Directions API is used and straight lines/arrows are drawn, 
	 * user is assumed to find the walking paths, bridges and required travel information himself 
	 * as the lines do NOT show the road map.
	 * 
	 * However, an auto-zoom feature (which will occur in DirectionsWriter Class) 
	 * is added so that the full route is visible.
	 * */
    GeoPoint getStartPlace;
    ArrayList<HashMap<String, String>> getRouteList;
    Context mDContext;
    
	public int getZoomLatE6;
	public int getZoomLongE6;
	public GeoPoint zoomGeoPoint;
	
    public void addDirectionPathParam (ArrayList<HashMap<String, String>> getRoute, 
    		GeoPoint gStartPoint){
		this.getRouteList = getRoute;
		this.getStartPlace = gStartPoint;
    }
    	
	@Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        super.draw(canvas, mapView, shadow);
    }

	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
			long when) {
		/*
		 * Writes the route in a single object. Easier than getting newer
		 * objects every time.
		 */
		GeoPoint gStart = getStartPlace;

		GeoPoint gStep;
		String[] sEndLoc = new String[2];

		Paint mPaint = new Paint();
		mPaint.setDither(true);
		mPaint.setColor(Color.RED);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth((float) 4.5);

		Point p1 = new Point();
		Point p2 = new Point();
		Path path = new Path();

		Projection projection = mapView.getProjection();

		int noOfSteps = getRouteList.size();
		for (int j = 0; j < noOfSteps; j++) {

			sEndLoc = getRouteList.get(j).get("stepsEndLoc").split(",");

			gStep = new GeoPoint((int) (Double.valueOf(sEndLoc[0].trim())
					.doubleValue() * 1E6), (int) (Double.valueOf(
					sEndLoc[1].trim()).doubleValue() * 1E6));

			projection.toPixels(gStart, p1);
			projection.toPixels(gStep, p2);

			path.moveTo(p2.x, p2.y);
			path.lineTo(p1.x, p1.y);

			gStart = gStep;
		}
		canvas.drawPath(path, mPaint);
		return super.draw(canvas, mapView, shadow, when);
	}
	
}