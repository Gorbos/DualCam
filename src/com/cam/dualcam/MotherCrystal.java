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
	private static final int SPLASH = 0;
	private static final int SOCIALMEDIA = 1;
	private static final int LOAD = 2;
	private static final int MAIN = 3;
	
	private boolean isResumed = false;

	private UiLifecycleHelper uiHelper;
	private MenuItem settings;
	
	private static final int CHILD_FRAGMENT = LOAD +1;
	
	private Fragment[] pieces = new Fragment[CHILD_FRAGMENT];
		
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.mother_crystal);
	    //savedInstanceState.putString("trial", "value");
	    Session session = Session.getActiveSession();
	    Log.i(TAG, "Session status = "+session.getState());
	    if(session!=null && !session.isClosed())
	    	session.close();
	    
	    FragmentManager fm = getSupportFragmentManager();
	    pieces[SPLASH] = fm.findFragmentById(R.id.splash_fragment);
	    pieces[SOCIALMEDIA] = fm.findFragmentById(R.id.socialmedia_fragment);
	    pieces[LOAD] = fm.findFragmentById(R.id.loading_fragment);
	    //pieces[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);
	    FragmentTransaction transaction = fm.beginTransaction();
	    
	    for(int i = 0; i < pieces.length; i++) {
	        transaction.hide(pieces[i]);
	    }
	    
	    transaction.commit();
	    sampleAct();
	} 
	
	private void sampleAct(){
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
	    // Only make changes if the activity is visible
		Toast.makeText(getApplicationContext(),"SessionState = "+state, Field.SHOWTIME).show();
		
		if(state == SessionState.OPENED || state == SessionState.OPENED_TOKEN_UPDATED){
			//Toast.makeText(getApplicationContext(),"open camera", Field.SHOWTIME).show();
			finish();
	  		Intent i = new Intent(getApplicationContext(), DualCamActivity.class); 
	  		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	  		i.putExtra("showSplashScreen", false);
	  		startActivity(i);
		}
		
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
	            showFragment(SOCIALMEDIA, false);
	            
	            Toast.makeText(getApplicationContext(),"onSessionStateChange", Field.SHOWTIME).show();
	    	    Log.i(TAG, "here at onSessionStateChange");
	    	    
	        } else if (state.isClosed()) {
	            // If the session state is closed:
	            // Show the login fragment
	            //showFragment(SPLASH, false);
	        	//showFragment(LOAD, false);
	        }
	    }
	    
	    
	}
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, 
				SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
	
	@Override
	protected void onResumeFragments() {
	    super.onResumeFragments();
	    Session session = Session.getActiveSession();

	    if (session != null && session.isOpened()) {
	        // if the session is already open,
	        // try to show the selection fragment
	        showFragment(SOCIALMEDIA, false);
	    } else {
	        // otherwise present the splash screen
	        // and ask the person to login.
	        showFragment(SPLASH, false);
	    }
	}
	
	
	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	    isResumed = true;
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	    isResumed = false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	    
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	
	public void doMagic(String magic){
		Toast.makeText(getApplicationContext(), magic, Field.SHOWTIME).show();
	}

}
