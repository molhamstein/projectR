package com.brainSocket.socialrosary.fragments;


import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brainSocket.socialrosary.AppBaseActivity;
import com.brainSocket.socialrosary.HomeCallbacks;
import com.brainSocket.socialrosary.R;
import com.brainSocket.socialrosary.ZicerSelectDialogForMe;
import com.brainSocket.socialrosary.ZicerSelectDialogForMe.selfZickerListener;
import com.brainSocket.socialrosary.data.DataStore;
import com.brainSocket.socialrosary.data.DataStore.DataRequestCallback;
import com.brainSocket.socialrosary.data.DataStore.DataStoreUpdatListener;
import com.brainSocket.socialrosary.data.ServerResult;
import com.brainSocket.socialrosary.helpers.AnimationHelper;
import com.brainSocket.socialrosary.model.AppContact.SOCIAL_MEDIA_ACCOUNT_TYPE;
import com.brainSocket.socialrosary.model.AppConversation;

public class FragMain extends Fragment implements OnClickListener{

	private enum SEGMENT_TYPE {MAIN, CONVERSATIONS} ;
	
	HomeCallbacks homeCallback ;
	
	View rlSegment1,rlSegment2 ;
	ListView lvConversations ;
	
	TextView tvSeg1, tvSeg2, tvHadeeth ;
	
	SEGMENT_TYPE currentSegment ;
	
	ConversationsAdapter adapter ;
	ArrayList<AppConversation> converastions ;
	View btnStartSelfTask ;
	ZicerSelectDialogForMe dialog;
	
	selfZickerListener callBack=new selfZickerListener() {
		
		@Override
		public void onSelfZickerAccepted(DialogFragment dialog,
				String zickerselected, int NumberPickerValue) {
			
			DataStore.getInstance().addSelfZeker(NumberPickerValue, 1, apiAddZickerToMySelfCallback);
			//when user click button dialog will be dismissed
			dialog.dismiss();
			Toast.makeText(getActivity(), "jzakAllahAlkher", Toast.LENGTH_SHORT).show();
			//TODO Add code to open new CountingActivity!!
			
			
		}

		@Override
		public void onSelfZickerCaseled(DialogFragment dialog) {
			dialog.dismiss();
			
			//TODO toast user that an operation is canseled!!
			Toast.makeText(getActivity(), "operationCanseled!!", Toast.LENGTH_SHORT).show();
		}
		
		
	};
 	 	
