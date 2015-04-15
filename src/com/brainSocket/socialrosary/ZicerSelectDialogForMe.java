package com.brainSocket.socialrosary;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;

import com.brainSocket.socialrosary.data.DataStore;
import com.brainSocket.socialrosary.model.AppZiker;

public class ZicerSelectDialogForMe extends DialogFragment implements android.view.View.OnClickListener ,OnValueChangeListener,OnItemClickListener
  {  
  private static final int MAXIMUM_NUMBER_OF_ZICKER=99;
  private static final int MINIMUM_NUMBER_OF_ZICKER=1;
  
  //element as in dialog
  AlertDialog.Builder builder;
  View view;
  ListView lvZickersList;
  ZickerListAdapter zickerAdapter;
  
  NumberPicker npCountOfZicker;
  Button btnselfZickerAccept;
  Button btnselfZickerCansel;
  
  Button btnIncCount;
  Button btnDecCount;
  TextView tvcount_of_zicker;
  
  selfZickerListener zicker_interface;
  //TOSO replace it by Zicker[]
  CharSequence[] sequence =DataStore.getInstance().getZickers() ;
  
  String selectedZicker;  
  int counter=0;
  

	   public ZicerSelectDialogForMe(selfZickerListener callBack) {
		   zicker_interface=callBack;
	   }


		@Override  
       public Dialog onCreateDialog(Bundle savedInstanceState) { 
         builder= new AlertDialog.Builder(getActivity());
          init();
          return builder.create();
       }  
		

	private void init() {
	        view=getActivity().getLayoutInflater().inflate(R.layout.dilaog_create_self_task, null);
			if ((android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD)) {
				findNumberPicer();
			}else{
				findNumberPicerComponanets();
			}
		
		addViewsToDialog();
		builder.setView(view);
	}



	private void findNumberPicer() {
		npCountOfZicker=(NumberPicker) view.findViewById(R.id.np_count_of_zicker);
		npCountOfZicker.setMaxValue(MAXIMUM_NUMBER_OF_ZICKER);
		npCountOfZicker.setMinValue(MINIMUM_NUMBER_OF_ZICKER);
		npCountOfZicker.setOnValueChangedListener(this);
		npCountOfZicker.setClickable(false);
		
	}
		

	private void findNumberPicerComponanets () {
		//add buttons&tv&listener;
		btnIncCount=(Button) view.findViewById(R.id.btnincnp);
		btnDecCount=(Button) view.findViewById(R.id.btndecnp);
		tvcount_of_zicker=(TextView) view.findViewById(R.id.tvcount_of_zicker);
		tvcount_of_zicker.setText("0");
		btnDecCount.setOnClickListener(this);
		btnIncCount.setOnClickListener(this);
		
	}
	
	private void addViewsToDialog() {
		btnselfZickerAccept=(Button) view.findViewById(R.id.btnSelfZickerAccept);
		btnselfZickerCansel=(Button) view.findViewById(R.id.btnSelfZickerCansel);
		btnselfZickerAccept.setOnClickListener(this);
		btnselfZickerCansel.setOnClickListener(this);
		
		lvZickersList=(ListView)view.findViewById(R.id.lvZickersList);
		zickerAdapter = new ZickerListAdapter(sequence,
											(LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)) ;
		lvZickersList.setAdapter(zickerAdapter);
		lvZickersList.setOnItemClickListener(this);
		
	}
	
	private void accept(){
		counter=npCountOfZicker.getValue();
		zicker_interface.onSelfZickerAccepted(this, selectedZicker, counter);
		
	}

	private void cansel() {
		zicker_interface.onSelfZickerCaseled(this);
		
	}  
	
	

	private void incCounter() {
		counter++;
		tvcount_of_zicker.setText(counter);
		}


	private void decCounter() {
		counter--;
		tvcount_of_zicker.setText(counter);
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
		case R.id.btnincnp:
			incCounter();
			break;
		case R.id.btndecnp:
			decCounter();
			break;
		}
	}







///--------------------------
	
	//ZickerListApdapter:
	
	private class ZickerListAdapter extends BaseAdapter {

		LayoutInflater inflater;
		AppZiker[] eleZikers;
		CharSequence[] elements ;
		
		public ZickerListAdapter (CharSequence[] elements,LayoutInflater inflater){
			this.elements = elements ;
			this.inflater=inflater;
		}
		
		class ViewHolder {
			TextView  tvZickerItem;
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
			ViewHolder holder;
			if (rowView==null){
				holder=new ViewHolder();
				rowView = inflater.inflate(R.layout.row_dilaog_zicker_list, parent, false);
				View vZickerItem = rowView.findViewById(R.id.tvzicker_item);
	            holder.tvZickerItem = (TextView) vZickerItem.findViewById(R.id.tvzicker_item);
	            rowView.setTag(holder);
	        }
			else{
				holder = (ViewHolder) rowView.getTag() ;
			}
			try {
				holder.tvZickerItem.setText(elements[position]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return rowView;
		}

		

		
	}
///--------------------------	


	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		// no need now to it but me
		
	}



@Override
public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	// TODO Auto-generated method stub
	
}
	
	
	
      
 
  }

