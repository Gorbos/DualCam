package com.cam.dualcam;

import com.cam.dualcam.utility.Field;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class MotherCrystal extends FragmentActivity {
	
	private static final String TAG = "MotherCrystal";
	
	public static final int SPLASH = 0;
	public static final int SOCIALMEDIA = 1;
	public static final int CAM = 2;
	public static final int CAMERA = 3;
	
	private boolean isResumed = false;

	private UiLifecycleHelper uiHelper;
	private MenuItem settings;
	
	private static final int CHILD_FRAGMENT = CAM +1;
	
	private Fragment[] pieces = new Fragment[CHILD_FRAGMENT];
		
	public Bundle bundyDundy;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setBundyDundy(savedInstanceState);
	    if(getBundyDundy() == null)
	    	setBundyDundy(new Bundle());
	    
	    uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
	    Log.i(TAG, "from onCreate");
	    setContentView(R.layout.mother_crystal);

//	    Session session = Session.getActiveSession();
//	    Log.i(TAG, "Session status = "+session.getState());
//	    if(session!=null && !session.isClosed())
//	    	session.close();
	    
	    FragmentManager fm = getSupportFragmentManager();
	    pieces[SPLASH] = fm.findFragmentById(R.id.splash_fragment);
	    pieces[SOCIALMEDIA] = fm.findFragmentById(R.id.socialmedia_fragment);
	    pieces[CAM] = fm.findFragmentById(R.id.cam_fragment);
	    //pieces[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);
	    FragmentTransaction transaction = fm.beginTransaction();
	    
	    for(int i = 0; i < pieces.length; i++) {
	        transaction.hide(pieces[i]);
	    }
	    
	    transaction.commit();
	    linkStart();
	    
	} 
	
	private void linkStart(){
		showFragment(SPLASH, false);
		CountDownTimer splashTime = new CountDownTimer(2000,1000) {
			
			@Override
			public void onTick(long millisUntilFinished) {

			}
			
			@Override
			public void onFinish() {
				showFragment(SOCIALMEDIA, false);
			}
		};
		
		splashTime.start();
		
	}

	public void showFragment(int fragmentIndex, boolean addToBackStack) {
		Log.i(TAG, "from showFragment("+fragmentIndex+","+addToBackStack+")");
		
		switch(fragmentIndex){
		case SPLASH:
			bundyDundy.putBoolean(Field.splash+Field.isShown, true);
			
			bundyDundy.putBoolean(Field.social+Field.isShown, false);
			bundyDundy.putBoolean(Field.cam+Field.isShown, false);
			break;
			
		case SOCIALMEDIA:
			bundyDundy.putBoolean(Field.social+Field.isShown, true);
			
			bundyDundy.putBoolean(Field.splash+Field.isShown, false);
			bundyDundy.putBoolean(Field.cam+Field.isShown, false);
			break;
			
		case CAM:
			bundyDundy.putBoolean(Field.cam+Field.isShown, true);
			
			bundyDundy.putBoolean(Field.social+Field.isShown, false);
			bundyDundy.putBoolean(Field.splash+Field.isShown, false);
			break;
			
		case CAMERA:
			bundyDundy.putBoolean(Field.camera+Field.isShown, true);
			
			bundyDundy.putBoolean(Field.social+Field.isShown, false);
			bundyDundy.putBoolean(Field.splash+Field.isShown, false);
			break;
			
		}
		
	    FragmentManager fm = getSupportFragmentManager();
	    FragmentTransaction transaction = fm.beginTransaction();
	    for (int i = 0; i < pieces.length; i++) {
	        if (i == fragmentIndex) {
	            transaction.show(pieces[i]);
	            
	        } else {
	            transaction.hide(pieces[i]);
	        }
	    }
	    if (addToBackStack) {
	        transaction.addToBackStack(null);
	    }
	    transaction.commit();
	    
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		Log.i(TAG,"from onSessionStateChange.");
	    // Only make changes if the activity is visible
		Toast.makeText(getApplicationContext(),"SessionState = "+state, Field.SHOWTIME).show();
		
	    if (isResumed) {
	        FragmentManager manager = getSupportFragmentManager();
	        // Get the number of entries in the back stack
	        int backStackSize = manager.getBackStackEntryCount();
	        // Clear the back stack
	        for (int i = 0; i < backStackSize; i++) {
	            manager.popBackStack();
	        }
	        if (state.isOpened()) {
	            // If the session state is open:
	            // Show the authenticated fragment
	            //showFragment(SOCIALMEDIA, false);
	        	if(bundyDundy.getBoolean(Field.social+Field.isShown)){
	        		showFragment(CAM, false);
	        	}
	    	    
	        } else if (state.isClosed()) {
	            // If the session state is closed:
	            // Show the login fragment
	            //showFragment(SPLASH, false);
	        	//showFragment(CAM, false);
	        	showFragment(SOCIALMEDIA, false);
	        }
	    }
	    
	    
	}
	
	public Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, 
				SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
	
	@Override
	protected void onResumeFragments() {
	    super.onResumeFragments();
	    Log.i(TAG, "from onResumeFragments.");
//	    Session session = Session.getActiveSession();
//
//	    if (session != null && session.isOpened()) {
//	        // if the session is already open,
//	        // try to show the selection fragment
//	        showFragment(SOCIALMEDIA, false);
//	    } else {
//	        // otherwise present the splash screen
//	        // and ask the person to login.
//	        showFragment(SPLASH, false);
//	    }
	}
	
	
	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	    isResumed = true;
	    Log.i(TAG, "from onResume.");
	    
//	    FragmentManager fm = getSupportFragmentManager();
//	    pieces[SPLASH] = fm.findFragmentById(R.id.splash_fragment);
//	    pieces[SOCIALMEDIA] = fm.findFragmentById(R.id.socialmedia_fragment);
//	    pieces[CAM] = fm.findFragmentById(R.id.loading_fragment);
//	    //pieces[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);
//	    FragmentTransaction transaction = fm.beginTransaction();
//	    
//	    for(int i = 0; i < pieces.length; i++) {
//	        transaction.hide(pieces[i]);
//	    }
//	    
//	    transaction.commit();
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	    isResumed = false;
	    Log.i(TAG, "from onPause.");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	    Log.i(TAG, "from onActivityResult.");
	    
//	    Session session = Session.getActiveSession();
//	    if(session != null && session.isOpened()){
//	    	showFragment(CAM, false);
//	    }
//	    else{
//	    	showFragment(SPLASH, false);
//	    }
	    	
	    
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	    Log.i(TAG, "from onDestroy.");
	}
	
	@Override
	public void onStop(){
		super.onStop();
		if(uiHelper != null)
			uiHelper.onStop();
	    Log.i(TAG, "from onStop.");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	    Log.i(TAG, "from onSaveInstanceState.");
	}
	
	public void doMagic(String magic){
		Toast.makeText(getApplicationContext(), magic, Field.SHOWTIME).show();
	}
	
	public Bundle getBundyDundy(){
		return bundyDundy;
	}
	
	public void setBundyDundy(Bundle bundydundy){
		bundyDundy = bundydundy;
	}

}
