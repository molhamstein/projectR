package com.brainSocket.socialrosary.drawer;

import java.util.Arrays;
import java.util.List;

import com.brainSocket.socialrosary.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class AppActivity extends FragmentActivity{
	List<String> drawerItems;
	final int TODO_INDEX = 0;
	final int HISTORY_INDEX = 1;
	final int SETTINGS_INDEX = 2;
	final int ABOUT_INDEX = 3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawer);
		drawerItems = Arrays.asList(getResources().getStringArray(R.array.drawer_items));		
		final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		final ListView drawerListView = (ListView) findViewById(R.id.left_drawer);
		drawerListView.setAdapter(new NavDrawerAdapter(this, drawerItems));
		drawerListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {				
				Fragment selectedFragment = selectFragment(position);
				FragmentManager fragmentManager = getSupportFragmentManager();
				fragmentManager.beginTransaction()
                .replace(R.id.content_frame, selectedFragment)
                .commit();
				drawerListView.setItemChecked(position, true);				
	            drawerListView.setSelection(position);
			    setTitle(drawerItems.get(position));
			    drawerLayout.closeDrawer(drawerListView);
			}
		});
	}
	protected Fragment selectFragment(int position) {
		Fragment sFragment = null;
		switch(position){
		case TODO_INDEX:
			sFragment = new ToDoFragment();
			break;
		case HISTORY_INDEX:
			sFragment = new HistoryFragment();
			break;
		case SETTINGS_INDEX:
			sFragment = new SettingsFragment();
			break;
		case ABOUT_INDEX:
			sFragment = new AboutFragment();
			break;
		}
		
		return sFragment;
	}

}
