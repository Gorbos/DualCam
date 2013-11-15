package com.cam.dualcam;


//Local Widget/Class imports
import com.cam.dualcam.widget.GifWebView;
import com.cam.dualcam.twitter.TwitterConstant;
import com.cam.dualcam.twitter.TwitterUtil;
import com.cam.dualcam.utility.Field;
import com.cam.dualcam.utility.SetMyFBSession;
import com.cam.dualcam.widget.LoadingDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.TextView;

import com.cam.dualcam.utility.*;

//Facebook imports
import com.facebook.*;
import com.facebook.model.*;
import com.facebook.widget.LoginButton;


import twitter4j.auth.RequestToken;

public class SocialMediaActivity extends Activity {
	
	private GifWebView gifView;
	private int orientationOfPhone;
	private boolean showSpalshScreen = true;
	private String enterCamera;
	public static String TAG = "DualCamActivity";
	
	public boolean isLoggedIn = false;
	
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private Session session;
	private int checker = -1;


	private String gear = "2nd";
	
	private SetMyFBSession sessionObject;
	
	private Bundle globalBundle;
	
	private LoadingDialog loading;
	private boolean isLoading = false;
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = 
	     new Session.StatusCallback() {
	     @Override
	     public void call(Session session, 
	             SessionState state, Exception exception) {
	         //onSessionStateChange(session, state, exception);
	     }
	 };
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//uiHelper = new UiLifecycleHelper(SocialMediaActivity.this, callback);
	    //uiHelper.onCreate(savedInstanceState);
		globalBundle = savedInstanceState;
		loading = new LoadingDialog(SocialMediaActivity.this);
		
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
				//gifView.setGifAssetPath("file://	/android_asset/funny.gif");
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
		
