package com.brainSocket.socialrosary.helpers;


import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

public class ScaleAndSlideAnimation extends Animation {
	private View mView;
	private float mToHeight;
	private float mFromHeight;

	private float mToWidth;
	private float mFromWidth;
	
	private float mToX;
	private float mFromX;

	public ScaleAndSlideAnimation(View v, float fromWidth, float fromHeight, float toWidth, float toHeight, int fromX, int toX) {
		mToHeight = toHeight;
		mToWidth = toWidth;
		mFromHeight = fromHeight;
		mFromWidth = fromWidth;
		mFromX = fromX ;
		mToX = toX ;
		mView = v;
		setDuration(400);
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		float height = (mToHeight - mFromHeight) * interpolatedTime + mFromHeight;
		float width = (mToWidth - mFromWidth) * interpolatedTime + mFromWidth;
		float x = (mToX - mFromX) * interpolatedTime + mFromX ;
		LayoutParams p = mView.getLayoutParams();
		p.height = (int) height;
		p.width = (int) width;
		try {
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mView.getLayoutParams() ;
			//int mar 
			//params.setMargins(, top, right, bottom);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		mView.requestLayout();
	}
}
