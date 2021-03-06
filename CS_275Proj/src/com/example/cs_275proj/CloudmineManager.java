
package com.example.cs_275proj;

import java.util.ArrayList;
//import java.util.Iterator;
import java.util.Map;
import java.util.Set;
 

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.temboo.Library.CloudMine.ObjectStorage.ObjectGet;
import com.temboo.Library.CloudMine.ObjectStorage.ObjectGet.ObjectGetInputSet;
import com.temboo.Library.CloudMine.ObjectStorage.ObjectGet.ObjectGetResultSet;
import com.temboo.Library.CloudMine.ObjectStorage.ObjectUpdate;
import com.temboo.Library.CloudMine.ObjectStorage.ObjectUpdate.ObjectUpdateInputSet;
import com.temboo.Library.CloudMine.ObjectStorage.ObjectUpdate.ObjectUpdateResultSet;
import com.temboo.core.TembooException;
import com.temboo.core.TembooSession;


    public class CloudmineManager {

        private String apikey = "dfc150fe989c41869778972802e70b04";
        private String appkey = "976aa71a1bfe48258fcac26c097c9789";
        private TembooSession session;
        private String LocDB = "LocDB";
        private String UserDB = "UserDB";
        private String ReviewDB = "ReviewDB";
        
        public CloudmineManager(TembooSession s) {
        	session = s;    		
        }
        
        public ArrayList<String> filterGoogle(ArrayList<String> googlePlaceList) throws TembooException{
            
            ObjectGet objectGetChoreo = new ObjectGet(session);
            ObjectGetInputSet objectGetInputs = objectGetChoreo.newInputSet();
            objectGetInputs.set_APIKey(apikey);
            objectGetInputs.set_ApplicationIdentifier(appkey);
            objectGetInputs.set_Keys(LocDB); // get the locations database

            ObjectGetResultSet objectGetResults = objectGetChoreo.execute(objectGetInputs);
            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(objectGetResults.get_Response());
            JsonObject rootobj = root.getAsJsonObject().get("success").getAsJsonObject();
            // the whole location database
            JsonObject LocDatabase = rootobj.get(LocDB).getAsJsonObject();
            
            ArrayList<String> placesNearby = new ArrayList<String>();
                for (int j = 0; j < googlePlaceList.size(); j++) {
                    if (LocDatabase.has(googlePlaceList.get(j))) {
                    	placesNearby.add(googlePlaceList.get(j));
                    }
                }
         
         /*     
            HashSet<String> listToSet new HashSet<String>(placesNearby);
            List<String> placesNearbyWithoutDuplicates = new ArrayList<String>(listToSet);
         */       
            return placesNearby;
     
        }
        
        public Integer getRating(String locid) throws TembooException{
            int finalRating = 0;
            int count = 0;  
            
            ObjectGet objectGetChoreo = new ObjectGet(session);

            ObjectGetInputSet objectGetInputs = objectGetChoreo.newInputSet();
            objectGetInputs.set_APIKey(apikey);
            objectGetInputs.set_ApplicationIdentifier(appkey);
            objectGetInputs.set_Keys(ReviewDB+","+LocDB); // get the locations database
            ObjectGetResultSet objectGetResults = objectGetChoreo.execute(objectGetInputs);
     
            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(objectGetResults.get_Response());
            JsonObject rootobj = root.getAsJsonObject().get("success").getAsJsonObject();
            JsonObject ReviewDatabase = rootobj.get(ReviewDB).getAsJsonObject();
              
            JsonObject userObject = rootobj.get(LocDB).getAsJsonObject();
            JsonArray reviewIds = userObject.get(locid).getAsJsonObject().get("reviews").getAsJsonArray();
           
            
       
            for (int i=0; i<reviewIds.size(); i++) {
                   finalRating=+ReviewDatabase.get(reviewIds.get(i).getAsString()).getAsJsonObject().get("rating").getAsInt();
                   
                   count = count + 1;
            }
            finalRating = finalRating / count;
            
            return finalRating;
        }
        	
        public void sendReview(String review,String rating, UserInfo user,String locid) throws TembooException{
            
        	
        	
            JsonObject reviewObject = new JsonObject();
            
            reviewObject.addProperty("rating", rating);
            reviewObject.addProperty("text",review);    
            reviewObject.addProperty("userid",user.getUserId());
            reviewObject.addProperty("locationid",locid);
            
            
            
            JsonObject singleReviewObject = new JsonObject();
            String reviewId = getNextReviewId();
            singleReviewObject.add(reviewId,reviewObject);
            
            JsonObject mainObject = new JsonObject();
            
            mainObject.add(ReviewDB,singleReviewObject);
            
            
            ObjectUpdate objectUpdateChoreo = new ObjectUpdate(session);
            
            ObjectUpdateInputSet objectUpdateInputs = objectUpdateChoreo.newInputSet();
            
            objectUpdateInputs.set_Data(mainObject.toString());
            objectUpdateInputs.set_APIKey(apikey);
            objectUpdateInputs.set_ApplicationIdentifier(appkey);
            
            ObjectUpdateResultSet objectUpdateResults = objectUpdateChoreo.execute(objectUpdateInputs);
           
           updateUserPoints(user,locid);
           updateReviewIds(user,locid,reviewId);
           
           return; 
        }
        
        private void updateReviewIds(UserInfo user,String locId,String reviewId) throws TembooException{
        	
        	
        	
        	ObjectGet objectGetChoreo = new ObjectGet(session);

            ObjectGetInputSet objectGetInputs = objectGetChoreo.newInputSet();
            objectGetInputs.set_APIKey(apikey);
            objectGetInputs.set_ApplicationIdentifier(appkey);
            objectGetInputs.set_Keys(UserDB+","+LocDB); // get the locations database
            ObjectGetResultSet objectGetResults = objectGetChoreo.execute(objectGetInputs);
     
            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(objectGetResults.get_Response());
            JsonObject rootobj = root.getAsJsonObject().get("success").getAsJsonObject();
            JsonObject UserDatabase = rootobj.get(UserDB).getAsJsonObject();
            JsonObject LocDatabase = rootobj.get(LocDB).getAsJsonObject();
        	
        	
        	
        	JsonArray userJson = UserDatabase.get(user.getUserId()).getAsJsonObject().get("reviews").getAsJsonArray();
        	JsonArray locJson = LocDatabase.get(locId).getAsJsonObject().get("reviews").getAsJsonArray();
        	JsonPrimitive ri = new JsonPrimitive(reviewId);
        	
            userJson.add(ri);
            locJson.add(ri);
            
    
            JsonObject revLObject = new JsonObject();
            JsonObject revUObject = new JsonObject();
            revUObject.add("reviews",userJson);
            revLObject.add("reviews",locJson);
            
            JsonObject userObject = new JsonObject();
            JsonObject locObject = new JsonObject();
            
            userObject.add(user.getUserId(),revUObject);
            locObject.add(locId,revLObject);
            
            JsonObject m_userObject = new JsonObject();
            JsonObject m_locObject = new JsonObject();
            
            m_userObject.add(UserDB,userObject);
            m_locObject.add(LocDB,locObject);
            
            
            ObjectUpdate objectUpdateChoreo = new ObjectUpdate(session);
            
            ObjectUpdateInputSet objectUpdateInputs = objectUpdateChoreo.newInputSet();
            
            objectUpdateInputs.set_Data(m_locObject.toString());
            objectUpdateInputs.set_APIKey(apikey);
            objectUpdateInputs.set_ApplicationIdentifier(appkey);
            
            objectUpdateChoreo.execute(objectUpdateInputs);
            
            objectUpdateInputs.set_Data(m_userObject.toString());
            objectUpdateChoreo.execute(objectUpdateInputs);
            return;
        }
        
        private void updateUserPoints(UserInfo user,String locId) throws TembooException {
        
            ObjectGet objectGetChoreo = new ObjectGet(session);

            // Get an InputSet object for the choreo
            ObjectGetInputSet objectGetInputs = objectGetChoreo.newInputSet();
            
            // Set inputs
            objectGetInputs.set_APIKey(apikey);
            objectGetInputs.set_ApplicationIdentifier(appkey);
            objectGetInputs.set_Keys("UserDB");
            
            // Execute Choreo
            ObjectGetResultSet objectGetResults = objectGetChoreo.execute(objectGetInputs);
            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(objectGetResults.get_Response());
            JsonObject rootobj = root.getAsJsonObject().get("success").getAsJsonObject();
            // the whole user database
            JsonObject userArray = rootobj.get(UserDB).getAsJsonObject();
            JsonObject pointObj = userArray.get(user.getUserId()).getAsJsonObject().get("Points").getAsJsonObject();
            int points = 1;
            
            if(pointObj.has(locId)){
            	points = pointObj.get(locId).getAsInt() + 1;
            }
            
            
            JsonObject pointJson = new JsonObject();
            
            pointJson.addProperty(locId, points);
            
    
            JsonObject UIDObject = new JsonObject();
           
            UIDObject.add(user.getUserId(),pointJson);
            
            JsonObject mainObject = new JsonObject();
            
            mainObject.add(UserDB,UIDObject);
            
            
            ObjectUpdate objectUpdateChoreo = new ObjectUpdate(session);
            
            ObjectUpdateInputSet objectUpdateInputs = objectUpdateChoreo.newInputSet();
            
            objectUpdateInputs.set_Data(mainObject.toString());
            objectUpdateInputs.set_APIKey(apikey);
            objectUpdateInputs.set_ApplicationIdentifier(appkey);
            
            ObjectUpdateResultSet objectUpdateResults = objectUpdateChoreo.execute(objectUpdateInputs);
            
            return;
            
              
        }
                
                
                
        private String getNextReviewId() throws TembooException{
        
            ObjectGet objectGetChoreo = new ObjectGet(session);

            // Get an InputSet object for the choreo
            ObjectGetInputSet objectGetInputs = objectGetChoreo.newInputSet();
            
            
            // Set inputs
            objectGetInputs.set_APIKey(apikey);
            objectGetInputs.set_ApplicationIdentifier(appkey);
            objectGetInputs.set_Keys("ReviewDB");
            
            
            // Execute Choreo
            ObjectGetResultSet objectGetResults = objectGetChoreo.execute(objectGetInputs);
            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(objectGetResults.get_Response());
            JsonObject rootobj = root.getAsJsonObject().get("success").getAsJsonObject();
            // the whole location database
            JsonObject userObject = rootobj.get(ReviewDB).getAsJsonObject();
            
            
            Set<Map.Entry<String,JsonElement>> tempSet = userObject.entrySet();
            int size = tempSet.size();
            String s_size = ""+size;
            
            return s_size;
                
                }
                
                
                
            public String getLocName(String locid) throws TembooException{
            
            ObjectGet objectGetChoreo = new ObjectGet(session);

            // Get an InputSet object for the choreo
            ObjectGetInputSet objectGetInputs = objectGetChoreo.newInputSet();
            
            
            // Set inputs
            objectGetInputs.set_APIKey(apikey);
            objectGetInputs.set_ApplicationIdentifier(appkey);
            objectGetInputs.set_Keys("LocDB");
            
            
            // Execute Choreo
            ObjectGetResultSet objectGetResults = objectGetChoreo.execute(objectGetInputs);
            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(objectGetResults.get_Response());
            JsonObject rootobj = root.getAsJsonObject().get("success").getAsJsonObject();
            // the whole location database
            JsonObject userObject = rootobj.get(LocDB).getAsJsonObject();
            
            String locationName = userObject.get(locid).getAsJsonObject().get("name").getAsString();
            return locationName;
            }
            
            
            
            
            public String getLocDesc(String locid) throws TembooException{
            
            ObjectGet objectGetChoreo = new ObjectGet(session);

            // Get an InputSet object for the choreo
            ObjectGetInputSet objectGetInputs = objectGetChoreo.newInputSet();
            
            // Set inputs
            objectGetInputs.set_APIKey(apikey);
            objectGetInputs.set_ApplicationIdentifier(appkey);
            objectGetInputs.set_Keys("LocDB");
            
            // Execute Choreo
            ObjectGetResultSet objectGetResults = objectGetChoreo.execute(objectGetInputs);
            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(objectGetResults.get_Response());
            JsonObject rootobj = root.getAsJsonObject().get("success").getAsJsonObject();
            // the whole location database
            JsonObject userObject = rootobj.get(LocDB).getAsJsonObject();
            
            String locationDesc = userObject.get(locid).getAsJsonObject().get("description").getAsString();
            return locationDesc;
           
            }
            
            
            
            public ArrayList<String> getLocPrizes(String locid) throws TembooException {
                
                ObjectGet objectGetChoreo = new ObjectGet(session);

                // Get an InputSet object for the choreo
                ObjectGetInputSet objectGetInputs = objectGetChoreo.newInputSet();
                
                // Set inputs
                objectGetInputs.set_APIKey(apikey);
                objectGetInputs.set_ApplicationIdentifier(appkey);
                objectGetInputs.set_Keys("LocDB");
                
                // Execute Choreo
                ObjectGetResultSet objectGetResults = objectGetChoreo.execute(objectGetInputs);
                JsonParser jp = new JsonParser();
                JsonElement root = jp.parse(objectGetResults.get_Response());
                JsonObject rootobj = root.getAsJsonObject().get("success").getAsJsonObject();
                // the whole location database
                JsonObject userObject = rootobj.get(LocDB).getAsJsonObject();
                JsonObject location = userObject.get(locid).getAsJsonObject();
                JsonArray rewards = location.get("rewards").getAsJsonArray();
                
                ArrayList<String> rewardString = new ArrayList<String>();
                for (int i=0; i<rewards.size(); i++) {
                	rewardString.add(rewards.get(i).getAsJsonObject().get("reward").getAsString());
                	//System.out.println(reward);
                }
                
                return rewardString;
                }
            
            
            	public Integer getPoints (String locid, String userid) {
                
                ObjectGet objectGetChoreo = new ObjectGet(session);

                // Get an InputSet object for the choreo
                ObjectGetInputSet objectGetInputs = objectGetChoreo.newInputSet();
            
                // Set inputs
                objectGetInputs.set_APIKey(apikey);
                objectGetInputs.set_ApplicationIdentifier(appkey);
                objectGetInputs.set_Keys("UserDB");
            
                // Execute Choreo
                ObjectGetResultSet objectGetResults = null;
                try {
                    objectGetResults = objectGetChoreo.execute(objectGetInputs);
                } catch (TembooException e) {
                    e.printStackTrace();
                }
                JsonParser jp = new JsonParser();
                JsonElement root = jp.parse(objectGetResults.get_Response());
                JsonObject rootobj = root.getAsJsonObject().get("success").getAsJsonObject();
                // the whole location database
                JsonObject userObject = rootobj.get(UserDB).getAsJsonObject();
                JsonObject user = userObject.get(userid).getAsJsonObject();
                JsonObject Points = user.get("Points").getAsJsonObject();
                
                int rating = Points.get(locid).getAsInt();
                
            return rating;
            }
            
            
            
            	 public ArrayList<String> getReviews(String locid) throws TembooException{
                     
                     ObjectGet objectGetChoreo = new ObjectGet(session);

                     // Get an InputSet object for the choreo
                     ObjectGetInputSet objectGetInputs = objectGetChoreo.newInputSet();
                 
                     // Set inputs
                     objectGetInputs.set_APIKey(apikey);
                     objectGetInputs.set_ApplicationIdentifier(appkey);
                     objectGetInputs.set_Keys("LocDB");
                 
                     // Execute Choreo
                     ObjectGetResultSet objectGetResults = objectGetChoreo.execute(objectGetInputs);
                     JsonParser jp = new JsonParser();
                     JsonElement root = jp.parse(objectGetResults.get_Response());
                     JsonObject rootobj = root.getAsJsonObject().get("success").getAsJsonObject();
                     // the whole location database
                     JsonObject userObject = rootobj.get(LocDB).getAsJsonObject();
                     JsonArray reviewIds = userObject.get(locid).getAsJsonObject().get("reviews").getAsJsonArray();
                    
                     JsonObject reviewDb = getReviewDB();
                    
                     ArrayList<String> rev = new ArrayList<String> ();
                     for (int i=0; i<reviewIds.size(); i++) {
                                                                                   
                    	 rev.add(reviewDb.get(reviewIds.get(i).getAsString()).getAsJsonObject().get("text").getAsString());
                     }
                     
                 return rev;
                 
                 }
            
            // just used for the function above; we need to put most of the Json parsing in a function at some point
           
            public JsonObject getReviewDB () throws TembooException {
                ObjectGet objectGetChoreo = new ObjectGet(session);

                // Get an InputSet object for the choreo
                ObjectGetInputSet objectGetInputs = objectGetChoreo.newInputSet();
            
                // Set inputs
                objectGetInputs.set_APIKey(apikey);
                objectGetInputs.set_ApplicationIdentifier(appkey);
                objectGetInputs.set_Keys("ReviewDB");
            
                // Execute Choreo
                ObjectGetResultSet objectGetResults = objectGetChoreo.execute(objectGetInputs);
                JsonParser jp = new JsonParser();
                JsonElement root = jp.parse(objectGetResults.get_Response());
                JsonObject rootobj = root.getAsJsonObject().get("success").getAsJsonObject();
                
                return rootobj;
            }
            
            
            
            public String spendPoints (String loid, String userid,String rewardID){
            
            	
            	
            return "";
            
            }
            
            public void addUser (UserInfo user) throws TembooException{
            	
            	JsonObject userName = new JsonObject();
                
                userName.addProperty("name",user.getName());
                userName.add("Points",new JsonObject());
                userName.add("reviews",new JsonArray());
                
                
            	JsonObject userJson = new JsonObject();
                
                userJson.add(user.getUserId(),userName);
               
                
                JsonObject mainObject = new JsonObject();
                
                mainObject.add(UserDB,userJson);
                
                
                ObjectUpdate objectUpdateChoreo = new ObjectUpdate(session);
                
                ObjectUpdateInputSet objectUpdateInputs = objectUpdateChoreo.newInputSet();
                
                objectUpdateInputs.set_Data(mainObject.toString());
                objectUpdateInputs.set_APIKey(apikey);
                objectUpdateInputs.set_ApplicationIdentifier(appkey);
                
                ObjectUpdateResultSet objectUpdateResults = objectUpdateChoreo.execute(objectUpdateInputs);
            	
            }
    }
    