package com.brainSocket.socialrosary.data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.view.accessibility.AccessibilityEventSource;

import com.brainSocket.socialrosary.RosaryApp;
import com.brainSocket.socialrosary.model.AppContact;
import com.brainSocket.socialrosary.model.AppEvent;
import com.brainSocket.socialrosary.model.AppSession;
import com.brainSocket.socialrosary.model.AppUser;

public class ServerAccess {
	// GENERIC ERROR CODES
	
	
	public static final String ERROR_CODE_userNotExists ="-101";
	public static final String ERROR_CODE_unknown ="-100";
	public static final String ERROR_CODE_userExistsBefore ="-102";
	public static final String ERROR_CODE_tokenNotExists = "-103";
	public static final String ERROR_CODE_accessTokenExpired = "-104";
	public static final String ERROR_CODE_noEnrolledFriends = "-105";
	public static final String ERROR_CODE_invalidAccessToken = "-106";
	public static final String ERROR_CODE_contactsArrayParsingError = "-107";
	public static final String ERROR_CODE_versionNotValid = "-108";
	public static final String ERROR_CODE_verificationMessageNotExists = "-109";
	public static final String ERROR_CODE_cantFindUserTaskProcess="-110";
	public static final String ERROR_CODE_sessionNotExists="-111";
	public static final String ERROR_CODE_requsterUserIsNotInThisSession="-112";
	public static final String ERROR_CODE_destUserIsInThisSessionBefore="-113";
	public static final String ERROR_CODE_destMobileNumberNotExists="-114";
	public static final String ERROR_CODE_receivedEventIdNotExists="-115";
	
	public static final String RESPONCE_FORMAT_ERROR_CODE = "-19" ;
	public static final String CONNECTION_ERROR_CODE = "-20" ;
	
	

	// api
	static final int MAIN_PORT_NUM = 3000 ;
	static final String BASE_SERVICE_URL = "http://104.217.253.15:"+MAIN_PORT_NUM;
	static final String baseServiceURL_FOR_UDP = "198.38.91.194" ;

	private static final int CHECK_UPDATES_PORT = 2522;

	// api keys
		private static final String FLAG = "flag";
		private static final String KEY = "key";

	
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
	
	
	
