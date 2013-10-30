package com.cam.dualcam;

import com.cam.dualcam.widget.GifWebView;
import com.cam.dualcam.utility.Field;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

public class DuplicateGifWebviewDisplayActivity extends FragmentActivity {
	
	private GifWebView gifView;
	private int orientationOfPhone;
	private boolean showSpalshScreen = true;
	private String enterCamera = "カメラ";
	
	private UiLifecycleHelper uiHelper;
	private boolean isResumed = false;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gif_webview_layout);
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
		
		
		
		gifView = (GifWebView) findViewById(R.id.gif_view);
		
		orientationOfPhone = this.getResources().getConfiguration().orientation;
		
		
		setAllViews();
		
	}
	
	public void setAllViews(){
		if (orientationOfPhone == Configuration.ORIENTATION_PORTRAIT) {
			gifView.setGifAssetPath("file:///android_asset/falls.gif");
			gifView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
			//gifView.setGifAssetPath("file:///android_asset/cute.gif");
			//gifView.setGifAssetPath("file:///android_asset/funny.gif");
		} else if (orientationOfPhone == Configuration.ORIENTATION_LANDSCAPE) {
			gifView.setGifAssetPath("file:///android_asset/landscape_train.gif");
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
		    	Intent i = new Intent(DuplicateGifWebviewDisplayActivity.this, DualCamActivity.class); 
		    	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.putExtra("showSplashScreen", false);
				startActivity(i);
		    }
		});
	}
	
	
	//For the social networking sites :D
	private Session.StatusCallback callback =
		new Session.StatusCallback() {
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				onSessionStateChange(session, state, exception);
			}
		};
		
	private void onSessionStateChange(Session session, SessionState state, Exception exception)	{
		
		if(isResumed) {
			//do something
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onPause(){
		super.onPause();
		uiHelper.onPause();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		uiHelper.onResume();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		uiHelper.onDestroy();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}
}
