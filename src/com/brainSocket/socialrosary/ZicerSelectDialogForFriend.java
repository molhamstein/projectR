package com.brainSocket.socialrosary;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.brainSocket.socialrosary.data.DataStore;

public class ZicerSelectDialogForFriend extends DialogFragment implements android.view.View.OnClickListener,OnItemClickListener,NumberPicker.OnValueChangeListener {
	private static final String TAG = "ZicerSelectDialogForFriend";

	private static final int MAXIMUM_NUMBER_OF_ZICKER = 99;
	private static final int MINIMUM_NUMBER_OF_ZICKER = 1;
	
	View view;
	AlertDialog.Builder builder ;
	ListView lvZickerArr;
	ZickerListAdapter adapter;
	Button btnSendZicker;
	Button btnCanselOperation;
	Button btnIncCountOfZicker;
	Button btnDecCountOfZicker;
	TextView tvCountOfZicker;
	NumberPicker nmPickerForZicker;

	CharSequence[] sequence = DataStore.getInstance().getZickers();
	DialogZickerPickerSelectForFriend_Interface zicker_interface;
	String selectedZicker;
	int countOfZickerToSend = 0;
	int indexOfSelectedZicker;

	public ZicerSelectDialogForFriend(
			DialogZickerPickerSelectForFriend_Interface callBack) {
		zicker_interface = callBack;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		builder= new AlertDialog.Builder(getActivity());
		view= getActivity().getLayoutInflater().inflate(R.layout.dilaog_zicker_select_for_friend, null);
		if(android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD){
			findNumberPicker();
		}else{
			findNumberPickerComponanets();
		}
		findList();
		findDecisionButtons();
		builder.setView(view);
		return builder.create();
	}

	void findNumberPickerComponanets(){
		btnIncCountOfZicker=(Button) view.findViewById(R.id.btn_inc_sended_count);
		btnIncCountOfZicker.setOnClickListener(this);
		btnDecCountOfZicker=(Button) view.findViewById(R.id.btn_dec_sended_zicker);
		btnDecCountOfZicker.setOnClickListener(this);
		tvCountOfZicker=(TextView) view.findViewById(R.id.tvcount_of_sended_count);
	}
	
	void findDecisionButtons(){
		btnSendZicker=(Button) view.findViewById(R.id.btn_sendZickerToFriend);
		btnSendZicker.setOnClickListener(this);
		btnCanselOperation=(Button) view.findViewById(R.id.btn_cansel_zicker);
		btnCanselOperation.setOnClickListener(this);
		
	}
	
	void findList(){
		lvZickerArr=(ListView) view.findViewById(R.id.lvSendedZickerList);
		adapter=new ZickerListAdapter(sequence, (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
		lvZickerArr.setAdapter(adapter);
		lvZickerArr.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lvZickerArr.setOnItemClickListener(this);
	}

	void findNumberPicker(){
		nmPickerForZicker = (NumberPicker) view.findViewById(R.id.np1);
		nmPickerForZicker.setMaxValue(MAXIMUM_NUMBER_OF_ZICKER);
		nmPickerForZicker.setMinValue(MINIMUM_NUMBER_OF_ZICKER);
		
	}
	
	public interface DialogZickerPickerSelectForFriend_Interface {
		
		public void dialogZickerSelectForFriensSendClick(
				DialogFragment dialog, String selectedZicker,
				int NumberPickerValue);

		public void dialogZickerSelectForFriendCansel(
				DialogFragment dialog);

	}
	
	@Override
	public void onClick(View v) {
		int viewId=v.getId();
		switch (viewId) {
		case R.id.btn_sendZickerToFriend:
				sendZicker();
			break;
		case R.id.btn_cansel_zicker:
				cansel();
			break;
		case R.id.btn_inc_sended_count:
				countOfZickerToSend++;
				tvCountOfZicker.setText(countOfZickerToSend);
			break;		
		case R.id.btn_dec_sended_zicker:
				countOfZickerToSend--;
				tvCountOfZicker.setText(countOfZickerToSend);
			break;
		}
	}

	private void cansel() {
		// TODO execute methode in callBack
		zicker_interface.dialogZickerSelectForFriendCansel(this);
	}

	private void sendZicker() {
		// TODO get NP valeu
		// get selected Zicker
		//execute methode in callBack
		zicker_interface.dialogZickerSelectForFriensSendClick(this,(String)sequence[indexOfSelectedZicker],countOfZickerToSend);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View rowView, int position,long id) {
		lvZickerArr.setItemChecked(indexOfSelectedZicker, false);
		lvZickerArr.setItemChecked(position, true);
        rowView.setBackgroundColor(Color.BLUE);
		// TODO Auto-generated method stub
		indexOfSelectedZicker=position;
	}


	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		// TODO Auto-generated method stub
		countOfZickerToSend=newVal;
		
	}


	
	///--------------------------
		//ZickerListApdapter:
		private class ZickerListAdapter extends BaseAdapter {
			LayoutInflater inflater;
			CharSequence[] elements;
			
			public ZickerListAdapter (CharSequence[] elements,LayoutInflater inflater){
			this.inflater=inflater;
			this.elements=elements;
			
			
			}
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return elements.length;
			}
		
			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return null;
			}
		
			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return 0;
			}
		
			
			@Override
			public View getView(int position, View rowView, ViewGroup parent) {
				Holder holder;
				if (rowView==null){
					holder=new Holder();
					rowView = inflater.inflate(R.layout.row_dilaog_zicker_list, parent, false);
					View vZickerItem = rowView.findViewById(R.id.tvzicker_item);
		            holder.tvZickerItem = (TextView) vZickerItem.findViewById(R.id.tvzicker_item);
		            rowView.setTag(holder);
		        }
				else{
					holder = (Holder) rowView.getTag() ;
				}
				try {
					holder.tvZickerItem.setText(elements[position]);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return rowView;
			}
			
			public class Holder{
				TextView tvZickerItem;
			}
		}
	///--------------------------	

		
		

}