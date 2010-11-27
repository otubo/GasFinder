package com.gasfinder;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class PreferencesActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textview = new TextView(this);
        textview.setText("Preferences view");
        setContentView(textview);
    }
}
