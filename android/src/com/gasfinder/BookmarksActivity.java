package com.gasfinder;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class BookmarksActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textview = new TextView(this);
        textview.setText("Bookmarks view");
        setContentView(textview);
    }
}
