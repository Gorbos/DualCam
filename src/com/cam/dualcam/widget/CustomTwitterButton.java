package com.cam.dualcam.widget;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.cam.dualcam.*;

import com.cam.dualcam.socialpackage.MyTwitter;
//Twitter
import com.cam.dualcam.twitter.TwitterConstant;
import com.cam.dualcam.twitter.TwitterUtil;
import com.cam.dualcam.twitter.TwitterWebview;
import com.facebook.internal.SessionTracker;
import com.hintdesk.core.activities.AlertMessageBox;
import com.hintdesk.core.util.OSUtil;
import com.hintdesk.core.util.StringUtil;

public class CustomTwitterButton extends Button{
	
	private Context theContext;
	private String buttonText;
	
	public boolean isLoggedInTwitter = false;
	
	private String TAG = "CustomTwitterButton";
	
	private MyTwitter myTwitter;

	public CustomTwitterButton(Context context, AttributeSet attrs)  {
		super(context, attrs);

		//initiate the design
		this.setGravity(Gravity.CENTER);
        this.setTextColor(getResources().getColor(R.color.com_facebook_loginview_text_color));
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.com_facebook_loginview_text_size));
        this.setTypeface(Typeface.DEFAULT_BOLD);
        this.setBackgroundResource(R.drawable.twitter_button);
        //this.setBackgroundResource(R.drawable.com_facebook_button_blue);
        //this.setCompoundDrawablesWithIntrinsicBounds(R.drawable.com_facebook_inverse_icon, 0, 0, 0);
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
		SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		if (sPrefs.getBoolean(TwitterConstant.PREFERENCE_TWITTER_IS_LOGGED_IN,false))
		{
			isLoggedInTwitter = true;
			buttonText = "Log-Out from Twitter";
		}
		else{
			isLoggedInTwitter = false;
			buttonText = "Log-In to Twitter";
		}
		
		this.setText(buttonText);
	}


}
