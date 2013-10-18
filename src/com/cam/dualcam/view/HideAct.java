package com.cam.dualcam.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.cam.dualcam.utility.Field;
import com.cam.dualcam.utility.PhoneChecker;

public class HideAct implements AnimationListener{
	
	Context context;
	Animation anim;
	//AnimParams animParams = new AnimParams();
	View view;
	ViewGroup layout;
	boolean isShown;
	int left;
	int top;
	int right;
	int bottom;
	
	int width;
	int height;
	
	int screenHeight;
	int screenWidth;
	
	public static String TAG = "HideAct";
	
	public HideAct(Context appContext) {
		context = appContext;
		screenHeight = new PhoneChecker(context).screenHeight;
		screenWidth = new PhoneChecker(context).screenWidth;
	}
	
	public void hideThisView(View toBeHidden, Integer directionToHide){
		view = toBeHidden;
		width = view.getWidth();
		height = view.getHeight();
		
		switch(directionToHide){
		
		case Field.hideToLeft:
			anim = new TranslateAnimation(0, -width, 0, 0);
			break;
		
		case Field.hideToTop:
			anim = new TranslateAnimation(0, 0, 0, -height);
			break;
			
		case Field.hideToRight:
			break;
			
		case Field.hideToBottom:
			anim = new TranslateAnimation(0, 0, 0, height);
			break;

		default:
			break;
		}
		
		doTheMagic("HIDE");
	}
	
	public void showThisView(View toBeShown, Integer directionToShow){
		view = toBeShown;		
		width = view.getWidth();
		height = view.getHeight();
		
		switch(directionToShow){
		
		case Field.showToLeft:
			anim = new TranslateAnimation(-width, 0, 0, 0);
			
			break;
		
		case Field.showToTop:
			anim = new TranslateAnimation( 0, 0,-height, 0);
			break;
			
		case Field.showToRight:
			//anim = new TranslateAnimation( right, 0, 0, 0);
			break;
			
		case Field.showToBottom:
			anim = new TranslateAnimation( 0, 0, height, 0);
			break;

		default:
			break;
		}
		
		doTheMagic("SHOW");
	}
	
	
	public void hideThisViewGroup(ViewGroup toBeHidden, Integer directionToHide){
		layout = toBeHidden;
		width = layout.getWidth();
		height = layout.getHeight();
		
		switch(directionToHide){
		
		case Field.hideToLeft:
			anim = new TranslateAnimation(0, -width, 0, 0);
			break;
		
		case Field.hideToTop:
			break;
			
		case Field.hideToRight:
			break;
			
		case Field.hideToBottom:
			anim = new TranslateAnimation(0, 0, 0, height/2);
			break;
			
		default:
			break;
		}
		
		doTheMagic("SHOW");
	}
	
	public void showThisViewGroup(ViewGroup toBeShown, Integer directionToShow){
		layout = toBeShown;
		width = layout.getWidth();
		height = layout.getHeight();
		
		switch(directionToShow){
		
		case Field.showToLeft:
			anim = new TranslateAnimation(-width, 0, 0, 0);
			break;
		
		case Field.showToTop:
			//anim = new TranslateAnimation( 0, 0,-top, 0);
			break;
			
		case Field.showToRight:
			//anim = new TranslateAnimation( right, 0, 0, 0);
			break;
			
		case Field.showToBottom:
			//anim = new TranslateAnimation( 0, 0, 0, -bottom);
			anim = new TranslateAnimation(0, 0, height/2, 0);
			break;
			
		default:
			break;
		}
		
		doTheMagic("SHOW");
	}
	
	public void doTheMagic(String act){
		
		if(view != null){
			
			if(act == "SHOW")
				view.setClickable(true);
				//view.setVisibility(ImageView.VISIBLE);


			anim.setDuration(500);
			anim.setFillAfter(true);
			view.startAnimation(anim);
			if(act == "HIDE")
				view.setClickable(false);
				//view.setVisibility(ImageView.GONE);
			view = null;
		}
		else if(layout != null){
//			if(act == "HIDE")
//				layout.setVisibility(ViewGroup.GONE);
//			else if(act == "SHOW")
//				layout.setVisibility(ViewGroup.VISIBLE);
			
			anim.setDuration(500);
			anim.setFillAfter(true);
			layout.startAnimation(anim);
			layout = null;
			
		}
		
		
	}
	
	
	public void showMagic(){
		//anim = new TranslateAnimation(0, 0, -top, 0);
		view.setVisibility(View.VISIBLE);
		anim.setDuration(500);
		anim.setFillAfter(true);
	}
	
	public void hideMagic(){
		//anim = new TranslateAnimation(0, -left, 0, 0);
		//anim = new TranslateAnimation(0, 0, 0,-top);
		view.setVisibility(View.GONE);
		anim.setDuration(500);
		anim.setFillAfter(true);
	}
	
	public void ninjaMoves(){
//		if(view.isShown())
//			hideView();
//		else if(!view.isShown())
//			showView();
//		
//		view.startAnimation(anim);
	}

	@Override
	public void onAnimationEnd(Animation arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationRepeat(Animation arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public Animation animateTheDead(){
		
		
		return anim;
		
	}

}
