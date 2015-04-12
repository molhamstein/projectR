package com.brainSocket.socialrosary;

import com.brainSocket.socialrosary.model.AppContact;
import com.brainSocket.socialrosary.model.AppConversation;


public interface HomeCallbacks {
	public void showProgress(boolean show, int msg);
	public void showToast (String msg);
	public void setTitle(String title);
	public void showConversation(AppConversation conversation);
	public void sendSocialMediaZeker (AppContact contact);
}
