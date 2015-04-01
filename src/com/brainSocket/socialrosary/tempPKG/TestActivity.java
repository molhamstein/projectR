package com.brainSocket.socialrosary.tempPKG;


import com.brainSocket.socialrosary.R;
import com.brainSocket.socialrosary.tempPKG.ZicerSelectDialogForFriend.DialogZickerPickerSelectForFriend_Interface;
import com.brainSocket.socialrosary.tempPKG.ZicerSelectDialogForMe.DialogZickerPickerSelectForMe_Interface;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class TestActivity extends FragmentActivity implements DialogZickerPickerSelectForMe_Interface ,
				DialogZickerPickerSelectForFriend_Interface {

	private static final String TAG="PickerDemoTest";
	
	Button btn_openDialogZickerPicker_friend;
	Button btn_openDialogZickerPicker_me;
	Button btn_sendZickerToFriend;
	TextView tv_countOfZicker;
	Button btn_decremantZickerCount;
	int countOfZicker=3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		initUI();
	}

	private void initUI() {
		btn_openDialogZickerPicker_friend=(Button) 
						findViewById(R.id.btn_openDialogZickerPicker_Frined);
		btn_openDialogZickerPicker_me=(Button) 
						findViewById(R.id.btn_openDialogZickerPicker_me);
		btn_openDialogZickerPicker_friend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
					Log.d(TAG, "1");
					ZicerSelectDialogForFriend dialog=new ZicerSelectDialogForFriend();
					dialog.show(getFragmentManager(), TAG);
			}
		});
		btn_openDialogZickerPicker_me.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
					Log.d(TAG, "2");
					ZicerSelectDialogForMe dialog=new ZicerSelectDialogForMe();
					dialog.show(getFragmentManager(), TAG);
					
			}
		});
		tv_countOfZicker=(TextView) findViewById(R.id.tvCountOfZicker);
		tv_countOfZicker.setText(""+countOfZicker);
		
		btn_decremantZickerCount=(Button) findViewById(R.id.btn_decZickerCountByOne);
		btn_decremantZickerCount.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (countOfZicker>0) {
					tv_countOfZicker.setText(""+(countOfZicker--));	
				}
				else {
					tv_countOfZicker.setText("Finish!!");
				}
			}
		});
		
	}



	@Override
	public void dialogZickerSelectForFriendPositiveClick(
			android.app.DialogFragment dialog, String selectedZicker,
			int NumberPickerValue) {
		Log.d("NumberPickerValue", ""+NumberPickerValue);
		Log.d("selectedZicker", selectedZicker);
	}
	
	@Override
	public void dialogZickerSelectForFriendNegativeClick(
			android.app.DialogFragment dialog) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dialogZickerSelectForMePositiveClick(
			android.app.DialogFragment dialog, String zickerselected,
			int NumberPickerValue) {
		Log.d("NumberPickerValue", ""+NumberPickerValue);
		Log.d("selectedZicker", zickerselected);
		
	}

	@Override
	public void dialogZickerSelectForMeNegativeClick(
			android.app.DialogFragment dialog) {
		// TODO Auto-generated method stub
		
	}


	


	
}
