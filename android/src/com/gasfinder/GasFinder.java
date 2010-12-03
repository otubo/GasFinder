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

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class GasFinder extends MapActivity implements OnTabChangeListener {

	static final String LIST_TAB_TAG = "List";
	static final String MAP_TAB_TAG = "Map";

    TabHost tabHost;
	ListView listView;
	MapView mapView;
	MapController mc;
	private LocationManager lm;
	private LocationListener locationListener;
	
	GeoPoint p;
	
	private class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location loc) {
			if (loc != null) {
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
	
	class MapOverlay extends com.google.android.maps.Overlay
    {
        @Override
        public boolean draw(Canvas canvas, MapView mapView, 
        boolean shadow, long when) 
        {
            super.draw(canvas, mapView, shadow);                   
 
            //---translate the GeoPoint to screen pixels---
            Point screenPts = new Point();
            mapView.getProjection().toPixels(p, screenPts);
 
            //---add the marker---
            Bitmap bmp = BitmapFactory.decodeResource(
                getResources(), R.drawable.pushpin);            
            canvas.drawBitmap(bmp, screenPts.x, screenPts.y-50, null);         
            return true;
        }
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//GPS information
        //---use the LocationManager class to obtain GPS locations---
        lm = (LocationManager) 
            getSystemService(Context.LOCATION_SERVICE);    
        
        locationListener = new MyLocationListener();
        
        lm.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 
            0, 
            0, 
            locationListener);
        
		tabHost = (TabHost) findViewById(android.R.id.tabhost);

		// setup must be called if you are not inflating the tabhost from XML
		tabHost.setup();
		tabHost.setOnTabChangedListener(this);

		// setup list view
		listView = (ListView) findViewById(R.id.list);
		listView.setEmptyView((TextView) findViewById(R.id.empty));

		// create some dummy coordinates to add to the list
		List<GeoPoint> pointsList = new ArrayList<GeoPoint>();
		pointsList.add(new GeoPoint((int)(37.441*1E6),(int)(-122.1419*1E6)));
		listView.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, pointsList));

		// add an onclicklistener to see point on the map
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view,
					int position, long id) {

				GeoPoint geoPoint = (GeoPoint) listView.getAdapter().getItem(position);
				if (geoPoint != null) {
					// have map view moved to this point
					setMapZoomPoint(geoPoint, 12);
					// programmatically switch tabs to the map view
					tabHost.setCurrentTab(1);
				}
			}
		});
		
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
		tabHost.addTab(tabHost.newTabSpec(LIST_TAB_TAG).setIndicator("List")
				.setContent(new TabContentFactory() {
					public View createTabContent(String arg0) {
						return listView;
					}
				}));
		tabHost.addTab(tabHost.newTabSpec(MAP_TAB_TAG).setIndicator("Map")
				.setContent(new TabContentFactory() {
					public View createTabContent(String arg0) {
						return mapView;
					}
				}));

		// HACK to get the list view to show up first,
		// otherwise the mapview would be bleeding through and visible
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
		if (tabName.equals(MAP_TAB_TAG)) {
			// do something on the map

		} else if (tabName.equals(LIST_TAB_TAG)) {
			// do something on the list
		}
	}
}
