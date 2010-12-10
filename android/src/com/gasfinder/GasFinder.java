package com.gasfinder;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.GpsStatus.Listener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;

import com.google.android.maps.GeoPoint;

public class GasFinder extends ListActivity implements OnClickListener {

	private LocationManager lm;
	private LocationListener locationListener;

	boolean gps_enabled = false;
	boolean network_enabled = false;
	Location loc = null;
	public boolean gps_is_found = false;
	public double latitude = 0.00, longitude = 0.00;
	public boolean once_control = true;

	GeoPoint p;
	ProgressDialog dialog = null;

	public List<Map<String, Object>> resourceNames = new ArrayList<Map<String, Object>>();
	public Map<String, Object> data;
	public String[] index = new String[50];

	Message msg = new Message();
	ListView listview = null;

	class RefreshHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			GasFinder.this.updateUI(resourceNames);
		}

		public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	};

	private void updateUI(List<Map<String, Object>> resourceNames) {
		android.widget.SimpleAdapter notes = new android.widget.SimpleAdapter(
				this, resourceNames, R.layout.row, new String[] { "line1",
						"line2", "image" }, new int[] { R.id.text1, R.id.text2,
						R.id.img });

		notes.setViewBinder(new MyViewBinder());
		listview.setAdapter(notes);
	}

	public class MyViewBinder implements ViewBinder {

		public boolean setViewValue(View view, Object data,
				String textRepresentation) {
			if ((view instanceof ImageView) & (data instanceof Bitmap)) {
				ImageView iv = (ImageView) view;
				Bitmap bm = (Bitmap) data;
				iv.setImageBitmap(bm);
				return true;
			}
			return false;
		}
	}

	public class GasStationList extends ListActivity {
		public void buildList(double latitude, double longitude) {
			URL url = null;
			URLConnection con = null;
			String jsonTxt = null;
			JSONObject json = null;
			JSONArray postos = null;

			try {
				url = new URL(
						"http://developer.meuspostos.com.br/api/busca.json?lat="
								+ latitude + "&lon=" + longitude);

//				Log.i("GasFinder", "URL: " + url);

				con = url.openConnection();
				jsonTxt = IOUtils.toString(con.getInputStream());
				json = new JSONObject(jsonTxt);
				postos = json.getJSONObject("data").getJSONArray("Postos");

			} catch (Exception e1) {
				e1.printStackTrace();
			}

			for (int i = 0; i < postos.length(); i++) {
				data = new HashMap<String, Object>();
				JSONObject posto = null;
				URL myFileUrl = null;
				Bitmap bmImg = null;
				try {
					posto = postos.getJSONObject(i);
//					Log.i("GasFinder", posto.getString("nome")
//							+ posto.getString("endereco")
//							+ posto.getString("bandeira"));

					data.put("line1", posto.getString("nome"));
					data.put("line2", posto.getString("endereco"));

					myFileUrl = new URL(posto.getString("icone"));

					HttpURLConnection conn = (HttpURLConnection) myFileUrl
							.openConnection();
					conn.setDoInput(true);
					conn.connect();
					InputStream is = conn.getInputStream();

					bmImg = BitmapFactory.decodeStream(is);

					data.put("image", bmImg);
					resourceNames.add(data);

					index[i] = posto.getString("posto");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			updateUI(resourceNames);
		}
	}

	private class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location loc) {
			if (loc != null) {
				if ((int) (loc.getLatitude() * 1E6) == 0
						|| (int) (loc.getLongitude() * 1E6) == 0) {
					return;
				}
				p = new GeoPoint((int) (loc.getLatitude() * 1E6), (int) (loc
						.getLongitude() * 1E6));
				latitude = loc.getLatitude();
				longitude = loc.getLongitude();
				Log
						.i("GasFinder", "onLocationChanged: " + latitude
								+ longitude);

				if (once_control) {
					GasStationList list = new GasStationList();
					list.buildList(latitude, longitude);
					once_control = false;
				}
			}
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	private final Listener onGpsStatusChange = new GpsStatus.Listener() {
		public void onGpsStatusChanged(int event) {
			switch (event) {
			case GpsStatus.GPS_EVENT_STARTED:
				Log.i("GasFinder", "GPS STARTED");
				break;
			case GpsStatus.GPS_EVENT_FIRST_FIX:
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
		listview = (ListView) findViewById(android.R.id.list);

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
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
					locationListener);

		} else if (network_enabled) {
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
					locationListener);

		} else {
			loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (loc == null)
				loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

			p = new GeoPoint((int) (loc.getLatitude() * 1E6), (int) (loc
					.getLongitude() * 1E6));

			latitude = loc.getLatitude();
			longitude = loc.getLongitude();

			GasStationList list = new GasStationList();
			list.buildList(loc.getLatitude(), loc.getLongitude());
		}

		resourceNames.add(data);

		android.widget.SimpleAdapter notes = new android.widget.SimpleAdapter(
				this, resourceNames, R.layout.row, new String[] { "line1",
						"line2", "img" }, new int[] { R.id.text1, R.id.text2,
						R.id.img });

		listview.setAdapter(notes);

		// add an onclicklistener to see point on the map
		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view,
					int position, long id) {

				Intent myIntent = new Intent();
				Bundle stats = new Bundle();

				Log.i("GasFinder", "LOCATION ------------ " + latitude 	+ longitude);
				stats.putString("postoid", index[position - 1]);
				stats.putDouble("eu_latitude", latitude);
				stats.putDouble("eu_longitude", longitude);
				myIntent.putExtras(stats);
				myIntent.setClass(getApplicationContext(),
						com.gasfinder.Details.class);
				startActivity(myIntent);
			}
		});
	}

	public void onClick(View v) {
	}
}
