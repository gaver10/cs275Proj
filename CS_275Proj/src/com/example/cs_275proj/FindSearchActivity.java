package com.example.cs_275proj;

import java.util.ArrayList;

import com.temboo.core.TembooException;
import com.temboo.core.TembooSession;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FindSearchActivity extends Activity {
	ListView results;
	
	String searchTerm;
	ArrayAdapter<String> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_search);
		
		Intent intent = getIntent();
		searchTerm = intent.getExtras().getString("searchTerm");
		
		results = (ListView) findViewById(R.id.searchResults);
		
		CloudmineManager s;
		try {
			s = new CloudmineManager(new TembooSession("gaver10", "myFirstApp", "c336c82fc4c641279410c79d9071a3c4"));
			s.getLocDesc(searchTerm);
		} catch (TembooException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.find_search, menu);
		return true;
	}
}
