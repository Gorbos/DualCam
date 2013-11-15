package com.cam.dualcam.widget;

import com.cam.dualcam.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class SharingDialog extends Dialog {
	
	private Activity sdActivity;
	private Context  sdContext;
	
	public Button cancel;
	public Button ok;
	
	public SharingDialog(Context context) {
		super(context);
		sdContext = context;
	}

	
	public SharingDialog(Activity a) {
		super(a);
		// TODO Auto-generated constructor stub
		this.sdActivity = a;
		this.sdContext  = a.getApplicationContext();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sharing_menu);
		setCancelable(true);
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		cancel = (Button) findViewById(R.id.shareCancelBtn);
		ok	   = (Button) findViewById(R.id.shareOkBtn);
//		cancel.setOnTouchListener(this);
	
	}
	
	public void setButtons(String button, View.OnTouchListener touchListener){
		try{
		if(button == "ok"){
			if(ok == null)
				ok = (Button) findViewById(R.id.shareOkBtn);
			ok.setOnTouchListener(touchListener);
		}

		if(button == "cancel"){
			if(cancel == null)
				cancel = (Button) findViewById(R.id.shareCancelBtn);
			
			cancel.setOnTouchListener(touchListener);
		}
		}
		catch(Exception e){
			Log.i("SharingDialog"," e = "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	

}
