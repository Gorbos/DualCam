package com.cam.dualcam;

import com.cam.dualcam.widget.LoadingDialog;
import com.facebook.UiLifecycleHelper;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public class SplashFragment extends Fragment{
	private static final String TAG = "SplashFragment";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    Log.i(TAG, "from onCreate.");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.splash_fragment, 
	            container, false);
	    Log.i(TAG, "from onCreateView.");
	    return view;
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    Log.i(TAG, "from onResume.");
	}

	@Override
	public void onPause() {
	    super.onPause();
	    Log.i(TAG, "from onPause.");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    Log.i(TAG, "from onActivityResult.");
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    Log.i(TAG, "from onDestroy.");
	}
	
	@Override
	public void onDestroyView() {
	    super.onDestroyView();
	    Log.i(TAG, "from onDestroyView.");
	}
	
	@Override
	public void onStop(){
		super.onStop();
	    Log.i(TAG, "from onStop.");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    Log.i(TAG, "from onSaveInstanceState.");
	}
}
