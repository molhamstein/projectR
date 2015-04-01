package com.brainSocket.socialrosary.tempPKG;

import com.brainSocket.socialrosary.R;
import com.brainSocket.socialrosary.data.ServerAccess;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

public class ZicerSelectDialogForFriend extends DialogFragment  
  {  
     
  private static final int MAXIMUM_NUMBER_OF_ZICKER=20;
  private static final int MINIMUM_NUMBER_OF_ZICKER=1;
  
  Button btn_sendZickerToFriend;
  NumberPicker nm_pickerForZicker;
  
  //TODO: we can modifay them from ZickerArr Class
  ZickerArr arrayOfZickerToSelectFromIt=new ZickerArr();
  CharSequence[] sequence =arrayOfZickerToSelectFromIt.getZickers();
  DialogZickerPickerSelectForFriend_Interface zicker_interface;
  String selectedZicker;
  int countOfZickerToSend=0;
  int indexOfSelectedZicker;
  //TODO add code to retrive it from past activity
  String destMobileNumber="0994591395";
  
   @Override  
       public Dialog onCreateDialog(Bundle savedInstanceState) {  
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());  
            View view = getActivity().getLayoutInflater().
            			inflate(R.layout.dilaog_zicker_select_for_friend, null);    
		    btn_sendZickerToFriend=(Button) view.findViewById(R.id.btn_sendZickerToFriend);
		    btn_sendZickerToFriend.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					String accessToken="accessToken";
					Log.d("btn_sendZickerToFriend", "Clicked!");
					//TODO add sending code here!!
					ServerAccess sendToServer=ServerAccess.getInstance();
								//TODO Ask molham about maining of them!!
					int contentId = indexOfSelectedZicker;;
					int goal = countOfZickerToSend;
					sendToServer.sendZekrToUsers(accessToken, destMobileNumber, contentId, goal);
				}
			});
		    nm_pickerForZicker=(NumberPicker) view.findViewById(R.id.np1);
		    nm_pickerForZicker.setMaxValue(MAXIMUM_NUMBER_OF_ZICKER);
		    nm_pickerForZicker.setMinValue(MINIMUM_NUMBER_OF_ZICKER);
		    nm_pickerForZicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
				
				@Override
				public void onValueChange(NumberPicker p, int oldVal, int newVal) {
					Log.d("ValueChange", ""+oldVal);
					countOfZickerToSend=newVal;
					Log.d("ValueChange", ""+countOfZickerToSend);
				}
			});
		    
		    builder.setView(view);
            
            builder.setTitle("Choose zicker...");  
            builder.setSingleChoiceItems(sequence, -1, new OnClickListener() {  
                 

				@Override  
                 public void onClick(DialogInterface dialog, int which) {  
                     indexOfSelectedZicker=which; 
                	 selectedZicker = (String)sequence[which];  
                 }  
            });  
            builder.setPositiveButton("OK", new OnClickListener() {  
                 @Override  
                 public void onClick(DialogInterface dialog, int which) {  
                 zicker_interface.dialogZickerSelectForFriendPositiveClick
                 					(ZicerSelectDialogForFriend.this,selectedZicker, countOfZickerToSend);  
                 }  
            });  
           builder.setNegativeButton("CANCEL", new OnClickListener() {  
                 @Override  
                 public void onClick(DialogInterface dialog, int which) {  
                 zicker_interface.dialogZickerSelectForFriendNegativeClick
                      				(ZicerSelectDialogForFriend.this);  
                 }  
            });
           	
           return builder.create();  
       }  
   
       public interface DialogZickerPickerSelectForFriend_Interface  
       {  
            public void dialogZickerSelectForFriendPositiveClick(DialogFragment dialog,
            				String selectedZicker,int NumberPickerValue);  
            public void dialogZickerSelectForFriendNegativeClick(DialogFragment dialog);
			 
       }  
      
	@SuppressLint("NewApi")
       @Override  
            public void onAttach(Activity activity) {  
                 // TODO Auto-generated method stub  
                 super.onAttach(activity);  
                 try  
                 {  
                 zicker_interface = (DialogZickerPickerSelectForFriend_Interface) activity;  
                 }  
                 catch(ClassCastException e)  
                 {  
                      throw new ClassCastException(activity.toString() + "must override methods..");  
                 }  
            }  
  }  