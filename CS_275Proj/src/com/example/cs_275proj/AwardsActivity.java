package com.example.cs_275proj;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class AwardsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_awards);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.awards, menu);
		return true;
	}

}
