package com.cam.dualcam.widget;


import com.cam.dualcam.R;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;

public class CustomFBButton extends Button{

	public CustomFBButton(Context context, AttributeSet attrs)  {
		super(context, attrs);
		//initiate the design
		this.setGravity(Gravity.CENTER);
        this.setTextColor(getResources().getColor(R.color.com_facebook_loginview_text_color));
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.com_facebook_loginview_text_size));
        this.setTypeface(Typeface.DEFAULT_BOLD);
        //this.setBackgroundResource(R.drawable.twitter_button);
        this.setBackgroundResource(R.drawable.com_facebook_button_blue);
        this.setCompoundDrawablesWithIntrinsicBounds(R.drawable.com_facebook_inverse_icon, 0, 0, 0);
        this.setCompoundDrawablePadding(
                getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_compound_drawable_padding));
        this.setPadding(getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_left),
                getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_top),
                getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_right),
                getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_bottom));

	}
	
}
