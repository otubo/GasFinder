package com.gasfinder;

import android.app.Activity;
import android.app.TabActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.TabHost;
import android.widget.TextView;
import android.graphics.drawable.*;

public class GasFinderTabWidget extends TabActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, MapsActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("maps").setIndicator("",
                res.getDrawable(R.drawable.ic_tab_artists))
            .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, ListActivity.class);
        spec = tabHost.newTabSpec("list").setIndicator("Lists",
                res.getDrawable(R.drawable.ic_tab_artists))
            .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, StatisticsActivity.class);
        spec = tabHost.newTabSpec("statistics").setIndicator("Statistics",
                res.getDrawable(R.drawable.ic_tab_artists))
            .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, BookmarksActivity.class);
        spec = tabHost.newTabSpec("bookmarks").setIndicator("Bookmarks",
                res.getDrawable(R.drawable.ic_tab_artists))
            .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, PreferencesActivity.class);
        spec = tabHost.newTabSpec("preferences").setIndicator("Preferences",
                res.getDrawable(R.drawable.ic_tab_artists))
            .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(1);
    }
}
