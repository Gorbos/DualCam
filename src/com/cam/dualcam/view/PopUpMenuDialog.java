package com.cam.dualcam.view;

import com.cam.dualcam.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.app.Dialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;

@SuppressLint("ValidFragment")
public class PopUpMenuDialog extends DialogFragment implements OnClickListener{
	
	Context mContext;
	
	public PopUpMenuDialog(){

	}
	
//	@Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//            Bundle savedInstanceState) {
//		
////		AlertDialog.Builder popUpMenu = new AlertDialog.Builder(getActivity());
//		LinearLayout menuLinear = new LinearLayout(getActivity());
//		menuLinear.setOrientation(1);
//		
//		menuLinear.addView(newLine(getResources().getString(R.string.afctitle),"TITLE"));
//		menuLinear.addView(newLine(getResources().getString(R.string.afc1),"OPTION"));
//		menuLinear.addView(newLine(getResources().getString(R.string.afc2),"OPTION"));
//		
//		//popUpMenu.setView(menuLinear);
//		
////        View view = inflater.inflate(R.layout.fragment_edit_name, container);
////        mEditText = (EditText) view.findViewById(R.id.txt_your_name);
//        getDialog().setTitle("Hello");
//
//
//        return menuLinear;
//    }

	
	
	
	 @Override
	 public Dialog onCreateDialog(Bundle savedInstanceState) {
		 AlertDialog.Builder popUpMenu = new AlertDialog.Builder(getActivity());
		 
		// popUpMenu.setit
			
		 	//popUpMenu.setv
			LinearLayout menuLinear = new LinearLayout(getActivity());
			menuLinear.setOrientation(1);
			
			menuLinear.addView(newLine(getResources().getString(R.string.afctitle),"TITLE"));
			menuLinear.addView(newLine(getResources().getString(R.string.afc1),"OPTION"));
			menuLinear.addView(newLine(getResources().getString(R.string.afc2),"OPTION"));
			
			popUpMenu.setView(menuLinear);
			
			
			return popUpMenu.create();
	 }
	
	 public LinearLayout newLine(String itemMessage, String type){

			LinearLayout optionsLinear = new LinearLayout(getActivity());
			optionsLinear.setOrientation(0);
			
			RelativeLayout optionRelative = new RelativeLayout(getActivity());
			
			
			if(type == "TITLE"){
				TextView title = new TextView(getActivity());
				title.setTextSize(24);
				title.setTextColor(Color.BLACK);
				title.setBackgroundColor(Color.GRAY);
				title.setText(itemMessage);
				
				optionsLinear.addView(title);
			}
			else if(type == "OPTION"){
				CheckBox checkBox = new CheckBox(getActivity());
				TextView option = new TextView(getActivity());
				option.setText(itemMessage);
				
//				optionRelative.addView(option, RelativeLayout.ALIGN_PARENT_LEFT);
//				optionRelative.addView(checkBox, RelativeLayout.ALIGN_PARENT_RIGHT);
//				optionsLinear.addView(optionRelative);
				optionsLinear.addView(option);
				optionsLinear.addView(checkBox);
			}
			

		return optionsLinear;
		 
	 }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}
