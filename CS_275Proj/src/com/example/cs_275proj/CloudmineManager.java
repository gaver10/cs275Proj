
package com.example.cs_275proj;

import java.util.ArrayList;
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
        private String LocDB;
        private String UserDB;
        private String ReviewDB;
        
        public void CloudmineManger(TembooSession s,String l,String u,String r) { 
            
             session = s;
             LocDB = l;
             UserDB = u;
             ReviewDB = r;
        }
        
       
        //Todo
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
        
        public Integer getRating(String locId){
            int finalRating = 0;
            int count = 0;  
            
            ObjectGet objectGetChoreo = new ObjectGet(session);

            ObjectGetInputSet objectGetInputs = objectGetChoreo.newInputSet();
            objectGetInputs.set_APIKey(apikey);
            objectGetInputs.set_ApplicationIdentifier(appkey);
            objectGetInputs.set_Keys(ReviewDB); // get the locations database
            ObjectGetResultSet objectGetResults = objectGetChoreo.execute(objectGetInputs);
     
            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(objectGetResults.get_Response());
            JsonObject rootobj = root.getAsJsonObject().get("success").getAsJsonObject();
            JsonObject ReviewDatabase = rootobj.get(ReviewDB).getAsJsonObject();
              

            for (JsonObject key: ReviewDatabase) {
                if (key.get("locationid").getAsString().equals(locId)){
                    finalRating += Integer.parseInt(key.get("rating").getAsString());
                    count++;
                }
            }
            
            finalRating = finalRating / count;
            return (Integer) finalRating;
            
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
           
           //TODO:
           updateUserPoints(user,locid);
           updateReviewIds(user,locid,reviewId);
           
           return; 
        }
        
        private void updateReviewIds(UserInfo user,String locId,String reviewId) throws TembooException{
        	
        	JsonArray userJson = new JsonArray();
            
        	JsonPrimitive ri = new JsonPrimitive(reviewId);
        	
            userJson.add(ri);
            
    
            JsonObject revObject = new JsonObject();
           
            revObject.add("reviews",userJson);
            
            JsonObject userObject = new JsonObject();
            JsonObject locObject = new JsonObject();
            
            userObject.add(user.getUserId(),revObject);
            locObject.add(locId,revObject);
            
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
            
            
            
            
            
            public ArrayList<String> getLocPrizes(String locid) {
            
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
            JsonObject rewards = location.get("rewards").getAsJsonObject();
            
            ArrayList<String> rewardString = new ArrayList<String>();
            for (JsonObject rewardID: rewards ) {
               JsonObject eachReward =  rewards.get("rewardID").getAsJsonObject();
               String name = eachReward.get("reward").getAsString();
               String cost = eachReward.get("cost").getAsString(); 
                  rewardString.add(name + "   " + cost);
            }
            
            return rewardString;
            }
            
            
            public Integer getPoints (String locid, String userid) {
                
                Integer points = 0;
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
                // the whole location database
                JsonObject userObject = rootobj.get(UserDB).getAsJsonObject();
                JsonObject user = userObject.get(userid).getAsJsonObject();
                JsonArray Points = user.get("Points").getAsJsonArray();
                
                //!!!!!!!!!!!!!!!!!!!!!! probably not right
                for (int i=0; i<Points.size(); i++) {
                    if (Points.get(i).get(locid).equals(locid)) {
                        Points = Points.get(i).get(locid).get("Points").getAsInt();
                    }
                }
                
            return Points;
            }
            
            
            
            public ArrayList<String> getReviews(String locid){
                
                
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
                // the whole location database
                JsonObject userObject = rootobj.get(UserDB).getAsJsonObject();
                JsonArray reviewIds = userObject.get(locid).getAsJsonObject().get("reviews").getAsJsonArray();
                
                JsonObject reviewDb = getReviewDB();
                
                
                ArrayList<String> rev = new ArrayList<String> ();
                for (int i=0; i<reviewIds.size(); i++) {
                       for (JsonObject key: reviewDb) {
                           if (reviewIds.get(i).equals(key)) {
                               add(reviewDb.get(key).getAsJsonObject().get("text").getAsString());
                           }
                       }
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
    