package com.brainSocket.socialrosary;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.brainSocket.socialrosary.data.GCMHandler;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService 
{
    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) 
    {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        Log.d("push", "push rec");
        String messageType = gcm.getMessageType(intent);

        if(!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if(GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
//                sendNotification("Send error: " + extras.toString());
            }
            else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
//                sendNotification("Deleted messages on server: " + extras.toString());
            // If it's a regular GCM message, do some work.
            } 
            else if(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {                
                // Post notification of received message.
                sendNotification(extras);
            }
            
           Log.d("push", extras.toString());
            
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    /**
     * Passes the message extras to the GCM handler
     * @param extras
     */
    private void sendNotification(Bundle extras)
    {
    	if(RosaryApp.getAppContext() == null) {
        	RosaryApp.setAppContext(getApplicationContext());
    	}
    	/*if(RosaryApp.getAppHandler() == null) {
        	Configuration.setAppHandler(new Handler());
    	}*/
    	GCMHandler.handleReceivedPushMessage(this.getApplicationContext(), extras);
    }
}