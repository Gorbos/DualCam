package com.cam.dualcam;

import com.cam.dualcam.utility.Field;
import com.cam.dualcam.widget.GifMovieView;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;

public class GifActivity extends Activity {
	private int orientationOfPhone;
	private boolean showSpalshScreen = true;
	private String enterCamera = "カメラ";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.gif_activity_layout);
		setContentView(R.layout.dummygif);
		final GifMovieView gif1 = (GifMovieView) findViewById(R.id.gif1);
		
		orientationOfPhone = this.getResources().getConfiguration().orientation;
		
		if (orientationOfPhone == Configuration.ORIENTATION_PORTRAIT) {
			//gif1.setMovieResource(R.drawable.portrait_baby);
			//gif1.setMovieResource(R.drawable.falls);
			//gif1.setMovieResource(R.drawable.funny);
			gif1.setMovieResource(R.drawable.cute);
		} else if (orientationOfPhone == Configuration.ORIENTATION_LANDSCAPE) {

			//gif1.setMovieResource(R.drawable.landscape_train);
			gif1.setMovieResource(R.drawable.karate);
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
		    	Intent i = new Intent(GifActivity.this, DualCamActivity.class); 
		    	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.putExtra("showSplashScreen", false);
				startActivity(i);
		    }
		});
		
	}

	
	// clickable GIF
	/*public void onGifClick(View v) {
		GifMovieView gif = (GifMovieView) v;
		gif.setPaused(!gif.isPaused());
	}*/

}
