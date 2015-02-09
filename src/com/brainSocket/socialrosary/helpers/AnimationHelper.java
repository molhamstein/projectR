package com.brainSocket.socialrosary.helpers;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;

import com.brainSocket.socialrosary.RosaryApp;

public class AnimationHelper {

	public static void popup(View view) {

		AnimationSet animation = new AnimationSet(true);
		float pivotX = view.getWidth() / 2;
		float pivotY = view.getHeight() / 2;
		ScaleAnimation anim = new ScaleAnimation(0f, 1.1f, 0f, 1.1f, pivotX,
				pivotY);
		anim.setInterpolator(new LinearInterpolator());
		anim.setDuration(300);
		animation.addAnimation(anim);

		anim = new ScaleAnimation(1.1f, 1f, 1.1f, 1f, pivotX, pivotY);
		anim.setInterpolator(new LinearInterpolator());
		anim.setDuration(100);
		anim.setStartOffset(300);
		animation.addAnimation(anim);

		view.startAnimation(animation);
	}

	
	
	public static void appliyPredefinedAmin (View view , int resAnim){
		Animation anim = AnimationUtils.loadAnimation(RosaryApp.getAppContext(), resAnim);
		view.startAnimation(anim);
	}
	

}
