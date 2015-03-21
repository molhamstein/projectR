package com.brainSocket.socialrosary.fragments;


import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.brainSocket.socialrosary.AppBaseActivity;
import com.brainSocket.socialrosary.HomeCallbacks;
import com.brainSocket.socialrosary.R;
import com.brainSocket.socialrosary.contacts.ContactsList;
import com.brainSocket.socialrosary.data.DataStore;
import com.brainSocket.socialrosary.data.DataStore.DataStoreUpdatListener;
import com.brainSocket.socialrosary.model.AppContact;
import com.brainSocket.socialrosary.model.AppContact.SOCIAL_MEDIA_ACCOUNT_TYPE;
import com.brainSocket.socialrosary.model.AppConversation;

public class FragMain extends Fragment implements OnClickListener{

	private enum SEGMENT_TYPE {MAIN, CONVERSATIONS} ;
	
	HomeCallbacks homeCallback ;
	
	View rlSegment1,rlSegment2 ;
	ListView lvConversations ;
	
	TextView tvSeg1, tvSeg2 ;
	
	SEGMENT_TYPE currentSegment ;
	
	ConversationsAdapter adapter ;
	ArrayList<AppConversation> converastions ;
	
	DataStoreUpdatListener dataUpdateListener = new DataStoreUpdatListener() {
		@Override
		public void onDataStoreUpdate() {
			if(adapter != null){
				converastions = DataStore.getInstance().getConverastions() ;
				adapter.updateData(converastions);
			}	
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_main, container, false);
	}
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init();
	}

	public void setHomeCallback(HomeCallbacks callback){
		homeCallback = callback ;
	}
	
	private void init(){
		rlSegment1 = getView().findViewById(R.id.RlSegment1);
		rlSegment2 = getView().findViewById(R.id.RlSegment2);
		tvSeg1 = (TextView) getView().findViewById(R.id.tvSegment1);
		tvSeg2 = (TextView) getView().findViewById(R.id.tvSegment2);
		lvConversations = (ListView) getView().findViewById(R.id.lvContacts);
		
		tvSeg1.setOnClickListener(this);
		tvSeg2.setOnClickListener(this);
		
		adapter = new ConversationsAdapter(converastions) ;
		
		// initial state ;
		rlSegment1.setVisibility(View.VISIBLE);
		rlSegment2.setVisibility(View.GONE);
		currentSegment = SEGMENT_TYPE.MAIN ;
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		DataStore.getInstance().addUpdateBroadcastListener(dataUpdateListener);
		DataStore.getInstance().triggerDataUpdate();

	}
	@Override
	public void onPause() {
		super.onPause();
		DataStore.getInstance().removeUpdateBroadcastListener(dataUpdateListener);
	}
	
	private void switchSegment(SEGMENT_TYPE newSegment){
		if(currentSegment == newSegment)
			return;
			
		switch (newSegment) {
		case MAIN:
			rlSegment1.setVisibility(View.VISIBLE);
			rlSegment2.setVisibility(View.GONE);
			break;
		case CONVERSATIONS:
			rlSegment1.setVisibility(View.GONE);
			rlSegment2.setVisibility(View.VISIBLE);
			Intent i = new Intent(getActivity(),ContactsList.class);
			startActivity(i);;
			break;
		}
		
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId(); 
		switch (id) {
		case R.id.tvSegment1:
			switchSegment(SEGMENT_TYPE.MAIN);
			break;
		case R.id.tvSegment2:
			switchSegment(SEGMENT_TYPE.CONVERSATIONS);
			break;
		}
	}
	
	
	private class ConversationsAdapter extends BaseAdapter /*implements Filterable*/{

		LayoutInflater inflater ;
		ArrayList<AppConversation> conversations ;
		//ArrayList<TrackInfo> fullTracksList ;
		
		
		
		
		
		public void updateData(ArrayList<AppConversation> cons){
			if(cons == null)
				this.conversations = new ArrayList<AppConversation> () ;
			else {
				this.conversations= cons ;  
			}			
			notifyDataSetChanged() ;
		}

		
		public ConversationsAdapter(ArrayList<AppConversation> cons){
			this.inflater = (LayoutInflater) getActivity().getSystemService(AppBaseActivity.LAYOUT_INFLATER_SERVICE);
			if(cons != null)
				this.conversations = cons ;
			else
				this.conversations= new ArrayList<AppConversation>(); 
		}
		
		@Override
		public int getCount() {
			return conversations.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			Holder holder ;
			 if (convertView == null) {
		            holder = new Holder();
		            convertView = inflater.inflate(R.layout.row_conversation, parent, false);
		            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
		            holder.ivPhoto= (ImageView) convertView.findViewById(R.id.ivPhoto);
		            holder.ivNetworkIcon= (ImageView) convertView.findViewById(R.id.ivNetworkIcon);
		            convertView.setTag(holder);
		        }else{
				holder = (Holder) convertView.getTag() ;
			}
			 
			AppConversation conversation = conversations.get(position); 
     		holder.tvName.setText(conversation.getName());
			
     		SOCIAL_MEDIA_ACCOUNT_TYPE contactType = conversation.getNetwork() ; 
     		if(contactType == SOCIAL_MEDIA_ACCOUNT_TYPE.WHATSAP ){
     			holder.ivNetworkIcon.setVisibility(View.VISIBLE);
     		}else if(contactType == SOCIAL_MEDIA_ACCOUNT_TYPE.VIBER ){
     			holder.ivPhoto.setVisibility(View.VISIBLE);
     			
     		}
			return convertView;
		}
		
		
		class Holder {
			TextView tvName ;
			ImageView ivPhoto, ivNetworkIcon;
		}


/*		@Override
		public Filter getFilter() {
			
			return new FilterByTrackName();
		}
		
		public class FilterByTrackName extends Filter {

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,FilterResults results) {
				
				if (results.values != null)
					updateData((ArrayList<TrackInfo>) results.values);
					//tracks = (ArrayList<TrackInfo>) results.values;
				//notifyDataSetChanged();
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {

				FilterResults results = new FilterResults();
				ArrayList<TrackInfo> originalList = DataStore.getInstance().getTrackInfos() ;
				ArrayList<TrackInfo> FilteredArrayNames = new ArrayList<TrackInfo>();
				String constraint1 = constraint.toString().toLowerCase(Locale.ENGLISH);
				
				
				for (int i = 0; i < originalList.size(); i++) {
					TrackInfo trackInf = originalList.get(i);
					String na = trackInf.getName();
					String desc = trackInf.getDescription();
					if (na.toLowerCase().contains(constraint1) || (desc!=null && desc.toLowerCase().contains(constraint1))) {
						FilteredArrayNames.add(trackInf);
					}
				}

				results.count = FilteredArrayNames.size();
				results.values = FilteredArrayNames;
				return results;
			}
		}
*/		
	} 
	
}
