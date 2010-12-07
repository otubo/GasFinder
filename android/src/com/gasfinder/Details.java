package com.gasfinder;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Details extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.details);

        TextView textview = new TextView(this);
        textview.setText("This is the Artists tab");
        setContentView(textview);
    }
}