package com.foodsniff.shreyas.main.overlay;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.widget.Toast;

import com.foodsniff.shreyas.main.ref.DirectionsWriter;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

/**
 * Overlay Class to write the List of locations along a confined Radius.
 * Includes an important feature which takes in User-input and passes them to write the Directions.
 * */
public class LocationMarkerOverlay extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	DirectionWriterOverlay setRouteOverlay;
	
	Context mContext;
	GeoPoint getSrcLocForDirections;
	MapView mapView;
	String gettranstype;

	Drawable setMarkerImage;
	GeoPoint getLocForMarker;
	int setMarkerID;
	String setMarkerText;
	
    private static final int FONT_SIZE = 14;
    private static final int TITLE_MARGIN = 4;
    private int markerHeight;

	/*
	 * Both the constructors serve the same objective. 
	 * However, it is deliberately designed to distinguish Source and Destination Objects.
	 * */
	
	public LocationMarkerOverlay(Drawable defaultMarker, int getMarkerID) {
        super(boundCenterBottom(defaultMarker));
        setMarker(defaultMarker, getMarkerID);
        mOverlays = new ArrayList<OverlayItem>();
        populate();
    }

	public LocationMarkerOverlay(Drawable defaultMarker, Context context, int getMarkerID) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
		setMarker(defaultMarker,getMarkerID);
		mOverlays = new ArrayList<OverlayItem>();
        populate();
	}

	private void setMarker(Drawable getMarker, int getMarkerID) {
		this.setMarkerImage = getMarker;
		this.setMarkerID = getMarkerID;
	}

	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	
/*	*//**
	 * Constructor Method. 
	 * Used to get the Location and the Location Place(with Ratings) to write the Marker Image.
	 * Varies as per calls as new objects are created for Source and Destination Locations.
	 * @param context 
	 * *//*
	public void addLocForMarker(Context getContext, GeoPoint getLocFromClass, String getMarkerText){
		this.getLocForMarker = getLocFromClass;
		this.setMarkerText = getMarkerText;
		this.mContext = getContext;
	}*/
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);

		for (OverlayItem item : mOverlays) {
			/*
			 * Converts latitude & longitude of this overlay item to coordinates
			 * on screen. As we have called boundCenterBottom() in constructor,
			 * so these coordinates will be of the bottom center position of the
			 * displayed marker.
			 */
			GeoPoint point = item.getPoint();
			Point markerBottomCenterCoords = new Point();
			mapView.getProjection().toPixels(point, markerBottomCenterCoords);

			/* Find the width and height of the title */
			TextPaint paintText = new TextPaint();
			Paint paintRect = new Paint();

			Rect rect = new Rect();
			paintText.setTextSize(FONT_SIZE);
			paintText.getTextBounds(item.getTitle(), 0, item.getTitle()
					.length(), rect);

			rect.inset(-TITLE_MARGIN, -TITLE_MARGIN);
			rect.offsetTo(markerBottomCenterCoords.x - rect.width() / 2,
					markerBottomCenterCoords.y - markerHeight - rect.height());

			paintText.setTextAlign(Paint.Align.CENTER);
			paintText.setTextSize(FONT_SIZE);
			paintText.setARGB(255, 255, 255, 255);
			paintRect.setARGB(130, 0, 0, 0);

			canvas.drawRoundRect(new RectF(rect), 2, 2, paintRect);
			canvas.drawText(item.getTitle(), rect.left + rect.width() / 2,
					rect.bottom - TITLE_MARGIN, paintText);
		}
	}
	
	/**
	 * Constructor method.
	 * Used to get the Source Location and the Transport Mode.
	 * Once the MapOverlay loads up and the locations(using Radius Tracker) are uploaded, 
	 * we need a common MapView Object and an Overlay Object where this is populated on.
	 * Thus, this gets invoked while the array of locations is being loaded up as
	 * further directions from the source to the selected point needs to be passed via this Overlay only.
	 * 
	 * NOTE: 
	 * Fired only once as the destination would change but the source location doesn't. See LocationMarker
	 * for the flow.
	 * */
	public void addParams(GeoPoint srcLoc, String transtype,
			DirectionWriterOverlay getRouteOverlay) {
		this.getSrcLocForDirections = srcLoc;
		this.gettranstype = transtype;
		this.setRouteOverlay = getRouteOverlay;
	}
	
	@Override
	protected boolean onTap(int index) {
		return true;
	}
	
	@Override
	public boolean onTap (final GeoPoint p, final MapView mapView){
		/**
		 * Method used to get our target/destination location. 
		 * When the LocationMarker finishes loading the list of locations, the same ItemizedOverlay Object 
		 * and the MapView Object have to be taken into consideration as the list is loaded onto them.
		 * Once these are found, the user then selects the location he is interested in, which further,
		 * calls the DirectionsWriter Class to write the route path onto the same MapView object.
		 * 
		 * Requirement: onTap(index) must be overridden and should return "True" for this method to be fired.
		 * 
		 * IMPORTANT: This will be fired for every location tapped. Also, since this extends GestureListener Class,
		 * it can be said that this acts like a Target Location Listener.
		 * */
		
	    boolean isTapped = super.onTap(p, mapView);
	    if (isTapped){
	    	getDestination(p,mapView,setRouteOverlay);
	    }           
	    return true;
	}


	protected void getDestination(GeoPoint getDest, MapView getmapView,
			DirectionWriterOverlay getRouteOverlay) {
		/**
		 * The Direction Calling Method. 
		 * This actually gets the target destination of the tapped location from the user and populates the
		 * route based on the Transport Mode and Source-Destination pair
		 * parameters.
		 * */

		// Check if the clicked location is not the source Location itself.
		if (getDest != getSrcLocForDirections) {
			DirectionsWriter directionsWriter = new DirectionsWriter();
			directionsWriter.getDirections(getmapView, getRouteOverlay,
					getSrcLocForDirections, getDest, gettranstype);
		} else {
			Toast.makeText(
					mContext,
					"You have selected your current Location! Please try again.",
					Toast.LENGTH_SHORT).show();
		}

	}

	/*@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		//---when user lifts his finger---
        if (event.getAction() == 1) {                
        	destLocPoint = mapView.getProjection().fromPixels(
                (int) event.getX(),
                (int) event.getY());
        }         
		return false;
	}*/

}
