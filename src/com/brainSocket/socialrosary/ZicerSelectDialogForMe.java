package com.brainSocket.socialrosary;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;

import com.brainSocket.socialrosary.data.DataStore;

public class ZicerSelectDialogForMe extends DialogFragment  
  {  
  private static final String TAG="ZicerSelectDialogForMe";
  private static final String DialogTitle="Choose zicker...";
  
  CharSequence[] sequence =DataStore.getInstance().getZickers() ;
  DialogZickerPickerSelectForMe_Interface zicker_interface;
  Button btnselfZickerCounter;
  String selectedZicker;  
  int counter=0;
  

	   public ZicerSelectDialogForMe(DialogZickerPickerSelectForMe_Interface callBack) {
		   zicker_interface=callBack;
	   }


		@Override  
       public Dialog onCreateDialog(Bundle savedInstanceState) {  
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());  
            View view=getActivity().getLayoutInflater().inflate(R.layout.dilaog_zicker_select_for_me, null);
	        btnselfZickerCounter=(Button) view.findViewById(R.id.btnSelfZickerCounter);
			btnselfZickerCounter.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {counter++;}
			});		
			builder.setView(view);
            builder.setTitle(DialogTitle);  
            builder.setSingleChoiceItems(sequence, -1, new OnClickListener() {  
                 @Override  
                 public void onClick(DialogInterface dialog, int which) {  
                	 selectedZicker = (String)sequence[which];  
                 }  
            });
            
            builder.setPositiveButton("OK", new OnClickListener() {  
                 @Override  
                 public void onClick(DialogInterface dialog, int which) {  
             		System.out.println(zicker_interface.toString());
             		zicker_interface.dialogZickerSelectForMePositiveClick(ZicerSelectDialogForMe.this, selectedZicker, counter);
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
		

	public interface DialogZickerPickerSelectForMe_Interface   {  
            public void dialogZickerSelectForMePositiveClick(DialogFragment dialog,String zickerselected,int NumberPickerValue);  
            public void dialogZickerSelectForMeNegativeClick(DialogFragment dialog);  
       }  
      
 
  }  