		//THE FACEBOOK MUSH-UP with the Original
		else if(gear == "2nd"){
			    setContentView(R.layout.socialmedia_gear_second);
			    gifView = (GifWebView) findViewById(R.id.gif_view);
//				simpleSession();
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


			  //Button login_button = (Button) findViewById(R.id.login_button);
			  //ImageView login_button = (ImageView) findViewById(R.id.fbBtn);
			  LinearLayout login_button = (LinearLayout) findViewById(R.id.fbButton);
			  login_button.setOnClickListener(new Button.OnClickListener() {
				    public void onClick(View v) {
				    	
				    	ultimateSession(globalBundle);
//				    	sessionTime();

				    }
				});
				
			  Button BtnTwitterLogin = (Button) findViewById(R.id.btnLoginTwitter);
			  BtnTwitterLogin.setOnClickListener(new Button.OnClickListener() {
				    public void onClick(View v) {
				    	/*//detect if there is an internet
				        if (!OSUtil.IsNetworkAvailable(getApplicationContext())) {
				            AlertMessageBox.Show(SocialMediaActivity.this, "Internet connection", "A valid internet connection can't be established", AlertMessageBox.AlertMessageBoxIcon.Info);
				            return;
				        }
				        
				        //detect if constants has a null or whitespace
				        if (StringUtil.isNullOrWhitespace(TwitterConstant.TWITTER_CONSUMER_KEY) || StringUtil.isNullOrWhitespace(TwitterConstant.TWITTER_CONSUMER_SECRET)) {
				            AlertMessageBox.Show(SocialMediaActivity.this, "Twitter oAuth infos", "Please set your twitter consumer key and consumer secret", AlertMessageBox.AlertMessageBoxIcon.Info);
				            return;
				        }*/
				    	ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
				    	if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || 
					            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
				    		
				    	}else{
				    		AlertDialog alertDialog = new AlertDialog.Builder(SocialMediaActivity.this).create();
							 
			        		alertDialog.setTitle("No Connection");
					    	alertDialog.setMessage("This application requires internet connection to run, Cross check your connectivity and try again.");
					    	//alertDialog.setIcon(R.drawable.wifilogo);
					    	// Setting OK Button
					    		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
					    			public void onClick(DialogInterface dialog, int which) {
			                	}
					    	});
			 
					    		// Showing Alert Message
					    	alertDialog.show();
				    	}
				    	
				    	if (TwitterConstant.TWITTER_CONSUMER_KEY == null || TwitterConstant.TWITTER_CONSUMER_SECRET == null) {
				    		AlertDialog alertDialog = new AlertDialog.Builder(SocialMediaActivity.this).create();
			        		alertDialog.setTitle("Null Key");
					    	alertDialog.setMessage("This application has no Twitter Key");
					    	//alertDialog.setIcon(R.drawable.wifilogo);
					    	// Setting OK Button
					    		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
					    			public void onClick(DialogInterface dialog, int which) {
			                	}
					    	});
					    		// Showing Alert Message
					    	alertDialog.show();
				    	}
				    	
				    	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			            if (!sharedPreferences.getBoolean(TwitterConstant.PREFERENCE_TWITTER_IS_LOGGED_IN,false))
			            {
			                new TwitterAuthenticateTask().execute();
			            }
			            else
			            {
			                /*Intent intent = new Intent(MainActivity.this, TwitterActivity.class);
			                startActivity(intent);*/
			            }
			            
				    }
				});
			  
		}
		
		//From the creators of FACEBOOK ...
		else if(gear == "3rd"){
			setContentView(R.layout.socialmedia_gear_third);
			gifView = (GifWebView) findViewById(R.id.gif_view);
			//setSession();
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
//	@Override
//    public void onStart() {
//        super.onStart();
//        Session.getActiveSession().addCallback(statusCallback);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        Session.getActiveSession().removeCallback(statusCallback);
//    }
//    
   
//
//    @Override
//    protected void onSaveInstanceState (Bundle outState) {
//        super.onSaveInstanceState(outState);
//        Session session = Session.getActiveSession();
//        Session.saveSession(session, outState);
//    }
	
	public boolean logCheck(){
		Session session = Session.getActiveSession();
		
		if(session == null)
			return false;


		return session.isOpened();		
	}
	
	
	 private class SessionStatusCallback implements Session.StatusCallback {
	        @Override
	        public void call(Session session, SessionState state, Exception exception) {
	        	Log.i(TAG,"It went here at SessionStatusCallback");
	        }
	    }
	 
	 private class LogInCallback implements Session.StatusCallback {
	        @Override
	        public void call(Session session, SessionState state, Exception exception) {
        	 	if(session.isOpened()){
        	 		finish();
			    	Intent i = new Intent(SocialMediaActivity.this, DualCamActivity.class); 
			    	//Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
			    	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			    	i.putExtra("session", session);
					i.putExtra("showSplashScreen", false);
					startActivity(i);
        	 	}
	        }
	    }
	 
	 private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		    // Only make changes if the activity is visible
		   
		}
	
	
	 
	 private void setSession(){
		//session = mySession();
		
		simpleSession();
	 }
	 
	 private void simpleSession(){
		 sessionObject = new SetMyFBSession(getApplicationContext(), this);
		 session = sessionObject.getMySession();
	 }
	 
	 private void ultimateSession(Bundle bundy){
		 loading.show();
		 sessionObject = new SetMyFBSession(getApplicationContext(), this, bundy);
//		 sessionObject.setClassName("SocialMediaActivity");
		 session = sessionObject.startMySession();
//		 Toast.makeText(getApplicationContext(), "It Happened. session = "+session.getState().toString(), Field.SHOWTIME).show();
//		 makeMeRequest(session);
	 }
	 
	 private void makeMeRequest(final Session session) {
		    Request request = Request.newMeRequest(session, 
		            new Request.GraphUserCallback() {

		        @Override
		        public void onCompleted(GraphUser user, Response response) {
		            // If the response is successful
		           if (user != null) {
	                    //String facebookId = user.getId();
	                    isLoggedIn = true;
	                    Toast.makeText(getApplicationContext(), "It Happened. User = "+user.getName(), Field.SHOWTIME).show();
	           		 
	               }
		           else
		        	   Toast.makeText(getApplicationContext(), "It Happened. User = null", Field.SHOWTIME).show();
	           		 
		            
//		            else
//		            	isLoggedIn = false;
//		            
//		            if (response.getError() != null) {
//		                // Handle error
//		            	isLoggedIn = false;
//		            }
		        }
		    });
		    request.executeAsync();
		} 
	 
	 
	 private void sessionTime(){
			Session session = Session.getActiveSession();
			Log.i(TAG,"sessionTime active");	
			//Log.i(TAG,"session = "+session.getState());	  
	        if (session == null) {
	        	Log.i(TAG,"session itself is null");
//		            	Session.openActiveSession(parentActivity, true,statusCallback);
	        	//session = new Session(getApplicationContext());
		        session = Session.openActiveSession(SocialMediaActivity.this, true, new Session.StatusCallback() {
					
					@Override
					public void call(Session session, SessionState state, Exception exception) {
						if(session.isOpened()){
			    	 		Log.i(TAG,"It went here at LogInCallback");
			    	 		Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
//								
			    			  // callback after Graph API response with user object
			    			  @Override
			    			  public void onCompleted(GraphUser user, Response response) {
								  if (user != null) {
									  	Toast.makeText(getApplicationContext(), "Hello!! "+user.getFirstName()+".",Field.SHOWTIME).show();
//									  	if(parentName == gifClassName){
									  		ultimateSession(globalBundle);
				    				  		finish();
				    				  		Intent i = new Intent(getApplicationContext(), DualCamActivity.class); 
				    				  		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				    				  		i.putExtra("showSplashScreen", false);
				    				  		startActivity(i);
				    				  		
//				    				  	}
								  } 
				    				  
			    			  }
			    			});
			    	 	}
					}
				});
	        }
	        else{
	        	Log.i(TAG,"sessionTime to UltimateSession");
	        	
	        	ultimateSession(globalBundle);
	        }
	        
	            
		 }
	 
	@Override
	public void onResume() {
	    super.onResume();
	    //uiHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
	    super.onSaveInstanceState(bundle);
	    //uiHelper.onSaveInstanceState(bundle);
	}
	@Override
	public void onPause() {
	    super.onPause();
	    //uiHelper.onPause();
	}
	 @Override
	    public void onDestroy(){
	    	super.onDestroy();
	    	loading.dismiss();
	    	//uiHelper.onDestroy();
	    	//session.closeAndClearTokenInformation();
	    	if(sessionObject != null && session != null){
	    		sessionObject.storeMySession();
	    		//session.closeAndClearTokenInformation();
	    	}
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        if (requestCode == 100) {
	        //uiHelper.onActivityResult(requestCode, resultCode, data);
	    }
    }
	
	 class TwitterAuthenticateTask extends AsyncTask<String, String, RequestToken> {

	        @Override
	        protected void onPostExecute(RequestToken requestToken) {
	            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL()));
	            startActivity(intent);
	        }

	        @Override
	        protected RequestToken doInBackground(String... params) {
	            return TwitterUtil.getInstance().getRequestToken();
	        }
	}
	
}
