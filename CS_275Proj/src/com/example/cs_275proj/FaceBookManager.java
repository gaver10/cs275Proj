package com.example.cs_275proj;

//import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.temboo.Library.Facebook.OAuth.FinalizeOAuth.FinalizeOAuthResultSet;
import com.temboo.Library.Facebook.OAuth.FinalizeOAuth;
import com.temboo.Library.Facebook.OAuth.FinalizeOAuth.FinalizeOAuthInputSet;
import com.temboo.Library.Facebook.OAuth.InitializeOAuth.InitializeOAuthResultSet;
import com.temboo.Library.Facebook.OAuth.InitializeOAuth;
import com.temboo.Library.Facebook.OAuth.InitializeOAuth.InitializeOAuthInputSet;
import com.temboo.Library.Facebook.Reading.User;
import com.temboo.Library.Facebook.Reading.User.UserInputSet;
import com.temboo.Library.Facebook.Reading.User.UserResultSet;
import com.temboo.core.TembooException;
import com.temboo.core.TembooSession;

public class FaceBookManager {
	public static void main (String args[]) throws TembooException {
		// Temboo data
		String acctName = "denisaqori";
		String appKeyName = "myFirstApp";
		String appKeyValue = "a96514d3faf44939920604af74946009";
		
		//Facebook Data
		String appID = "1378780555708712";
		String appSecret = "fcba183d790175e766279553bc544450";
		
		TembooSession session = new TembooSession (acctName, appKeyName, appKeyValue );
		InitializeOAuthResultSet initializeOAuthResultSet = initialize (session, appID);
		
		String authURL = initializeOAuthResultSet.get_AuthorizationURL();
		String callbackID = initializeOAuthResultSet.get_CallbackID();
		
		System.out.println("Go to the URL below and allow the application to access your Facebook account: ");
		System.out.println("Press enter to continue the execution of the program:");
		System.out.println(authURL);
		
		FinalizeOAuthResultSet finalizeOAuthResults = finalize (session, appID, appSecret, callbackID);
		String accessToken = finalizeOAuthResults.get_AccessToken();
		String errorMessage = finalizeOAuthResults.get_ErrorMessage();
		String expires = finalizeOAuthResults.get_Expires();
		
		String userID = getUserID(session, accessToken);
	}
	
	public static InitializeOAuthResultSet initialize (TembooSession session, String appID) throws TembooException {
		InitializeOAuth initializeOAuthChoreo = new InitializeOAuth(session);

		// Get an InputSet object for the choreo
		InitializeOAuthInputSet initializeOAuthInputs = initializeOAuthChoreo.newInputSet();

		// Set inputs
		initializeOAuthInputs.set_AppID(appID);

		// Execute Choreo
		InitializeOAuthResultSet initializeOAuthResults = initializeOAuthChoreo.execute(initializeOAuthInputs);
		return initializeOAuthResults;
	}
	
	public static FinalizeOAuthResultSet finalize (TembooSession session, String appID, String appSecret, String callbackID) throws TembooException {
		FinalizeOAuth finalizeOAuthChoreo = new FinalizeOAuth(session);

		// Get an InputSet object for the choreo
		FinalizeOAuthInputSet finalizeOAuthInputs = finalizeOAuthChoreo.newInputSet();

		// Set inputs
		finalizeOAuthInputs.set_CallbackID(callbackID);
		finalizeOAuthInputs.set_AppSecret(appSecret);
		finalizeOAuthInputs.set_AppID(appID);

		// Execute Choreo
		FinalizeOAuthResultSet finalizeOAuthResults = finalizeOAuthChoreo.execute(finalizeOAuthInputs);
		return finalizeOAuthResults;
	}
	
	public static String getUserID (TembooSession session, String accessToken) throws TembooException {
		User userChoreo = new User(session);
		UserInputSet userInputs = userChoreo.newInputSet();
		
		userInputs.set_AccessToken(accessToken);
		UserResultSet userResults = userChoreo.execute(userInputs);
		
		String jsonResponse = userResults.get_Response();
		
		JsonParser jp = new JsonParser();
		JsonElement root = jp.parse(jsonResponse);
		JsonObject rootObj = root.getAsJsonObject();
		
		String userID = rootObj.getAsString();
		
		return userID;
	}
}
