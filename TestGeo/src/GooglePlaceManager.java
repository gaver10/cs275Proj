

import com.temboo.Library.Google.Places.PlaceSearch;
import com.temboo.Library.Google.Places.PlaceSearch.PlaceSearchInputSet;
import com.temboo.Library.Google.Places.PlaceSearch.PlaceSearchResultSet;
import com.temboo.core.TembooException;
import com.temboo.core.TembooSession;

public class GooglePlaceManager {

	
	private String apiKEY = "AIzaSyDpTTKmO2dPprtUv5UROog8eaEKsycaI8A";
	
	
	public String getNearby(String lat,String lon) throws TembooException{
		
		// Instantiate the Choreo, using a previously instantiated TembooSession object, eg:
		TembooSession session = new TembooSession("gaver10", "myFirstApp", "c336c82fc4c641279410c79d9071a3c4");
		PlaceSearch placeSearchChoreo = new PlaceSearch(session);

		// Get an InputSet object for the choreo
		PlaceSearchInputSet placeSearchInputs = placeSearchChoreo.newInputSet();

		// Set inputs
		placeSearchInputs.set_Key(apiKEY);
		placeSearchInputs.set_Latitude(lat);
		placeSearchInputs.set_Longitude(lon);
		placeSearchInputs.set_Radius(1000);

		// Execute Choreo
		PlaceSearchResultSet placeSearchResults = placeSearchChoreo.execute(placeSearchInputs);
		
		String jsonData = placeSearchResults.get_Response();
		System.out.println(jsonData);
		return jsonData;
		
	}
	
	
	
}
