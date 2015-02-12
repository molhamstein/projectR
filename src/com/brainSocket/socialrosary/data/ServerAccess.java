package com.brainSocket.socialrosary.data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import com.brainSocket.socialrosary.RosaryApp;
import com.brainSocket.socialrosary.model.AppUser;

public class ServerAccess {
	// GENERIC ERROR CODES
	public static final String CONNECTION_ERROR_CODE = "-99";
	public static final String ERROR_CODE_USER_ID = "-100";
	public static final String ERROR_CODE_USER_NOT_ACCEPTABLE = "-101";
	public static final String ERROR_CODE_USER_NOT_ACTIVATED = "-102";
	public static final String RESPONCE_FORMAT_ERROR_CODE = "-110";

	// api
	static final String BASE_SERVICE_URL = "http://brain-socket.com/rosary";
	static final String baseServiceURL_FOR_UDP = "198.38.91.194" ;
	private static final int CHECK_UPDATES_PORT = 2522;

	// api keys
		private static final String FLAG = "flag";
		private static final String KEY = "key";
		private static final String IS_REGISTERED_BEFORE = "isRegisteredBefore";
	
	private static ServerAccess serverAccess = null;
	//private ConnectivityManager cm ;

	private ServerAccess() {
		Context c = RosaryApp.getAppContext();
		//cm =(ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	public static ServerAccess getInstance() {
		if (serverAccess == null) {
			serverAccess = new ServerAccess();
		}
		return serverAccess;
	}
	
	//----------------------------------
	// Server Utils 
	//-----------------------
	
	/*public boolean isOnline() {
		try{
		    NetworkInfo netInfo = cm.getActiveNetworkInfo();
		    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
		        return true;
		    }
		}catch(Exception e){}
	    return false;
	}*/

