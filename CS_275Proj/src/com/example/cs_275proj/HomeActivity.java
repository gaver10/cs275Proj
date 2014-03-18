package com.example.cs_275proj;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

public class HomeActivity extends Activity {
	Button location;
	Button profile;
	Button awards;
	Button search;
	
	static ArrayAdapter<String> adapter;
	
	String username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		Intent intent = getIntent();
		username = intent.getExtras().getString("name");

		
		location = (Button) findViewById(R.id.location);
		profile = (Button) findViewById(R.id.profile);
		awards = (Button) findViewById(R.id.awards);
		search = (Button) findViewById(R.id.search);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.location:
				Intent loc = new Intent(HomeActivity.this, LocationsActivity.class);
				loc.putExtra("name", username);

				startActivity(loc);
				break;
			case R.id.profile:
				Intent prof = new Intent(HomeActivity.this, ProfileActivity.class);

				startActivity(prof);
				break;
			case R.id.awards:
				Intent awa = new Intent(HomeActivity.this, AwardsActivity.class);
				
				startActivity(awa);
				
				break;
			case R.id.search:
				Intent search = new Intent(HomeActivity.this, SearchActivity.class);

				startActivity(search);
				break;
		}
	}
}
