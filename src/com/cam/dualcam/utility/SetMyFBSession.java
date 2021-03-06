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
	private Session.StatusCallback loginCallback = new LogInCallback();
	private Session.StatusCallback restoreCallback = new RestoreStatusCallback();
	private Context sessionContext;
	private Activity parentActivity;
	private Bundle myBundle;
	public Session mySession;
	

	private String theUser = "Dunno";
	private String parentName = null;
	private String gifClassName = "com.cam.dualcam.SocialMediaActivity";
	
	public SetMyFBSession(Context context, Activity activity){
		sessionContext = context;
		parentActivity = activity;
//		mySession = mySession();
		parentName = activity.getClass().getName().toString();
//		Log.i(TAG +" : "+parentName, parentName);
//		boolean b = gifClassName == parentName;
//		Log.i(TAG +" : "+parentName,parentName+" & "+gifClassName+" is = "+ b);
	}
	
	public SetMyFBSession(Context context, Activity activity, Bundle currentBundle){
		sessionContext = context;
		parentActivity = activity;
		myBundle = currentBundle;
//		mySession = mySession();
		parentName = activity.getClass().getName().toString();
//		Log.i(TAG, "Parent class = "+parentName);
//		boolean b = gifClassName == parentName;
//		Log.i(TAG +" : "+parentName,parentName+" & "+gifClassName+" is = "+ b);
	}
	
	private Session mySession(){
		Session session = Session.getActiveSession();
		
		if (session == null || session.isOpened()) {
			
			//if restorable
            if (myBundle != null && Session.restoreSession(sessionContext, null, restoreCallback, myBundle) != null) {
            	Log.i(TAG +" : "+parentName,"savedInstanceState is not null");
                session = Session.restoreSession(sessionContext, null, restoreCallback, myBundle);
            }
            
            //if not yet logged in            
            if (session == null) {
            	Log.i(TAG +" : "+parentName,"session itself is null");
//            	Session.openActiveSession(parentActivity, true,statusCallback);
//            	session = new Session(sessionContext);
            	session = Session.openActiveSession(parentActivity, true, loginCallback);
            }
            
            //finally, load the session
            Session.setActiveSession(session);
            Log.i(TAG +" : "+parentName,"SessionState = "+session.getState());
            
            //session has a cache but not yet opened
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
            	Log.i(TAG +" : "+parentName,"session openForRead CREATED_TOKEN_LOADED");
                session.openForRead(new Session.OpenRequest(parentActivity).setCallback(statusCallback));
            }
            
            //session doesn't have a cache and not yet opened
            else if(session.getState().equals(SessionState.CREATED)){
            	Log.i(TAG +" : "+parentName,"session openForRead CREATED");
            	session.openForRead(new Session.OpenRequest(parentActivity).setCallback(loginCallback));
            }
            
            //session was opened successfully, party starter :D
            else if(session.getState().equals(SessionState.OPENED)){
            	Log.i(TAG +" : "+parentName,"session is OPENED");
            	if(parentName == gifClassName)
            		partyStarter();
//            	parentActivity.finish();
//		  		Intent i = new Intent(sessionContext, DualCamActivity.class); 
//		  		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		  		i.putExtra("showSplashScreen", false);
//		  		parentActivity.startActivity(i);
            }
            
        }
		
		
/*        if (session == null) {
        	Session.openActiveSession(parentActivity, true,
        			statusCallback
			);

       	 
        }
        
        if (session == null || session.isOpened()) {
            if (myBundle != null) {
            	Log.i(TAG +" : "+parentName,"savedInstanceState is not null");
                session = Session.restoreSession(sessionContext, null, statusCallback, myBundle);
            }
            
            if (session == null) {
            	Log.i(TAG +" : "+parentName,"session itself is null");
            	//Session.openActiveSession(parentActivity, true,statusCallback);
                session = new Session(sessionContext);
            }
            
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
            	Log.i(TAG +" : "+parentName,"session openForRead");
                session.openForRead(new Session.OpenRequest(parentActivity).setCallback(statusCallback));
            }
        }*/
        
        //return Session.openActiveSessionFromCache(context) != null;
        return session;
	 }
	
	private class RestoreStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
        	Log.i(TAG +" : "+parentName,"It went here at RestoreStatusCallback : "+state);
        	if (session.isOpened()) {
	    		
	    		// make request to the /me API
	    		Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

    			  // callback after Graph API response with user object
    			  @Override
    			  public void onCompleted(GraphUser user, Response response) {
    				  	Toast.makeText(sessionContext, "From restore Logged in as "+user.getName()+".",Field.SHOWTIME).show();
    				  
    			  }
    			});
	    	}
        }
    }

	
	
	private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
        	Log.i(TAG +" : "+parentName,"It went here at SessionStatusCallback : "+state);
        	if (session.isOpened()) {
//        		parentActivity.finish();
//		  		Intent i = new Intent(sessionContext, DualCamActivity.class); 
//		  		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		  		i.putExtra("showSplashScreen", false);
//		  		parentActivity.startActivity(i);
//	    		// make request to the /me API
	    		Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

    			  // callback after Graph API response with user object
    			  @Override
    			  public void onCompleted(GraphUser user, Response response) {
    				  	//Toast.makeText(sessionContext, "From session status Logged in as "+user.getName()+".",Field.SHOWTIME).show();
    				  	
//    				  	if(parentName == gifClassName){
//    				  		parentActivity.finish();
//    				  		Intent i = new Intent(sessionContext, DualCamActivity.class); 
//    				  		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//    				  		i.putExtra("showSplashScreen", false);
//    				  		parentActivity.startActivity(i);
    				  		
//    				  	}
    			  }
    			});
	    	}
        }
    }
	
	private class LogInCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
    	 	if(session.isOpened()){
    	 		Log.i(TAG +" : "+parentName,"It went here at LogInCallback");
    	 		Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
//					
    			  // callback after Graph API response with user object
    			  @Override
    			  public void onCompleted(GraphUser user, Response response) {
					  if (user != null) {
						  	Toast.makeText(sessionContext, "Hello!! "+user.getFirstName()+".",Field.SHOWTIME).show();
//						  	if(parentName == gifClassName){
						  	partyStarter();
//	    				  		parentActivity.finish();
//	    				  		Intent i = new Intent(sessionContext, DualCamActivity.class); 
//	    				  		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//	    				  		i.putExtra("showSplashScreen", false);
//	    				  		parentActivity.startActivity(i);
	    				  		
//	    				  	}
					  } 
	    				  
    			  }
    			});
    	 	}
        }
    }
	
	public Session getMySession(){
		return mySession;
	}
	
	public void setMySession(Session theSession){
		mySession = theSession;
	}
	
	public Session startMySession(){
		mySession = mySession();
		return mySession;
	}
	
	public void storeMySession(){
		Session.saveSession(mySession, myBundle);
		boolean b = mySession == null;
		Log.i(TAG +" : "+parentName, "Storing the Session.");
		//Toast.makeText(sessionContext, "Session is saved. Session = "+b+" User : "+theUser,Field.SHOWTIME).show();
		
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
	
	public void setClassName(String name){
		parentName = name;
	}
	
	public void partyStarter(){
		parentActivity.finish();
  		Intent i = new Intent(sessionContext, DualCamActivity.class); 
  		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
  		i.putExtra("showSplashScreen", false);
  		parentActivity.startActivity(i);
  	}
 
}
