package com.gasfinder;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.location.GpsStatus.Listener;
import android.location.GpsStatus;
import android.app.ProgressDialog;
import android.content.Intent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import com.gasfinder.Details;

public class GasFinder extends MapActivity implements OnTabChangeListener {

	TabHost tabHost;
	ListView listView;
	ListView listView2;
	MapView mapView;
	MapController mc;
	private LocationManager lm;
	private LocationListener locationListener;

	boolean gps_enabled = false;
	boolean network_enabled = false;
	Location loc = null;

	GeoPoint p;
	
	ProgressDialog dialog;
	
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

	private final Listener onGpsStatusChange = new GpsStatus.Listener() {
		public void onGpsStatusChanged(int event) {
			switch (event) {
			case GpsStatus.GPS_EVENT_STARTED:
				dialog.show(GasFinder.this, "", "Getting GPS Status", true);
				break;
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				if (dialog != null && dialog.isShowing())
					dialog.dismiss();
				break;
			case GpsStatus.GPS_EVENT_STOPPED:
				// Stopped...
				break;
			}
		}
	};
	
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// GPS information
		// ---use the LocationManager class to obtain GPS locations---
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		lm.addGpsStatusListener(onGpsStatusChange);

		// exceptions will be thrown if provider is not permitted.
		try {
			gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
		}
		try {
			network_enabled = lm
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}

		// don't start listeners if no provider is enabled
		if (!gps_enabled && !network_enabled)
			return;

		locationListener = new MyLocationListener();

		if (gps_enabled) {
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0, locationListener);
			
		}else if(network_enabled) {
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
			
		}else{
			loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if(loc == null)
				loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			
			p = new GeoPoint((int) (loc.getLatitude() * 1E6), (int) (loc
					.getLongitude() * 1E6));
			mc.animateTo(p);
			mc.setZoom(17);
			setMapZoomPoint(p, 12);
		}

		tabHost = (TabHost) findViewById(android.R.id.tabhost);

		// setup must be called if you are not inflating the tabhost from XML
		tabHost.setup();
		tabHost.setOnTabChangedListener(this);

		// setup list view
		listView = (ListView) findViewById(R.id.list);

		// create some dummy coordinates to add to the list
		List<GeoPoint> pointsList = new ArrayList<GeoPoint>();
		pointsList.add(new GeoPoint((int) (37.441 * 1E6), (int) (-122.1419 * 1E6)));
		listView.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, pointsList));

		// add an onclicklistener to see point on the map
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view,
					int position, long id) {

				GeoPoint geoPoint = (GeoPoint) listView.getAdapter().getItem(
						position);
				if (geoPoint != null) {
					// have map view moved to this point
					setMapZoomPoint(geoPoint, 12);
					Intent myIntent = new Intent();
					myIntent.setClass(getApplicationContext(), com.gasfinder.Details.class);
					startActivity(myIntent);
					// programmatically switch tabs to the map view
//					tabHost.setCurrentTab(1);
				}
			}
		});
		
		// setup second list view
		listView2 = (ListView) findViewById(R.id.list2);
//		listView2.setEmptyView((TextView) findViewById(R.id.text2));

		// create some dummy coordinates to add to the list
		List<String> strings = new ArrayList<String>();
		strings.add("hello");
		strings.add("hello");
		strings.add("hello");
		listView2.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, strings));

		// setup map view
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mc = mapView.getController();

		MapOverlay mapOverlay = new MapOverlay();
		List<Overlay> listOfOverlays = mapView.getOverlays();
		listOfOverlays.clear();
		listOfOverlays.add(mapOverlay);

		mapView.invalidate();

		// add views to tab host
		tabHost.addTab(tabHost.newTabSpec("List 1").setIndicator("List")
				.setContent(new TabContentFactory() {
					public View createTabContent(String arg0) {
						return listView;
					}
				}));
		tabHost.addTab(tabHost.newTabSpec("Map 1").setIndicator("Map")
				.setContent(new TabContentFactory() {
					public View createTabContent(String arg0) {
						return mapView;
					}
				}));
		tabHost.addTab(tabHost.newTabSpec("Second List 1").setIndicator("Second List")
				.setContent(new TabContentFactory() {
					public View createTabContent(String arg0) {
						return listView2;
					}
				}));


		// HACK to get the list view to show up first,
		// otherwise the mapview would be bleeding through and visible
		tabHost.setCurrentTab(2);
		tabHost.setCurrentTab(1);
		tabHost.setCurrentTab(0);
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

	/**
	 * From MapActivity, we ignore it for this demo
	 */
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	/**
	 * Implement logic here when a tab is selected
	 */
	public void onTabChanged(String tabName) {
		if (tabName.equals("Map 1")) {
			// do something on the map

		} else if (tabName.equals("List 1")) {
			// do something on the list
		} else if (tabName.equals("Second List 1")) {
			// do something on the list
		}
	}
}
