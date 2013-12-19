package com.cam.dualcam.socialpackage;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.cam.dualcam.MotherCrystal;
import com.cam.dualcam.twitter.TwitterConstant;
import com.cam.dualcam.twitter.TwitterUtil;
import com.cam.dualcam.twitter.TwitterWebview;
import com.cam.dualcam.utility.Field;
import com.hintdesk.core.util.StringUtil;

public class MyTwitter {
	
	private Activity theAct;
	private Context theCon; 
	private int TWITTER_AUTH;
	private int fragShown;
	
	private static final String TAG = "MyTwitter";
	
	
	private String buttonText;
	
	public boolean isLoggedInTwitter = false;
	
	public MyTwitter(Activity act){
		theAct = act;
		theCon = act.getApplicationContext();
		TWITTER_AUTH = Field.TWITTER_AUTH;
	}
	
	public boolean isTwitterOn(){
		
		SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(theCon);
		if (sPrefs.getBoolean(TwitterConstant.PREFERENCE_TWITTER_IS_LOGGED_IN,false))
			return true;//Twitter is Logged in
		else
			return false;//Twitter is Logged out
		
	}
	
	public void setLogValues(Button b){
		SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(b.getContext());
		if (sPrefs.getBoolean(TwitterConstant.PREFERENCE_TWITTER_IS_LOGGED_IN,false))
		{
			isLoggedInTwitter = true;
			buttonText = "Log-Out from Twitter";
		}
		else{
			isLoggedInTwitter = false;
			buttonText = "Log-In to Twitter";
		}
		
		b.setText(buttonText);
	}

	
	public void logOutTwitter(){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(theCon);
		SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TwitterConstant.PREFERENCE_TWITTER_OAUTH_TOKEN,null);
        editor.putString(TwitterConstant.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET,null);
        editor.putBoolean(TwitterConstant.PREFERENCE_TWITTER_IS_LOGGED_IN, false);
        editor.commit();
        TwitterUtil.getInstance().reset();
        //setLogValues();
	}
	
	public void logInTwitter(){
		new TwitterAuthenticateTask().execute();
	}
	
	public void prepAccessToken(String oauthVerifier){
		new TwitterGetAccessTokenTask().execute(oauthVerifier);
	}
		
	private void reloadFragment(){
		fragShown = MotherCrystal.fragmentShown;
		switch(fragShown){
		case Field.MOTHER_SPLASH:
			Log.i(TAG, "To be done : Field.MOTHER_MOTHER_SPLASH");
			break;
			
		case Field.MOTHER_SOCIALMEDIA:
			Log.i(TAG, "showFragment : Field.MOTHER_SOCIALMEDIA");
			((MotherCrystal)theAct).showFragment(Field.MOTHER_CAM, false);
			break;	
			
		case Field.MOTHER_CAM:
			Log.i(TAG, "To be done : Field.MOTHER_CAM");
			break;
			
		default:
			Log.i(TAG, "To be done : default");
			break;
		}
		
	}

	private class TwitterGetAccessTokenTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String userName) {
            //textViewUserName.setText(Html.fromHtml("<b> Welcome " + userName + "</b>"));
        	//showFragment(CAM, false);
        	reloadFragment();
        }

        @Override
        protected String doInBackground(String... params) {
        	
            Twitter twitter = TwitterUtil.getInstance().getTwitter();
            RequestToken requestToken = TwitterUtil.getInstance().getRequestToken();
            if (!StringUtil.isNullOrWhitespace(params[0])) {
                try {
                	System.out.println("requestToken -->" + requestToken); 
                	System.out.println("params[0]  -->" + params[0]); 
                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, params[0]);
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(theCon);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(TwitterConstant.PREFERENCE_TWITTER_OAUTH_TOKEN, accessToken.getToken());
                    editor.putString(TwitterConstant.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, accessToken.getTokenSecret());
                    editor.putBoolean(TwitterConstant.PREFERENCE_TWITTER_IS_LOGGED_IN, true);
                    editor.commit();
                    
                    
                    return twitter.showUser(accessToken.getUserId()).getName();
                } catch (TwitterException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            } else {
            	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(theCon);
                String accessTokenString = sharedPreferences.getString(TwitterConstant.PREFERENCE_TWITTER_OAUTH_TOKEN, "");
                String accessTokenSecret = sharedPreferences.getString(TwitterConstant.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, "");
                AccessToken accessToken = new AccessToken(accessTokenString, accessTokenSecret);
                try {
                    TwitterUtil.getInstance().setTwitterFactory(accessToken);
                    return TwitterUtil.getInstance().getTwitter().showUser(accessToken.getUserId()).getName();
                } catch (TwitterException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
	
	private class TwitterAuthenticateTask extends AsyncTask<String, String, RequestToken> {

        @Override
        protected void onPostExecute(RequestToken requestToken) {
        	Log.i(TAG, "from onPostExecute : onPostExecute");
            Intent i = new Intent(theCon, TwitterWebview.class);
			i.putExtra("URL", requestToken.getAuthenticationURL());
			theAct.startActivityForResult(i, TWITTER_AUTH);
			if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.HONEYCOMB) {
			     // only for gingerbread and newer versions
			}else{
				
			}
			
        }

        @Override
        protected RequestToken doInBackground(String... params) {
        	Log.i(TAG, "from onPostExecute : doBackground");
            return TwitterUtil.getInstance().getRequestToken();
        }
    }
	

}
