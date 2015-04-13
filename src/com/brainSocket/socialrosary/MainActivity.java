package com.brainSocket.socialrosary;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.brainSocket.socialrosary.ZicerSelectDialogForMe.selfZickerListener;
import com.brainSocket.socialrosary.data.DataStore;
import com.brainSocket.socialrosary.data.DataStore.DataRequestCallback;
import com.brainSocket.socialrosary.data.ServerResult;
import com.brainSocket.socialrosary.fragments.FragMain;
import com.brainSocket.socialrosary.model.AppContact;
import com.brainSocket.socialrosary.model.AppConversation;
import com.brainSocket.socialrosary.views.TextViewCustomFont;

public class MainActivity extends AppBaseActivity implements OnClickListener, HomeCallbacks, OnBackStackChangedListener{
	
		
	enum FRAG_TYPE {MAIN, TODO, HISTORY, SETTINGS, ABOUT} ;
	

	
	Fragment fragment = null;
 	FragmentManager fragmentManager;
 	FRAG_TYPE currentFrag ;
 	ListView lvDrawer ;
 	DrawerAdapter adapter ;
 	SearchView svSearchView  ;
 	ProgressBar spinner ;

 	LinearLayout llLoading ;
 	TextViewCustomFont tvLoadingMsg ;
 	//used for the VideoRec Fragment
 	
 	ImageView ivMenu,ivBackHome,ivLogo;
 	DrawerLayout dlDrawer ;
 	TextViewCustomFont tvFragTitle ;
 	
