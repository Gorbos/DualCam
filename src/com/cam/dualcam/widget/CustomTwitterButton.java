package com.cam.dualcam.widget;

import com.cam.dualcam.*;
//import com.facebook.android.R;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;

public class CustomTwitterButton extends Button{

	public CustomTwitterButton(Context context, AttributeSet attrs)  {
		super(context, attrs);
		this.setBackgroundResource(R.drawable.twitter_button);
		this.setGravity(Gravity.CENTER);
        this.setTextColor(getResources().getColor(R.color.com_facebook_loginview_text_color));
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.com_facebook_loginview_text_size));
        this.setTypeface(Typeface.DEFAULT_BOLD);
        this.setBackgroundResource(R.drawable.com_facebook_button_blue);
        this.setCompoundDrawablesWithIntrinsicBounds(R.drawable.com_facebook_inverse_icon, 0, 0, 0);
        this.setCompoundDrawablePadding(
                getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_compound_drawable_padding));
        this.setPadding(getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_left),
                getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_top),
                getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_right),
                getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_bottom));
//		 if (attrs.getStyleAttribute() == 0) {
//	            // apparently there's no method of setting a default style in xml,
//	            // so in case the users do not explicitly specify a style, we need
//	            // to use sensible defaults.
//	            this.setGravity(Gravity.CENTER);
//	            this.setTextColor(getResources().getColor(R.color.com_facebook_loginview_text_color));
//	            this.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//	                    getResources().getDimension(R.dimen.com_facebook_loginview_text_size));
//	            this.setTypeface(Typeface.DEFAULT_BOLD);
//	            if (isInEditMode()) {
//	                // cannot use a drawable in edit mode, so setting the background color instead
//	                // of a background resource.
//	                this.setBackgroundColor(getResources().getColor(R.color.com_facebook_blue));
//	                // hardcoding in edit mode as getResources().getString() doesn't seem to work in IntelliJ
//	                //loginText = "Log in with Facebook";
//	            } else {
//	                this.setBackgroundResource(R.drawable.com_facebook_button_blue);
//	                this.setCompoundDrawablesWithIntrinsicBounds(R.drawable.com_facebook_inverse_icon, 0, 0, 0);
//	                this.setCompoundDrawablePadding(
//	                        getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_compound_drawable_padding));
//	                this.setPadding(getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_left),
//	                        getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_top),
//	                        getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_right),
//	                        getResources().getDimensionPixelSize(R.dimen.com_facebook_loginview_padding_bottom));
//	            }
//	        }
	}


}
