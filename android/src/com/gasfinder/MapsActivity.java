package com.gasfinder;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MapsActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textview = new TextView(this);
        textview.setText("Map view");
        setContentView(textview);
    }
}
//package com.gasfinder;
//
//import com.google.android.maps.*;
//import android.os.Bundle;
//import android.widget.TextView;
//
//public class MapsActivity extends MapActivity {
//    @Override
//        protected boolean isRouteDisplayed() {
//            return false;
//        }
//
//    @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            setContentView(R.layout.main);
//            MapView mapView = (MapView) findViewById(R.id.mapview);
//            mapView.setBuiltInZoomControls(true);
//        }
//}
