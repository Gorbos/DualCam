package com.cam.dualcam;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public class LoadingFragment extends Fragment{
	private static final String TAG = "LoadingFragment";
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.loading_fragment, 
	            container, false);
	    return view;
	}
}
