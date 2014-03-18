package com.example.cs_275proj;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LocationsActivity extends Activity {
	ListView locations;

	static ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locations);
		
		locations = (ListView) findViewById(R.id.locations);
		
		ArrayList<String> locationList = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, locationList);
		adapter.setNotifyOnChange(true);
		getLocs();
		locations.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.locations, menu);
		return true;
	}

	public void getLocs() {
		GooglePlaceManager task = new GooglePlaceManager();
		task.execute();
	}
}
