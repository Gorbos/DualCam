package com.cam.dualcam;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class SuperButton extends Button implements OnClickListener{

	public SuperButton (Context context) {
	    super(context);
	    init();
	}
	
	public SuperButton (Context context, AttributeSet attrs) {
	    super(context, attrs);
	    this.setText("Button");
	    this.setBackgroundResource(R.layout.layout_bg);
	    init();
	}
	
	private void setBackground(int sharebuttonBg) {
		// TODO Auto-generated method stub
		
	}

	public SuperButton (Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	    init();
	}

	private void init() {
		setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		this.setText("Clicked");
		this.setBackgroundResource(R.layout.sharebutton_bg);
	}

}
