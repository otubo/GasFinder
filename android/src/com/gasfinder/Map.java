package com.gasfinder;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import android.os.Bundle;

import com.google.android.maps.MapView.LayoutParams;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import com.google.android.maps.Overlay;

public class Map extends MapActivity {
	public MapView mapView;
	public LinearLayout linearLayout; 
	MapController mc;
	GeoPoint eu, posto;
	public String bandeira = null;

	class MapOverlay extends com.google.android.maps.Overlay {
		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			super.draw(canvas, mapView, shadow);

			// ---translate the GeoPoint to screen pixels---
			Point screen_posto = new Point();
			try {
				mapView.getProjection().toPixels(posto, screen_posto);
			} catch (Exception ex) {
				return false;
			}

			// ---add the marker---
			Bitmap bmp_posto = BitmapFactory.decodeResource(getResources(),R.drawable.iimm2blue);
			canvas.drawBitmap(bmp_posto, screen_posto.x, screen_posto.y - 50, null);
			
//			// ---translate the GeoPoint to screen pixels---
//			Point screen_eu = new Point();
//			try {
//				mapView.getProjection().toPixels(eu, screen_eu);
//			} catch (Exception ex) {
//				return false;
//			}
//
//			// ---add the marker---
//			Bitmap bmp_eu = BitmapFactory.decodeResource(getResources(),R.drawable.iimm2blue);
//			canvas.drawBitmap(bmp_eu, screen_eu.x, screen_eu.y - 50, null);
			
			return true;
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		
		mapView = (MapView) findViewById(R.id.mapview);

		mapView.setBuiltInZoomControls(true);
		mc = mapView.getController();
		
		Intent callerIntent;
		callerIntent = getIntent();
		Bundle myBundle;
		myBundle = callerIntent.getExtras();

		double posto_latitude = myBundle.getDouble("posto_latitude");
		double posto_longitude = myBundle.getDouble("posto_longitude");
		double eu_latitude = myBundle.getDouble("eu_latitude");
		double eu_longitude = myBundle.getDouble("eu_longitude");
		bandeira = myBundle.getString("bandeira");
		
		MapOverlay mapOverlay = new MapOverlay();
		List<Overlay> listOfOverlays = mapView.getOverlays();
		listOfOverlays.clear();
		listOfOverlays.add(mapOverlay);

		eu = new GeoPoint((int) (eu_latitude * 1E6), (int) (eu_longitude * 1E6));
		posto = new GeoPoint((int) (posto_latitude * 1E6), (int) (posto_longitude * 1E6));
		mc.animateTo(posto);
		mc.setZoom(10);
		setMapZoomPoint(posto, 18);

		mapView.invalidate();
	}

	/**
	 * Instructs the map view to navigate to the point and zoom level specified.
	 * 
	 * @param geoPoint
	 * @param zoomLevel
	 */
	private void setMapZoomPoint(GeoPoint geoPoint, int zoomLevel) {
		mapView.getController().setCenter(geoPoint);
		mapView.getController().setZoom(zoomLevel);
		mapView.postInvalidate();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}

//public class Map extends MapActivity {
//	
//	MapView mapView;
//	MapController mc;
//	GeoPoint p;
//	
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.map);
//
//		Intent callerIntent;
//		callerIntent = getIntent();
//		Bundle myBundle;
//		myBundle = callerIntent.getExtras();
//
//		double posto_latitude = myBundle.getDouble("posto_latitude");
//		double posto_longitude = myBundle.getDouble("posto_longitude");
//		double eu_latitude = myBundle.getDouble("eu_latitude");
//		double eu_longitude = myBundle.getDouble("eu_longitude");
//
//		mapView = (MapView) findViewById(R.id.mapview);
//		mc = mapView.getController();
//		mapView.setBuiltInZoomControls(true);
//
//		List<Overlay> mapOverlays = mapView.getOverlays();
//		Drawable drawable = this.getResources().getDrawable(
//				R.drawable.iimm2blue);
//		ItemsOverlay itemizedoverlay = new ItemsOverlay(drawable,
//				getApplicationContext());
//
//		GeoPoint point = new GeoPoint((int) (eu_latitude * 1E6),
//				(int) (eu_longitude * 1E6));
//		OverlayItem overlayitem = new OverlayItem(point, "Yataah!", "To aqui!");
//
//		GeoPoint point2 = new GeoPoint((int) (posto_latitude * 1E6),
//				(int) (posto_longitude * 1E6));
//		OverlayItem overlayitem2 = new OverlayItem(point2, "Posto!",
//				"Posto de gasolina!");
//		
//
//		itemizedoverlay.addOverlay(overlayitem);
//		mapOverlays.add(itemizedoverlay);
//		
//	}
//	
//	/**
//	 * Instructs the map view to navigate to the point and zoom level specified.
//	 * 
//	 * @param geoPoint
//	 * @param zoomLevel
//	 */
//	private void setMapZoomPoint(GeoPoint geoPoint, int zoomLevel) {
//		mapView.getController().setCenter(geoPoint);
//		mapView.getController().setZoom(zoomLevel);
//		mapView.postInvalidate();
//	}
//
//	@Override
//	protected boolean isRouteDisplayed() {
//		return false;
//	}
//}