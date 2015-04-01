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
import android.widget.NumberPicker;

public class ZicerSelectDialogForMe extends DialogFragment  
  {  
  
  private static final int MAXIMUM_NUMBER_OF_ZICKER=20;
  private static final int MINIMUM_NUMBER_OF_ZICKER=1;
	
  CharSequence[] sequence =new ZickerArr().getZickers();
  DialogZickerPickerSelectForMe_Interface zicker_interface;
  NumberPicker numberPicker;
  String selectedZicker;  
  int counter=0;
  int contentId = 0;
  
   @Override  
       public Dialog onCreateDialog(Bundle savedInstanceState) {  
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());  
            View view=getActivity().getLayoutInflater().inflate(R.layout.dilaog_zicker_select_for_me, null);
			numberPicker=(NumberPicker) view.findViewById(R.id.np3);
			numberPicker.setMaxValue(MAXIMUM_NUMBER_OF_ZICKER);
			numberPicker.setMinValue(MINIMUM_NUMBER_OF_ZICKER);
			numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
				@Override
				public void onValueChange(NumberPicker picker, int oldValue, int newValue) {
						counter=newValue;
						Log.d("ValueChange", "oldValue "+oldValue+" "+"newValue "+counter);
				}
			});
            builder.setView(view);
            builder.setTitle("Choose zicker...");  
            builder.setSingleChoiceItems(sequence, -1, new OnClickListener() {  
                 @Override  
                 public void onClick(DialogInterface dialog, int which) {  
                	 contentId=which;
                	 selectedZicker = (String)sequence[which];  
                 }  
            });
            
            builder.setPositiveButton("OK", new OnClickListener() {  
                 @Override  
                 public void onClick(DialogInterface dialog, int which) {  
                 zicker_interface.dialogZickerSelectForMePositiveClick(ZicerSelectDialogForMe.this,selectedZicker,counter);
                 Log.d("setPositiveButton", "true");
                 //TODO send to Server here!
                 ServerAccess sendToServer=ServerAccess.getInstance();
                 String accessToken="accessToken";
                 int contentId = 0;
                 sendToServer.addSelfZeker(accessToken, counter, contentId);
                 }  
            });
            
           builder.setNegativeButton("CANCEL", new OnClickListener() {  
                 @Override  
                 public void onClick(DialogInterface dialog, int which) {  
                      zicker_interface.dialogZickerSelectForMeNegativeClick(ZicerSelectDialogForMe.this);
                 }  
            });
            
           return builder.create();  
       }  
       public interface DialogZickerPickerSelectForMe_Interface  
       {  
            public void dialogZickerSelectForMePositiveClick(DialogFragment dialog,String zickerselected,int NumberPickerValue);  
            public void dialogZickerSelectForMeNegativeClick(DialogFragment dialog);  
       }  
      
  
      
       @Override  
            public void onAttach(Activity activity) {  
                 super.onAttach(activity);  
                 try  
                 {  
                 zicker_interface = (DialogZickerPickerSelectForMe_Interface) activity;  
                 }  
                 catch(ClassCastException e)  
                 {  
                      throw new ClassCastException(activity.toString() + "must override methods..");  
                 }  
            }  
  }  