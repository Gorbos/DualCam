package com.cam.dualcam;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.cam.dualcam.utility.Field;
import com.cam.dualcam.utility.SetMyFBSession;
import com.cam.dualcam.widget.GifWebView;
import com.cam.dualcam.widget.LoadingDialog;

//Twitter
import com.cam.dualcam.twitter.TwitterConstant;
import com.cam.dualcam.twitter.TwitterUtil;
import com.cam.dualcam.twitter.TwitterWebview;
import com.hintdesk.core.activities.AlertMessageBox;
import com.hintdesk.core.util.OSUtil;
import com.hintdesk.core.util.StringUtil;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;








//Facebook
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;



public class SocialMediaFragment extends Fragment{
	private static final String TAG = "SocialMediaFragment";
	
	private GifWebView gifView;
	private int orientationOfPhone;
	private boolean showSpalshScreen = true;
	public boolean isLoggedIn = false;
	private String enterCamera;	
	
//	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private int checker = -1;

	private Session session;
	private SetMyFBSession sessionObject;
	private Bundle globalBundle;
	private LoadingDialog loading;
	private boolean isLoading = false;
	
	private UiLifecycleHelper uiHelper;
	
	//Widget and view declaration
	Button fbLogin;
	Button twLogin;
	Button cameraButton;
	LoginButton fbLoginButton;
	
	Bundle testB;
	
	private int TWITTER_AUTH;
	
	public static SocialMediaFragment newInstance() 
	{
		SocialMediaFragment thisFragment = new SocialMediaFragment();
	    return thisFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    uiHelper = new UiLifecycleHelper(getActivity(), ((MotherCrystal)getActivity()).callback);
	    uiHelper.onCreate(savedInstanceState);
	    Log.i(TAG, "from onCreate.");
	    
	    globalBundle = savedInstanceState;
		loading = new LoadingDialog(getActivity());
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
		
	    View view = inflater.inflate(R.layout.socialmedia_fragment, container, false);
	    Log.i(TAG, "from onCreateView.");
//	    Toast.makeText(getActivity().getApplicationContext(),
//	    		"getArguments() = "+getArguments(), 
//	    		Field.SHOWTIME).show();
		gifView = (GifWebView) view.findViewById(R.id.gif_view);
		fbLogin = (Button) view.findViewById(R.id.fbButton);
		fbLoginButton = (LoginButton)view.findViewById(R.id.fbLoginButton);
		twLogin = (Button) view.findViewById(R.id.twitterButton);
		cameraButton = (Button) view.findViewById(R.id.cameraButton);
//		simpleSession();
		orientationOfPhone = this.getResources().getConfiguration().orientation;
		
		if (orientationOfPhone == Configuration.ORIENTATION_PORTRAIT) {
			gifView.setGifAssetPath("file:///android_asset/cute.gif");
			gifView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		} else if (orientationOfPhone == Configuration.ORIENTATION_LANDSCAPE) {
			gifView.setGifAssetPath("file:///android_asset/karate.gif");
			gifView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		} 

		//get extras 
		Bundle extras = getActivity().getIntent().getExtras();
		if(extras != null)
			showSpalshScreen = extras.getBoolean("showSplashScreen");
		
		
//		Button buttonOne = (Button) view.findViewById(R.id.cameraButton);
//		buttonOne.setTextSize(36);
//		//buttonOne.setBackgroundColor(Color.TRANSPARENT);
//		buttonOne.setOnClickListener(new Button.OnClickListener() {
//		    public void onClick(View v) {
//		    	startCamera();
//		    }
//		});

		
		fbLogin.setOnClickListener(superButton);
		twLogin.setOnClickListener(superButton);
		cameraButton.setOnClickListener(superButton);
//		fbLoginButton.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Log.i(TAG, "fbLoginButton clicked");
//			}
//		});
		
		 //initControlTwitter();
	  return view;
	}

