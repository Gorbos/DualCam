package com.cam.dualcam.widget;

import com.cam.dualcam.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager.LayoutParams;



public class LoadingDialog extends Dialog{
	
	private Activity ldActivity;
	private Context  ldContext;

	public LoadingDialog(Context context) {
		super(context);
		ldContext = context;
	}

	
	public LoadingDialog(Activity a) {
		super(a);
		// TODO Auto-generated constructor stub
		this.ldActivity = a;
		this.ldContext  = a.getApplicationContext();
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.loading_dialog);
		setCancelable(false);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		getWindow().clearFlags(LayoutParams.FLAG_DIM_BEHIND);
	}
	
}
