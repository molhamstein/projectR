package com.brainSocket.socialrosary;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class TutActivity extends Activity implements OnClickListener{
	
	
	RelativeLayout rlTutStage1,rlTutStage2,rlTutStage3;
	ImageView ivBtnNext ;
	int currentStage = 1 ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tut);
		init();
	}
	
	private void init (){
		rlTutStage1 = (RelativeLayout) findViewById(R.id.rlTutStage1);
		rlTutStage2 = (RelativeLayout) findViewById(R.id.rlTutStage2);
		rlTutStage3 = (RelativeLayout) findViewById(R.id.rlTutStage3);
		ivBtnNext = (ImageView) findViewById(R.id.ivBtnNext);
		
		rlTutStage1.setVisibility(View.VISIBLE);
		rlTutStage2.setVisibility(View.GONE);
		rlTutStage3.setVisibility(View.GONE);
		
		ivBtnNext.setOnClickListener(this);
	}

	private void showNext(){
		switch (currentStage) {
		case 1:
			appliyPredefinedAmin(rlTutStage1, R.anim.slide_out_to_right);
			appliyPredefinedAmin(rlTutStage2, R.anim.slide_in_from_left);
			rlTutStage2.setVisibility(View.VISIBLE);
			break;
		case 2:
			appliyPredefinedAmin(rlTutStage2, R.anim.slide_out_to_right);
			appliyPredefinedAmin(rlTutStage3, R.anim.slide_in_from_left);
			rlTutStage3.setVisibility(View.VISIBLE);
			break;
		default:
			finish();
			break;
		}
	}
	
	
	public void appliyPredefinedAmin (View view , int resAnim){
		Animation anim = AnimationUtils.loadAnimation(this, resAnim);
		view.startAnimation(anim);
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.ivBtnNext:
			showNext();
			currentStage++ ;
			break;
		}
	}

}
