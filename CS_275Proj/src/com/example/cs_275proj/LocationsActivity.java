package com.example.cs_275proj;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LocationsActivity extends Activity {
	ListView locations;
	
	static UserInfo user;
	static ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locations);
		
		locations = (ListView) findViewById(R.id.locations);
		
		user = new UserInfo();
		Intent intent = getIntent();
		user.setName(intent.getExtras().getString("name"));
		user.setLat("37.029869");
		user.setLon("-76.345222");
		
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
