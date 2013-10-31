package com.cam.dualcam.widget;

import com.facebook.android.R;

import android.content.Context;
import android.widget.Button;

public class CustomFBLoginButton extends Button{

	public CustomFBLoginButton(Context context) {
		super(context);
		
		  this.setBackgroundResource(R.drawable.com_facebook_button_blue);
		  this.setCompoundDrawablesWithIntrinsicBounds(R.drawable.com_facebook_inverse_icon, 0, 0, 0);
		  this.setCompoundDrawablePadding(
	              getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_compound_drawable_padding));
		  this.setPadding(getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_left),
	              getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_top),
	              getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_right),
	              getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_bottom));
	  
	}

}
