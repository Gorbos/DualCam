package com.cam.dualcam.utility;

import com.cam.dualcam.DualCamActivity;
import com.cam.dualcam.SocialMediaActivity;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class SetMyFBSession {
	
	private String TAG = "SetMyFBSession";
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private Context sessionContext;
	private Activity parentActivity;
	private Bundle myBundle;
	public Session mySession;
	

	private String theUser = "Dunno";
	
	public SetMyFBSession(Context context, Activity activity){
		sessionContext = context;
		parentActivity = activity;
		mySession = mySession();
	}
	
	public SetMyFBSession(Context context, Activity activity, Bundle currentBundle){
		sessionContext = context;
		parentActivity = activity;
		myBundle = currentBundle;
		mySession = mySession();
	}
	
	private Session mySession(){
		Session session = Session.getActiveSession();
		
		if (session == null || session.isOpened()) {
            if (myBundle != null) {
            	Log.i(TAG,"savedInstanceState is not null");
                session = Session.restoreSession(sessionContext, null, statusCallback, myBundle);
            }
//            
            if (session == null) {
            	Log.i(TAG,"session itself is null");
            	Session.openActiveSession(parentActivity, true,statusCallback);
                session = new Session(sessionContext);
            }
            
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
            	Log.i(TAG,"session openForRead");
                session.openForRead(new Session.OpenRequest(parentActivity).setCallback(statusCallback));
            }
        }
		
		
/*        if (session == null) {
        	Session.openActiveSession(parentActivity, true,
        			statusCallback
			);

       	 
        }
        
        if (session == null || session.isOpened()) {
            if (myBundle != null) {
            	Log.i(TAG,"savedInstanceState is not null");
                session = Session.restoreSession(sessionContext, null, statusCallback, myBundle);
            }
            
            if (session == null) {
            	Log.i(TAG,"session itself is null");
            	//Session.openActiveSession(parentActivity, true,statusCallback);
                session = new Session(sessionContext);
            }
            
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
            	Log.i(TAG,"session openForRead");
                session.openForRead(new Session.OpenRequest(parentActivity).setCallback(statusCallback));
            }
        }*/
        
        //return Session.openActiveSessionFromCache(context) != null;
        return session;
	 }

	private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
        	Log.i(TAG,"It went here at SessionStatusCallback");
        	if (session.isOpened()) {
	    		
	    		// make request to the /me API
	    		Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

    			  // callback after Graph API response with user object
    			  @Override
    			  public void onCompleted(GraphUser user, Response response) {
    				  Toast.makeText(sessionContext, "Logged in as "+user.getName(),Field.SHOWTIME).show();
    				  
    			  }
    			});
	    	}
        }
    }
	
	private class LogInCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
    	 	if(session.isOpened()){
//    	 		parentActivity.finish();
//		  		Intent i = new Intent(sessionContext, DualCamActivity.class); 
//		  		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		  		i.putExtra("showSplashScreen", false);
//		  		parentActivity.startActivity(i);
    	 		
    	 		Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
//					
    			  // callback after Graph API response with user object
    			  @Override
    			  public void onCompleted(GraphUser user, Response response) {
					  if (user != null) {
						  	Toast.makeText(sessionContext, "Hello!! I'm "+user.getFirstName(),Field.SHOWTIME).show();
						  	parentActivity.finish();
					  		Intent i = new Intent(sessionContext, DualCamActivity.class); 
					  		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					  		i.putExtra("showSplashScreen", false);
					  		parentActivity.startActivity(i);
					  } 
	    				  
    			  }
    			});
    	 	}
        }
    }
	
	public Session getMySession(){
		return mySession;
	}
	
	public void storeMySession(){
		Session.saveSession(mySession, myBundle);
		boolean b = mySession == null;
		Toast.makeText(sessionContext, "Session is saved. Session = "+b+" User : "+theUser,Field.SHOWTIME).show();
		
	}
	
	public void newSession(){
		Session.openActiveSession(parentActivity, true,new LogInCallback());
		
	}
	
	public void whosLogged(){
		Request.executeMeRequestAsync(mySession, new Request.GraphUserCallback() {

			  // callback after Graph API response with user object
			  @Override
			  public void onCompleted(GraphUser user, Response response) {
				  //Toast.makeText(sessionContext, "Logged in as "+user.getName(),Field.SHOWTIME).show();
				  theUser =  user.getName();
			  }
			});
	}
 
}