	public ServerResult login(String phoneNum) {
		ServerResult result = new ServerResult();
		AppUser me  = null ;
		try {
			// parameters
			JSONObject jsonPairs = new JSONObject();
			jsonPairs.put("mobileNumber", phoneNum);
			// url
			String url = BASE_SERVICE_URL + "/users/login";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) { // check if response is empty
				JSONObject jsonResponse = new JSONObject(response);
				result.setFlag(jsonResponse.getString(FLAG));
				if(jsonResponse.has("user")){
					me = new AppUser(jsonResponse.getJSONObject("user"));
				}
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		result.addPair("appUser", me);

		return result;
	}
	
	/**
	 * register a new user with UserName and phoneNumber  
	 * @param name
	 * @param phoneNum
	 * @return
	 */
	public ServerResult registerUser(String name, String phoneNum, boolean isMale, String osVerison, String versionId) {
		ServerResult result = new ServerResult();
		AppUser me  = null ;
		try {
			// parameters
			JSONObject jsonPairs = new JSONObject();
			jsonPairs.put("userName", name);
			jsonPairs.put("mobileNumber", phoneNum);
			jsonPairs.put("osVerison", osVerison);
			jsonPairs.put("isMale", isMale);
			jsonPairs.put("versionId", versionId);
			
			// url
			String url = BASE_SERVICE_URL + "/users/register";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) { // check if response is empty
				JSONObject jsonResponse = new JSONObject(response);
				result.setFlag(jsonResponse.getString(FLAG));
				if(jsonResponse.has("user")){
					me = new AppUser(jsonResponse.getJSONObject("user"));
				}

			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		result.addPair("appUser", me);

		return result;
	}
	
	public ServerResult setGcmId(String accessToken, String gcmId) {
		ServerResult result = new ServerResult();
		AppUser me  = null ;
		boolean success = false;
		try {
			// parameters
			JSONObject jsonPairs = new JSONObject();
			jsonPairs.put("accessToken", accessToken);
			jsonPairs.put("gcmId", gcmId);
			// url
			String url = BASE_SERVICE_URL + "/users/setGcmId";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) { // check if response is empty
				JSONObject jsonResponse = new JSONObject(response);
				result.setFlag(jsonResponse.getString(FLAG));
				if(jsonResponse.has("user") && !jsonResponse.isNull("user")){
					me = new AppUser(jsonResponse.getJSONObject("user"));
					success = true ;
				}
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		result.addPair("success", success);
		return result;
	}
	
	
	public ServerResult getSessionsByDate(String accessToken, long date) {
		ServerResult result = new ServerResult();
		ArrayList<AppSession> sessions = null ;
		try {
			// parameters
			JSONObject jsonPairs = new JSONObject();
			jsonPairs.put("accessToken", accessToken);
			jsonPairs.put("fromDate", date);			
			// url
			String url = BASE_SERVICE_URL + "/sessions/getLastSessions";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse response
			if (response != null && !response.equals("")) { // check if response is empty
				JSONObject jsonResponse = new JSONObject(response);
				result.setFlag(jsonResponse.getString(FLAG));
				if(jsonResponse.has("sessions")){
					sessions = new ArrayList<AppSession>() ;
					if(!jsonResponse.isNull("sessions")){
						JSONArray jsonArray = jsonResponse.getJSONArray("sessions");
						for (int i = 0; i < jsonArray.length(); i++) {
							AppSession se = new AppSession(jsonArray.getJSONObject(i));
							sessions.add(se) ;
						}
					}
				}

			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		result.addPair("sessions", sessions);

		return result;
	}
	
	public ServerResult getSessionEvents(String accessToken, String sessionId) {
		ServerResult result = new ServerResult();
		ArrayList<AppEvent> sessionEvents = null ;
		try {
			JSONObject jsonPairs = new JSONObject();
			jsonPairs.put("accessToken", accessToken);
			jsonPairs.put("sessionId", sessionId);			
			String url = BASE_SERVICE_URL + "/sessions/getSessionEvents";
			String response = sendPostRequest(url, jsonPairs);
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				result.setFlag(jsonResponse.getString(FLAG));
				if(jsonResponse.has("events")){
					sessionEvents = new ArrayList<AppEvent>() ;
					if(!jsonResponse.isNull("events")){
						JSONArray jsonArray = jsonResponse.getJSONArray("events");
						for (int i = 0; i < jsonArray.length(); i++) {
							AppEvent event = new AppEvent(jsonArray.getJSONObject(i));
							sessionEvents.add(event) ;
						}
					}
				}
			} else {result.setFlag(CONNECTION_ERROR_CODE);}
		} catch (Exception e) {result.setFlag(RESPONCE_FORMAT_ERROR_CODE);}
		result.addPair("events", sessionEvents);
		return result;
	}

	public ServerResult getEnrolledFriends(String accessToken, ArrayList<AppContact> contatcs) {
		ServerResult result = new ServerResult();
		ArrayList<AppContact> enrolledFriends = null ;
		try {
			JSONArray jsonContacts = new JSONArray() ;
			if(contatcs != null){
				for (AppContact appContact : contatcs) {
					if(appContact.getPhoneNum() != null && !appContact.getPhoneNum().isEmpty()){
						JSONObject jsn = new JSONObject();
						jsn.put("mobileNumber", appContact.getPhoneNum());
						jsonContacts.put(jsn);
					}
				}
			}
			JSONObject jsonPairs = new JSONObject();
			jsonPairs.put("accessToken", accessToken);
			jsonPairs.put("contactsArray", jsonContacts);			
			String url = BASE_SERVICE_URL + "/users/getEnrolledFriends";
			String response = sendPostRequest(url, jsonPairs);
			if (response != null && !response.equals("")) {
				JSONObject jsonResponse = new JSONObject(response);
				result.setFlag(jsonResponse.getString(FLAG));
				if(jsonResponse.has("enrolledUsers")){
					enrolledFriends = new ArrayList<AppContact>() ;
					if(!jsonResponse.isNull("enrolledUsers")){
						JSONArray jsonArray = jsonResponse.getJSONArray("enrolledUsers");
						for (int i = 0; i < jsonArray.length(); i++) {
							AppContact enrolledFriend = new AppContact(jsonArray.getJSONObject(i));
							enrolledFriends.add(enrolledFriend) ;
						}
					}
				}
			} else {result.setFlag(CONNECTION_ERROR_CODE);}
		} catch (Exception e) {result.setFlag(RESPONCE_FORMAT_ERROR_CODE);}
		result.addPair("enrolledUsers", enrolledFriends);
		return result;
	}
	

/*	public HashMap<String, Object> getMe(String accessToken) {
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
	}*/

	/**
	 * import facebook friends
	 * @param userId
	 * @param sessionAccessToken
	 * @return 
	 */
	public ServerResult importFacebookFriends(String accessToken, String sessionAccessToken) {
		ServerResult result = new ServerResult();
		HashMap<String, String> facebookFirends = new HashMap<String, String>();
		try {
			// parameters
			JSONObject jsonPairs = new JSONObject();
			jsonPairs.put("facebookAccessToken", accessToken);
			jsonPairs.put("facebookAccessToken", sessionAccessToken);
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
					JSONArray fbFriendsJson = jsonResponse.getJSONArray("newFriends");
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

	public ServerResult addSelfZeker(String accessToken,int counter,int contentId){
		
		ServerResult result = new ServerResult();
		try {
			// parameters
			JSONObject jsonPairs = new JSONObject();
			jsonPairs.put("accessToken", accessToken);
			jsonPairs.put("counter", counter);
			jsonPairs.put("contentId", contentId);
			// url
			String url = BASE_SERVICE_URL + "/selfZeker/addSelfZeker";
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
	
	
	
	
	/**
	 * send Zecer to friend
	 * @param sessionAccessToken
	 * @param destMobileNumber friend's mobile number
	 * @param contentId
	 * @param goal
	 * @return ServerResult 
	 */
	public ServerResult sendZekrToUsers(String accessToken,String destMobileNumber,int contentId,int goal){
		ServerResult result = new ServerResult();
		System.out.println("ServerAccess.sendZekrToUsers()");
		try {
			// parameters
			
			JSONObject jsonPairs = new JSONObject();
			jsonPairs.put("accsessToken",accessToken);
			jsonPairs.put("destMobileNumber",destMobileNumber);
			jsonPairs.put("contentId",contentId);
			jsonPairs.put("goal",goal);
			
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
	
	public ServerResult getTodayHadith(String accessToken){
		ServerResult result = new ServerResult();
		int type = 1;
		String value = null;
		try {
			// parameters
			JSONObject jsonPairs = new JSONObject();
			jsonPairs.put("accessToken", accessToken);						
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
					JSONObject valueObject = jsonResponse.getJSONObject("content");
					type = valueObject.getInt("contentTypeId");
					value = valueObject.getString("value");
				}
			} else {
				result.setFlag(CONNECTION_ERROR_CODE);
			}
		} catch (Exception e) {
			result.setFlag(RESPONCE_FORMAT_ERROR_CODE);
		}
		result.addPair("type", type);
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
			//TODO it should be BASE_SERVICE_URL + "tasks/updateTaskCounter";
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
	
	
	/**
	 * Register the device with the API to receive Push notifications
	 * @param regId
	 * The registration Id received from the GCM provider
	 */
	/*public void sendGCMRegistratinId(String apiAccessToken, String regId, String userId)
	{
		try {
			if(regId != null && !regId.isEmpty()) {
				// parameters
				JSONObject jsonPairs = new JSONObject();
				jsonPairs.put("token", regId);
				jsonPairs.put("type", "3");
				
				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("token", regId));
				pairs.add(new BasicNameValuePair("type", "3"));
				// url
				String url = BASE_SERVICE_URL + "/users/register_device?access_token=" + apiAccessToken;
				// send request
				String response = sendPostRequest(url, jsonPairs);
			}
		}
		catch (Exception e) {}
	}*/
	
	
	// Request executers //
	static HttpClient client  ;
	private String sendGetRequest(String url) {
		client = new DefaultHttpClient();
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
		client = new DefaultHttpClient();
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
			ex.printStackTrace();
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
	
	
/*	static {
	    HttpParams params = new BasicHttpParams();
	    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	    HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
	    HttpProtocolParams.setUseExpectContinue(params, false);  
	    HttpConnectionParams.setConnectionTimeout(params, 10000);
	    HttpConnectionParams.setSoTimeout(params, 10000);
	    ConnManagerParams.setMaxTotalConnections(params, 100);
	    ConnManagerParams.setTimeout(params, 30000);

	    SchemeRegistry registry = new SchemeRegistry();
	    registry.register(new Scheme("http",PlainSocketFactory.getSocketFactory(), MAIN_PORT_NUM )); 
	    ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params, registry);
	    client = new DefaultHttpClient(manager, params);
	    //httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
	}*/
	
	
}
