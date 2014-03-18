package com.example.cs_275proj;

import java.util.ArrayList;

import android.os.AsyncTask;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.temboo.Library.Google.Places.PlaceSearch;
import com.temboo.Library.Google.Places.PlaceSearch.PlaceSearchInputSet;
import com.temboo.Library.Google.Places.PlaceSearch.PlaceSearchResultSet;
import com.temboo.core.TembooException;
import com.temboo.core.TembooSession;

public class GooglePlaceManager extends AsyncTask<Void, Void, Void> {
	ArrayList<String> locs;
		
	@Override
	protected void onPostExecute(Void arg0) {
		LocationsActivity.adapter.clear();
		
		for(int i = 0; i < locs.size(); i++) {
			LocationsActivity.adapter.add(locs.get(i));
		}
		
		LocationsActivity.adapter.notifyDataSetChanged();
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		locs = getNearby();
		
		return null;
	}
	
	public ArrayList<String> getNearby() {
		String apiKEY = "AIzaSyDVPNAcM0EkhqgGIDcaaN7Mr1d2pw4ZMBE";
		
		TembooSession session = null;
		try {
			session = new TembooSession("gaver10", "myFirstApp", "c336c82fc4c641279410c79d9071a3c4");
		} catch (TembooException e2) {
			e2.printStackTrace();
		}
		PlaceSearch placeSearchChoreo = new PlaceSearch(session);

		// Get an InputSet object for the choreo
		PlaceSearchInputSet placeSearchInputs = placeSearchChoreo.newInputSet();

		// Set inputs
		placeSearchInputs.set_Key(apiKEY);
		placeSearchInputs.set_Radius(1000);
		placeSearchInputs.set_Latitude("39.9");//userI.getLat());
		placeSearchInputs.set_Longitude("-75.1");//userI.getLon());

		// Execute Choreo
		PlaceSearchResultSet placeSearchResults = null;
		try {
			placeSearchResults = placeSearchChoreo.execute(placeSearchInputs);
		} catch (TembooException e1) {
			e1.printStackTrace();
		}
		
		String jsonData = placeSearchResults.get_Response();
		
		JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(jsonData);
        JsonArray rootobj = root.getAsJsonObject().get("results").getAsJsonArray();
        ArrayList<String> alist = new ArrayList<String>();
        for(JsonElement e : rootobj){
        	alist.add(e.getAsJsonObject().get("id").getAsString());
        	System.out.println(e.getAsJsonObject().get("id").getAsString());
        }
		
		return alist;
	}
}