	//callback for mainActivity
	DataRequestCallback apiAddZickerToMySelfCallback = new DataRequestCallback() {
				@Override
		public void onDataReady(ServerResult result, boolean success) {
			try {
				if(success){
					//TODO toast user that zicker added and refrash UI Log.d(TAG, "zickerMyselfAdded");			
				}else{
					Toast.makeText(getActivity(), getString(R.string.error_connection_failed), Toast.LENGTH_SHORT).show();
				}
			}
			catch(Exception c)
			{	
				Toast.makeText(getActivity(), getString(R.string.error_signingin), Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	
	DataStoreUpdatListener dataUpdateListener = new DataStoreUpdatListener() {
		@Override
		public void onDataStoreUpdate() {
			if(adapter != null){
				converastions = DataStore.getInstance().getConverastions() ;
				adapter.updateData(converastions);
			}	
		}
	};
	DataRequestCallback todaHadeethCallback = new DataRequestCallback() {
		@Override
		public void onDataReady(ServerResult data, boolean success) {
			if(success){
				String hadeeth = (String) data.getValue("value");
				tvHadeeth.setText(hadeeth);
			}
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_main, container, false);
	}
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init();
		DataStore.getInstance().getTodayHadeeth(todaHadeethCallback);
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
		tvHadeeth = (TextView) getView().findViewById(R.id.tvHadeeth);
		btnStartSelfTask = getView().findViewById(R.id.btnStartSelfTask);
		
		btnStartSelfTask.setOnClickListener(this);
		tvSeg1.setOnClickListener(this);
		tvSeg2.setOnClickListener(this);
		
		adapter = new ConversationsAdapter(converastions) ;
		lvConversations.setAdapter(adapter);
		lvConversations.setOnItemClickListener(adapter);
		
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
	
	public HomeCallbacks getHomeCallback() {
		if(homeCallback == null)
			homeCallback = (HomeCallbacks) (getActivity());
		return homeCallback;
	}
	
	private void updateUI(SEGMENT_TYPE newSegType){
		switch (newSegType) {
		case MAIN:
			AnimationListener enlargeBtnListner = new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {					
				}
				@Override
				public void onAnimationRepeat(Animation animation) {					
				}
				@Override
				public void onAnimationEnd(Animation animation) {
					try {
						RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnStartSelfTask.getLayoutParams() ;
						params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
						params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,0);
						btnStartSelfTask.setLayoutParams(params);
					} catch (Exception e) {}
				}
			};
			AnimationHelper.appliyPredefinedAmin(btnStartSelfTask, R.anim.enlarge_to_right,enlargeBtnListner );
			
			break;
		case CONVERSATIONS:
			AnimationListener shrinkBtnListner = new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {					
				}
				@Override
				public void onAnimationRepeat(Animation animation) {					
				}
				@Override
				public void onAnimationEnd(Animation animation) {
					try {
						RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnStartSelfTask.getLayoutParams() ;
						params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
						params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
						btnStartSelfTask.setLayoutParams(params);
					} catch (Exception e) {}
				}
			};
			AnimationHelper.appliyPredefinedAmin(btnStartSelfTask, R.anim.shrink_to_left, shrinkBtnListner);
			break;
		}
	}
	
	private void switchSegment(SEGMENT_TYPE newSegment){
		if(currentSegment == newSegment)
			return;
			
		switch (newSegment) {
		case MAIN:
			rlSegment1.setVisibility(View.VISIBLE);
			rlSegment2.setVisibility(View.GONE);
			updateUI(SEGMENT_TYPE.MAIN);
			break;
		case CONVERSATIONS:
			rlSegment1.setVisibility(View.GONE);
			rlSegment2.setVisibility(View.VISIBLE);
			updateUI(SEGMENT_TYPE.CONVERSATIONS);
			//Intent i = new Intent(getActivity(),ContactsList.class);
			//startActivity(i);;
			break;
		}
		currentSegment = newSegment ;
		
	}
	
	private void startSelfTask (){
		dialog=new ZicerSelectDialogForMe(callBack);
		dialog.show(getActivity().getSupportFragmentManager(), "");
		
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
		case R.id.btnStartSelfTask:
			startSelfTask() ;
			break ;
		}
	}
	
	
	private class ConversationsAdapter extends BaseAdapter implements OnItemClickListener/*implements Filterable*/{

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
	
			 try {
				
				AppConversation conversation = conversations.get(position); 
	     		holder.tvName.setText(conversation.getName());
				
	     		SOCIAL_MEDIA_ACCOUNT_TYPE contactType = conversation.getNetwork() ; 
	     		if(contactType == SOCIAL_MEDIA_ACCOUNT_TYPE.WHATSAP ){
	     			holder.ivNetworkIcon.setVisibility(View.VISIBLE);
	     		}else if(contactType == SOCIAL_MEDIA_ACCOUNT_TYPE.SAB3EEN ){
	     			holder.ivPhoto.setVisibility(View.VISIBLE);
	     		}else{
	     			holder.ivPhoto.setVisibility(View.GONE);
	     		}
			} catch (Exception e) {}

			return convertView;
		}
		
		
		class Holder {
			TextView tvName ;
			ImageView ivPhoto, ivNetworkIcon;
		}


		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			AppConversation conv = converastions.get(position);
			if(conv.getNetwork() == SOCIAL_MEDIA_ACCOUNT_TYPE.SAB3EEN){
				getHomeCallback().showConversation(conv);
			}else{
				getHomeCallback().sendSocialMediaZeker(null);
			}			
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
