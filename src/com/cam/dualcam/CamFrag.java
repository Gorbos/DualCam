package com.cam.dualcam;

import com.cam.dualcam.CameraFragment.setTouchMode;
import com.cam.dualcam.utility.CameraUtility;
import com.cam.dualcam.utility.ColorPickerDialog;
import com.cam.dualcam.utility.Field;
import com.cam.dualcam.utility.MediaUtility;
import com.cam.dualcam.utility.PackageCheck;
import com.cam.dualcam.utility.PhoneChecker;
import com.cam.dualcam.view.CameraPreview;
import com.cam.dualcam.view.HideAct;
import com.cam.dualcam.widget.GifWebView;
import com.cam.dualcam.widget.LoadingDialog;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.ShutterCallback;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

@SuppressLint("NewApi")
public class CamFrag extends Fragment {

	private static final String TAG = "SplashFragment";
	
	// Defined variables
		// Jap Messages
		private String errorMessage;
		private String retakeMessage;
		private String restartMessage;
		private String saveMessage;
		private String addALabelText;
		private String typeTextHereText;
		private String fontSizeText;
		private String fontColorText;
		
	//Global variable
		private View mainView;
		
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    Log.i(TAG, "from onCreate.");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
		
	    View view = inflater.inflate(R.layout.cam_fragment, container, false);
	    Log.i(TAG, "from onCreateView.");
	    
