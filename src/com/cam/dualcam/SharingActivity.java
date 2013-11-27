package com.cam.dualcam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import com.cam.dualcam.twitter.TwitterConstant;
import com.cam.dualcam.twitter.TwitterUtil;
import com.cam.dualcam.utility.Field;
import com.hintdesk.core.util.StringUtil;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SharingActivity extends Activity {
 
	CheckBox cbFb, cbTwitter;
	Button btnShare, btnCancel;
	TextView tvMessageCounter;
	EditText shareMessage;
	public String filePath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sharing_layout);
		
		initControlTwitter();
		
		Intent intent = getIntent();
		filePath = getIntent().getStringExtra("imagePath");
		System.out.println(filePath);
		
		cbFb = (CheckBox)findViewById(R.id.checkBoxFb); 
		cbTwitter = (CheckBox)findViewById(R.id.checkBoxTwitter); 
		tvMessageCounter = (TextView)findViewById(R.id.tvMessageCounter);	
		btnShare = (Button)findViewById(R.id.btn_yes);
		btnCancel = (Button)findViewById(R.id.btn_no);
		shareMessage = (EditText)findViewById(R.id.shareMessage);
		
		cbFb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (buttonView.isChecked()) {
					Toast.makeText(getApplicationContext(), "FB Checked", Field.SHOWTIME).show();
				} else {
					Toast.makeText(getApplicationContext(), "FB UnChecked", Field.SHOWTIME).show();
				}
				
			}
		}); 
		
		cbTwitter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				if (buttonView.isChecked()) {
					SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					
					if (!sharedPreferences.getBoolean(TwitterConstant.PREFERENCE_TWITTER_IS_LOGGED_IN,false))
						  {
				        	new TwitterAuthenticateTask().execute();
				            Toast.makeText(getApplicationContext(), "No log in acc. on Twitter.", Field.SHOWTIME).show();
				    }else {
				             new TwitterGetAccessTokenTask().execute("");
				             Toast.makeText(getApplicationContext(), "Has log in acc. on Twitter.", Field.SHOWTIME).show();
				           }
				} else {
					
				}
				
			}
		}); 
		
		shareMessage.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            	String charCount=""+shareMessage.getText();
            	tvMessageCounter.setText(""+charCount.length()+"/120");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            	Log.i("a","a2");

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            	Log.i("a","a3");
            } 

        });
		
		btnShare.setOnClickListener(new Button.OnClickListener() {
		    public void onClick(View v) {
		    	
		    	if(cbFb.isChecked()){
					/*pushFBRequest(shareMessage.getText().toString());
					dialog.dismiss();
					loading.show();*/
		    		System.out.println("FB naka Check");	
				}
				else if(!cbFb.isChecked())
					/*Toast.makeText(getApplicationContext(), "Please choose at least 1 media.", Field.SHOWTIME).show();
					dialog.dismiss();*/
					System.out.println("FB di naka Check");		
				
		    	if(cbTwitter.isChecked()) {
						/*fileName = mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE).toString();
						String TwitText = shareMessage.getText().toString();
						String TwitStatus = TwitText + " via #DualCam";*/
						//Toast.makeText(getApplicationContext(), "TwitStatus" + TwitStatus, Field.SHOWTIME).show();  
						//new TwitterUpdateStatusTask().execute(TwitStatus);
					System.out.println("Twitter naka Check");				  
				}else if(!cbTwitter.isChecked()) {
					System.out.println("Twitter di naka Check");	   					
				}
				
		    }
		});
			
	}

	// Twitter Log in
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
		
		// Twitter Log out function
		private void twitterLogout() {
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	        SharedPreferences.Editor editor = sharedPreferences.edit();
	        editor.putString(TwitterConstant.PREFERENCE_TWITTER_OAUTH_TOKEN, "");
	        editor.putString(TwitterConstant.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, "");
	        editor.putBoolean(TwitterConstant.PREFERENCE_TWITTER_IS_LOGGED_IN, false);
	        editor.commit();
	        Toast.makeText(getApplicationContext(),  "Logging Out on Twitter", Toast.LENGTH_LONG).show();
		}
		
		// Twitter initial Control for getting token Data
		private void initControlTwitter() {
	        Uri uri = getIntent().getData();
	        if (uri != null && uri.toString().startsWith(TwitterConstant.TWITTER_CALLBACK_URL)) {
	            String verifier = uri.getQueryParameter(TwitterConstant.URL_PARAMETER_TWITTER_OAUTH_VERIFIER);
	            new TwitterGetAccessTokenTask().execute(verifier);
	        } else
	            new TwitterGetAccessTokenTask().execute("");
		}

		// Background task on getting Access token in twitter
		class TwitterGetAccessTokenTask extends AsyncTask<String, String, String> {

	        @Override
	        protected void onPostExecute(String userName) {
	            //textViewUserName.setText(Html.fromHtml("<b> Welcome " + userName + "</b>"));
	        }

	        @Override
	        protected String doInBackground(String... params) {

	            Twitter twitter = TwitterUtil.getInstance().getTwitter();
	            RequestToken requestToken = TwitterUtil.getInstance().getRequestToken();
	            if (!StringUtil.isNullOrWhitespace(params[0])) {
	                try {

	                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, params[0]);
	                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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
	                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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

		// Background task on Posting Tweet on Twitter 
	    class TwitterUpdateStatusTask extends AsyncTask<String, String, Boolean> {

	        @Override
	        protected void onPostExecute(Boolean result) {
	            if (result)
	                Toast.makeText(getApplicationContext(), "Tweet successfully", Toast.LENGTH_SHORT).show();
	            else
	                Toast.makeText(getApplicationContext(), "Tweet failed", Toast.LENGTH_SHORT).show();
	        }
	        
	        @Override
	        protected Boolean doInBackground(String... params) {
	            try {
	                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	                String accessTokenString = sharedPreferences.getString(TwitterConstant.PREFERENCE_TWITTER_OAUTH_TOKEN, "");
	                String accessTokenSecret = sharedPreferences.getString(TwitterConstant.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, "");

	                if (!StringUtil.isNullOrWhitespace(accessTokenString) && !StringUtil.isNullOrWhitespace(accessTokenSecret)) {
	                    AccessToken accessToken = new AccessToken(accessTokenString, accessTokenSecret);
	                    
	                   // working original
	                   // twitter4j.Status status = TwitterUtil.getInstance().getTwitterFactory().getInstance(accessToken).updateStatus(params[0]);
	                    
	                    
	                    Twitter twitter =  TwitterUtil.getInstance().getTwitterFactory().getInstance(accessToken);

	                    // Update status

	                    StatusUpdate ad=new StatusUpdate(params[0]);

	                    File finalFile = new File(filePath);
	                   
	                    FileInputStream xs = new FileInputStream(finalFile);
	                    
	                    ad.setMedia(params[0],xs);
	                    
	                    twitter4j.Status response = twitter.updateStatus(ad);
	                    
	                    return true;
	                }
	                
	            } catch (TwitterException e) {
	                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
	            } catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            return false;  //To change body of implemented methods use File | Settings | File Templates.
	            
	        }
	    }
	    

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sharing, menu);
		return true;
	}

}