	private View.OnClickListener superButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        	if(v.getId() == R.id.fbButton){
        		
        		//Log-in to FB
//            	ultimateSession(globalBundle);
        		Session ses = Session.getActiveSession();
        		if(ses != null && ses.isOpened()){
        			Log.i(TAG, "ses is not null and is "+ses.getState());
        			((MotherCrystal)getActivity()).showFragment(MotherCrystal.CAM, false);
        		}
        		else{
        			Log.i(TAG, "ses is null");
        			
        			ses.openActiveSession(getActivity()	, 
        					SocialMediaFragment.this, 
        					true, 
        					((MotherCrystal)getActivity()).callback);
        		}
        	}
        	else if(v.getId() == R.id.twitterButton){
        		//Log-in to Twitter
        		
        		//
//        		 SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
//                 if (!sharedPreferences.getBoolean(TwitterConstant.PREFERENCE_TWITTER_IS_LOGGED_IN,false))
//	            	
//	            {
//	                new TwitterAuthenticateTask().execute();
//	            } 
//	            else 
//	            	((MotherCrystal)getActivity()).showFragment(MotherCrystal.CAM, false);
        		
        		if(!((MotherCrystal)getActivity()).myTwit.isTwitterOn()){
        			((MotherCrystal)getActivity()).myTwit.logInTwitter();
        		}
        		else
        			((MotherCrystal)getActivity()).showFragment(MotherCrystal.CAM, false);
        		
            
        	}
        	else if(v.getId() == R.id.cameraButton){
        		//Start Cam
        		((MotherCrystal)getActivity()).showFragment(MotherCrystal.CAM, false);
        	}
    

        }
    };
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	    Log.i(TAG, "from onActivityResult.");

	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	    Log.i(TAG, "from onResume.");
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
	    super.onSaveInstanceState(bundle);
	    uiHelper.onSaveInstanceState(bundle);
	    Log.i(TAG, "from onSaveInstanceState.");
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	    Log.i(TAG, "from onPause.");
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	    Log.i(TAG, "from onDestroy.");
	}
	
	@Override
	public void onDestroyView() {
	    super.onDestroyView();
	    if(uiHelper != null)
	    	uiHelper.onDestroy();
	    Log.i(TAG, "from onDestroyView.");
	}
	
	@Override
	public void onStop(){
		super.onStop();
		if(uiHelper != null)
			uiHelper.onStop();
	    Log.i(TAG, "from onStop.");
	    
//	    ((MotherCrystal)getActivity()).bundyDundy.putString("Ai", "Love");
//	    setArguments(((MotherCrystal)getActivity()).bundyDundy);
	}
	
	private void trial(){
		((MotherCrystal)getActivity()).showFragment(0, false);
	}
	
/*	Net-Work detector	*/
	@SuppressWarnings("deprecation")
	private void checkNetworkConnection() {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion <= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
		    // Do something for API 15 and below versions
			Log.i(TAG,"Do something for API 15 and below versions");
			ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
			if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || 
		            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

		         }
		         else{
		        	 
		        	 AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
		        		alertDialog.setTitle("No Connection");
				    	alertDialog.setMessage("This application requires internet connection to run, Cross check your connectivity and try again.");
				    	// Setting OK Button
				    		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				    			public void onClick(DialogInterface dialog, int which) {
		                	}
				    	});
		 
				    		// Showing Alert Message
				    	alertDialog.show();
		         }
			
			
		} else{
			Log.i(TAG,"do something for phones running an SDK above API 15");
		    // do something for phones running an SDK above API 15
			///detect if there is an internet
	        if (!OSUtil.IsNetworkAvailable(getActivity().getApplicationContext())) {
	            AlertMessageBox.Show(getActivity(), "Internet connection", "A valid internet connection can't be established", AlertMessageBox.AlertMessageBoxIcon.Info);
	            return;
	        }
	        
	        //detect if constants has a null or whitespace
	        if (StringUtil.isNullOrWhitespace(TwitterConstant.TWITTER_CONSUMER_KEY) || StringUtil.isNullOrWhitespace(TwitterConstant.TWITTER_CONSUMER_SECRET)) {
	            AlertMessageBox.Show(getActivity(), "Twitter oAuth infos", "Please set your twitter consumer key and consumer secret", AlertMessageBox.AlertMessageBoxIcon.Info);
	            return; 
	        }
	          
		}
		
	}
	
	
	class TwitterAuthenticateTask extends AsyncTask<String, String, RequestToken> {

        @Override
        protected void onPostExecute(RequestToken requestToken) {
           /* Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL()));
            startActivity(intent);*/
            
            Intent i = new Intent(getActivity(), TwitterWebview.class);
			i.putExtra("URL", requestToken.getAuthenticationURL());
			getActivity().startActivityForResult(i, TWITTER_AUTH);
			
			if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.HONEYCOMB) {
			     // only for gingerbread and newer versions
			}else{
				
			}
			
        }

        @Override
        protected RequestToken doInBackground(String... params) {
            return TwitterUtil.getInstance().getRequestToken();
        }
    }
	
}