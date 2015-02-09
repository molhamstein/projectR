package com.brainSocket.socialrosary.data;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.brainSocket.socialrosary.R;
import com.brainSocket.socialrosary.RosaryApp;


public class PromptManager {
	
	private Context activityContext ;
	private static ProgressDialog progressDialog;
	private static PromptManager instanse ;
	
	public PromptManager() {
		activityContext = RosaryApp.getCurrentAcivity() ;
	}
	
	
	public static PromptManager getInstanse(){
		if(instanse == null)
			instanse = new PromptManager() ;
		return instanse ;
	}
	
	public void setContext (Context con){
		activityContext = con ;
	}
	
	public void showProgressDiag(String msg) {
		progressDialog = ProgressDialog.show(activityContext, activityContext.getString(R.string.pleas_wait), msg);
	}
	
	public void dismissProgressDiag(String msg) {
		if (progressDialog != null)
			progressDialog.dismiss();
	}
	
	public void PopDialog( String msg , String title, View.OnClickListener okClickListener) {

		if(activityContext == null)
			return ;
		
		Dialog alert = getBaseDialogWithTitle() ;
		//alert.setTitle(title).setMessage(msg);
		//alert.setPositiveButtonOnClick(okClickListener);
		showDialog(alert);
	}
	
	public void promptForIntrnet() {
		if(activityContext == null)
			return ;
		
		PopDialog( activityContext.getString(R.string.error_intrnet), activityContext.getString(R.string.error_intrnet_title),null) ;
	}
	
	/**
	 * this Method is useful in-case we decided to create a custom Toast
	 * @param msg
	 * @param Duration
	 */
	public void showToast(String msg , int Duration){
		if(activityContext == null)
			return;
		Toast.makeText(activityContext,msg,Duration).show();
	}

	/**
	 * this method will be used to veryfy if we can show the dialog with the current context 
	 * and to prevent app crashing if any thing goes wrong while showing the Dialog
	 * @param dialog
	 */
	private void showDialog(Dialog dialog){
		if(activityContext == null)
			return ; 
		try {
			dialog.show() ;
		} catch (Exception e) {}
	}
	
	/**
	 * creates a unified-style Dialogs for the app
	 * @return
	 */
	private Dialog getBaseDialogWithTitle(){
		if(activityContext == null)
			return null ;
		Dialog diag = new Dialog(activityContext,android.support.v7.appcompat.R.style.Theme_AppCompat ) ;
		diag.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		
		return diag ;
	}
	private Dialog getBaseDialogWithoutTitle(){
		if(activityContext == null)
			return null ;
		
		Dialog diag = new Dialog(activityContext,android.support.v7.appcompat.R.style.Theme_AppCompat ) ;
		diag.requestWindowFeature(Window.FEATURE_NO_TITLE);
		diag.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		
		return diag ;
	}
	
}
