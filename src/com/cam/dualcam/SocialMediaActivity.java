package com.cam.dualcam;

import com.cam.dualcam.widget.GifWebView;
import com.cam.dualcam.utility.Field;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SocialMediaActivity extends Activity {
	
	private GifWebView gifView;
	private int orientationOfPhone;
	private boolean showSpalshScreen = true;
	private String enterCamera = "カメラ";
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gif_webview_layout);
		gifView = (GifWebView) findViewById(R.id.gif_view);
		
		setView();
	}
	
	public void setView(){

		orientationOfPhone = this.getResources().getConfiguration().orientation;
		
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
		    	Intent i = new Intent(SocialMediaActivity.this, DualCamActivity.class); 
		    	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.putExtra("showSplashScreen", false);
				startActivity(i);
		    }
		});
	}
}
