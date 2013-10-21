package com.cam.dualcam;

import com.cam.dualcam.utility.Field;
import com.cam.dualcam.widget.GifMovieView;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class GifActivity extends Activity {
	
	private boolean showSpalshScreen = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gif_activity_layout);
		
		final GifMovieView gif1 = (GifMovieView) findViewById(R.id.gif1);
		gif1.setMovieResource(R.drawable.landscape_train);
		
		//get extras 
		Bundle extras = getIntent().getExtras();
		showSpalshScreen = extras.getBoolean("showSplashScreen");
		
		Button buttonOne = (Button) findViewById(R.id.btnEnterCamera);
		buttonOne.setOnClickListener(new Button.OnClickListener() {
		    public void onClick(View v) {
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
