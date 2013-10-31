package com.cam.dualcam;

import com.cam.dualcam.widget.GifWebView;
import com.cam.dualcam.utility.Field;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.content.Intent;

//Facebook imports
import com.facebook.*;
import com.facebook.model.*;


public class SocialMediaActivity extends Activity {
	
	private GifWebView gifView;
	private int orientationOfPhone;
	private boolean showSpalshScreen = true;
	private String enterCamera;
	public static String TAG = "DualCamActivity";
	
	public boolean isLoggedIn = false;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String gear = "3rd";
		setView(gear);
		
	}
	
	public void setView(String gear){

		//THE ORIGINAL!!
		if(gear == "1st"){
			setContentView(R.layout.gif_webview_layout);
			gifView = (GifWebView) findViewById(R.id.gif_view);
			
			enterCamera = getResources().getString(R.string.enter_camera_text);
			
			orientationOfPhone = this.getResources().getConfiguration().orientation;
			
			if (orientationOfPhone == Configuration.ORIENTATION_PORTRAIT) {
				gifView.setGifAssetPath("file:///android_asset/cute.gif");
				gifView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
				//gifView.setGifAssetPath("file:///android_asset/cute.gif");
				//gifView.setGifAssetPath("file:///android_asset/funny.gif");
			} else if (orientationOfPhone == Configuration.ORIENTATION_LANDSCAPE) {
				gifView.setGifAssetPath("file:///android_asset/karate.gif");
				gifView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
				//gifView.setGifAssetPath("file:///android_asset/karate.gif");
			} 

			//get extras 
			Bundle extras = getIntent().getExtras();
			showSpalshScreen = extras.getBoolean("showSplashScreen");
			
			Button buttonOne = (Button) findViewById(R.id.btnEnterCamera);
			buttonOne.setText(enterCamera);
			buttonOne.setTextSize(36);
			//buttonOne.setBackgroundColor(Color.TRANSPARENT);
			buttonOne.setOnClickListener(new Button.OnClickListener() {
			    public void onClick(View v) {
			    	finish();
			    	Intent i = new Intent(SocialMediaActivity.this, DualCamActivity.class); 
			    	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.putExtra("showSplashScreen", false);
					startActivity(i);
			    }
			});
		}
		
		//THE FACEBOOK MUSH-UP
		else if(gear == "2nd"){
			  setContentView(R.layout.socialmedia_gear_second);

			  // start Facebook Login
			  Session.openActiveSession(this, true, new Session.StatusCallback() {

			    // callback when session changes state
			    @Override
			    public void call(Session session, SessionState state, Exception exception) {
			    	
			    	Log.i(TAG, "This is = "+session.isOpened());
			    	
			    	if (session.isOpened()) {
			    		
			    		// make request to the /me API
			    		Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

		    			  // callback after Graph API response with user object
		    			  @Override
		    			  public void onCompleted(GraphUser user, Response response) {
		    				    finish();
			  			    	Intent i = new Intent(SocialMediaActivity.this, DualCamActivity.class); 
			  			    	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			  					i.putExtra("showSplashScreen", false);
			  					startActivity(i);
		    			  }
		    			});
			    	}
			    }
			  });
		}
		
		else if(gear == "3rd"){
			setContentView(R.layout.socialmedia_gear_third);
			gifView = (GifWebView) findViewById(R.id.gif_view);
			setSession();
			orientationOfPhone = this.getResources().getConfiguration().orientation;
			
			if (orientationOfPhone == Configuration.ORIENTATION_PORTRAIT) {
				gifView.setGifAssetPath("file:///android_asset/cute.gif");
				gifView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
				//gifView.setGifAssetPath("file:///android_asset/cute.gif");
				//gifView.setGifAssetPath("file:///android_asset/funny.gif");
			} else if (orientationOfPhone == Configuration.ORIENTATION_LANDSCAPE) {
				gifView.setGifAssetPath("file:///android_asset/karate.gif");
				gifView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
				//gifView.setGifAssetPath("file:///android_asset/karate.gif");
			} 

			//get extras 
			Bundle extras = getIntent().getExtras();
			if(extras != null)
				showSpalshScreen = extras.getBoolean("showSplashScreen");
			
			Button buttonOne = (Button) findViewById(R.id.btnEnterCamera);
			buttonOne.setTextSize(36);
			//buttonOne.setBackgroundColor(Color.TRANSPARENT);
			buttonOne.setOnClickListener(new Button.OnClickListener() {
			    public void onClick(View v) {
			    	finish();
			    	Intent i = new Intent(SocialMediaActivity.this, DualCamActivity.class); 
			    	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.putExtra("showSplashScreen", false);
					startActivity(i);
			    }
			});
			
			Button login_button = (Button) findViewById(R.id.login_button);
			login_button.setOnClickListener(new Button.OnClickListener() {
			    public void onClick(View v) {
			    	
					if(!isLoggedIn){
						// start Facebook Login
						  Session.openActiveSession(SocialMediaActivity.this, true, new Session.StatusCallback() {

						    // callback when session changes state
						    @Override
						    public void call(Session session, SessionState state, Exception exception) {
						    	
						    	Log.i(TAG, "This is = "+session.isOpened());
						    	
						    	if (session.isOpened()) {
						    		
						    		// make request to the /me API
						    		Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

					    			  // callback after Graph API response with user object
					    			  @Override
					    			  public void onCompleted(GraphUser user, Response response) {
					    				    finish();
						  			    	Intent i = new Intent(SocialMediaActivity.this, DualCamActivity.class); 
						  			    	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						  					i.putExtra("showSplashScreen", false);
						  					startActivity(i);
					    			  }
					    			});
						    	}
						    }
						  });
					}
					else{
						finish();
				    	Intent i = new Intent(SocialMediaActivity.this, DualCamActivity.class); 
				    	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						i.putExtra("showSplashScreen", false);
						startActivity(i);
					}
			    }
			});
			
			
			
			  
		}
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	public void setSession(){
		Session session = Session.getActiveSession();
		
		if(session != null)
			isLoggedIn = session.isOpened();
	}
	
	
	
}
