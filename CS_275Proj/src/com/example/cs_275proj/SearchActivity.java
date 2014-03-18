package com.example.cs_275proj;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;

public class SearchActivity extends Activity {
	EditText st;
	Button submit, cancel;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		st = (EditText) findViewById(R.id.searchTerm);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	public void onClick() {
		if(!st.getText().toString().isEmpty()) {
			Intent intent = new Intent(SearchActivity.this, FindSearchActivity.class);
			intent.putExtra("searchTerm", st.getText().toString());
		
			startActivityForResult(intent, 0);
		}
	}
	
	public void onCancel() {
		finish();
	}
}
