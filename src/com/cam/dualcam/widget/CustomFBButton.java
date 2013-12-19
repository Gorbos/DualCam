package com.cam.dualcam.widget;


import com.cam.dualcam.MotherCrystal;
import com.cam.dualcam.R;
import com.cam.dualcam.SocialMediaFragment;
import com.cam.dualcam.socialpackage.MyTwitter;
import com.cam.dualcam.twitter.TwitterConstant;
import com.facebook.Session;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;

public class CustomFBButton extends Button{
	
	private Context theContext;
	private String buttonText;
	
	public boolean isLoggedInFB = false;
	
	private String TAG = "CustomFBButton";

	public CustomFBButton(Context context, AttributeSet attrs)  {
		super(context, attrs);
		//initiate the design
		this.setGravity(Gravity.CENTER);
        this.setTextColor(getResources().getColor(R.color.com_facebook_loginview_text_color));
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.com_facebook_loginview_text_size));
        this.setTypeface(Typeface.DEFAULT_BOLD);
        //this.setBackgroundResource(R.drawable.twitter_button);
        this.setBackgroundResource(R.drawable.com_facebook_button_blue);
        this.setCompoundDrawablesWithIntrinsicBounds(R.drawable.com_facebook_inverse_icon, 0, 0, 0);
        this.setCompoundDrawablePadding(
                getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_compound_drawable_padding));
        this.setPadding(getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_left),
                getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_top),
                getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_right),
                getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_bottom));
        finishInit();
	}
	
	private void finishInit() {
	    //setOnClickListener(new TwitterLoginClickListener());
	    setButtonText();
//        if (!isInEditMode()) {
//            sessionTracker = new SessionTracker(getContext(), new LoginButtonCallback(), null, false);
//            fetchUserInfo();
//        }
    }
	
	public void setButtonText(){
		setLogValues();
	}
	
	private void setLogValues(){
		Session ses = Session.getActiveSession();
		if(ses != null && ses.isOpened()){
			isLoggedInFB = true;
			buttonText = "Log-Out from Facebook";
		}
		else{
			isLoggedInFB = false;
			buttonText = "Log-In to Facebook";
		}
		
		this.setText(buttonText);
	}
	
}
