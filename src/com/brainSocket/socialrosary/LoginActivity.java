package com.brainSocket.socialrosary;

import java.util.ArrayList;
import java.util.HashMap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.brainSocket.socialrosary.RosaryApp.phoeNumCheckResult;
import com.brainSocket.socialrosary.data.DataStore;
import com.brainSocket.socialrosary.data.DataStore.DataRequestCallback;
import com.brainSocket.socialrosary.model.AppUser;
import com.brainSocket.socialrosary.model.AppUser.GENDER;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

public class LoginActivity extends AppBaseActivity implements OnClickListener {

	private enum LoginMode {FB_SIGNUP, SIGNUP, SIGNIN } ;
	private enum LOGIN_STAGE {ENTER_PHONE_NUM, ENTER_USER_DETAILS}
	private boolean attemptingLogin = false ;

	// Values for email and password at the time of the login attempt.
	String attemptingUserName ;
	String attemptingPhoneNum ;
	GENDER gender ;
	boolean loginStageOnePassed = false ; //// user intered valid phone num and successfully validated by server

	// UI references.
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	
	//private LinearLayout llPhoneNumForm;
	//ScrollView svUserInfoForm ;
	EditText etPhoneNum, etSignupName;
	Button 	btnSignup, btnFBlogin, btnEnterPhoneNum;
	CheckBox chkGender ;
	
	private LoginMode mode ;
	private LOGIN_STAGE currentLoginStage ; 