 	/// Temp dataHolders 	

 	
	
 	
 	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		initCustomActionBar() ;
		switchSection(FRAG_TYPE.MAIN);
		getSupportFragmentManager().addOnBackStackChangedListener(this);
	}
	
	
	private void init(){
		fragmentManager= getSupportFragmentManager();
		lvDrawer = (ListView) findViewById(R.id.lvDrawer);
		adapter = new DrawerAdapter(this, lvDrawer);
		lvDrawer.setAdapter(adapter);
		spinner = (ProgressBar) findViewById(R.id.spinner);
		dlDrawer = (DrawerLayout) findViewById(R.id.dlDrawer);
		llLoading = (LinearLayout) findViewById(R.id.llLoading);
		tvLoadingMsg = (TextViewCustomFont) findViewById(R.id.tvLoadingMsg);
	
		showProgress(false, R.string.loading_loading);

		//getActionBar().setBackgroundDrawable(new ColorDrawable(R.color.orange_app_theme));
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
		
		tvFragTitle = (TextViewCustomFont) mCustomView.findViewById(R.id.tvFragTitle) ;
		ivMenu = (ImageView) mCustomView.findViewById(R.id.ivMenu);
		ivMenu.setOnClickListener(this);
	}

	/**
	 * update content of the actionBar accourding to the current fragment
	 * @param section
	 */
	private void updateActionbar(FRAG_TYPE section) {
		switch(section) {		
/*		case PICK_TRACK:
			ivBackHome.setVisibility(View.GONE) ;
			ivCamSwitch.setVisibility(View.GONE);
			tvFragTitle.setVisibility(View.GONE);
			ivMenu.setVisibility(View.VISIBLE);
			ivLogo.setVisibility(View.VISIBLE);
			svSearchView.setVisibility(View.VISIBLE);
			ivBack.setVisibility(View.GONE);
			break;*/
		}
	}
	
	
	void switchSection(FRAG_TYPE section){
		if(currentFrag == section)
			return ;
		
		loadSection(section);	
		updateActionbar(section);
		closeDrawer();			 
	}
	

	void loadSection(FRAG_TYPE section){	
		try {
			switch(section) {		
			case MAIN:
				FragMain fragPickTrack = new FragMain();
				fragment = fragPickTrack ;
				fragPickTrack.setHomeCallback(this);
				fragmentManager.beginTransaction()
					.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right,R.anim.slide_in_from_right,R.anim.slide_out_to_left)
					//.addToBackStack(section.name())
					.replace(R.id.content_frame, fragment)
					.commit();
				currentFrag = section ;
				break;
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void toggleDrawer(){
		try{
			if(dlDrawer.isDrawerOpen(Gravity.RIGHT)){
				dlDrawer.closeDrawer(Gravity.RIGHT);
			}else{
				dlDrawer.openDrawer(Gravity.RIGHT);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void closeDrawer(){
		try{
			if(dlDrawer.isDrawerOpen(Gravity.RIGHT)){
				dlDrawer.closeDrawer(Gravity.RIGHT);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	private  void backToHome(){
		try { 
			//switchSection(FRAG_TYPE.PICK_TRACK);
			//getFragmentManager().popBackStack (FRAG_TYPE.PICK_TRACK.name(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
			
            final FragmentManager fm = getSupportFragmentManager();
            while (fm.getBackStackEntryCount() > 0) {
                fm.popBackStackImmediate();
            }
		    
			adapter.onFragmentChange(FRAG_TYPE.MAIN);
			updateActionbar(FRAG_TYPE.MAIN);
			closeDrawer();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	
	@Override
	public void onClick(View v) {
		int id = v.getId() ;
		switch (id) {
		case R.id.ivMenu:
			toggleDrawer();
			break;
		}
	}
	
	public class DrawerAdapter extends BaseAdapter implements OnItemClickListener{
		
		  private DrawerElement [] elements  = {
			  new DrawerElement(R.string.drawer_main, R.drawable.ic_friends, R.drawable.ic_friends_active),
			  new DrawerElement(R.string.drawer_settings, R.drawable.ic_friends, R.drawable.ic_friends_active) ,
			  new DrawerElement(R.string.drawer_about, R.drawable.ic_friends, R.drawable.ic_friends_active) ,
			  };
		
		  protected final Context context;
		  protected Boolean selectable ;
		  protected List<Integer> selected ; 
		  protected ListView list ;
		  int selectedItemIndex = 0 ;
		  
		  public void setSelectedItemIndex(int selectedItemIndex) {
			this.selectedItemIndex = selectedItemIndex;
			notifyDataSetChanged() ;
		}
		  
		  public DrawerAdapter(Context context, ListView view ) {
		    super();
		    this.context = context;
		    
		    list = view ;
		    list.setOnItemClickListener(this);
		  }

		  public void onFragmentChange(FRAG_TYPE fragType){
			  switch (fragType) {
			case MAIN:
				selectedItemIndex = 0 ;
				break;
			default:
				selectedItemIndex = -1 ;
				break;
			}
			  notifyDataSetChanged();
		  }
		 
		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
			  
			View rowView ;
			if(convertView == null){  
			  LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
			  rowView = inflater.inflate(R.layout.row_drawer, parent, false);
			}else{
			  rowView = convertView ;
			}
			String title = context.getString(elements[position].stringId);
			int imRes  = elements[position].iconId;
			int color = Color.WHITE;
			
			if(selectedItemIndex == position){
				imRes  = elements[position].activeIconId;
				color = getResources().getColor(R.color.app_theme2);
			}
			
		    TextView txt  = (TextView) rowView.findViewById(R.id.title);
		    ImageView icon = (ImageView) rowView.findViewById(R.id.drawable_icon);
		    
		    txt.setText(title);
		    txt.setTextColor(color);
		    icon.setImageResource(imRes);
		    
		    return rowView;
		  }
		  
		@Override
		public int getCount() {
			return elements.length;
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
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			setSelectedItemIndex(arg2);
			switch (arg2) {
			case 0:
				backToHome();
				break;
/*			case 1:
				switchSection(FRAG_TYPE.MY_DUBS);
				break;
			case 2:
				switchSection(FRAG_TYPE.ADD_TRACK);
				break;*/
			}
		
		}	  
	}
	static class DrawerElement{
		int stringId ;
		int iconId ;
		int activeIconId ;
		
		public DrawerElement(int str , int ico, int activeIcon ) {
			stringId = str ;
			iconId = ico ;
			activeIconId  = activeIcon;
		}
	}


	
	@Override
	public void onBackStackChanged() {
		int  entrys = getSupportFragmentManager().getBackStackEntryCount() ;
		
		try {
			String entryName ;
			if(entrys == 0)
				entryName = FRAG_TYPE.MAIN.name();
			else
				entryName = getSupportFragmentManager().getBackStackEntryAt(entrys-1).getName() ;
			FRAG_TYPE fragType = FRAG_TYPE.valueOf(entryName) ;
			if( fragType != null ){
				currentFrag = fragType;
				updateActionbar(fragType);
				adapter.onFragmentChange(fragType) ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setTitle(String title) {
		if(title != null && tvFragTitle != null){
			tvFragTitle.setText(title);
		}
	}

	@Override
	public void showProgress(boolean show,int msg) {
		
		if(show){
			llLoading.setVisibility(View.VISIBLE);
			if(msg <=0){
				tvLoadingMsg.setVisibility(View.GONE);
			}else{
				tvLoadingMsg.setText(msg);
				tvLoadingMsg.setVisibility(View.VISIBLE);
			}
		}else{
			llLoading.setVisibility(View.GONE);
		}
	}


	@Override
	public void showConversation(AppConversation conversation) {
		if(conversation == null)
			return ;
		Intent i = new Intent(this, ConversationActivity.class);
		if(conversation.isHasSession()){
			i.putExtra("sessionId", conversation.getSession().getIdGlobal());
		}else{
			i.putExtra("contact", conversation.getContacts().get(0));
		}
		startActivity(i);
		
	}


	@Override
	public void sendSocialMediaZeker(AppContact contact) {
		String shareBody = "Dua dua dua ";
	    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
	        sharingIntent.setType("text/plain");
	        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "ÑÓáÊ Úä ØÑíÞ ÊØÈíÞ ÓÈÚíä");
	        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
	        startActivity(Intent.createChooser(sharingIntent,"ÇÎÊÑÚä ØÑíÞ Çí ÈÑäÇãÌ ÊÑíÏ ÇÑÓÇá ÏÚæÉ ÇáÐßÑ"));
	}
	



}
