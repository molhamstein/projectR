package com.brainSocket.socialrosary;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.StrictMode.VmPolicy;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.brainSocket.socialrosary.data.DataStore;
import com.brainSocket.socialrosary.data.ServerResult;
import com.brainSocket.socialrosary.data.DataStore.DataRequestCallback;
import com.brainSocket.socialrosary.data.PhotoProvider;
import com.brainSocket.socialrosary.data.DataStore.DataStoreUpdatListener;
import com.brainSocket.socialrosary.model.AppContact;
import com.brainSocket.socialrosary.model.AppContact.SOCIAL_MEDIA_ACCOUNT_TYPE;
import com.brainSocket.socialrosary.model.AppConversation;
import com.brainSocket.socialrosary.model.AppEvent;
import com.brainSocket.socialrosary.model.AppUser;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer.InitiateMatchResult;

public class ConversationActivity extends AppBaseActivity {

	ListView lvEvents ;
	ConversationEventsAdapter adapter ;
	ArrayList<AppEvent> events ;
	String sessionId ;
	
	DataRequestCallback eventsCallback = new DataRequestCallback() {
		@Override
		public void onDataReady(ServerResult data, boolean success) {
			if(success){
				events = (ArrayList<AppEvent>) data.getValue("events");
				adapter.updateData(events);
			}else{
				// TODO show error
			}
		}
	};
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		init();
		try{
			sessionId = getIntent().getExtras().getString("sessionId");
		}catch(Exception e){}
	}
	
	
	private void init(){
		lvEvents = (ListView) findViewById(R.id.lvEvents);
		adapter = new ConversationEventsAdapter(new ArrayList<AppEvent>());
		lvEvents.setAdapter(adapter);
		
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		DataStore.getInstance().requestSessionEvents(sessionId,eventsCallback );
	}
	
	
	
	private class ConversationEventsAdapter extends BaseAdapter {

		LayoutInflater inflater ;
		ArrayList<AppEvent> events ;
		//ArrayList<TrackInfo> fullTracksList ;	
		
		public void updateData(ArrayList<AppEvent> events){
			if(events == null)
				this.events = new ArrayList<AppEvent> () ;
			else {
				this.events= events ;  
			}			
			notifyDataSetChanged() ;
		}

		
		public ConversationEventsAdapter (ArrayList<AppEvent> cons){
			this.inflater = (LayoutInflater)ConversationActivity.this.getSystemService(AppBaseActivity.LAYOUT_INFLATER_SERVICE);
			if(cons != null)
				this.events = cons ;
			else
				this.events= new ArrayList<AppEvent>(); 
		}
		
		@Override
		public int getCount() {
			return events.size();
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
		            convertView = inflater.inflate(R.layout.row_conversation_events_list, parent, false);
		            holder.vMe = convertView.findViewById(R.id.vMe);
		            holder.vPeer = convertView.findViewById(R.id.vPeer);
		            holder.vinfo = convertView.findViewById(R.id.vInfo);
		            holder.tvMe = (TextView) holder.vMe.findViewById(R.id.tvMe);
		            holder.ivMe= (ImageView) holder.vMe.findViewById(R.id.ivMe);
		            holder.tvPeer= (TextView) holder.vPeer.findViewById(R.id.tvPeer);
		            holder.ivPeer= (ImageView) holder.vPeer.findViewById(R.id.ivPeer);
		            holder.tvInfo= (TextView) holder.vinfo.findViewById(R.id.tvInfo);
		            convertView.setTag(holder);
		        }else{
				holder = (Holder) convertView.getTag() ;
			}
			try{
				holder.vinfo.setVisibility(View.GONE);
				
				AppEvent event = events.get(position);
				
				if(event.isEventGeneratedByMe()){
					AppUser me = DataStore.getInstance().getMe() ;
					holder.vPeer.setVisibility(View.GONE);
					holder.vMe.setVisibility(View.VISIBLE);
					String pictureUrl = me.getPicture() ;
					if(pictureUrl != null && !pictureUrl.equals(""))
						PhotoProvider.getInstance().displayProfilePicture(me.getPicture(), holder.ivMe);
					else
						holder.ivMe.setImageResource(R.drawable.img_contact);
					String content = event.getContentValue() ;
					if(content != null)
						holder.tvMe.setText(content);
					//else
						// TODO display error
						
				}else{
					holder.vPeer.setVisibility(View.VISIBLE);
					holder.vMe.setVisibility(View.GONE);
					AppContact peer = event.getPeer() ;
					String pictureUrl =  peer.getPictureURI() ;
					if(pictureUrl != null && !pictureUrl.equals("") )
						PhotoProvider.getInstance().displayProfilePicture(peer.getPictureURI(), holder.ivPeer);
					else
						holder.ivPeer.setImageResource(R.drawable.img_contact);
					
					String content = event.getContentValue() ;
					if(content != null)
						holder.tvPeer.setText(content);
					//else
						// TODO display error
				}
			}catch(Exception e){}
			
			return convertView;
		}
		
		
		class Holder {
			View vMe,vPeer, vinfo ;
			ImageView ivPeer, ivMe;
			TextView tvMe, tvPeer, tvInfo ;
		}
	}
	
}