	Fragment currentFrag ;
	FragmentManager fragMgr ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_login);
		init() ;
	}
	
	private void init() {
		
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		
		fragMgr = getSupportFragmentManager() ;
		currentLoginStage = LOGIN_STAGE.ENTER_PHONE_NUM;
		switchLoginStage(currentLoginStage);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		//getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

	/**
	 * try login first using facebook if success then singning up to the API Server using the 
	 * facebook Id and phone number entered in the previous stage 
	 */
	public void attempFBtLogin() {

		if(!loginStageOnePassed || attemptingPhoneNum == null || attemptingPhoneNum.isEmpty())
			return;
					
		resetInputErrors();
		
		ArrayList<String> perm1=new ArrayList<String>();
		perm1.add("public_profile");
		perm1.add("user_friends");
		perm1.add("email");
		
		//Session.openActiveSession(this, true, permissions, callback)
		Session.StatusCallback callback =  new LoginStatsCallback() ;
		Session.openActiveSession(LoginActivity.this, true, perm1, callback ) ;
		showProgress(true);
	}
	
	public void attempSignUp() {
		
		// Store values at the time of the login attempt.
		attemptingUserName = etSignupName.getText().toString();
		resetInputErrors();
		boolean isMale = chkGender.isChecked();
		this.gender = (isMale)?GENDER.MALE : GENDER.FEMALE ;

		boolean cancel = false;
		View focusView = null;
		
		if(attemptingUserName == null || attemptingUserName.length() < 3){
			etSignupName.setError(getString(R.string.error_user_name_required));
			cancel = true;
			focusView = etSignupName ;
		}
		if (cancel) {
			focusView.requestFocus();
		} else { 
			DataStore.getInstance().attemptSingnUp(attemptingPhoneNum, attemptingUserName, this.gender, null, null, apiLoginCallback);
		}
	}
	
	public void attempLogIn() {
		
		attemptingPhoneNum = etPhoneNum.getText().toString();
		resetInputErrors();
		boolean cancel = false;
		View focusView = null;

		// Check for a valid email address.
		phoeNumCheckResult numValid = RosaryApp.validatePhoneNum(attemptingPhoneNum);
		
		switch (numValid) {
		case SHORT:
			etPhoneNum.setError(getString(R.string.error_short_phone_num));
			focusView = etPhoneNum;
			cancel = true;
			break;
		case EMPTY:
			etPhoneNum.setError(getString(R.string.error_field_required));
			focusView = etPhoneNum;
			cancel = true;
			break;
		case WRONG:
			etPhoneNum.setError(getString(R.string.error_incorrect_phone_num));
			focusView = etPhoneNum;
			cancel = true;
			break;
		}
		
		if (cancel) {
			focusView.requestFocus();
		} else {
			DataStore.getInstance().attemptLogin(attemptingPhoneNum, apiLoginCallback);
		}
	}

	
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	private void resetInputErrors(){
		try {
			etPhoneNum.setError(null);
		} catch (Exception e) {}
		try {
			etSignupName.setError(null);
		} catch (Exception e) {}
	}
	
	
	private void switchLoginStage(LOGIN_STAGE newStage) {
		try {
			switch(newStage) {		
			case ENTER_PHONE_NUM:
				currentFrag = new FragPhoenNumberForm();
				fragMgr.beginTransaction()
					.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right,R.anim.slide_in_from_right,R.anim.slide_out_to_left)
					//.addToBackStack(section.name())
					.replace(R.id.login_form, currentFrag)
					.commit();
				break;
			case ENTER_USER_DETAILS:
				currentFrag = new FragUserDetailsForm() ;
				fragMgr.beginTransaction()
					.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right,R.anim.slide_in_from_right,R.anim.slide_out_to_left)
					.addToBackStack(newStage.name())
					.replace(R.id.login_form, currentFrag)
					.commit();
				break;
			}
			currentLoginStage = newStage ;
			resetInputErrors();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
	
	
	
	//// temp Callback >>>> this is just a tempCallback cuz the API is not available yet 
	DataRequestCallback apiLoginCallback = new DataRequestCallback() {
		@Override
		public void onDataReady(HashMap<String, Object> data, boolean success) {
			try {
				attemptingLogin = false ;
				showProgress(false);
				// success means data is retrived from server and does not indicate login success 
				if(currentLoginStage == LOGIN_STAGE.ENTER_PHONE_NUM){
					switchLoginStage(LOGIN_STAGE.ENTER_USER_DETAILS) ;
				}else if(currentLoginStage == LOGIN_STAGE.ENTER_USER_DETAILS){
					setResult(RESULT_OK);
					finish();
				}

			}
			catch(Exception c)
			{	
				Toast.makeText(getApplicationContext(), getString(R.string.error_signingin), Toast.LENGTH_SHORT).show();
				attemptingLogin = false ;
			}
		}
	};
	
	 
/*	DataRequestCallback apiLoginCallback = new DataRequestCallback() {
		@Override
		public void onDataReady(HashMap<String, Object> data, boolean success) {
			try {
				attemptingLogin = false ;
				showProgress(false);
				// success means data is retrived from server and does not indicate login success 
				if(success){
					AppUser user = (AppUser) data.get("user");
					boolean loginSuccess = (Boolean) data.get("loginResult");
					LoginMode loginMode = (LoginMode) data.get("loginMode");
					if(loginMode == LoginMode.SIGNIN && !loginSuccess){ // user tried to login but server didnt recognize him "he isn't registered before"
						switchLoginStage(LOGIN_STAGE.ENTER_USER_DETAILS);
					}else{
						DataStore.getInstance().setMe(user);
						setResult(RESULT_OK);
						finish();
					}
				}else{
					// optinonaly we may extract some error message from "data" in some cases
					Toast.makeText(getApplicationContext(), getString(R.string.error_connection_failed), Toast.LENGTH_SHORT).show();
				}

			}
			catch(Exception c)
			{	
				Toast.makeText(getApplicationContext(), getString(R.string.error_signingin), Toast.LENGTH_SHORT).show();
				attemptingLogin = false ;
			}
		}
	};*/
	
	
	public class  LoginStatsCallback implements Session.StatusCallback {

		@Override
		public void call(Session session, SessionState state,Exception exception) {
			Request meRequest ;
			Request friendsRequest ;
			
			if (session.isOpened()){
				
	    		meRequest = Request.newMeRequest(session, new Request.GraphUserCallback() {

	    		  // callback after Graph API response with user object
	    		  @Override
	    		  public void onCompleted(GraphUser user, Response response) {
	    			  if (user != null) {
	    				  //Toast.makeText(LoginActivity.this, "Authenticated to facebook ID "+ user.getId(), Toast.LENGTH_LONG).show();
  	    				  mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
  	    				  String token = Session.getActiveSession().getAccessToken() ;
  	    				  String name = user.getName();
  	    				  String gender = user.getProperty("gender").toString() ;
  	    				  GENDER genderType = ( gender.equalsIgnoreCase("male") )? GENDER.MALE : GENDER.FEMALE ;
  	    				  DataStore.getInstance().attemptSingnUp(attemptingPhoneNum, name, genderType, user.getId(), token, apiLoginCallback);
	    				}
	    		  }
	    		});
	    		
	    		
	    		friendsRequest = new Request( session,"/me/friends", null, HttpMethod.GET,
	    		    new Request.Callback() {
	    		        public void onCompleted(Response response) {
	    		            /* handle the result */
	    		        	//Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
	    		        	//Log.i("friends", response.toString());
	    		        }
	    		    }
	    		);
	    		
	    		meRequest.executeAsync();
	    		friendsRequest.executeAsync();
	    		
	    	}
			
		}
		
	}

	@Override
	public void onClick(View arg0) {
		int vId = arg0.getId();
		
		switch (vId) {
		case R.id.btnFBLogin:
			attempFBtLogin();
			break;
		case R.id.btnEnterPhoneNum:
			attempLogIn() ;
			break;
		case R.id.btnSignup:
			attempSignUp();
			break;

		}
	}
	
	
	public class FragPhoenNumberForm extends Fragment  {


		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
			View view = inflater.inflate(R.layout.frag_enter_num, (ViewGroup) mLoginFormView, false); 
			return view;
		}
		
		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			init();
		}
		@Override
		public void onResume() {
			super.onResume();
		}
				
		private void init(){
			LoginActivity.this.etPhoneNum = (EditText) getView().findViewById(R.id.etPhoneNum);
			LoginActivity.this.btnEnterPhoneNum = (Button) getView().findViewById(R.id.btnEnterPhoneNum);
			LoginActivity.this.btnEnterPhoneNum.setOnClickListener(LoginActivity.this);
		}	
	}
	
	public class FragUserDetailsForm extends Fragment  {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
			View view = inflater.inflate(R.layout.frag_enter_user_details, (ViewGroup) mLoginFormView, false);
			return view;
		}
		
		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			init();
		}
				
		private void init(){
			LoginActivity.this.etSignupName = (EditText) getView().findViewById(R.id.etName);
			LoginActivity.this.btnFBlogin = (Button) getView().findViewById(R.id.btnFBLogin);
			LoginActivity.this.btnSignup = (Button) getView().findViewById(R.id.btnSignup);
			LoginActivity.this.chkGender = (CheckBox) getView().findViewById(R.id.chkGender);
			
			LoginActivity.this.btnFBlogin.setOnClickListener(LoginActivity.this);
			LoginActivity.this.btnSignup.setOnClickListener(LoginActivity.this);
		}			
			
	}
	
}
