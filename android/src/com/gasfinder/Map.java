package com.gasfinder;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class Map extends MapActivity{
	MapView mapView;
	MapController mc;
	
	private class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location loc) {
			if (loc != null) {
				if((int) (loc.getLatitude() * 1E6) == 0 || (int) (loc
						.getLongitude() * 1E6) == 0){
					return;
				}
				p = new GeoPoint((int) (loc.getLatitude() * 1E6), (int) (loc
						.getLongitude() * 1E6));
				mc.animateTo(p);
				mc.setZoom(17);
				setMapZoomPoint(p, 12);
			}
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}
	}
	
	class MapOverlay extends com.google.android.maps.Overlay {
		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			super.draw(canvas, mapView, shadow);

			// ---translate the GeoPoint to screen pixels---
			Point screenPts = new Point();
			try {mapView.getProjection().toPixels(p, screenPts);
			} catch (Exception ex){
				return false;
			}

			// ---add the marker---
			Bitmap bmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.pushpin);
			canvas.drawBitmap(bmp, screenPts.x, screenPts.y - 50, null);
			return true;
		}
	}
	
	// setup map view
	mapView = (MapView) findViewById(R.id.mapview);
	mapView.setBuiltInZoomControls(true);
	mc = mapView.getController();

	MapOverlay mapOverlay = new MapOverlay();
	List<Overlay> listOfOverlays = mapView.getOverlays();
	listOfOverlays.clear();
	listOfOverlays.add(mapOverlay);

	mapView.invalidate();
	

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

	/**
	 * From MapActivity, we ignore it for this demo
	 */
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
}