	    mainView = view;
	    setWidgets(view);
	    initVar();
		
		
	  return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    Log.i(TAG, "from onActivityResult.");
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    Log.i(TAG, "from onResume.");
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
	    super.onSaveInstanceState(bundle);
	    Log.i(TAG, "from onSaveInstanceState.");
	}

	@Override
	public void onPause() {
	    super.onPause();
	    Log.i(TAG, "from onPause.");
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
	
/*		ACTS OF THE PAST		*/	
	//state checkers
	private boolean isBackTaken = false;
	private boolean isFrontTaken = false;
	private boolean isSharable = false;
	private boolean isSavable = false;
	private boolean isTextEditable = false;
	private boolean isTextAdded = false;
	private boolean isTextBeingEdited = false;
	private boolean isRetryable = false;
	private boolean isSavePathset = false;
	private boolean isZoomSupported = false;
	private boolean isSmoothZoomSupported = false;
	
	private boolean isAlreadySharable 	= true;
	private boolean isAlreadySavable 	= true;
	private boolean isAlreadyTextable	= true;
	private boolean isAlreadyRetryable  = true;
	
	private boolean isDoubleTapAction = false;
	private boolean isReadyToShoot = false;
	private boolean isCameraFocused = false;
	private boolean isRetaking = false;
	private boolean hasCameraFocus = false;
	private boolean killMe = false;
	
	//Phone settings
	private String orientationScreen = null;
	
	//Camera objects
	// Camera Settings
		public Parameters param;
		public Camera mCamera;
		public Integer maxCameraZoom;
		public Integer currentCameraZoom;
		public Integer cameraAction;
		public static int result = 0;
		public static int degrees = 0;
	
	
	//Classes and Dialogs
		public PackageCheck packageCheck;
		public static MediaUtility mediaUtility;
		public static CameraUtility cameraUtility;
		public static HideAct hideAct;
		public static ColorPickerDialog colorPickerDialog;
		public Intent sharingIntent;
		private LoadingDialog loading;
	
	//ImageViews that serves as Buttons
		private ImageView captureButton, textButton, saveButton, retryButton, shareButton;
		private ImageView backPreview, frontPreview, previewImage;
		private ImageView focusMarker;
	//For the text freature
		private ImageView okButton,	noButton;
		
	//Previews
		public LinearLayout pictureLayout, utilityLayout;
		public RelativeLayout toSaveLayout;
		public FrameLayout mainPreview, createTextFrameLayout;
		public CameraPreview cameraPreview;	
		
	//Dialogs
		public AlertDialog popUpDialog;
		
	//Touch Actions
		// Touch and click events
		public Integer bottomTapCount = 0;
		public Integer topTapCount = 0;
		public Integer tapCount = 0;
		public Integer touchCount;
		public Integer tCount;
		public Integer performedAction;
		public Integer firstPointer;
		public Integer firstPointerIndex;
		public Integer secondPointer;
		public Integer secondPointerIndex;
		public Float pointerDistance;
		public Float changedPointerDistance;
		
	
	private void setWidgets(View view){
		//load and set the classes and Dialogs
		mediaUtility = new MediaUtility(getActivity().getApplicationContext());
		packageCheck = new PackageCheck(getActivity().getApplicationContext());
		loading = new LoadingDialog(getActivity());
		hideAct = new HideAct(getActivity().getApplicationContext());
		
		//Menu Dialog, pero baka palitan ko nalang instead na costum pop up
		popUpDialog = null;//customPopUpMenu();
		
		//Main ImageViews that serves as Buttons 
		captureButton = (ImageView) view.findViewById(R.id.captureButton);
		textButton = (ImageView) view.findViewById(R.id.textButton);
		saveButton = (ImageView) view.findViewById(R.id.saveButton);
		retryButton = (ImageView) view.findViewById(R.id.retryButton);
		shareButton = (ImageView) view.findViewById(R.id.shareButton);
		backPreview = (ImageView) view.findViewById(R.id.cumPreviewBack);
		frontPreview = (ImageView) view.findViewById(R.id.cumPreviewFront);
		previewImage = (ImageView) view.findViewById(R.id.previewImage);
		
		//For the text freature	(Dialog)
		okButton = (ImageView) view.findViewById(R.id.okButton);
		noButton = (ImageView) view.findViewById(R.id.noButton);
		
		
		//Main Layouts/Previews(Save,Utility,Views,Dialog)
		mainPreview = (FrameLayout) view.findViewById(R.id.cumshot);
		pictureLayout = (LinearLayout) view.findViewById(R.id.picLayout);
		utilityLayout = (LinearLayout) view.findViewById(R.id.utilityButtonLayout);
		createTextFrameLayout = (FrameLayout) view.findViewById(R.id.createTextFrame);
		toSaveLayout = (RelativeLayout) view.findViewById(R.id.overAllLayout);
		
		
		
		//Set the listeners
		captureButton.setOnClickListener(superButton);
		textButton.setOnClickListener(superButton);
		saveButton.setOnClickListener(superButton);
		retryButton.setOnClickListener(superButton);
		shareButton.setOnClickListener(superButton);
		
		//For the text freature	(Dialog)
		okButton.setOnClickListener(superButton);
		noButton.setOnClickListener(superButton);
		
		//For the focus marker
		backPreview.setOnTouchListener(new setTouchMode());
		frontPreview.setOnTouchListener(new setTouchMode());
		
	}
	
	//Phone settings and values
		public int movingJutsu;
		public int flashStatus;
		public int melodyStatus;
		public int orStatus;
		public static int orientationOfPhone = 0;
		public Integer screenHeight;
		public Integer screenWidth;
		public Integer touchAction;
		public Integer jutsuAction;
		
	//Timers
		public CountDownTimer longClickTimer;
		public CountDownTimer cameraFocusTimer;
		public CountDownTimer doubleTapTimer;
	
	//Initial Settings
	private void initVar(){
		orientationOfPhone = getActivity().getResources().getConfiguration().orientation;
		screenHeight = new PhoneChecker(getActivity().getApplicationContext()).screenHeight;
		screenWidth = new PhoneChecker(getActivity().getApplicationContext()).screenWidth;
		
		SharedPreferences settings = getActivity().getSharedPreferences(Field.PREFS_DUALCAM, 0);
		movingJutsu = settings.getInt(Field.PREFS_SET_AUTOFOCUS, Field.MODE_DefaultAF_and_Capture);
		flashStatus = settings.getInt(Field.PREFS_SET_FLASH, Field.MODE_Flash_Default);
		melodyStatus = settings.getInt(Field.PREFS_SET_MELODY, Field.MODE_Melody_DEFAULT);
		orStatus = settings.getInt(Field.PREFS_SET_ORIENTATION, Field.MODE_Orientation_DEFAULT);
		
		touchAction = Field.ActionStateClickable;
		jutsuAction = Field.standbyToCaptureJutsu;
		
		//Timer for the focus
		doubleTapTimer = null;//doDoubleTap();
	}
	
	private String cameraSide = null;
	private void setInteractions(){
//		setSide("BACK");
//		
//		setButton(shareButton);
//		setButton(saveButton);
//		setButton(textButton);
//		setButton(retryButton);
	}
	
	public void setUntake(String cameraSide) {
		if (cameraSide == "BACK") {
			isBackTaken = false;
		} else if (cameraSide == "FRONT") {
			isFrontTaken = false;
		}
	}	
	
	public void setSide(String thisside) {
		touchAction = Field.ActionStateClickable;
		cameraSide = thisside;
		seePreview(cameraSide);
	}
	
	public ImageView getPressedPreview(String cameraSide) {
		ImageView buttonView = null;

		if (cameraSide == "BACK")
			buttonView = backPreview;
		if (cameraSide == "FRONT")
			buttonView = frontPreview;

		return buttonView;
	}
	

	public void setButton(View view){
		
		switch(view.getId()){
		
		case R.id.saveButton:
			if (isSavable){
				saveButton.setImageResource(R.drawable.save1);
				hideAct.showThisView(saveButton, Field.showToBottom);
			}
			else{
				saveButton.setImageResource(R.drawable.save2);
				hideAct.hideThisView(saveButton, Field.hideToBottom);
			}
			break;
			
		case R.id.shareButton:
			if (isSharable){
				shareButton.setImageResource(R.drawable.share1);
				hideAct.showThisView(shareButton, Field.showToBottom);
			}
			else{
				shareButton.setImageResource(R.drawable.share2);
				hideAct.hideThisView(shareButton, Field.hideToBottom);
			}
			break;
			
		case R.id.textButton:
			if (isTextEditable){
				textButton.setImageResource(R.drawable.text1);
				hideAct.showThisView(textButton, Field.showToBottom);
			}
			else{
				textButton.setImageResource(R.drawable.text2);
				hideAct.hideThisView(textButton, Field.hideToBottom);
			}
			break;
			
		case R.id.retryButton:
			if (isRetryable){
				retryButton.setImageResource(R.drawable.retry1);
				hideAct.showThisView(retryButton, Field.showToBottom);
			}
			else{
				retryButton.setImageResource(R.drawable.retry2);
				hideAct.hideThisView(retryButton, Field.hideToBottom);
			}
			break;
			
		default:
			break;
		}
		
	}
	

	public void seePreview(String cameraSide) {
		try {

			previewImage.setVisibility(ImageView.GONE);
			mainPreview.setVisibility(FrameLayout.VISIBLE);
			isSharable = false;
			isSavable = false;
			isTextEditable = false;
			isRetryable = false;
			
			setButton(saveButton);
			setButton(shareButton);
			setButton(textButton);
			setButton(retryButton);
			//setButtons(isSharable, isSavable, isTextEditable, isRetryable);
			createTextFrameLayout.setVisibility(FrameLayout.VISIBLE);
			// saveButton.setImageResource(R.drawable.save2);
			releaseCamera();
			setUntake(cameraSide);
			ImageView buttonView = getPressedPreview(cameraSide);
			cameraUtility = new CameraUtility(getActivity().getApplicationContext());
			Log.i(TAG, "1");

			// Normal
			// mCamera =
			// cameraUtility.getCameraInstance(cameraSide,screenHeight,screenWidth,orientationScreen);
			mCamera = cameraUtility.getCameraInstance(cameraSide,
					screenHeight / 2, screenWidth, orientationScreen);

			// Instantiate camera Zoom
			param = mCamera.getParameters();
			isZoomSupported = param.isZoomSupported();
			isSmoothZoomSupported = param.isSmoothZoomSupported();
			if (isZoomSupported || isSmoothZoomSupported) {
				maxCameraZoom = param.getMaxZoom();
				currentCameraZoom = param.getZoom();
			}
			Log.i(TAG, "isZS = " + isZoomSupported + " : isSZS = "
					+ isSmoothZoomSupported);

			setCameraDisplayOrientation(getActivity(),
					cameraUtility.findCamera(cameraSide), mCamera);
			Log.i(TAG, "b");
			cameraPreview = new CameraPreview(getActivity().getApplicationContext(), mCamera);
			mainPreview.removeAllViews();

			android.widget.RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) mainPreview
					.getLayoutParams();

			// LayoutParams grav = (LayoutParams) mainPreview.getLayoutParams();
			// int gr = grav.getClass()
			RelativeLayout rl = (RelativeLayout) mainView.findViewById(R.id.addCamPreview);
			// android.widget.RelativeLayout.LayoutParams layoutParams =
			// (android.widget.RelativeLayout.LayoutParams)
			// rl.getLayoutParams();

			if (orientationScreen == "PORTRAIT") {
				// Log.i(TAG,"PASOK DITO!!!");
				if (cameraSide == "BACK") {
					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 1);
					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);

					FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
							screenWidth, (screenHeight * 3) / 4);
					if (Field.sdk >= android.os.Build.VERSION_CODES.JELLY_BEAN)
						lp.setMargins(0, 0, 0, 0);
					else
						layoutParams.setMargins(0, 0, 0, 0);
					cameraPreview.setLayoutParams(lp);
				} else {

					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
					FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
							screenWidth, (screenHeight * 3) / 4);
					if (Field.sdk >= android.os.Build.VERSION_CODES.JELLY_BEAN)
						lp.setMargins(0, screenHeight / 4, 0, 0);
					else
						layoutParams.setMargins(0, screenHeight / 4, 0, 0);
					cameraPreview.setLayoutParams(lp);
				}

			} else if (orientationScreen == "LANDSCAPE") {
				if (cameraSide == "BACK") {
					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);
					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);

					FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
							(screenWidth * 3) / 4, screenHeight);
					if (Field.sdk >= android.os.Build.VERSION_CODES.JELLY_BEAN)
						lp.setMargins(0, 0, 0, 0);
					else
						layoutParams.setMargins(0, 0, 0, 0);
					cameraPreview.setLayoutParams(lp);
				} else {
					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);

					FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
							(screenWidth * 3) / 4, screenHeight);
					// lp.setMargins(screenWidth/4,0, 0, 0);
					if (Field.sdk >= android.os.Build.VERSION_CODES.JELLY_BEAN)
						lp.setMargins(screenWidth / 4, 0, 0, 0);
					else
						layoutParams.setMargins(screenWidth / 4, 0, 0, 0);
					cameraPreview.setLayoutParams(lp);
				}
			}

			mainPreview.setLayoutParams(layoutParams);
			mainPreview.addView(cameraPreview);
			Log.i(TAG, "2");

			buttonView.setBackgroundDrawable(null);
			buttonView.setImageBitmap(null);
			// mCamera.getParameters().
			hasCameraFocus = getActivity().getPackageManager().hasSystemFeature(
					PackageManager.FEATURE_CAMERA_AUTOFOCUS);
			// touchAction = Field.ActionAutoFocus;
			Log.i(TAG, "seePreview : touchAction = " + touchAction);
			
			if(cameraSide == "BACK"){
				
				cameraAction = Field.CameraAutoFocus;
				touchAction = Field.ActionStateNotClickable;
				setFocus("FOCUS", performedAction, null, null, null);
			}
			else
			{
				cameraAction = Field.CameraFocusingEnd;
				touchAction = Field.ActionStateClickable;
				
			}

			// if(touchAction == Field.ActionNothing){
			// touchAction = Field.ActionAutoFocus;
			//
			// }

			// cameraOnFocus().start();
			// setFocusMarker("FOCUS",null,null,null);
		} catch (Exception e) {
			Log.e(TAG, "Di ko na alam to wtf ftw");
			Log.e(TAG, "e = " + e.getCause());

			// Toast.makeText(getApplicationContext(),"OOPS!! Error = "+e.getMessage(),Field.SHOWTIME).show();
			Toast.makeText(getActivity().getApplicationContext(), errorMessage,
					Field.SHOWTIME).show();
			// linkSTART();
		}
	}
	

	public static void setCameraDisplayOrientation(Activity activity,
			int cameraId, android.hardware.Camera camera) {
		Parameters params;
		int width = 0;
		int height = 0;
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();

		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}
		
		Log.i(TAG, "Degrees = " + degrees);
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}

		width = cameraUtility.getCamWidth();
		height = cameraUtility.getCamHeight();

		Log.i(TAG, "width " + width);
		Log.i(TAG, "RESULT = " + result);
		camera.setDisplayOrientation(result);
	}
	
	public void setFocus(final String focusCommand, final Integer action,
			final Float coordX, final Float coordY, final View beingFocused) {
		// performedAction = action;
		cameraFocusTimer = cameraSetFocus(focusCommand, coordX, coordY,beingFocused);
		Log.i(TAG, "performedAction = " + performedAction
				+" :cameraAction = "	+cameraAction
				+" :touchAction = " 	+touchAction
				+" :focusCommand = " 	+focusCommand
				+" :cameraSide = "		+cameraSide
				+" :hasCameraFocus = "	+hasCameraFocus);

		// HAS focus 
		if (focusCommand == "FOCUS" && hasCameraFocus && cameraSide == "BACK") {
			isCameraFocused = false;
			setFocusMarker(focusCommand, coordX, coordY, beingFocused);
			cameraFocusTimer.start();
			//cameraSetFocus(focusCommand, coordX, coordY,beingFocused);
		}

		// NO focus
		else if (focusCommand == "FOCUS" && !hasCameraFocus && cameraSide == "BACK") {
			isCameraFocused = true;
			//setFocusMarker("SHOOT", coordX, coordY, beingFocused);
			setFocusMarker(focusCommand, coordX, coordY, beingFocused);
		}
		
		
		else if (focusCommand == "FOCUS" && cameraSide == "FRONT") {
			isCameraFocused = true;
			cameraAction = Field.CameraCanCapture;
			//setFocusMarker("SHOOT", coordX, coordY, beingFocused);
			setFocusMarker(focusCommand, coordX, coordY, beingFocused);
		}

		else if (focusCommand == "SHOOT") {
			isCameraFocused = true;
			setFocusMarker(focusCommand, coordX, coordY, beingFocused);
			// if(mCamera != null)
			// takeAShot();
		}

		else if (focusCommand == "UNFOCUS") {
			setFocusMarker(focusCommand, coordX, coordY, beingFocused);
		}

	}

	public void setFocusMarker(String focusState, Float touchX, Float touchY,
			View beingTouched) {
		Log.i(TAG, "Touch Coordinates X = " + touchX
				+ " : Touch Coordinates Y = " + touchY + " : focusState = "
				+ focusState);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(80, 80);
		lp.gravity = Gravity.TOP;
		final int gravityLP = lp.gravity;
		Log.i(TAG, "The Gravity = " + gravityLP);
		if (focusState == "FOCUS") {
			focusMarker = new ImageView(getActivity().getApplicationContext());
			focusMarker.setImageDrawable(getResources().getDrawable(
					R.drawable.focusmark1));

			// FrameLayout.LayoutParams lp = new
			// FrameLayout.LayoutParams(80,80);
			// Log.i(TAG, "The Size = "+focusMarker.getWidth());
			if (touchX != null && touchY != null) {
				if (cameraSide == "BACK"
						&& beingTouched.getId() == R.id.cumPreviewBack) {
					lp.setMargins((int) Math.abs(touchX - 40),
							(int) Math.abs(touchY - 40), 0, 0);
					// Log.i(TAG, "It went here"+(int) Math.abs(touchX - 40));
					// lp.setMargins((screenWidth - 80) / 4 , (screenHeight -
					// 80) / 2, 0,0);
					focusMarker.setLayoutParams(lp);
					mainPreview.addView(focusMarker);
				} else if (cameraSide == "FRONT"
						&& beingTouched.getId() == R.id.cumPreviewFront) {
					
					focusMarker.setImageDrawable(getResources().getDrawable(
							R.drawable.focusmark2));
					
					if (orientationScreen == "PORTRAIT") {
						if (Field.sdk >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
							lp.setMargins((int) Math.abs(touchX - 40),
									(int) Math.abs(touchY - 40)
											+ (screenHeight / 2), 0, 0);
						} else {
							// lp.setMargins((int) Math.abs(touchX - 40) , (int)
							// Math.abs(touchY ) + 150, 0,0);
							lp.setMargins(
									(int) Math.abs(touchX - 40),
									(int) Math.abs(touchY - 40)
											+ frontPreview.getHeight() / 2, 0,
									0);

						}

					} else if (orientationScreen == "LANDSCAPE") {
						if (Field.sdk >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
							// lp.setMargins((int) Math.abs(touchX - 40) , (int)
							// Math.abs(touchY - 40) + (screenHeight /2), 0,0);
							lp.setMargins((int) Math.abs(touchX - 40)
									+ (screenWidth / 2),
									(int) Math.abs(touchY - 40), 0, 0);

						} else {
							// lp.setMargins((int) Math.abs(touchX - 40) , (int)
							// Math.abs(touchY ) + 150, 0,0);
							// lp.setMargins((int) Math.abs(touchX - 40) , (int)
							// Math.abs(touchY - 40 ) + frontPreview.getHeight()
							// / 2 , 0,0);
							lp.setMargins((int) Math.abs(touchX - 40)
									+ (frontPreview.getWidth() / 2),
									(int) Math.abs(touchY - 40), 0, 0);

						}
					}

					focusMarker.setLayoutParams(lp);
					mainPreview.addView(focusMarker);
				}

			} 
//			else if (touchX == null && touchY == null && beingTouched != null) {
//
//				if (cameraSide == "BACK"
//						&& beingTouched.getId() == R.id.cumPreviewBack) {
//					if (orientationScreen == "PORTRAIT")
//						lp.setMargins((screenWidth - 80) / 2,
//								(screenHeight - 80) / 4, 0, 0);
//					else if (orientationScreen == "LANDSCAPE")
//						lp.setMargins((screenWidth - 80) / 4,
//								(screenHeight - 80) / 2, 0, 0);
//
//					focusMarker.setLayoutParams(lp);
//					mainPreview.addView(focusMarker);
//				} else if (cameraSide == "FRONT"
//						&& beingTouched.getId() == R.id.cumPreviewFront) {
//					if (orientationScreen == "PORTRAIT")
//						lp.setMargins((screenWidth - 80) / 2,
//								((screenHeight - 80) * 3) / 4, 0, 0);
//					else if (orientationScreen == "LANDSCAPE")
//						lp.setMargins(((screenWidth - 80) * 3) / 4,
//								(screenHeight - 80) / 2, 0, 0);
//
//					focusMarker.setLayoutParams(lp);
//					mainPreview.addView(focusMarker);
//				}
//
//			}
			// else if(touchX == null && touchY == null && beingTouched ==
			// null){
			else {
				if (cameraSide == "BACK") {
					if (orientationScreen == "PORTRAIT")
						lp.setMargins((screenWidth - 80) / 2,
								(screenHeight - 80) / 4, 0, 0);
					else if (orientationScreen == "LANDSCAPE")
						lp.setMargins((screenWidth - 80) / 4,
								(screenHeight - 80) / 2, 0, 0);

					focusMarker.setLayoutParams(lp);
					mainPreview.addView(focusMarker);
				} else if (cameraSide == "FRONT") {
					if (orientationScreen == "PORTRAIT") {
						// lp.gravity = -1;
						// lp.setMargins((screenWidth - 80) / 2 ,
						// (frontPreview.getHeight() /2) +
						// frontPreview.getHeight(), 0,0);
						// Log.i(TAG,
						// "screenWidth = "+screenWidth+" : screenHeight = "+screenHeight+" : frontPreview.getHeight() = "+frontPreview.getHeight()+" : backPreview.getHeight() = "+backPreview.getHeight()+" : mainPreview = "+mainPreview.getHeight()+" : ((screenHeight - 80) * 3 )/4 = "+((screenHeight
						// - 80) * 3 )/4);
						lp.setMargins((screenWidth - 80) / 2,
								(int) ((screenHeight - 80) * 3) / 4, 0, 0);
						// lp.setMargins((screenWidth - 80) / 2
						// ,frontPreview.getHeight(), 0,0);
					} else if (orientationScreen == "LANDSCAPE")
						lp.setMargins(((screenWidth - 80) * 3) / 4,
								(screenHeight - 80) / 2, 0, 0);

					focusMarker.setLayoutParams(lp);
					// mainPreview.addView(focusMarker);
				}

			}

		} else if (focusState == "UNFOCUS") {
			if (focusMarker != null)
				mainPreview.removeView(focusMarker);
			// focusMarker.setImageDrawable(getResources().getDrawable(R.drawable.focusmark2));
		} else if (focusState == "SHOOT") {
			if (focusMarker != null) {
				focusMarker.setImageDrawable(getResources().getDrawable(
						R.drawable.focusmark2));

				// Callback for capture button
				if (touchX == null && touchY == null
						&& performedAction != Field.ActionLongClickEnd) {
					if (cameraSide == "BACK") {
						if (orientationScreen == "PORTRAIT")
							lp.setMargins((screenWidth - 80) / 2,
									(screenHeight - 80) / 4, 0, 0);
						else if (orientationScreen == "LANDSCAPE")
							lp.setMargins((screenWidth - 80) / 4,
									(screenHeight - 80) / 2, 0, 0);

						focusMarker.setLayoutParams(lp);
					} else if (cameraSide == "FRONT") {
						if (orientationScreen == "PORTRAIT")
							lp.setMargins((screenWidth - 80) / 2,
									((screenHeight - 80) * 3) / 4, 0, 0);
						else if (orientationScreen == "LANDSCAPE")
							lp.setMargins(((screenWidth - 80) * 3) / 4,
									(screenHeight - 80) / 2, 0, 0);

						focusMarker.setLayoutParams(lp);
					}

				}

			}

		}

	}
	
	public CountDownTimer cameraSetFocus(final String focusState,
			final Float touchX, final Float touchY, final View beingTouched) {
		//CountDownTimer camera = new CountDownTimer(500, 100) {
		CountDownTimer camera = new CountDownTimer(1000,1000) {
			public void onTick(long millisUntilFinished) {
				touchAction = Field.ActionStateNotClickable;
				cameraAction = Field.CameraFocusing;
				
				if(jutsuAction == Field.singleTapCaptureJutsu)
				{

				}
				else if(jutsuAction == Field.doubleTapCaptureJutsu)
				{
					
				}
				else if(jutsuAction == Field.longTapCaptureJutsu)
				{
					if (performedAction == Field.ActionClick || performedAction == Field.ActionLongClick)
						performedAction = Field.ActionLongClick;
				}
				else if(jutsuAction == Field.noTapCaptureJutsu)
				{
					
				}
				else if(jutsuAction == Field.standbyToCaptureJutsu)
				{
					
				}
			}

			public void onFinish() {
				// setFocusMarker( "SHOOT", null, null, null);
				Log.i(TAG, "@cameraSetFocus "
						+" :performedAction = " +performedAction
						+" :cameraAction = "	+cameraAction
						+" :touchAction = " 	+touchAction
						+" :jutsuAction = " 	+jutsuAction
						+" :cameraSide = "		+cameraSide
						+" :hasCameraFocus = "	+hasCameraFocus);
				if (mCamera != null) {
					mCamera.autoFocus(new AutoFocusCallback() {
						@Override
						public void onAutoFocus(boolean arg0, Camera arg1) {

							cameraAction = Field.CameraFocusingEnd;
							touchAction = Field.ActionStateClickable;
							setFocus("SHOOT", performedAction, touchX, touchY,
									beingTouched);
							
							if(jutsuAction == Field.singleTapCaptureJutsu)
							{
								takeAShot();
							}
							else if(jutsuAction == Field.doubleTapCaptureJutsu)
							{
								
							}
							else if(jutsuAction == Field.longTapCaptureJutsu)
							{
								performedAction = Field.ActionLongClickEnd;
								takeAShot();
							}
							else if(jutsuAction == Field.noTapCaptureJutsu)
							{
								
							}
							else if(jutsuAction == Field.standbyToCaptureJutsu)
							{
								
							}
							
						}
					});

				}

			}
		};

		return camera;
	}
	
	public void takeAShot() {
		
		if (mCamera != null && cameraAction != Field.CameraCannotCapture) {
			try {
				//added by gelo
	    		bgMusicUtility("pause");
				mCamera.setErrorCallback(ec);
				
				//etong method ang take a pic na mismo :D
				//mCamera.takePicture(shutterKACHANG, null, getPic);

			} catch (Exception e) {
				Log.i(TAG, "Error at capture button : e = " + e.getCause());
				Toast.makeText(getActivity().getApplicationContext(), errorMessage,
						Field.SHOWTIME).show();
			}

		}
	}
	
	public ErrorCallback ec = new ErrorCallback() {

		@Override
		public void onError(int data, Camera camera) {
			Log.i(TAG, "ErrorCallback received");
		}

	};
	
	private final ShutterCallback shutterKACHANG = new ShutterCallback() {
        public void onShutter() {
            AudioManager mgr = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);
        }
    };


	private View.OnClickListener superButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        	if(v.getId() == R.id.captureButton){
        		
        	}	
        	else if(v.getId() == R.id.textButton){
        		
        	}
        	//... etc.
        	

        }
    };
    
    public class setTouchMode implements View.OnTouchListener {

		@Override
		public boolean onTouch(final View view, final MotionEvent event) {
			// Toast.makeText(getApplicationContext(),
			// "Number of points = "+event.getPointerCount(),
			// Field.SHOWTIME).show();
//			Log.i(TAG, "touchAction = " + touchAction + " : performedAction = " + performedAction);
//			longClickTimer = cameraFocus();
//			touchCount = event.getPointerCount();
//			
//			if (touchAction != Field.ActionStateNotClickable)
//				switch (event.getActionMasked()) {
//				case MotionEvent.ACTION_POINTER_DOWN:
//					// Listen zoom event
//					Log.i(TAG, "MotionEvent = ACTION_POINTER_DOWN");
//					topTapCount = 0;
//					bottomTapCount = 0;
//
//					performedAction = Field.ActionZoom;
//					touchAction = Field.ActionZoom;
//
//					setFocus("UNFOCUS", performedAction, null, null, null);
//
//					// Cancel all Timer
////					if (cameraFocusTimer != null)
////						cameraFocusTimer.cancel();
//
//					if (longClickTimer != null)
//						longClickTimer.cancel();
//
//					firstPointer = event.getPointerId(0);
//					secondPointer = event.getPointerId(1);
//					firstPointerIndex = event.findPointerIndex(firstPointer);
//					secondPointerIndex = event.findPointerIndex(secondPointer);
//
//					// pointerDistance =
//					// Math.abs(event.getX(firstPointerIndex)/event.getY(firstPointerIndex)
//					// -
//					// event.getX(secondPointerIndex)/event.getY(secondPointerIndex));
//					Float pointX = Math.abs(event.getX(firstPointerIndex)
//							- event.getX(secondPointerIndex));
//					Float pointY = Math.abs(event.getY(firstPointerIndex)
//							- event.getY(secondPointerIndex));
//					// + Math.abs(event.getY(firstPointerIndex) -
//					// event.getY(secondPointerIndex))^2
//					pointerDistance = (float) Math.sqrt((pointX * pointX)
//							+ (pointY * pointY));
//					// Log.i(TAG,
//					// "firstPointerIndex = "+firstPointerIndex+" : secondPointerIndex = "+secondPointerIndex);
//					break;
//
//				case MotionEvent.ACTION_DOWN:
//					
//					//Single tap capture
//					ninjaMoves(view,event,movingJutsu);
//					
//					//Double tap capture
//					//ninjaMoves(view,event,Field.doubleTapCaptureJutsu);
//					
//					//Double tap capture
//					//ninjaMoves(view,event,Field.noTapCaptureJutsu);
//					
//					//Double tap capture
//					//ninjaMoves(view,event,Field.longTapCaptureJutsu);
//					
//					
//					break;
//
//				case MotionEvent.ACTION_UP:
//					//Single tap capture
//					ninjaMoves(view,event,movingJutsu);
//					
//					//Double tap capture
//					//ninjaMoves(view,event,Field.doubleTapCaptureJutsu);
//					
//					//Double tap capture
//					//ninjaMoves(view,event,Field.noTapCaptureJutsu);
//					
//					//Double tap capture
//					//ninjaMoves(view,event,Field.longTapCaptureJutsu);
//					
//					break;
//
//				case MotionEvent.ACTION_POINTER_UP:
//					// Drop zoom event
//					Log.i(TAG, "MotionEvent = ACTION_POINTER_UP");
//					if (touchAction == Field.ActionZoom
//							|| touchAction == Field.ActionZooming) {
//						performedAction = Field.ActionZoomEnd;
//						touchAction = Field.ActionZoomEnd;
//					}
//					// End event of zoom
//					// Cancel the action, there's nothing more to do
//					break;
//
//				case MotionEvent.ACTION_MOVE:
//					// Execute zoom event
//					Log.i(TAG, "MotionEvent = ACTION_MOVE");
//					// setFocusMarker("UNFOCUS");
//					if (performedAction == Field.ActionZoom
//							|| performedAction == Field.ActionZooming) {
//						performedAction = Field.ActionZooming;
//						touchAction = Field.ActionZooming;
//
//						if (event.getPointerCount() > 1) {
//
//							// Log.i(TAG,
//							// "Finger 1 = "+event.getX(firstPointerIndex));
//							// Log.i(TAG,
//							// "Finger 2 = "+event.getX(secondPointerIndex));
//							// pointerDistance =
//							// Math.abs(event.getX(firstPointerIndex)/event.getY(firstPointerIndex)
//							// -
//							// event.getX(secondPointerIndex)/event.getY(secondPointerIndex));
//							Float pointXC = Math.abs(event
//									.getX(firstPointerIndex)
//									- event.getX(secondPointerIndex));
//							Float pointYC = Math.abs(event
//									.getY(firstPointerIndex)
//									- event.getY(secondPointerIndex));
//							// + Math.abs(event.getY(firstPointerIndex) -
//							// event.getY(secondPointerIndex))^2
//							changedPointerDistance = (float) Math
//									.sqrt((pointXC * pointXC)
//											+ (pointYC * pointYC));
//							// changedPointerDistance =
//							// Math.abs(event.getX(firstPointerIndex)/event.getY(firstPointerIndex)
//							// -
//							// event.getX(secondPointerIndex)/event.getY(secondPointerIndex));
//							Log.i(TAG, "Distance of both fingers = "
//									+ pointerDistance + " : "
//									+ changedPointerDistance);
//							if (pointerDistance > changedPointerDistance) {
//								// Do zoom in
//								doZoom(pointerDistance, changedPointerDistance);
//								pointerDistance = changedPointerDistance;
//							} else if (pointerDistance < changedPointerDistance) {
//								// Do zoom out
//								doZoom(pointerDistance, changedPointerDistance);
//								pointerDistance = changedPointerDistance;
//							}
//						}
//
//					}
//					break;
//
//				}
//
//			Log.i(TAG, "Number of points = " + event.getPointerCount()
//					+ " : Event = " + event.getAction());
			return true;
		}

	}
    
	public void bgMusicUtility(String action){
//		
//		if(action == "initialize"){
//			mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.loopingmelody);
//			mMediaPlayer.setLooping(true);
//			
//			if(melodyStatus == Field.MODE_Melody_ON)
//				mMediaPlayer.start();
//		}
//		else if(action == "pause"){
//			mMediaPlayer.pause();
//		}
//		else if(action == "stop"){
//			mMediaPlayer.stop();
//		}
//		
//		if(melodyStatus == Field.MODE_Melody_ON){
//
//			if(action == "start"){
//				mMediaPlayer.start();
//			}
//			
//			else if(action == "release"){
//				releaseSound();
//			}
//			else if(action == "captureresume"){
//				new CountDownTimer(500,250) {
//					
//					public void onTick(long millisUntilFinished) {
//						
//					}
//					
//					public void onFinish() {
//						mMediaPlayer.start();
//					}
//				}.start();
//			}
//		}
//		
//		
	}
	
	//Freedom Methods
	public void releaseCamera() {
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
	}

	
}
