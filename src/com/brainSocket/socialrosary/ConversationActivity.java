package com.brainSocket.socialrosary;

import java.util.ArrayList;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.brainSocket.socialrosary.ZicerSelectDialogForFriend.DialogZickerPickerSelectForFriend_Interface;
import com.brainSocket.socialrosary.data.DataStore;
import com.brainSocket.socialrosary.data.DataStore.DataRequestCallback;
import com.brainSocket.socialrosary.data.PhotoProvider;
import com.brainSocket.socialrosary.data.ServerResult;
import com.brainSocket.socialrosary.model.AppContact;
import com.brainSocket.socialrosary.model.AppConversation;
import com.brainSocket.socialrosary.model.AppConversation.CONVERSATION_TYPE;
import com.brainSocket.socialrosary.model.AppEvent;
import com.brainSocket.socialrosary.model.AppUser;
import com.brainSocket.socialrosary.views.TextViewCustomFont;

public class ConversationActivity extends AppBaseActivity implements OnClickListener{

	ListView lvEvents ;
	ConversationEventsAdapter adapter ;
	ArrayList<AppEvent> events ;
	AppConversation conversation ;
	ArrayList<AppContact> peers ;
	CONVERSATION_TYPE conversationType ;
	String sessionId ;
	
	View btnSendZiker ;  
	
	ImageView ivlogo ;
	TextView tvTitle ;
	
	
	
	
	//callback for conversation history activity
	
	DataRequestCallback eventsCallback = new DataRequestCallback() {
		@Override
		public void onDataReady(ServerResult data, boolean success) {
			if(success){
				events = (ArrayList<AppEvent>) data.getValue("events");
				adapter.updateData(events);
			}else{
				// TODO show error
				Toast.makeText(getApplicationContext(), "·«Ì„ﬂ‰ «·≈ „«„ «·√‰ «·—Ã«¡ «·„Õ«Ê·… ·«Õﬁ«",
						Toast.LENGTH_SHORT).show();
			}
		}
	};
		
	//callback for send Zicker to user dialog
	
	DialogZickerPickerSelectForFriend_Interface SendZickercallBack=new DialogZickerPickerSelectForFriend_Interface() {
		
		@Override
		public void dialogZickerSelectForFriensSendClick(DialogFragment dialog,
				String selectedZicker, int NumberPickerValue) {
				String destMobileNumber=peers.get(0).getPhoneNum();
			int contentId=0;
			DataStore.getInstance().sendZekrToUsers(destMobileNumber,contentId, NumberPickerValue, eventsCallback);
			dialog.dismiss();
			//TODO toast user
			Toast.makeText(getBaseContext(), "ZickerSended\njzakAllahAlkher", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void dialogZickerSelectForFriendCansel(DialogFragment dialog) {
				dialog.dismiss();			
				//TODO toast user
				Toast.makeText(getBaseContext(), "Canseled!!", Toast.LENGTH_SHORT).show();
		}

	
		
	};
	
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		init();
		initCustomActionBar() ;
		try{
			Bundle extras = getIntent().getExtras() ;
			if(extras.containsKey("sessionId")){
				sessionId = getIntent().getExtras().getString("sessionId");
				conversation = DataStore.getInstance().getConversationBySessionId(sessionId);
				peers = conversation.getSession().getPeers();
				conversationType = conversation.getSession().getType() ;
			}else{
				sessionId = null ;
				AppContact contact = (AppContact) extras.getParcelable("contact");
				peers = new ArrayList<AppContact>() ;
				peers.add(contact);
				conversationType = CONVERSATION_TYPE.SINGLE ;
				conversation = new AppConversation(contact);
			}
			
			tvTitle.setText( conversation.getName() );
		}catch(Exception e){}
	}
	
	
	private void init(){
		lvEvents = (ListView) findViewById(R.id.lvEvents);
		btnSendZiker = findViewById(R.id.btnSendZikr);
		adapter = new ConversationEventsAdapter(new ArrayList<AppEvent>());
		lvEvents.setAdapter(adapter);
		
		btnSendZiker.setOnClickListener(this);
	}
	
	
	private void initCustomActionBar() {
		
		ActionBar mActionBar = getSupportActionBar();
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayUseLogoEnabled(false);
		mActionBar.setDisplayHomeAsUpEnabled(false) ;
		mActionBar.setHomeAsUpIndicator(null);
		//LayoutInflater mInflater = LayoutInflater.from(this); 
		mActionBar.setCustomView(R.layout.custom_actionbar);
		mActionBar.setDisplayShowCustomEnabled(true);
		View mCustomView = mActionBar.getCustomView() ;
		mCustomView.invalidate();
		
		tvTitle = (TextViewCustomFont) mCustomView.findViewById(R.id.tvFragTitle) ;
		ImageView	ivMenu = (ImageView) mCustomView.findViewById(R.id.ivMenu);
		ivlogo = (ImageView) mCustomView.findViewById(R.id.ivLogo);
		
		ivlogo.setVisibility(View.VISIBLE);
		ivMenu.setVisibility(View.GONE);
		tvTitle.setVisibility(View.GONE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		DataStore.getInstance().requestSessionEvents(sessionId,eventsCallback );
	}
	
	private void sendZiker () {
		if(peers != null){
			ZicerSelectDialogForFriend dialog=new ZicerSelectDialogForFriend(SendZickercallBack);
			dialog.show(getSupportFragmentManager(), "Dialog to send Zicker To User");
		}
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btnSendZikr:
			sendZiker();
			break;

		default:
			break;
		}
		
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
