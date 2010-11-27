package com.gasfinder;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class StatisticsActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textview = new TextView(this);
        textview.setText("Statistics view");
        setContentView(textview);
    }
}