	// API calls // ------------------------------------------------
	
	
	/**
	 * register a new user with UserName and phoneNumber  
	 * @param name
	 * @param phoneNum
	 * @return
	 */
	public ServerResult registerUser(String name, String phoneNum, String countryId, String osVerison) {
		ServerResult result = new ServerResult();		
		String key = null;
		String isRegisterdBefore = null;
		try {
			// parameters
			JSONObject jsonPairs = new JSONObject();
			jsonPairs.put("username", name);
			jsonPairs.put("phoneNumber", phoneNum);
			// url
			String url = BASE_SERVICE_URL + "/users/register";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) { // check if response
															// is empty
				JSONObject jsonResponse = new JSONObject(response);
				result.setFlag(jsonResponse.getString(FLAG));
				key = jsonResponse.getString(KEY);
				isRegisterdBefore = jsonResponse.getString(IS_REGISTERED_BEFORE);
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		result.addPair(KEY, key);
		result.addPair(IS_REGISTERED_BEFORE, isRegisterdBefore);
		return result;
	}

	/**
	 * retrives the current User Data
	 * 
	 * @param accessToken
	 * @return HashMap Containing the Current AppUser as "appUser"
	 */
	public HashMap<String, Object> getMe(String accessToken) {
		HashMap<String, Object> mapResult = new HashMap<String, Object>();
		AppUser appUser = null;
		String error = null;

		try {
			// Formating URL
			String url = BASE_SERVICE_URL + "/users/me?id=" + accessToken;
			String response = sendGetRequest(url); // // in this method we are
													// using get request
			if (response != null && response.equals("")) { // check if we gut a
															// response
				JSONObject jsonResponse = new JSONObject(response);
				if (jsonResponse != null) {
					if (jsonResponse.has("error")) {
						error = jsonResponse.getString("error");
					} else if (jsonResponse.has("user")) {
						appUser = new AppUser(
								jsonResponse.getJSONObject("user"));
					}
				}
			} else {
				error = CONNECTION_ERROR_CODE;
			}
		} catch (Exception e) {
		}
		mapResult.put("error", error);
		mapResult.put("appUser", appUser);
		return mapResult;
	}

	/**
	 * import facebook friends
	 * @param userId
	 * @param sessionAccessToken
	 * @return 
	 */
	public ServerResult importFacebookFriends(String userId,
			String sessionAccessToken) {
		ServerResult result = new ServerResult();
		HashMap<String, String> facebookFirends = new HashMap<String, String>();
		try {
			// parameters
			JSONObject jsonPairs = new JSONObject();
			jsonPairs.put("userId", userId);
			jsonPairs.put("sessionAccessToken", sessionAccessToken);
			// url
			String url = BASE_SERVICE_URL + "/users/importFacebookFriends";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				String flag = jsonResponse.getString(FLAG);
				result.setFlag(flag);
				if(!flag.equals("-1")) {
					JSONArray fbFriendsJson = jsonResponse.getJSONArray("users");
					for(int i=0;i<fbFriendsJson.length();++i){
						JSONObject fbFriend = fbFriendsJson.getJSONObject(i);
						facebookFirends.put(fbFriend.getString("id"), fbFriend.getString("userName"));
					}
				}
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		result.addPair("users", facebookFirends);
		return result;
	}

	public ServerResult sendZekrToUsers(String userId, List<String> toUsers, String ZekrTitle, String zekrValue){
		ServerResult result = new ServerResult();
		try {
			// parameters
			JSONObject jsonPairs = new JSONObject();
			jsonPairs.put("userId", userId);
			JSONArray destinationUsers = new JSONArray(toUsers);
			jsonPairs.put("destinationUserIds", destinationUsers);
			JSONObject zeker = new JSONObject();
			zeker.put("title", ZekrTitle);
			zeker.put("value", zekrValue);
			jsonPairs.put("zekerValue", zeker);
			// url
			String url = BASE_SERVICE_URL + "/events/sendZekerToUsers";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				String flag = jsonResponse.getString(FLAG);
				result.setFlag(flag);								
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}	
		return result;
	}
	public ServerResult ResponseToZekr(String userId, String eventId, String hasAgree){
		ServerResult result = new ServerResult();
		try {
			// parameters
			JSONObject jsonPairs = new JSONObject();
			jsonPairs.put("userId", userId);			
			jsonPairs.put("recievedEventId", eventId);
			jsonPairs.put("hasAgree", hasAgree);
			// url
			String url = BASE_SERVICE_URL + "/events/acceptOrRejectZeker";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				String flag = jsonResponse.getString(FLAG);
				result.setFlag(flag);								
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		return result;
	}
	public ServerResult getTodayHadith(String userId){
		ServerResult result = new ServerResult();
		String title = null;
		String value = null;
		try {
			// parameters
			JSONObject jsonPairs = new JSONObject();
			jsonPairs.put("userId", userId);						
			// url
			String url = BASE_SERVICE_URL + "/contents/getTodayHadith";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				String flag = jsonResponse.getString(FLAG);
				result.setFlag(flag);
				if(flag.equals("0")) {
					JSONObject valueObject = jsonResponse.getJSONObject("value");
					title = valueObject.getString("title");
					value = valueObject.getString("value");
				}
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		result.addPair("title", title);
		result.addPair("value", value);
		return result;
	}
	public ServerResult getUserScore(String userId){
		ServerResult result = new ServerResult();		
		try {
			// parameters
			JSONObject jsonPairs = new JSONObject();
			jsonPairs.put("userId", userId);						
			// url
			String url = BASE_SERVICE_URL + "/users/getUserScore";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				String flag = jsonResponse.getString(FLAG);
				result.setFlag(flag);				
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		return result;
	}
	public ServerResult getNotification(){
		ServerResult result = new ServerResult();
		return result;
	}
	public ServerResult isVersionValid(String versionNumber){
		ServerResult result = new ServerResult();
		try {
			// parameters
			JSONObject jsonPairs = new JSONObject();
			jsonPairs.put("versionNumber", versionNumber);						
			// url
			String url = BASE_SERVICE_URL + "/versions/isVersionValid";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				String flag = jsonResponse.getString(FLAG);
				result.setFlag(flag);				
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		return result;
	}
	public ServerResult getWhoHasTheApp(String[] phoneNumbers){
		ServerResult result = new ServerResult(); 
		try {
			// parameters
			JSONObject jsonPairs = new JSONObject();
			for(String phone:phoneNumbers) {
				jsonPairs.put("phoneNumber", phone);
			}
									
			// url
			String url = BASE_SERVICE_URL + "/versions/isVersionValid";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				String flag = jsonResponse.getString(FLAG);
				result.setFlag(flag);
				if(!flag.equals("-1")) {
					JSONArray usersHasTheApp = jsonResponse.getJSONArray("users");
					for(int i=0 ; i<usersHasTheApp.length() ; i++){
						JSONObject user = usersHasTheApp.getJSONObject(i);
						result.addPair("userId", user.get("userId"));
						result.addPair("userName", user.get("userName"));
					}
				}
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		return result;
	}
	public ServerResult getUserConversations(String userId, String destUserId, String fromPage){
		ServerResult result = new ServerResult();
		try {
			// parameters
			JSONObject jsonPairs = new JSONObject();			
				jsonPairs.put("userId", userId);
				jsonPairs.put("destUserId", destUserId);
				jsonPairs.put("fromPage", fromPage);
									
			// url
			String url = BASE_SERVICE_URL + "/events/getUserConversations";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				String flag = jsonResponse.getString(FLAG);
				result.setFlag(flag);
				if(!flag.equals("-1") && !flag.equals("-2")) {	
					JSONArray messages = jsonResponse.getJSONArray("messages");
					for(int i=0 ; i<20 ; i++){					
						HashMap<String, String> message = new HashMap<String, String>();
						JSONObject messageJson = messages.getJSONObject(i);
						message.put("userId", messageJson.getString("userId"));
						message.put("userName", messageJson.getString("userName"));
						message.put("date", messageJson.getString("date"));
						message.put("haditTitle", messageJson.getJSONObject("value").getString("hadithTitle"));
						result.addPair("message", message);
					}
				}
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		return result;
	}
	public ServerResult editUserProfile(String userId, String userName, String gender, String userFile){
		ServerResult result = new ServerResult();
		try {
			// parameters
			JSONObject jsonPairs = new JSONObject();
			jsonPairs.put("userId", userId);
			jsonPairs.put("userName", userName);
			jsonPairs.put("gender", gender);
			jsonPairs.put("userFile", userFile);
									
			// url
			String url = BASE_SERVICE_URL + "/users/editUserProfile";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				String flag = jsonResponse.getString(FLAG);
				result.setFlag(flag);
				
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
	
		return result;
	}
	public ServerResult updateZekrConter(String userId, String taskId, String counterValue){
		ServerResult result = new ServerResult();
		try {
			// parameters
			JSONObject jsonPairs = new JSONObject();
			jsonPairs.put("userId", userId);
			jsonPairs.put("taskId", taskId);
			jsonPairs.put("counterValue", counterValue);
									
			// url
			String url = BASE_SERVICE_URL + "/tasks/updateZekerCounter";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				String flag = jsonResponse.getString(FLAG);
				result.setFlag(flag);
				
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		return result;
	}
	public ServerResult updatePersonalZekrConter(String userId, String counterValue){
		ServerResult result = new ServerResult();
		try {
			// parameters
			JSONObject jsonPairs = new JSONObject();
			jsonPairs.put("userId", userId);			
			jsonPairs.put("counterValue", counterValue);
									
			// url
			String url = BASE_SERVICE_URL + "/users/updatePersonalZekerCounter";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				String flag = jsonResponse.getString(FLAG);
				result.setFlag(flag);
				
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		return result;
	}
	public ServerResult getPersonalZekrConter(String userId){
		ServerResult result = new ServerResult();
		try {
			// parameters
			JSONObject jsonPairs = new JSONObject();
			jsonPairs.put("userId", userId);						
									
			// url
			String url = BASE_SERVICE_URL + "/users/getPersonalZekerCounter";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				String flag = jsonResponse.getString(FLAG);
				result.setFlag(flag);
				
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		return result;
	}
	public ServerResult getTodoList(String userId){
		ServerResult result = new ServerResult();
		try {
			// parameters
			JSONObject jsonPairs = new JSONObject();
			jsonPairs.put("userId", userId);						
									
			// url
			String url = BASE_SERVICE_URL + "/tasks/getTodoList";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				String flag = jsonResponse.getString(FLAG);
				result.setFlag(flag);
				if(!flag.equals("-1")){
					JSONArray tasks = jsonResponse.getJSONArray("tasks");
					for(int i=0 ; i<tasks.length() ; i++) {
						JSONObject taskJson = tasks.getJSONObject(i);
						HashMap<String, String> task = new HashMap<String, String>();
						task.put("taskId", taskJson.getString("taskId"));
						task.put("userId", taskJson.getString("userId"));
						task.put("userName", taskJson.getString("userName"));
						task.put("goal", taskJson.getString("goal"));
						task.put("processed", taskJson.getString("processed"));
						task.put("date", taskJson.getString("date"));
						result.addPair("task", task);
						}
				}
				
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		return result;
	}
	
	private ServerResult sendRequestAndGetFlag(String url, JSONObject postParams){
		ServerResult result = new ServerResult();
		try {			
			String response = sendPostRequest(url, postParams);
			// parse response
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				String flag = jsonResponse.getString(FLAG);
				result.setFlag(flag);								
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		return result;
	}
	// Request executers //
	
	private String sendGetRequest(String url) {
		HttpClient client = new DefaultHttpClient();
		String result = null;

		try {
			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent()));
				StringBuilder str = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					str.append(line + "\n");
				}
				reader.close();
				result = str.toString();
			} catch (Exception ex) {
				result = null;
			}
		} catch (Exception ex) {
			result = null;
		}

		return result;
	}

	private String sendPostRequest(String url, JSONObject jsonPairs) {
		HttpClient client = new DefaultHttpClient();
		String result = null;
		try {
			HttpPost post = new HttpPost(url);
			StringEntity entity = new StringEntity(jsonPairs.toString(),HTTP.UTF_8);
			entity.setContentType("application/json");
			post.setEntity(entity);
			HttpResponse response = client.execute(post);

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent()));
				StringBuilder str = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					str.append(line + "\n");
				}
				reader.close();
				result = str.toString().trim();
			} catch (Exception ex) {
			}
		} catch (Exception ex) {
		}
		return result;
	}
	
	private String sendUDPRequest(String url) {
		
		String text = null;
	    byte[] message = new byte[1500];
	    try{
		    InetAddress serveraddr = InetAddress.getByName(baseServiceURL_FOR_UDP);
		    DatagramPacket res = new DatagramPacket(message,message.length);
		    DatagramPacket send = new DatagramPacket(url.getBytes(),url.getBytes().length,serveraddr,CHECK_UPDATES_PORT);		    
		    DatagramSocket s = new DatagramSocket();
		    try {
			    s.setSoTimeout(1500);    
			    s.send(send);
			    s.receive(res);
			    text = new String(message, 0, res.getLength(),"UTF-8");
			    s.close();
			} catch (Exception e) {
				s.close();
			}
	    }catch(Exception e){
	    }
	    return text;
	}
	
	
	
}
