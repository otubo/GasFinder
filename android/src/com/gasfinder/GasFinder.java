package com.gasfinder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.net.*;
import java.io.*;

import org.json.*;

import org.apache.commons.io.IOUtils;

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
import android.app.Activity;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import com.gasfinder.Details;
import com.zeetha.hello.mac.R;

public class GasFinder extends Activity {

	ListView listView;

	private LocationManager lm;
	private LocationListener locationListener;

	boolean gps_enabled = false;
	boolean network_enabled = false;
	Location loc = null;
	public double latitude, longitude;

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
				latitude = loc.getLatitude();
				longitude = loc.getLongitude();				
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		View row=super.getView(position, convertView, parent);

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
			
			latitude = loc.getLatitude();
			longitude = loc.getLongitude();
		}

		//setup http request
		URL url = new URL("http://developer.meuspostos.com.br/api/busca.json?lat=" + latitude + "&lon=" + longitude);
		URLConnection con = url.openConnection();
		BufferedReader buffer = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
    
        String jsonTxt = IOUtils.toString(buffer);

		JSONObject json = new JSONObject(jsonTxt);
		JSONArray postos = json.getJSONObject("data").getJSONArray("Postos");
		
		List<String> nome = new ArrayList<String>();
		List<String> endereco = new ArrayList<String>();
		List<String> bandeira = new ArrayList<String>();
		
		for (int i = 0; i < postos.length(); i++) {
			JSONObject posto = postos.getJSONObject(i);
			nome.add(posto.getString("nome"));
			endereco.add((String) posto.getString("endereco"));
			bandeira.add(posto.getString("bandeira"));
		}
		//XXX
		// setup list view
		TextView label=(TextView)row.findViewById(R.id.label);

		
		// create some dummy coordinates to add to the list
		listView.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, pointsList));

		// add an onclicklistener to see point on the map
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view,
					int position, long id) {

				GeoPoint geoPoint = (GeoPoint) listView.getAdapter().getItem(
						position);
				if (geoPoint != null) {
					// have map view moved to this point
					Intent myIntent = new Intent();
					myIntent.setClass(getApplicationContext(), com.gasfinder.Details.class);
					startActivity(myIntent);
					// programmatically switch tabs to the map view
//					tabHost.setCurrentTab(1);
				}
			}
		});
	}
}
