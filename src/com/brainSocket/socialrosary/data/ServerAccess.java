package com.brainSocket.socialrosary.data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.brainSocket.socialrosary.RosaryApp;
import com.brainSocket.socialrosary.model.AppUser;

public class ServerAccess {
	// GENERIC ERROR CODES
	static final String CONNECTION_ERROR_CODE = "-100";
	static final String RESPONCE_FORMAT_ERROR_CODE = "-110";

	// api
	static final String BASE_SERVICE_URL = "http://brain-socket.com/rosary";
	static final String baseServiceURL_FOR_UDP = "198.38.91.194" ;
	private static final int CHECK_UPDATES_PORT = 2522;

	
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
	public HashMap<String, Object> registerUser(String name, String phoneNum) {
		HashMap<String, Object> mapResult = new HashMap<String, Object>();
		String error = null;
		String accessToken = null;
		AppUser appUser = null;
		try {
			// parameters
			JSONObject jsonPairs = new JSONObject();
			jsonPairs.put("username", name);
			jsonPairs.put("phoneNumber", phoneNum);
			// url
			String url = BASE_SERVICE_URL + "/users/";
			// send request
			String response = sendPostRequest(url, jsonPairs);
			// parse request
			if (response != null && !response.equals("")) { // check if response is empty
				JSONObject jsonResponse = new JSONObject(response);
				if (jsonResponse.has("error")) { // / an error is returned
					error = jsonResponse.getString("error");
				} else if (jsonResponse.has("login_token")) { // no error returned, so we can extract data From response
					accessToken = jsonResponse.getString("login_token");
					appUser = new AppUser(jsonResponse);
				}
			} else {
				error = CONNECTION_ERROR_CODE;
			}
		} catch (Exception e) {
			error = RESPONCE_FORMAT_ERROR_CODE;
		}
		mapResult.put("error", error);
		mapResult.put("accessToken", accessToken);
		mapResult.put("appUser", appUser);
		return mapResult;
	}
	
	/**
	 * retrives the current User Data
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
			String response = sendGetRequest(url); // // in this method we are using get request
			if (response != null && response.equals("")) { // check if we gut a response
				JSONObject jsonResponse = new JSONObject(response);
				if (jsonResponse != null) {
					if (jsonResponse.has("error")) {
						error = jsonResponse.getString("error");
					} else if (jsonResponse.has("user")) {
						appUser = new AppUser(jsonResponse.getJSONObject("user"));
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
