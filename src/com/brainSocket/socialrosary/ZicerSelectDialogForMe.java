package com.brainSocket.socialrosary;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;

import com.brainSocket.socialrosary.data.DataStore;

public class ZicerSelectDialogForMe extends DialogFragment implements android.view.View.OnClickListener ,OnValueChangeListener
  {  
  private static final String TAG="ZicerSelectDialogForMe";
  private static final String DialogTitle="Choose zicker...";
  private static final int MAXIMUM_NUMBER_OF_ZICKER=99;
  private static final int MINIMUM_NUMBER_OF_ZICKER=1;
  
  //element as in dialog
  AlertDialog.Builder builder ;
  View view;
  ListView lvZickersList;
  ZickerListAdapter zickerAdapter;
  
  NumberPicker npCountOfZicker;
  Button btnselfZickerAccept;
  Button btnselfZickerCansel;
  
  selfZickerListener zicker_interface;
  
  CharSequence[] sequence =DataStore.getInstance().getZickers() ;
  
  String selectedZicker;  
  int counter=0;
  

	   public ZicerSelectDialogForMe(selfZickerListener callBack) {
		   zicker_interface=callBack;
	   }


		@Override  
       public Dialog onCreateDialog(Bundle savedInstanceState) { 
			view=buildDialog();
            addViewsToDialog(view);
    		builder.setView(view);
           return builder.create();  
           
       }  
		

	private void addViewsToDialog(View view) {
		npCountOfZicker=(NumberPicker) view.findViewById(R.id.np_count_of_zicker);
		npCountOfZicker.setMaxValue(MAXIMUM_NUMBER_OF_ZICKER);
		npCountOfZicker.setMinValue(MINIMUM_NUMBER_OF_ZICKER);
		npCountOfZicker.setOnValueChangedListener(this);
		
		btnselfZickerAccept=(Button) view.findViewById(R.id.btnSelfZickerAccept);
		btnselfZickerCansel=(Button) view.findViewById(R.id.btnSelfZickerCansel);
		btnselfZickerAccept.setOnClickListener(this);
		btnselfZickerCansel.setOnClickListener(this);
		
		lvZickersList=(ListView)view.findViewById(R.id.lvZickersList);
		lvZickersList.setAdapter(zickerAdapter);
		
		}
	
	private void accept(){
		counter=npCountOfZicker.getValue();
		zicker_interface.onSelfZickerAccepted(this, selectedZicker, counter);
		
	}

	private void cansel() {
		zicker_interface.onSelfZickerCaseled(this);
		
	}  
	
	private View buildDialog() {
		builder= new AlertDialog.Builder(getActivity());  
        view=getActivity().getLayoutInflater().inflate(R.layout.dilaog_zicker_select_for_me, null);
        builder.setTitle(DialogTitle);
        builder.setSingleChoiceItems(sequence, 1, new OnClickListener() {  
             @Override  
             public void onClick(DialogInterface dialog, int which) {  
            	 selectedZicker = (String)sequence[which];  
             }  
        });	
        return view;
		}

	
	public interface selfZickerListener   {  
            public void onSelfZickerAccepted(DialogFragment dialog,String zickerselected,int NumberPickerValue);  
            public void onSelfZickerCaseled(DialogFragment dialog);  
            
       }


	@Override
	public void onClick(View v) {
		int idValue=v.getId();
		switch (idValue) {
		case R.id.btnSelfZickerAccept:
			accept();
			break;

		case R.id.btnSelfZickerCansel:
			cansel();
			break;
		}
	}


///--------------------------
	
	//ZickerListApdapter:
	
	private class ZickerListAdapter extends BaseAdapter {

		LayoutInflater inflater;

		class ViewHolder {
			TextView  tvZickerItem;
		}
		
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 4;
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
			ViewHolder holder;
			if (rowView==null){
				holder=new ViewHolder();
				rowView = inflater.inflate(R.layout.row_dilaog_zicker_list, parent, false);
				View vZickerItem = rowView.findViewById(R.id.tvzicker_item);
	            holder.tvZickerItem = (TextView) vZickerItem.findViewById(R.id.tvMe);
	            rowView.setTag(holder);
	        }
			else{
				holder = (ViewHolder) rowView.getTag() ;
			}
			
			holder.tvZickerItem.setText(sequence[position]);
			return rowView;
		}

		

		
	}
///--------------------------	


	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		// no need now to it but me
		
	}
	
	
	
      
 
  }

