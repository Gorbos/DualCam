package com.cam.dualcam;

import com.cam.dualcam.utility.Field;
import com.cam.dualcam.utility.SetMyFBSession;
import com.cam.dualcam.widget.GifWebView;
import com.cam.dualcam.widget.LoadingDialog;

//Twitter
import com.cam.dualcam.twitter.TwitterConstant;
import com.cam.dualcam.twitter.TwitterUtil;
import com.hintdesk.core.activities.AlertMessageBox;
import com.hintdesk.core.util.OSUtil;
import com.hintdesk.core.util.StringUtil;
import twitter4j.auth.RequestToken;

//Facebook
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import android.app.Activity;
import android.content.Intent;
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
	
	public static SocialMediaFragment newInstance() 
	{
		SocialMediaFragment thisFragment = new SocialMediaFragment();
	    return thisFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
	    
	    globalBundle = savedInstanceState;
		loading = new LoadingDialog(getActivity());
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
		
	    View view = inflater.inflate(R.layout.socialmedia_fragment, container, false);
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

		
		//fbLogin.setOnClicListener(superButton);
		twLogin.setOnClickListener(superButton);
		cameraButton.setOnClickListener(superButton);
		
	  return view;
	}

	private View.OnClickListener superButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        	if(v.getId() == R.id.fbButton){
        		
        		//Log-in to FB
//            	ultimateSession(globalBundle);
        	}
        	else if(v.getId() == R.id.twitterButton){
        		//Log-in to Twitter
        		
        	}
        	else if(v.getId() == R.id.cameraButton){
        		//Start Cam
        		trial();
        	}
    

        }
    };
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	    Toast.makeText(getActivity().getApplicationContext(),TAG, Field.SHOWTIME).show();
	    Log.i(TAG, "here at "+TAG);
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
	    super.onSaveInstanceState(bundle);
	    uiHelper.onSaveInstanceState(bundle);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}
	

	private void makeMeRequest(final Session session) {
	    // Make an API call to get user data and define a 
	    // new callback to handle the response.
	    Request request = Request.newMeRequest(session, 
	            new Request.GraphUserCallback() {
	        @Override
	        public void onCompleted(GraphUser user, 
	        		Response response) {
	            // If the response is successful
	            if (session == Session.getActiveSession()) {
	                if (user != null) {
	                    // Get values
	                    
	                }
	            }
	            if (response.getError() != null) {
	                // Handle errors, will do so later.
	            }
	        }
	    });
	    request.executeAsync();
	} 
	
	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
	    if (session != null && session.isOpened()) {
	        // Get the user's data.
	        makeMeRequest(session);
	    }
	}
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(final Session session, final SessionState state, final Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	public void startCamera(){
		//getActivity().finish();
    	Intent i = new Intent(); 
    	i.setClass(getActivity(), DualCamActivity.class);
    	i.putExtra("showSplashScreen", false);
    	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(i, -1);
	}
	
	private void trial(){
		//getActivity().getApplication().sh..
		//((MotherCrystal)getActivity()).doMagic("HELO");
		((MotherCrystal)getActivity()).showFragment(0, false);
		
		
	}
	
}
