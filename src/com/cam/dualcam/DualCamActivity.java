package com.cam.dualcam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.Surface;
import android.view.View;
import android.view.Menu;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.ImageView.ScaleType;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.SeekBar;
import android.widget.TextView;
import android.view.OrientationEventListener;

import com.cam.dualcam.utility.*;
import com.cam.dualcam.utility.ColorPickerDialog.*;
import com.cam.dualcam.bitmap.*;
import com.cam.dualcam.twitter.*;
import com.cam.dualcam.view.*;
import com.cam.dualcam.widget.LoadingDialog;
import com.cam.dualcam.SharingDialog;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.hintdesk.core.util.StringUtil;



@SuppressLint("NewApi")
public class DualCamActivity extends Activity implements OnClickListener,
		OnColorChangedListener {

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

	private String ok;
	private String cancel;
	private String yes;
	private String no;

	public static String TAG = "DualCamActivity";
	private Bitmap fileBitmap = null;
	public static File   filePath	= null;
	private String fileName = null;
	private String cameraSide = null;
	private String orientationScreen = null;

	private Bitmap tempPic = null;
	private Bitmap frontPic = null;
	private Bitmap backPic = null;
	public BitmapFactory.Options options = null;

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
	
	private boolean showSpalshScreen = true;

	public Integer screenHeight;
	public Integer screenWidth;

	public static int result = 0;
	public static int degrees = 0;
	public static int orientationOfPhone = 0;
	public static int sdk = android.os.Build.VERSION.SDK_INT;

	// Utility
	public PackageCheck packageCheck;
	public static MediaUtility mediaUtility;
	public static CameraUtility cameraUtility;
	public static HideAct hideAct;
	public static ColorPickerDialog colorPickerDialog;
	public Intent sharingIntent;

	// Camera Settings
	public Parameters param;
	public Camera mCamera;
	public Integer maxCameraZoom;
	public Integer currentCameraZoom;
	public Integer cameraAction;
	// Widgets

	// Previews
	public ImageView backPreview, frontPreview, previewImage;

	public LinearLayout pictureLayout, utilityLayout;
	public RelativeLayout toSaveLayout;
	public FrameLayout mainPreview, createTextFrameLayout;
	public CameraPreview cameraPreview;

	// Buttons
	public ImageView captureButton, textButton, saveButton, retryButton,
			shareButton, focusMarker;

	// Touch and click events
	public Integer bottomTapCount = 0;
	public Integer topTapCount = 0;
	public Integer tapCount = 0;
	public Integer touchCount;
	public Integer tCount;
	public Integer performedAction;
	public Integer touchAction;
	public Integer jutsuAction;
	public Integer firstPointer;
	public Integer firstPointerIndex;
	public Integer secondPointer;
	public Integer secondPointerIndex;
	public Float pointerDistance;
	public Float changedPointerDistance;

	public CountDownTimer longClickTimer;
	public CountDownTimer cameraFocusTimer;
	public CountDownTimer doubleTapTimer;

	// For additional Features
	// Add text
	public static int fontSize = 1;
	public static String textToShow;
	public TextView addedText;
	public static int charCount;
	public RelativeLayout.LayoutParams textFrameLayoutParams;

	// Changes by Aid
	public static int fontColor; // aid
	private static final String COLOR_PREFERENCE_KEY = "color"; // aid
	private static int initialColor;
	private int addedTextX = 0;
	private int addedTextY = 0;
	private int defaultTextSize = 40;
	private int minimumTextSize = 31;
	private int maximumTextSize = 79;
	private int frameLayoutX = 0;
	private int frameLayoutY = 0;
	private int frameLayoutW = 0;
	private int frameLayoutH = 0;
	public ImageView okButton,noButton;
	// Changes by Aid
	

	//Pop up items
	public AlertDialog popUpDialog;
	public CheckBox  afc1Box, afc2Box, afc3Box;
	public CheckBox  flash1Box, flash2Box, flash3Box;
	public CheckBox  melody1Box, melody2Box;
	public CheckBox  or0Box,or1Box, or2Box;
	public TextView  optionafc1, optionafc2, optionafc3;
	public TextView  optionflash1, optionflash2, optionflash3;
	public TextView  optionmelody1, optionmelody2;
	public TextView  optionor0,optionor1, optionor2;
	public LinearLayout afc1Linear,afc2Linear,afc3Linear,
						flash1Linear,flash2Linear,flash3Linear,
						melody1Linear,melody2Linear,or0Linear,or1Linear,or2Linear;
	
	public int movingJutsu;
	public int flashStatus;
	public int melodyStatus;
	public int orStatus;
	
	//Sound items
	public MediaPlayer mMediaPlayer;
	private Bundle savedInstanceState;
	
	//Facebook objects
	private SetMyFBSession myFBSession;
	private Session mySession;
	public Bundle globalBundle;
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = 
	     new Session.StatusCallback() {
	     @Override
	     public void call(Session session, 
	             SessionState state, Exception exception) {
	         //onSessionStateChange(session, state, exception);
	     }
	 };
	 private LoadingDialog loading;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(DualCamActivity.this, callback);
	    uiHelper.onCreate(savedInstanceState);
		letThereBeLight(savedInstanceState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case Field.FILE_SELECT_CODE:
			if (resultCode == RESULT_OK) {
				try {
					// Get the Uri of the selected file
					Uri uri = data.getData();
					Log.d(TAG, "File Uri: " + uri.toString());
					// Get the path
					fileName = mediaUtility.getPath(getApplicationContext(),
							uri);
					isSavePathset = true;
					Log.d(TAG, "File Path: " + fileName);
					linkSTART();
					// Get the file instance
					// File file = new File(path);
					// Initiate the upload
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}

			break;

		case DirectoryPicker.PICK_DIRECTORY:
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				fileName = (String) extras
						.get(DirectoryPicker.CHOSEN_DIRECTORY);
				isSavePathset = true;
				// do stuff with path
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void onClick(View view) {

		try {
			if (view.getId() == R.id.smileyButton) {
				// setFocusMarker("FOCUS", null,null,null);
				if (isCameraFocused)
					takeAShot();
				
			}

			else if (view.getId() == R.id.textButton) {

				// if(isSavable)
				try {
					if (isTextEditable)
						setEditText();
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), errorMessage,
							Field.SHOWTIME).show();
				}
				// else
				// Toast.makeText(getApplicationContext(),"You don't want a pic of yourself?",Field.SHOWTIME).show();

			}

			else if (view.getId() == R.id.saveButton) {

				if (isSavable)
					try {
						loading.show();
						toSaveLayout.buildDrawingCache();
						saveImage(toSaveLayout.getDrawingCache());
						toSaveLayout.destroyDrawingCache();
						// saveImage();
					} catch (Exception e) {
						Toast.makeText(getApplicationContext(), errorMessage,
								Field.SHOWTIME).show();
					}
				// else
				// Toast.makeText(getApplicationContext(),"You don't want a pic of yourself?",Field.SHOWTIME).show();

			}

			else if (view.getId() == R.id.retryButton) {
				if (isRetryable) {

					AlertDialog.Builder retryDialog = new AlertDialog.Builder(
							DualCamActivity.this);

					// set title
					// retryDialog.setTitle("");

					// set dialog message
					retryDialog
							.setMessage(restartMessage)
							.setCancelable(false)
							.setPositiveButton(yes,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											linkSTART();
										}
									})
							.setNegativeButton(no,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {

										}
									});

					// create alert dialog
					AlertDialog alert = retryDialog.create();
					alert.show();

				}
			}

			else if (view.getId() == R.id.shareButton) {
				try {
					Log.i(TAG, "isSharable = " + isSharable);
					if (isSharable)
						shareFunction(); 
						//shareFunctionInActivity();
				} catch (Exception e) {
					Log.i(TAG, "isSharable = " + isSharable);
					Log.i(TAG, "ERROR = " + e.getCause());
				}
			}
 
			
			else if (view.getId() == R.id.cumPreviewBack) {
				Log.i(TAG, ".cumPreviewBack is clicked");
				if (isSavable) {
					// setSide("BACK");
					retakeImage("BACK");
					// createAlert("","","");

				} else if (isBackTaken && !isFrontTaken) {
					retakeImage("BACK");
					ImageView buttonView = getPressedPreview("FRONT");
					buttonView.setImageDrawable(getResources().getDrawable(
							R.drawable.previewfront));
				} else {
					if (cameraSide == "BACK")
						takeAShot();
				}
				// else{
				// mCamera.autoFocus(new AutoFocusCallback(){
				// @Override
				// public void onAutoFocus(boolean arg0, Camera arg1) {
				// //camera.takePicture(shutterCallback, rawCallback,
				// jpegCallback);
				// }
				// });
				// }
			}

			else if (view.getId() == R.id.cumPreviewFront) {
				if (isSavable) {
					// setSide("FRONT");
					retakeImage("FRONT");
				} else {
					if (cameraSide == "FRONT")
						takeAShot();
				}
				// else{
				// mCamera.autoFocus(new AutoFocusCallback(){
				// @Override
				// public void onAutoFocus(boolean arg0, Camera arg1) {
				// //camera.takePicture(shutterCallback, rawCallback,
				// jpegCallback);
				// }
				// });
				// }
				//
				// if(cameraSide == "BACK")
				// {
				// try{
				// //mCamera.takePicture(null, null, s3FixIloveS3);
				// mCamera.setErrorCallback(ec);
				// mCamera.takePicture(null, null, mPicture);
				// //Toast.makeText(getApplicationContext(),"Nice shot!",Field.SHOWTIME).show();
				//
				// }catch(Exception e){
				// //mCamera.takePicture(null, null, s3FixIloveS3);
				// Toast.makeText(getApplicationContext(),errorMessage,Field.SHOWTIME).show();
				//
				// }
				// }
			}

			else if (view.getId() == R.id.cumshot) {
				// if(isSavable){
				// if(cameraSide == "BACK")
				// setSide("BACK");
				// else if(cameraSide == "FRONT")
				// setSide("FRONT");
				// }

			}
			
			else if (view.getId() == R.id.okButton) {
				
				
				textFeatureUtility("hide");
			}

			else if (view.getId() == R.id.noButton) {
				createTextFrameLayout.removeAllViews();
				
				textFeatureUtility("hide");
			}
			
		} catch (Exception e) {
			Log.i(TAG, "Error in here View = " + view.getId()
					+ ": Cause? I don't effing know -> " + e.getMessage());
			Toast.makeText(this, errorMessage, Field.SHOWTIME).show();
		}
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event)
	// {
	// if(keyCode == KeyEvent.KEYCODE_HOME)
	// {
	// relenquishTheSoul();
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	// @Override
	// public void onBackPressed() {
	// // do something on back.
	// Log.i(TAG,"BACK is pressed");
	// return;
	// }

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		Log.i(TAG, "KeyCode = " + keyCode);
//		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//			relenquishTheSoul();
//			return true;
//		}
//		// else if(keyCode == KeyEvent.KEYCODE_HOME){
//		// relenquishTheSoul();
//		// return true;
//		// }
//
//		return super.onKeyDown(keyCode, event);
//	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
		myFBSession.storeMySession();
		mySession.closeAndClearTokenInformation();
		Log.i(TAG, "Destroying onDestroy : killMe = "+ killMe);
		//releaseCamera();
		unleashTheKraken();
		if(!killMe){
			 // killMe = false;
			Log.i(TAG, "Destroying onDestroy : releasing onDestroy");
			 relenquishTheSoul();
		}

	}

	
	
	//Aid watch out
	 @Override
	 public void onConfigurationChanged(Configuration newConfig) {
	 super.onConfigurationChanged(newConfig);
	
	 // Checks the orientation of the screen
		 if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			 killMe = true;
			 //Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
			 //setContentView(R.layout.dualcam);
			 linkRESTART();
		 } 
		 else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
			 killMe = true;
			 //Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
			 linkRESTART();
		 }
	 }
	//Aid watch out
	 
	 

	// @Override
	// public void onConfigurationChanged(Configuration newConfig)
	// {
	// super.onConfigurationChanged(newConfig);09164508877
	// killMe = true;
	// Log.i(TAG, "OnConfChange");
	// }

	 @Override
	 protected void onResume() {
	 super.onResume();
	 uiHelper.onResume();
//	 linkSTART();
	
	 }

	@Override
	protected void onPause() {
		super.onPause();
		uiHelper.onPause();
		Log.i(TAG, "Destroying onPause : killMe = " + killMe);
		//releaseCamera();
		unleashTheKraken();
		if(!killMe){
			 // killMe = false;
			Log.i(TAG, "Destroying onPause : releasing onPause");
			relenquishTheSoul();
		}

	}
	
//	@Override
//	protected void onStop() {
//		super.onStop();
//		Log.i(TAG, "Destroying onPause : killMe = "
//				+ killMe);
//		releaseCamera();
//		// if(!killMe){
//		// // killMe = false;
//		relenquishTheSoul();
//		// }
//
//	}

	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
		 //getMenuInflater().inflate(R.menu.dual_cam, menu);
		 //Toast.makeText(getApplicationContext(), "OptionsMenu is clicked", Field.SHOWTIME).show();
//		 popUpMenu.show(this.getFragmentManager(), "popUpMenu");
		 //customPopUpMenu().show();
		 if(popUpDialog != null)
			 popUpDialog.show();
		 return false;
		 }
//		
//	 public boolean onOptionsItemSelected(MenuItem item) {
//		 (Menu
//		 switch (item.getItemId()) {
//	//		 case R.id.savePath:
//	//		 //startActivity(new Intent(this, About.class));
//	//		 showFileChooser();
//	//		 return true;
//		 
//		 case R.id.afcmanual:
//			 //startActivity(new Intent(this, About.class));
//			 //showFileChooser();
//			 return true;
//		 default:
//		 return super.onOptionsItemSelected(item);
//		 }
//	 }
//		 
//	 @Override
//	 public boolean onPrepareOptionsMenu(Menu menu) {
//	     super.onPrepareOptionsMenu(menu);
//	     //menu.findItem(R.id.sort_by_name).setChecked(true);
//	     return true;
//	 }	 

	@Override
	public void onSaveInstanceState(Bundle toSave) {
		super.onSaveInstanceState(toSave);
		uiHelper.onSaveInstanceState(toSave);
		// killMe = true;
		Log.i(TAG, "from onSaveInstance killMe = "+killMe);
		toSave.putBoolean("isBackTaken", isBackTaken);
		toSave.putBoolean("isFrontTaken", isFrontTaken);
		toSave.putBoolean("isSavable", isSavable);
		toSave.putBoolean("isSharable", isSharable);
		toSave.putBoolean("isSavePathset", isSavePathset);
		toSave.putBoolean("isTextEditable", isTextEditable);
		toSave.putBoolean("isTextAdded", isTextAdded);
		toSave.putBoolean("isRetryable", isRetryable);
		// toSave.putBoolean("killMe", killMe);
		toSave.putInt("fontSize", fontSize);

		if (frontPic != null)
			toSave.putParcelable("frontPic", frontPic);

		if (backPic != null)
			toSave.putParcelable("backPic", backPic);

		if (cameraSide != null)
			toSave.putString("cameraSide", cameraSide);

		if (fileName != null)
			toSave.putString("fileName", fileName);

		if (textToShow != null)
			toSave.putString("textToShow", textToShow);

		// if(mCamera != null)
		// toSave.put

	}

	// Custom Methods
	public void addText() {

	}

	public CountDownTimer doDoubleTap() {
		CountDownTimer longClick = new CountDownTimer(10000, 500) {

			public void onTick(long millisUntilFinished) {
				// Log.i(TAG," <------------------- millisUntilFinished : "+millisUntilFinished
				// );
				// if(performedAction == Field.ActionClickEnd)
				// performedAction = Field.ActionLongClick;
				if (performedAction == Field.ActionClick
						|| performedAction == Field.ActionClickEnd)
					isDoubleTapAction = true;
			}

			public void onFinish() {
				isDoubleTapAction = false;
				tapCount = 0;

			}
		};

		return longClick;
	}
	
//	public void cameraSetFocus(final String focusState,
//			final Float touchX, final Float touchY, final View beingTouched){
//		
//		touchAction = Field.ActionStateNotClickable;
//		cameraAction = Field.CameraFocusing;
//		
//		if(mCamera != null)
//			mCamera.autoFocus(new AutoFocusCallback() {
//				@Override
//				public void onAutoFocus(boolean arg0, Camera arg1) {
//					cameraAction = Field.CameraFocusingEnd;
//					touchAction = Field.ActionStateClickable;
//					setFocus("SHOOT", performedAction, touchX, touchY,
//							beingTouched);
//					
//				}
//			});
//
//	}

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
							// performedAction = Field.ActionLongClickEnd;
							// setFocusMarker("SHOOT",touchX,
							// touchY,beingTouched);
							// if(performedAction != Field.ActionAutoFocus)
							// doubleTapTimer.start();
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
							
							// Log.i(TAG,
							// "cameraSetFocus : touchAction = "+touchAction);
							// if(touchAction != Field.ActionAutoFocus){
							// touchAction = Field.ActionFocusingEnd;
							// s("SHOOT",
							// performedAction,
							// touchX,
							// touchY,
							// beingTouched);
							// }
							// else if(touchAction == Field.ActionAutoFocus){
							// touchAction = Field.ActionFocusingEnd;
							// setFocus("SHOOT",
							// performedAction,
							// null,
							// null,
							// null);
							// }

							// if(mCamera != null && isDoubleTapAction){
							// //doDoubleTap().cancel();
							// Log.i(TAG, "Is Double tap");
							// takeAShot();
							// isDoubleTapAction = false;
							// tapCount = 0;
							//
							// }
						}
					});

				}

			}
		};

		return camera;
	}

	public CountDownTimer cameraFocus() {
		CountDownTimer longClick = new CountDownTimer(2000, 1000) {

			public void onTick(long millisUntilFinished) {
				if (performedAction == Field.ActionClick)
					performedAction = Field.ActionLongClick;
			}

			public void onFinish() {

				if (mCamera != null && performedAction == Field.ActionLongClick) {
					mCamera.autoFocus(new AutoFocusCallback() {
						@Override
						public void onAutoFocus(boolean arg0, Camera arg1) {
							performedAction = Field.ActionLongClickEnd;
							// setFocusMarker("SHOOT",null, null,null);
							if (mCamera != null) {
								takeAShot();
							}
						}
					});

				}

			}
		};

		return longClick;
	}

	public void createAlert(String currentFunction, String thisside,
			String thismessage) {
		final String title = currentFunction;
		final String side = thisside;
		final String message = thismessage;

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				DualCamActivity.this);

		// set title
		// alertDialogBuilder.setTitle(title);

		// set dialog message
		alertDialogBuilder.setMessage(message).setCancelable(false)
				.setPositiveButton(yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (title == "retake") {
							isRetaking = true;
							if (isBackTaken && !isFrontTaken) {
								ImageView buttonView = getPressedPreview("FRONT");
								buttonView.setBackgroundDrawable(getResources()
										.getDrawable(R.drawable.whitebg));
								buttonView.setImageDrawable(getResources()
										.getDrawable(R.drawable.previewfront));
							}

							Log.i(TAG, "Initiating Retake :D");
							
							jutsuAction = Field.standbyToCaptureJutsu;
							releaseCamera();
							setSide(side);
						}
					}
				}).setNegativeButton(no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						isRetaking = false;

						cameraAction= Field.CameraCanCapture;
						touchAction = Field.ActionStateClickable;
						jutsuAction = Field.standbyToCaptureJutsu;
					}
				});

		// create alert dialog
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();

	}

	public void createAText() {

		// this is the method to create text on the picture
		// RelativeLayout rlv = (RelativeLayout)findViewById(R.id.buttonLayout);

		textFrameLayoutParams = (RelativeLayout.LayoutParams) createTextFrameLayout
				.getLayoutParams();
		final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				new FrameLayout.MarginLayoutParams(
						FrameLayout.LayoutParams.WRAP_CONTENT,
						FrameLayout.LayoutParams.WRAP_CONTENT));

		// layoutParams.addRule(RelativeLayout.ABOVE);
		addedText = new TextView(getApplicationContext());
		// addedText.setTextSize(50);
		// addedText.setGravity(Gravity.END);
		// addedText.setGravity(Gravity.BOTTOM);
		// addedText.setGravity(Gravity.RIGHT);
		addedText.setText("");
		addedText.setTextSize(40);
		addedText.setTextColor(Color.RED);
		addedText.setGravity(Gravity.TOP);
		addedText.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getActionMasked()) {
				case MotionEvent.ACTION_DOWN:
					addedTextX = (int) event.getX();
					addedTextY = (int) event.getY();
					// selected_item = v;
					
					
					break;
				default:
					break;
				}

				return false;
			}
		});

		createTextFrameLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getActionMasked()) {
				// case MotionEvent.ACTION_DOWN:
				// addedTextX -= (int)event.getX();
				// addedTextY -= (int)event.getY();
				// //
				// break;

				case MotionEvent.ACTION_MOVE:
					frameLayoutX = (int) event.getX() - addedTextX;
					frameLayoutY = (int) event.getY() - addedTextY;

					frameLayoutW = getWindowManager().getDefaultDisplay()
							.getWidth() - 100;
					frameLayoutH = getWindowManager().getDefaultDisplay()
							.getHeight() - 100;

					if (frameLayoutX > frameLayoutW)
						frameLayoutX = frameLayoutW;
					if (frameLayoutY > frameLayoutH)
						frameLayoutY = frameLayoutH;

					lp.setMargins(frameLayoutX, frameLayoutY, 0, 0);
					lp.gravity = Gravity.TOP;
					addedText.setLayoutParams(lp);
					break;

				default:
					break;
				}
				return true;
			}
		});

		// layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, 1);
		// layoutParams.setMargins(screenWidth - (textToShow.length() *
		// fontSize),0, 0,0);
		// layoutParams.setMargins(0, (screenHeight - (saveButton.getHeight())),
		// 0,0);
		// layoutParams.setMargins(screenWidth - (textToShow.length() *
		// fontSize), (screenHeight - (fontSize *2)), 0,0);

		// layoutParams.setMargins(screenWidth - (80), (screenHeight -
		// (saveButton.getHeight() * 2)), 0,0);
		textFrameLayoutParams.setMargins(10, 10, 0, 0);
		textFrameLayoutParams.addRule(RelativeLayout.ABOVE);
		addedText.setLayoutParams(textFrameLayoutParams);

//		isTextAdded = true;
//		isTextEditable = true;
//		setButton(textButton);
		//setButtons(isSharable, isSavable, isTextEditable, isRetryable);
		createTextFrameLayout.addView(addedText, textFrameLayoutParams);
		createTextFrameLayout.bringToFront();
		// Log.i(TAG, ":D = "addedText.isShown());
		// rlv.bringToFront();
	}
	
	//The customPupUpMenu
	public AlertDialog customPopUpMenu(){
		AlertDialog.Builder popUpMenu = new AlertDialog.Builder(DualCamActivity.this);
		ScrollView sv = new ScrollView(DualCamActivity.this);
		
		LinearLayout menuLinear = new LinearLayout(DualCamActivity.this);
		menuLinear.setOrientation(1);
		
		/*menuLinear.addView(newLine(getResources().getString(R.string.mainctitle),"TITLE"));
		
		Button btnHome = new Button(this);
		//btnHome.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		btnHome.setText("Home");  
		btnHome.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(DualCamActivity.this, SocialMediaActivity.class); 
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.putExtra("showSplashScreen", false);
				startActivity(i);
			}
		});
	    menuLinear.addView(btnHome);*/
		
	    Button btnFacebookLogout = new Button(this);
	    //btnFacebookLogout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	    btnFacebookLogout.setText("Facebook Log Out");  
	    btnFacebookLogout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	    menuLinear.addView(btnFacebookLogout);
	    
	    Button btnTwitterLogout = new Button(this);
	    //btnTwitterLogout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	    btnTwitterLogout.setText("Twitter Log Out"); 
	    btnTwitterLogout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				twitterLogout();
			}
		});
	    menuLinear.addView(btnTwitterLogout);
	    
		menuLinear.addView(newLine(getResources().getString(R.string.afctitle),"TITLE"));
		menuLinear.addView(newLine(getResources().getString(R.string.afc1),"OPTION"));
		menuLinear.addView(newLine(getResources().getString(R.string.afc2),"OPTION"));
		menuLinear.addView(newLine(getResources().getString(R.string.afc3),"OPTION"));
		
		menuLinear.addView(newLine(getResources().getString(R.string.flashtitle),"TITLE"));
		menuLinear.addView(newLine(getResources().getString(R.string.flash1),"OPTION"));
		menuLinear.addView(newLine(getResources().getString(R.string.flash2),"OPTION"));
		menuLinear.addView(newLine(getResources().getString(R.string.flash3),"OPTION"));
		
		menuLinear.addView(newLine(getResources().getString(R.string.melodytitle),"TITLE"));
		menuLinear.addView(newLine(getResources().getString(R.string.melody1),"OPTION"));
		menuLinear.addView(newLine(getResources().getString(R.string.melody2),"OPTION"));
		
		menuLinear.addView(newLine(getResources().getString(R.string.orientationtitle),"TITLE"));
		menuLinear.addView(newLine(getResources().getString(R.string.orientation0),"OPTION"));
		menuLinear.addView(newLine(getResources().getString(R.string.orientation1),"OPTION"));
		menuLinear.addView(newLine(getResources().getString(R.string.orientation2),"OPTION"));
		
//		menuLinear.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Log.i(TAG, "View ID = "+v.getTag().toString());
//			}
//		});afterSoundStatus
		
		//Set the checked value for AutoFocus
		switch(movingJutsu){
		case Field.noTapCaptureJutsu:
			
				afc1Box.setChecked(true);
				afc2Box.setChecked(false);
				afc3Box.setChecked(false);
				changeSettings(Field.SET_AutoFocus,movingJutsu);
			break;
			
		case Field.singleTapCaptureJutsu:
			
			afc1Box.setChecked(false);
			afc2Box.setChecked(true);
			afc3Box.setChecked(false);
			changeSettings(Field.SET_AutoFocus,movingJutsu);
		break;
		
		case Field.doubleTapCaptureJutsu:
			
			afc1Box.setChecked(false);
			afc2Box.setChecked(false);
			afc3Box.setChecked(true);
			changeSettings(Field.SET_AutoFocus,movingJutsu);
		break;
		
		default:
			changeSettings(Field.SET_AutoFocus,Field.noTapCaptureJutsu);
			break;
		}

		//Set the checked value for Flash
		switch(flashStatus){
		case Field.MODE_Flash_Auto:
				
			flash1Box.setChecked(true);
			flash2Box.setChecked(false);
			flash3Box.setChecked(false);
			changeSettings(Field.SET_Flash,flashStatus);
		break;
			
		case Field.MODE_Flash_ON:
			
			flash1Box.setChecked(false);
			flash2Box.setChecked(true);
			flash3Box.setChecked(false);
			changeSettings(Field.SET_Flash,flashStatus);
		break;
		
		case Field.MODE_Flash_OFF:
			
			flash1Box.setChecked(false);
			flash2Box.setChecked(false);
			flash3Box.setChecked(true);
			changeSettings(Field.SET_Flash,flashStatus);
		break;
		
		default:
			changeSettings(Field.SET_Flash,Field.MODE_Flash_Default);
			break;
		
		}
		
		//Set the melody value for Flash
		switch(melodyStatus){
		case Field.MODE_Melody_ON:
			
			melody1Box.setChecked(true);
			melody2Box.setChecked(false);
			changeSettings(Field.SET_Melody,melodyStatus);
			//bgMusicUtility("start");
		break;
		
		case Field.MODE_Melody_OFF:
			
			melody1Box.setChecked(false);
			melody2Box.setChecked(true);
			changeSettings(Field.SET_Melody,melodyStatus);
			//bgMusicUtility("pause");
		break;
		
		default:
			changeSettings(Field.SET_Melody,Field.MODE_Melody_DEFAULT);
			break;
		
		}
		switch(orStatus){
		case Field.MODE_Auto:
			or0Box.setChecked(true);
			or1Box.setChecked(false);
			or2Box.setChecked(false);
			changeSettings(Field.SET_Orientation,orStatus);
			//bgMusicUtility("start");
		break;
		case Field.MODE_Portrait:
			or0Box.setChecked(false);
			or1Box.setChecked(true);
			or2Box.setChecked(false);
			changeSettings(Field.SET_Orientation,orStatus);
			//bgMusicUtility("start");
		break;
		
		case Field.MODE_Landscape:
			or0Box.setChecked(false);
			or1Box.setChecked(false);
			or2Box.setChecked(true);
			changeSettings(Field.SET_Orientation,orStatus);
			//bgMusicUtility("pause");
		break;
		
		default:
			changeSettings(Field.SET_Orientation,Field.MODE_Orientation_DEFAULT);
			break;
		
		}
		
		
		sv.addView(menuLinear);
		popUpMenu.setView(sv);
		return popUpMenu.create();
	}
	
	public LinearLayout newLine(String itemMessage, String type){
		CheckBox checkBox = new CheckBox(this);
		TextView option = new TextView(this);
		
		LinearLayout optionsLinear = new LinearLayout(this);
		
		
		
		if(type == "TITLE"){
			TextView title = new TextView(this);
			title.setTextSize(24);
			title.setTextColor(Color.GRAY);
			title.setText(itemMessage);
			
//			RelativeLayout optionRelative = new RelativeLayout(this);
//			optionRelative.addView(title);
//			
			optionsLinear.setPadding(10, 0, 0, 0);
			//optionsLinear.setBackgroundColor(Color.BLACK);
			optionsLinear.addView(title);
//			
//
//
//			optionsLinear.addView(optionRelative);
//			
//			
//			RelativeLayout.LayoutParams paramsTEXT = (RelativeLayout.LayoutParams)option.getLayoutParams();
//			paramsTEXT.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//			paramsTEXT.setMargins(20,20,20,20);
//			
//			//paramsTEXT.addRule(RelativeLayout.CENTER_VERTICAL);
//			
//			option.setLayoutParams(paramsTEXT); //causes layout update
//			
//			RelativeLayout.LayoutParams paramsCHECKBOX = (RelativeLayout.LayoutParams)checkBox.getLayoutParams();
//			paramsCHECKBOX.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//			
//			checkBox.setLayoutParams(paramsCHECKBOX); //causes layout update

			
		}
		else if(type == "OPTION"){
			
			if(itemMessage == getResources().getString(R.string.afc1)){
				optionsLinear.setTag(Field.MODE_ManualAF_and_Capture);
				afc1Box = new CheckBox(this);
				optionafc1 = new TextView(this);
				afc1Linear = new LinearLayout(this);
				afc1Linear.setOrientation(0);
				
				optionafc1.setText(itemMessage);
				afc1Linear.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						changeSettings(Field.SET_AutoFocus,Field.noTapCaptureJutsu);
					}
				});
				
				
				afc1Box.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						changeSettings(Field.SET_AutoFocus,Field.noTapCaptureJutsu);
					}
				});
				
				
				checkBox = afc1Box;
				option	 = optionafc1;
				optionsLinear = afc1Linear;
			}
			else if(itemMessage == getResources().getString(R.string.afc2)){
				optionsLinear.setTag(Field.MODE_SingleAF_and_Capture);
				afc2Box = new CheckBox(this);
				optionor1 = new TextView(this);
				afc2Linear = new LinearLayout(this);
				afc2Linear.setOrientation(0);
				optionor1.setText(itemMessage);
				afc2Linear.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						changeSettings(Field.SET_AutoFocus,Field.singleTapCaptureJutsu);
					}
				});
				afc2Box.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						changeSettings(Field.SET_AutoFocus,Field.singleTapCaptureJutsu);
					}
				});
				
				checkBox = afc2Box;
				option	 = optionor1;
				optionsLinear = afc2Linear;
			}
			
			else if(itemMessage == getResources().getString(R.string.afc3)){
				optionsLinear.setTag(Field.MODE_DoubleAF_and_Capture);
				afc3Box = new CheckBox(this);
				optionafc3 = new TextView(this);
				optionafc3.setText(itemMessage);
				
				afc3Linear = new LinearLayout(this);
				afc3Linear.setOrientation(0);
				
				afc3Linear.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						changeSettings(Field.SET_AutoFocus,Field.doubleTapCaptureJutsu);
					}
				});
				afc3Box.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						changeSettings(Field.SET_AutoFocus,Field.doubleTapCaptureJutsu);
					}
				});
				
				checkBox = afc3Box; 
				option	 = optionafc3;
				optionsLinear = afc3Linear;
			}
			
			else if(itemMessage == getResources().getString(R.string.flash1)){
				optionsLinear.setTag(Field.MODE_Flash_Auto);
				flash1Box = new CheckBox(this);
				optionflash1 = new TextView(this);
				optionflash1.setText(itemMessage);
				
				flash1Linear = new LinearLayout(this);
				flash1Linear.setOrientation(0);
				
				flash1Linear.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						changeSettings(Field.SET_Flash,Field.MODE_Flash_Auto);
					}
				});
				flash1Box.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						changeSettings(Field.SET_Flash,Field.MODE_Flash_Auto);
					}
				});
				
				checkBox = flash1Box;
				option	 = optionflash1;
				optionsLinear = flash1Linear;
			}
			
			else if(itemMessage == getResources().getString(R.string.flash2)){
				optionsLinear.setTag(Field.MODE_Flash_ON);
				flash2Box = new CheckBox(this);
				optionflash2 = new TextView(this);
				optionflash2.setText(itemMessage);
				
				flash2Linear = new LinearLayout(this);
				flash2Linear.setOrientation(0);
				
				flash2Linear.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						changeSettings(Field.SET_Flash,Field.MODE_Flash_ON);
					}
				});
				flash2Box.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						changeSettings(Field.SET_Flash,Field.MODE_Flash_ON);
					}
				});
				
				checkBox = flash2Box;
				option	 = optionflash2;
				optionsLinear = flash2Linear;
			}
			
			else if(itemMessage == getResources().getString(R.string.flash3)){
				optionsLinear.setTag(Field.MODE_Flash_OFF);
				flash3Box = new CheckBox(this);
				optionflash3 = new TextView(this);
				optionflash3.setText(itemMessage);
				
				flash3Linear = new LinearLayout(this);
				flash3Linear.setOrientation(0);
				
				flash3Linear.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						changeSettings(Field.SET_Flash,Field.MODE_Flash_OFF);
					}
				});
				flash3Box.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						changeSettings(Field.SET_Flash,Field.MODE_Flash_OFF);
					}
				});
				
				checkBox = flash3Box;
				option	 = optionflash3;
				optionsLinear = flash3Linear;
			}
			
			else if(itemMessage == getResources().getString(R.string.melody1)){
				optionsLinear.setTag(Field.MODE_Melody_ON);
				melody1Box = new CheckBox(this);
				optionmelody1 = new TextView(this);
				optionmelody1.setText(itemMessage);
				
				melody1Linear = new LinearLayout(this);
				melody1Linear.setOrientation(0);
				
				melody1Linear.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						changeSettings(Field.SET_Melody,Field.MODE_Melody_ON);
					}
				});
				melody1Box.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						changeSettings(Field.SET_Melody,Field.MODE_Melody_ON);
					}
				});
				
				checkBox = melody1Box;
				option	 = optionmelody1;
				optionsLinear = melody1Linear;
			}
			
			else if(itemMessage == getResources().getString(R.string.melody2)){
				optionsLinear.setTag(Field.MODE_Melody_OFF);
				melody2Box = new CheckBox(this);
				optionmelody2 = new TextView(this);
				optionmelody2.setText(itemMessage);
				
				melody2Linear = new LinearLayout(this);
				melody2Linear.setOrientation(0);
				
				melody2Linear.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						changeSettings(Field.SET_Melody,Field.MODE_Melody_OFF);
					}
				});
				melody2Box.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						changeSettings(Field.SET_Melody,Field.MODE_Melody_OFF);
					}
				});
				
				checkBox = melody2Box;
				option	 = optionmelody2;
				optionsLinear = melody2Linear;
			}
			////////////////////
			else if(itemMessage == getResources().getString(R.string.orientation0)){
				optionsLinear.setTag(Field.MODE_Auto);
				or0Box = new CheckBox(this);
				optionor0 = new TextView(this);
				or0Linear = new LinearLayout(this);
				or0Linear.setOrientation(0);
				optionor0.setText(itemMessage);
				checkBox = or0Box;
				option	 = optionor0;
				optionsLinear = or0Linear;
				or0Linear.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						changeSettings(Field.SET_Orientation,Field.MODE_Auto);
						OrientationUtility("Auto");
					}
				});
				or0Box.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						changeSettings(Field.SET_Orientation,Field.MODE_Auto);
						OrientationUtility("Auto");
					}
				});
				
			}
			///////////////////
			else if(itemMessage == getResources().getString(R.string.orientation1)){
				optionsLinear.setTag(Field.MODE_Portrait);
				or1Box = new CheckBox(this);
				optionor1 = new TextView(this);
				or1Linear = new LinearLayout(this);
				or1Linear.setOrientation(0);
				optionor1.setText(itemMessage);
				checkBox = or1Box;
				option	 = optionor1;
				optionsLinear = or1Linear;
				or1Linear.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						changeSettings(Field.SET_Orientation,Field.MODE_Portrait);
						OrientationUtility("Portrait");
					}
				});
				or1Box.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						changeSettings(Field.SET_Orientation,Field.MODE_Portrait);
						OrientationUtility("Portrait");
					}
				});
				
				checkBox = or1Box;
				option	 = optionor1;
				optionsLinear = or1Linear;
			}
			
			else if(itemMessage == getResources().getString(R.string.orientation2)){
				optionsLinear.setTag(Field.MODE_Portrait);
				or2Box = new CheckBox(this);
				optionor2 = new TextView(this);
				or2Linear = new LinearLayout(this);
				or2Linear.setOrientation(0);
				optionor2.setText(itemMessage);
				checkBox = or2Box;
				option	 = optionor2;
				optionsLinear = or2Linear;
				or2Linear.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						changeSettings(Field.SET_Orientation,Field.MODE_Landscape);
						OrientationUtility("Landscape");
					}
				});
				or2Box.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						changeSettings(Field.SET_Orientation,Field.MODE_Landscape);
						OrientationUtility("Landscape");
					}
				});
				
			}
			
			
//			LinearLayout optionsLinearTEXT = new LinearLayout(this);
//			optionsLinearTEXT.setOrientation(1);
//			RelativeLayout optionRelativeTEXT = new RelativeLayout(this);
//			optionRelativeTEXT.addView(option);
//			optionsLinear.addView(optionRelativeTEXT);
//			
//			RelativeLayout optionRelativeCHECKBOX = new RelativeLayout(this);
//			optionRelativeCHECKBOX.addView(checkBox);
//			optionsLinear.addView(optionRelativeCHECKBOX);
			
			
//			optionsLinear.addView(option);
//			optionsLinear.addView(checkBox);
			option.setTextColor(Color.WHITE);
			
			RelativeLayout optionRelative = new RelativeLayout(this);
			optionRelative.addView(checkBox);
			optionRelative.addView(option);
			optionsLinear.addView(optionRelative);
			//optionsLinear.setBackgroundColor(Color.rgb(255, 0, 255));
			
			RelativeLayout.LayoutParams paramsTEXT = (RelativeLayout.LayoutParams)option.getLayoutParams();
			paramsTEXT.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			paramsTEXT.setMargins(30,30,20,20);
			
			//paramsTEXT.addRule(RelativeLayout.CENTER_VERTICAL);
			
			option.setLayoutParams(paramsTEXT); //causes layout update
			
			RelativeLayout.LayoutParams paramsCHECKBOX = (RelativeLayout.LayoutParams)checkBox.getLayoutParams();
			paramsCHECKBOX.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			paramsCHECKBOX.setMargins(0,0,20,0);
			checkBox.setLayoutParams(paramsCHECKBOX); //causes layout update

			
		}
		
	return optionsLinear;
	 
 }
	
	
	//Change the settings
	public void changeSettings(int set, int mode){
		PackageManager pm = getBaseContext().getPackageManager();
		
		//final Parameters p = mCamera.getParameters();
		final Parameters p;
		SharedPreferences settings = getSharedPreferences(Field.PREFS_DUALCAM, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    
	    
		if(set == Field.SET_AutoFocus){
			
			switch(mode){
			case Field.noTapCaptureJutsu:
				afc1Box.setChecked(true);
				afc2Box.setChecked(false);
				afc3Box.setChecked(false);
				movingJutsu = Field.noTapCaptureJutsu;
				editor.putInt(Field.PREFS_SET_AUTOFOCUS, Field.noTapCaptureJutsu);
				break;
				
			case Field.singleTapCaptureJutsu:
				afc1Box.setChecked(false);
				afc2Box.setChecked(true);
				afc3Box.setChecked(false);
				movingJutsu = Field.singleTapCaptureJutsu;
				editor.putInt(Field.PREFS_SET_AUTOFOCUS, Field.singleTapCaptureJutsu);
				break;
				
			case Field.doubleTapCaptureJutsu:
				afc1Box.setChecked(false);
				afc2Box.setChecked(false);
				afc3Box.setChecked(true);
				movingJutsu = Field.doubleTapCaptureJutsu;
				editor.putInt(Field.PREFS_SET_AUTOFOCUS, Field.doubleTapCaptureJutsu);
				break;
			}
		}
		else if(set == Field.SET_Flash){
			switch(mode){
			case Field.MODE_Flash_Auto:
				flash1Box.setChecked(true);
				flash2Box.setChecked(false);
				flash3Box.setChecked(false);
				flashStatus = Field.MODE_Flash_Auto;
				editor.putInt(Field.PREFS_SET_FLASH, Field.MODE_Flash_Auto);
				if(mCamera != null)
					if(isFlashSupported(pm)) {
						p = mCamera.getParameters();
						p.setFlashMode(Parameters.FLASH_MODE_AUTO);
						mCamera.setParameters(p);
					}
				break;
				
			case Field.MODE_Flash_ON:
				flash1Box.setChecked(false);
				flash2Box.setChecked(true);
				flash3Box.setChecked(false);
				flashStatus = Field.MODE_Flash_ON;
				editor.putInt(Field.PREFS_SET_FLASH, Field.MODE_Flash_ON);
				if(mCamera != null)
					if(isFlashSupported(pm)) {
						p = mCamera.getParameters();
						p.setFlashMode(Parameters.FLASH_MODE_ON);
						mCamera.setParameters(p);
					}
				break;
				
			case Field.MODE_Flash_OFF:
				flash1Box.setChecked(false);
				flash2Box.setChecked(false);
				flash3Box.setChecked(true);
				flashStatus = Field.MODE_Flash_OFF;
				editor.putInt(Field.PREFS_SET_FLASH, Field.MODE_Flash_OFF);
				
				if(mCamera != null)
					if(isFlashSupported(pm)) {
						p = mCamera.getParameters();
						p.setFlashMode(Parameters.FLASH_MODE_OFF);
						mCamera.setParameters(p);
					}
				break;
			}
		}
		else if(set == Field.SET_Melody){
			switch(mode){
			case Field.MODE_Melody_ON:
				melody1Box.setChecked(true);
				melody2Box.setChecked(false);
				melodyStatus = Field.MODE_Melody_ON;
				editor.putInt(Field.PREFS_SET_MELODY, Field.MODE_Melody_ON);
				bgMusicUtility("start");
				break;
				
			case Field.MODE_Melody_OFF:
				melody1Box.setChecked(false);
				melody2Box.setChecked(true);
				melodyStatus = Field.MODE_Melody_OFF;
				editor.putInt(Field.PREFS_SET_MELODY, Field.MODE_Melody_OFF);
				bgMusicUtility("pause");
				break;
			}
		}
		
		else if(set == Field.SET_Orientation){
			switch(mode){
			case Field.MODE_Auto:
				or0Box.setChecked(true);
				or1Box.setChecked(false);
				or2Box.setChecked(false);
				orStatus = Field.MODE_Auto;
				editor.putInt(Field.PREFS_SET_ORIENTATION, Field.MODE_Auto);
				//OrientationUtility("Auto");
				break;
			case Field.MODE_Portrait:
				or0Box.setChecked(false);
				or1Box.setChecked(true);
				or2Box.setChecked(false);
				orStatus = Field.MODE_Portrait;
				editor.putInt(Field.PREFS_SET_ORIENTATION, Field.MODE_Portrait);
				//OrientationUtility("Portrait");
				break;
				
			case Field.MODE_Landscape:
				or0Box.setChecked(false);
				or1Box.setChecked(false);
				or2Box.setChecked(true);
				orStatus = Field.MODE_Landscape;
				editor.putInt(Field.PREFS_SET_ORIENTATION, Field.MODE_Landscape);
				//OrientationUtility("Landscape");
				break;
			}
		}
		
		
		editor.commit();
		
	}
	
	private void OrientationUtility(String string) {
		if(string=="Auto"){
			super.onCreate(savedInstanceState);
			letThereBeLight(savedInstanceState);
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		}
		if(string=="Portrait"){
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		if(string=="Landscape"){
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}

	//Initial Settings
	public void initiateSettings(){
		SharedPreferences settings = getSharedPreferences(Field.PREFS_DUALCAM, 0);
		movingJutsu = settings.getInt(Field.PREFS_SET_AUTOFOCUS, Field.MODE_DefaultAF_and_Capture);
		flashStatus = settings.getInt(Field.PREFS_SET_FLASH, Field.MODE_Flash_Default);
		melodyStatus = settings.getInt(Field.PREFS_SET_MELODY, Field.MODE_Melody_DEFAULT);
		orStatus = settings.getInt(Field.PREFS_SET_ORIENTATION, Field.MODE_Orientation_DEFAULT);
	}

	public void customAlertdialog() {

		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final int progressBarCompensation = 30;
		LinearLayout linear = new LinearLayout(this);

		linear.setOrientation(1);
		createAText();
		// utilityLayout.setVisibility(LinearLayout.GONE);

		// The EditText
		final EditText toBeText = new EditText(this);
		toBeText.setHint(typeTextHereText);
		toBeText.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				charCount++;
				Log.i(TAG, "charCount = " + charCount);
				addedText.setText(toBeText.getText().toString());
				// addedText.setText(addedText.getText().toString());
				// tv.setText(String.valueOf(i) + " / " +
				// String.valueOf(charCounts));

			}
		});

		// The TextView
		final TextView textfontSize = new TextView(this);
		textfontSize.setText(fontSizeText + " = " + (fontSize + 40));
		textfontSize.setPadding(10, 10, 10, 10);

		// The SickBar
		SeekBar seek = new SeekBar(this);
		// seek.setProgress(50);

		seek.setProgress(defaultTextSize - progressBarCompensation);
		seek.setMax(maximumTextSize - progressBarCompensation);
		// seek.setProgress(fontSize);

		// The Color Pallete
		Button bt = new Button(this);
		bt.setText(fontColorText);
		bt.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		bt.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				// addedText

				// Changes by Aid
				initialColor = PreferenceManager.getDefaultSharedPreferences(
						DualCamActivity.this).getInt(COLOR_PREFERENCE_KEY,
						Color.WHITE);
				colorPickerDialog = new ColorPickerDialog(DualCamActivity.this,
						DualCamActivity.this, initialColor, addedText);
				// Changes by Aid
				colorPickerDialog.show();
			}
		});
		
		linear.addView(toBeText);
		linear.addView(seek);
		linear.addView(textfontSize);
		linear.addView(bt);

		alert.setView(linear);

		seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				textfontSize.setText(fontSizeText + " = " + fontSize);
				fontSize = progress + progressBarCompensation;
				addedText.setTextSize(fontSize);
			}

			public void onStartTrackingTouch(SeekBar arg0) {

			}

			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

		alert.setPositiveButton(ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				isTextBeingEdited = false;
				
				textFeatureUtility("show");
			
				
			}
		});

		alert.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// Toast.makeText(getApplicationContext(),
				// "Cancel Pressed",Toast.LENGTH_LONG).show();
				createTextFrameLayout.removeAllViews();
				isTextAdded = false;
				isTextEditable = true;
				isTextBeingEdited = false;
				setButton(textButton);
				//setButtons(isSharable, isSavable, isTextEditable, isRetryable);

				// utilityLayout.setVisibility(LinearLayout.VISIBLE);
				return;
			}
		});
		alert.show();

	}

	// public void customAlertdialog(){
	// ///Code ni Aid here :D
	//
	// }

	public void doZoom(Float firstDistance, Float secondDistance) {
		if (firstDistance < secondDistance) {
			// Do zoom in
			Log.i(TAG, "Zoom in");
			// if(isSmoothZoomSupported){
			//
			// }
			// else
			if (isZoomSupported) {
				if (currentCameraZoom < maxCameraZoom) {

					int zoomLevel = currentCameraZoom + 1;
					param.setZoom(zoomLevel);
					mCamera.setParameters(param);
					currentCameraZoom = param.getZoom();
				}
			} else {
				// Zoom not supported
				// Toast.makeText(getApplicationContext(),"Sorry, your phone don't have zoom features",Field.SHOWTIME).show();
			}

		} else if (firstDistance > secondDistance) {
			// Do zoom out
			Log.i(TAG, "Zoom out");
			// if(isSmoothZoomSupported){
			//
			// }
			// else
			if (isZoomSupported) {
				if (currentCameraZoom > 0) {
					int zoomLevel = currentCameraZoom - 1;
					param.setZoom(zoomLevel);
					mCamera.setParameters(param);
					currentCameraZoom = param.getZoom();
				}
			} else {
				// Zoom not supported
			}
		}
	}

	public ErrorCallback ec = new ErrorCallback() {

		@Override
		public void onError(int data, Camera camera) {
			Log.i(TAG, "ErrorCallback received");
		}

	};

	public PictureCallback getPic = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			try {
				isRetaking = false;
				isRetryable = true;
				setButton(retryButton);
				//setButtons(isSharable, isSavable, isTextEditable, isRetryable);
				ImageView buttonView = getPressedPreview(cameraSide);
				Matrix matrix = new Matrix();
				int width = 0;
				int height = 0;
				int extraWidth = 0;
				int extraHeight = 0;
				int marginalWidth = 0;
				int marginalHeight = 0;
				
				bgMusicUtility("captureresume");
				// if(tempPic != null){
				// tempPic.recycle();
				// tempPic = null;
				// }
				Log.i(TAG, "Pic taken");
				if (cameraSide == "BACK") {
					Log.i(TAG, "Side = " + cameraSide);
					setRetake(cameraSide);
					matrix.postRotate(result);

					options = new BitmapFactory.Options();
					options.inSampleSize = 1;
					options.inJustDecodeBounds = true;

					// Determine how much to scale down the image
					// int scaleFactor = Math.min(photoW/targetW,
					// photoH/targetH);

					Bitmap temp = BitmapFactory.decodeByteArray(data, 0,
							data.length);
					int xW = temp.getWidth();
					int xH = temp.getHeight();
					// Determine how much to scale down the image
					// int scaleFactor = Math.min(xW/screenHeight,
					// xH/screenWidth);
					int scaleFactor = Math.max(xW / screenHeight, xH
							/ screenWidth);
					// Calculate inSampleSize
					// options.inSampleSize =
					// bitmapResizer.calculateInSampleSize(options, shortWidth,
					// shortHeight);
					options.inSampleSize = scaleFactor;
					// Decode bitmap with inSampleSize set
					options.inJustDecodeBounds = false;
					tempPic = BitmapFactory.decodeByteArray(data, 0,
							data.length, options);
					width = tempPic.getWidth();
					height = tempPic.getHeight();

					Log.i(TAG, "Before SShot");
					Log.i(TAG,
							"*******************   TADAA!!   ***************************");
					Log.i(TAG, "xW = " + xW + ": xH = " + xH);
					Log.i(TAG, "Width = " + tempPic.getWidth() + ": Height = "
							+ tempPic.getHeight());
					Log.i(TAG, "screenWidth = " + screenWidth
							+ ": screenHeight = " + screenHeight);
					Log.i(TAG, "scaleFactor = " + scaleFactor);

					Log.i(TAG,
							"*******************   TADAA!!   ***************************");
					if (width > screenHeight || height > screenWidth) {
						if (width > 1280 || height > 1280) {
							tempPic = Bitmap.createScaledBitmap(tempPic,
									Math.round(width / 2),
									Math.round(height / 2), true);
						}
						tempPic = Bitmap.createBitmap(tempPic, 0, 0,
								tempPic.getWidth(), tempPic.getHeight(),
								matrix, true);
						width = tempPic.getWidth();
						height = tempPic.getHeight();
						extraWidth = width - screenWidth;
						extraHeight = height - screenHeight;
						marginalWidth = Math.round(extraWidth / 2);
						marginalHeight = Math.round(extraHeight / 2);
						if (marginalHeight < 0)
							marginalHeight = 0;
						if (marginalWidth < 0)
							marginalWidth = 0;
						if (extraWidth < 0)
							extraWidth = 0;
						if (extraHeight < 0)
							extraHeight = 0;

						Log.i(TAG, "Width = " + width + ": Height = " + height);
						Log.i(TAG, "screenWidth = " + screenWidth
								+ ": screenHeight = " + screenHeight);
						Log.i(TAG, "marginalWidth = " + marginalWidth
								+ ": marginalHeight = " + marginalHeight);

						if (orientationScreen == "PORTRAIT") {
							tempPic = Bitmap.createBitmap(tempPic,
									marginalWidth, marginalHeight, width
											- extraWidth, height
											- marginalHeight);
							// tempPic = Bitmap.createBitmap(tempPic,0,0,width,
							// height);
						}
					} else {
						tempPic = Bitmap.createBitmap(tempPic, 0, 0,
								tempPic.getWidth(), tempPic.getHeight(),
								matrix, true);
						width = tempPic.getWidth();
						height = tempPic.getHeight();
						Log.i(TAG, "Width = " + width + ": Height = " + height);
						Log.i(TAG, "screenWidth = " + screenWidth
								+ ": screenHeight = " + screenHeight);
					}

					width = tempPic.getWidth();
					height = tempPic.getHeight();
					// tempPic = Bitmap.createBitmap(tempPic, 0,0,width,
					// Math.round(height/2));

					if (orientationScreen == "PORTRAIT") {
						// Portrait
						tempPic = Bitmap.createBitmap(tempPic, 0, 0, width,
								height - Math.round(height / 3));

					} else if (orientationScreen == "LANDSCAPE") {
						// Landscape
						tempPic = Bitmap.createBitmap(tempPic, 0, 0, width
								- Math.round(width / 3), height);

					}

					settoBackground(buttonView, tempPic);
					backPic = tempPic;
					mCamera.stopPreview();
					releaseCamera();
					previewImage.setVisibility(ImageView.GONE);
					isBackTaken = true;

					if (!isFrontTaken) {
						setSide("FRONT");
						// setUntake("BACK");
					}

				} else {
					Log.i(TAG, "Side = " + cameraSide);
					setRetake(cameraSide);
					matrix.postRotate(result);
					matrix.preScale(-1, 1);

					options = new BitmapFactory.Options();
					options.inSampleSize = 1;
					options.inJustDecodeBounds = true;

					// Determine how much to scale down the image
					// int scaleFactor = Math.min(photoW/targetW,
					// photoH/targetH);

					Bitmap temp = BitmapFactory.decodeByteArray(data, 0,
							data.length);
					int xW = temp.getWidth();
					int xH = temp.getHeight();
					// Determine how much to scale down the image
					int scaleFactor = Math.min(xW / screenHeight, xH
							/ screenWidth);
					// Calculate inSampleSize
					// options.inSampleSize =
					// bitmapResizer.calculateInSampleSize(options, shortWidth,
					// shortHeight);
					options.inSampleSize = scaleFactor;
					// Decode bitmap with inSampleSize set
					options.inJustDecodeBounds = false;

					tempPic = BitmapFactory.decodeByteArray(data, 0,
							data.length, options);
					width = tempPic.getWidth();
					height = tempPic.getHeight();

					Log.i(TAG, "Before SShot");
					Log.i(TAG, "Width = " + tempPic.getWidth() + ": Height = "
							+ tempPic.getHeight());

					if (width > screenHeight || height > screenWidth) {
						if (width > 1280 || height > 1280) {
							tempPic = Bitmap.createScaledBitmap(tempPic,
									Math.round(width / 2),
									Math.round(height / 2), true);
						}
						tempPic = Bitmap.createBitmap(tempPic, 0, 0,
								tempPic.getWidth(), tempPic.getHeight(),
								matrix, true);
						width = tempPic.getWidth();
						height = tempPic.getHeight();
						extraWidth = width - screenWidth;
						extraHeight = height - screenHeight;
						marginalWidth = Math.round(extraWidth / 2);
						marginalHeight = Math.round(extraHeight / 2);
						if (marginalHeight < 0)
							marginalHeight = 0;
						if (marginalWidth < 0)
							marginalWidth = 0;
						if (extraWidth < 0)
							extraWidth = 0;
						if (extraHeight < 0)
							extraHeight = 0;

						Log.i(TAG, "Width = " + width + ": Height = " + height);
						Log.i(TAG, "screenWidth = " + screenWidth
								+ ": screenHeight = " + screenHeight);
						Log.i(TAG, "marginalWidth = " + marginalWidth
								+ ": marginalHeight = " + marginalHeight);
						Log.i(TAG, "Resizing~ ching ching!");

						if (orientationScreen == "PORTRAIT") {
							tempPic = Bitmap.createBitmap(tempPic,
									marginalWidth, marginalHeight, width
											- extraWidth, height
											- marginalHeight);
						}
					} else {
						tempPic = Bitmap.createBitmap(tempPic, 0, 0,
								tempPic.getWidth(), tempPic.getHeight(),
								matrix, true);
						width = tempPic.getWidth();
						height = tempPic.getHeight();
						Log.i(TAG, "Width = " + width + ": Height = " + height);
						Log.i(TAG, "screenWidth = " + screenWidth
								+ ": screenHeight = " + screenHeight);
						Log.i(TAG, "Unresized booya!");
					}
					width = tempPic.getWidth();
					height = tempPic.getHeight();
					boolean b = tempPic.isMutable();
					Log.i(TAG, "The reason = " + b);
					Log.i(TAG, "Flag 1");
					// tempPic = Bitmap.createBitmap(tempPic,
					// 0,Math.round(height/2),width, height/2);

					if (orientationScreen == "PORTRAIT") {
						// Portrait
						tempPic = Bitmap.createBitmap(tempPic, 0,
								Math.round(height / 3), width,
								height - Math.round(height / 3));
					} else if (orientationScreen == "LANDSCAPE") {
						// Landscape
						tempPic = Bitmap.createBitmap(tempPic,
								Math.round(width / 3), 0,
								width - Math.round(width / 3), height);

					}

					Log.i(TAG, "Flag 2");
					settoBackground(buttonView, tempPic);
					frontPic = tempPic;
					Log.i(TAG, "Flag 3");
					mCamera.stopPreview();
					releaseCamera();
					isFrontTaken = true;

				}
				// FileOutputStream fos = new FileOutputStream(pictureFile);
				// fos.write(data);
				// fos.close();
				if (isBackTaken && isFrontTaken) {
					isRetryable = true;
					isSharable = false;
					isSavable = true;
					isTextEditable = true;
					// textButton.setImageResource(R.drawable.text1);
					// saveButton.setImageResource(R.drawable.save1);
					//setButtons(isSharable, isSavable, isTextEditable, isRetryable);
					// if(!isTextAdded)
					// setEditText();
//					setButton(shareButton);
					setButton(saveButton);
					setButton(textButton);
					setButton(retryButton);
				}

				cameraAction = Field.CameraCanCapture;
				touchAction = Field.ActionStateClickable;
				jutsuAction = Field.standbyToCaptureJutsu;
				
				playAfterSound();
				Log.i(TAG, "From getPic... "
						+"performedAction = " + performedAction
						+" :cameraAction = "+cameraAction
						+" :touchAction = " + touchAction
						+" :cameraSide = "+cameraSide);
			} catch (Exception e) {
				Log.i(TAG, "not isSharable");
				Log.e(TAG, "Error accessing file: " + e.getMessage());
				Toast.makeText(getApplicationContext(), errorMessage,
						Field.SHOWTIME).show();
				// linkSTART();

			}
		}
	};
	
	private final ShutterCallback shutterKACHANG = new ShutterCallback() {
        public void onShutter() {
            AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);
        }
    };

	public ImageView getPressedPreview(String cameraSide) {
		ImageView buttonView = null;

		if (cameraSide == "BACK")
			buttonView = backPreview;
		if (cameraSide == "FRONT")
			buttonView = frontPreview;

		return buttonView;
	}
	
	public void letThereBeLight(Bundle savedInstanceState) {
		initiateJapWords();
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			globalBundle = extras;
			showSpalshScreen = extras.getBoolean("showSplashScreen");
			movingJutsu		 = extras.getInt("movingJutsu");
			myFBSession = new SetMyFBSession(getApplicationContext(), this, extras);
			if(myFBSession.startMySession() != null)
				mySession		 = myFBSession.startMySession();
		}
			
		if(showSpalshScreen){
			setContentView(R.layout.splashscreen);
			
			CountDownTimer splashTime = new CountDownTimer(2000,1000) {
				
				@Override
				public void onTick(long millisUntilFinished) {

					
				}
				
				@Override
				public void onFinish() {
					/*finish();
					Intent i = getBaseContext().getPackageManager()
							.getLaunchIntentForPackage(getBaseContext().getPackageName());
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.putExtra("showSplashScreen", false);
					startActivity(i);*/
					
					// Calling GifActivity of Movie View 
					/*finish();
					Intent i = new Intent(DualCamActivity.this, GifActivity.class); 
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.putExtra("showSplashScreen", false);
					startActivity(i);*/
					
					finish();
//					Intent i = new Intent(DualCamActivity.this, GifWebviewDisplayActivity.class);
					Intent i = new Intent(DualCamActivity.this, SocialMediaActivity.class); 
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.putExtra("showSplashScreen", false);
					startActivity(i);
				}
			};
			
			splashTime.start();
			
		}
		
		else{
			setContentView(R.layout.dualcam);
			mediaUtility = new MediaUtility(getApplicationContext());
			packageCheck = new PackageCheck(getApplicationContext());

			captureButton = (ImageView) findViewById(R.id.smileyButton);
			textButton = (ImageView) findViewById(R.id.textButton);
			saveButton = (ImageView) findViewById(R.id.saveButton);
			retryButton = (ImageView) findViewById(R.id.retryButton);
			shareButton = (ImageView) findViewById(R.id.shareButton);
			backPreview = (ImageView) findViewById(R.id.cumPreviewBack);
			frontPreview = (ImageView) findViewById(R.id.cumPreviewFront);
			previewImage = (ImageView) findViewById(R.id.previewImage);
			
			//For the text button
			okButton = (ImageView) findViewById(R.id.okButton);
			noButton = (ImageView) findViewById(R.id.noButton);
			
			mainPreview = (FrameLayout) findViewById(R.id.cumshot);
			pictureLayout = (LinearLayout) findViewById(R.id.picLayout);
			utilityLayout = (LinearLayout) findViewById(R.id.utilityButtonLayout);
			createTextFrameLayout = (FrameLayout) findViewById(R.id.createTextFrame);
			toSaveLayout = (RelativeLayout) findViewById(R.id.overAllLayout);
			
			loading = new LoadingDialog(DualCamActivity.this);

			hideAct = new HideAct(getApplicationContext());
			captureButton.setOnClickListener(this);
			textButton.setOnClickListener(this);
			saveButton.setOnClickListener(this);
			retryButton.setOnClickListener(this);
			shareButton.setOnClickListener(this);
			
			//For the text button
			okButton.setOnClickListener(this);
			noButton.setOnClickListener(this);
			// utilityLayout.setOnClickListener(this);

			backPreview.setOnTouchListener(new setTouchMode());
			frontPreview.setOnTouchListener(new setTouchMode());
			//added by gelo
			bgMusicUtility("initialize");
			setSavedOrientation();
			try {
				orientationOfPhone = this.getResources().getConfiguration().orientation;
				screenHeight = new PhoneChecker(this).screenHeight;
				screenWidth = new PhoneChecker(this).screenWidth;
				doubleTapTimer = doDoubleTap();
				touchAction = Field.ActionStateClickable;
				jutsuAction = Field.standbyToCaptureJutsu;
				//
				
				
				initiateSettings();
				//movingJutsu = Field.noTapCaptureJutsu;

				if (orientationOfPhone == Configuration.ORIENTATION_PORTRAIT) {
					orientationScreen = "PORTRAIT";
				} else if (orientationOfPhone == Configuration.ORIENTATION_LANDSCAPE) {
					orientationScreen = "LANDSCAPE";
				} else {
					orientationScreen = "UNKNOWN";
				}
				
//				if (extras != null) {
//
//					Log.i(TAG, "Something happened");
//					isBackTaken = extras.getBoolean("isBackTaken",
//							isBackTaken);
//					isFrontTaken = extras.getBoolean("isFrontTaken",
//							isFrontTaken);
//					isSavable = extras.getBoolean("isSavable",
//							isSavable);
//					isSharable = extras.getBoolean("isSharable", isSharable);
//					isTextEditable = extras.getBoolean(
//							"isTextEditable", isTextEditable);
//					isTextAdded = extras.getBoolean("isTextAdded",
//							isTextAdded);
//					isRetryable = extras.getBoolean("isRetryable",
//							isRetryable);
//					cameraSide = extras.getString("cameraSide");
//					fontSize = extras.getInt("fontSize");
//					killMe = false;
//
//					topTapCount = 0;
//					bottomTapCount = 0;
//
//					if (isBackTaken) {
//						getPressedPreview("BACK").setVisibility(ImageView.VISIBLE);
//						getPressedPreview("BACK").setBackgroundDrawable(null);
//						getPressedPreview("BACK").setImageBitmap(null);
//						backPic = extras.getParcelable("backPic");
//						settoBackground(getPressedPreview("BACK"), backPic);
//					}
//
//					if (isFrontTaken) {
//						getPressedPreview("FRONT").setVisibility(ImageView.VISIBLE);
//						getPressedPreview("FRONT").setBackgroundDrawable(null);
//						getPressedPreview("FRONT").setImageBitmap(null);
//						frontPic = extras.getParcelable("frontPic");
//						settoBackground(getPressedPreview("FRONT"), frontPic);
//					}
//
//					if (isTextAdded) {
//						// Log.i(TAG,
//						// "The epic Text = "+isTextAdded+" and the textToShow is ="+textToShow);
//						textToShow = savedInstanceState.getString("textToShow")
//								.toString();
//						createTextFrameLayout.removeAllViews();
//						createAText();
//					}
//
//					if (isBackTaken && !isFrontTaken) {
//						setSide("FRONT");
//					} else if (!isBackTaken && isFrontTaken) {
//						setSide("BACK");
//					} else if (!isBackTaken && !isFrontTaken) {
//						setSide("BACK");
//					}
//
//					
//
//				} else
					setSide("BACK");
				
				setButton(shareButton);
				setButton(saveButton);
				setButton(textButton);
				setButton(retryButton);
				
				popUpDialog = customPopUpMenu();
				//setButtons(isSharable, isSavable, isTextEditable, isRetryable);
				
			} catch (Exception e) {
				Log.e(TAG, "Something went wrong inside splashScreen viewing");
			}
		}
	}

	public void linkSTART() {
		finish();
		Intent i = getBaseContext().getPackageManager()
				.getLaunchIntentForPackage(getBaseContext().getPackageName());
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.putExtra("showSplashScreen", false);
		i.putExtra("movingJutsu", movingJutsu);
		startActivity(i);
	}
	
	public void linkRESTART() {
		
		
		Intent toSave = getBaseContext().getPackageManager()
				.getLaunchIntentForPackage(getBaseContext().getPackageName());
		toSave.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		toSave.putExtra("showSplashScreen", false);
//		toSave.putExtra("isBackTaken", isBackTaken);
//		toSave.putExtra("isFrontTaken", isFrontTaken);
//		toSave.putExtra("isSavable", isSavable);
//		toSave.putExtra("isSharable", isSharable);
//		toSave.putExtra("isSavePathset", isSavePathset);
//		toSave.putExtra("isTextEditable", isTextEditable);
//		toSave.putExtra("isTextAdded", isTextAdded);
//		toSave.putExtra("isRetryable", isRetryable);
//		// toSave.putBoolean("killMe", killMe);
//		toSave.putExtra("fontSize", fontSize);
//
//		if (frontPic != null)
//			toSave.putExtra("frontPic", frontPic);
//
//		if (backPic != null)
//			toSave.putExtra("backPic", backPic);
//
//		if (cameraSide != null)
//			toSave.putExtra("cameraSide", cameraSide);
//
//		if (fileName != null)
//			toSave.putExtra("fileName", fileName);
//
//		if (textToShow != null)
//			toSave.putExtra("textToShow", textToShow);
		
		finish();
		startActivity(toSave);
	}
	
	
	
	public void unleashTheKraken(){
		releaseCamera();
		releaseSound();
	}
	
	public void releaseSound(){
		if(mMediaPlayer != null){
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	public void releaseCamera() {
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
	}

	public void relenquishTheSoul() {
		releaseCamera(); // release the camera immediately on pause event
		
		options = null;
		if (tempPic != null)
			tempPic.recycle();
		
		
		finish();
		System.exit(0);
	}
	
	public void resetAllActions(){
		cameraAction= Field.CameraCanCapture;
		touchAction = Field.ActionStateClickable;
		jutsuAction = Field.standbyToCaptureJutsu;
	}

	public void retakeImage(String thisside) {

		cameraSide = thisside;
		String message = retakeMessage;
		String title = "retake";
		createAlert(title, cameraSide, message);
	}

	public void saveImage(Bitmap bmp) {
		try {

			// MediaStore.Images.Media.insertImage(getContentResolver(),
			// bmp,"","");
			// Log.d(TAG,"the filename = "+mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE).toString());
			// if(!isSavePathset)
			fileBitmap = bmp;
			filePath = mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE);
			fileName = mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE)
					.toString();
			// Log.d(TAG,"The utility = "+mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE).toString());
			FileOutputStream out = new FileOutputStream(
					mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE));
			// Log.d(TAG,"Before saving");
			bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
			// Log.d(TAG,"After saving");
			mediaUtility.updateMedia(TAG, "file://"
					+ mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE)
							.toString());
			// Log.d(TAG,"file://"
			// +mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE).toString());
			out.flush();
			out.close();
			// Log.d(TAG,"Saved to "+mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE).toString());
			if(loading != null)
	        	loading.dismiss();
			Toast.makeText(getApplicationContext(), saveMessage,
					Field.SHOWTIME).show();
			isSharable = true;
			// shareButton.setImageResource(R.drawable.share1);
			setButton(shareButton);
			//setButtons(isSharable, isSavable, isTextEditable, isRetryable);

		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "Saving failed cause = " + e.getCause());
			Toast.makeText(getApplicationContext(), errorMessage,
					Field.SHOWTIME).show();

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
			cameraUtility = new CameraUtility(getApplicationContext());
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

			setCameraDisplayOrientation(this,
					cameraUtility.findCamera(cameraSide), mCamera);
			Log.i(TAG, "b");
			cameraPreview = new CameraPreview(getApplicationContext(), mCamera);
			mainPreview.removeAllViews();

			android.widget.RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) mainPreview
					.getLayoutParams();

			// LayoutParams grav = (LayoutParams) mainPreview.getLayoutParams();
			// int gr = grav.getClass()
			RelativeLayout rl = (RelativeLayout) findViewById(R.id.addCamPreview);
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
					if (sdk >= android.os.Build.VERSION_CODES.JELLY_BEAN)
						lp.setMargins(0, 0, 0, 0);
					else
						layoutParams.setMargins(0, 0, 0, 0);
					cameraPreview.setLayoutParams(lp);
				} else {

					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
					FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
							screenWidth, (screenHeight * 3) / 4);
					if (sdk >= android.os.Build.VERSION_CODES.JELLY_BEAN)
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
					if (sdk >= android.os.Build.VERSION_CODES.JELLY_BEAN)
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
					if (sdk >= android.os.Build.VERSION_CODES.JELLY_BEAN)
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
			hasCameraFocus = getPackageManager().hasSystemFeature(
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
			Toast.makeText(getApplicationContext(), errorMessage,
					Field.SHOWTIME).show();
			// linkSTART();
		}
	}
	
	public void setButton(View view){
		
		switch(view.getId()){
		
		case R.id.saveButton:
			if (isSavable){
				saveButton.setImageResource(R.drawable.save1);
				hideAct.showThisView(saveButton, Field.showToBottom);
				Log.i("asdf","asdf1");
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

	public void setButtons(boolean shareState, boolean saveState,
			boolean textState, boolean retryState) {

//		if (shareState){
////			if(shareButton.getDrawable() != getResources().getDrawable(R.drawable.share1) && shareState){
////				Log.i(TAG,"shareState went past here");
//				shareButton.setImageResource(R.drawable.share1);
//				hideAct.showThisView(shareButton, Field.showToBottom);
////			}
//		}
//		else{
////			if(shareButton.getDrawable() != getResources().getDrawable(R.drawable.share2) && !shareState){
//				shareButton.setImageResource(R.drawable.share2);
//				hideAct.hideThisView(shareButton, Field.hideToBottom);
////			}
//		}
//
//		if (saveState){
//			saveButton.setImageResource(R.drawable.save1);
//			hideAct.showThisView(saveButton, Field.showToBottom);
//		}
//		else{
//			saveButton.setImageResource(R.drawable.save2);
//			hideAct.hideThisView(saveButton, Field.hideToBottom);
//		}
//
//		if (textState){
//			textButton.setImageResource(R.drawable.text1);
//			hideAct.showThisView(textButton, Field.showToBottom);
//		}
//		else{
//			textButton.setImageResource(R.drawable.text2);
//			hideAct.hideThisView(textButton, Field.hideToBottom);
//		}
//
//		if (retryState){
//			retryButton.setImageResource(R.drawable.retry1);
//			.showThisView(retryButton, Field.showToBottom);
//		}
//		else{
//			retryButton.setImageResource(R.drawable.retry2);
//			hideAct.hideThisView(retryButton, Field.hideToBottom);
//		}
		
	if(isAlreadySharable != shareState)	{
		if (shareState){
			shareButton.setImageResource(R.drawable.share1);
			hideAct.showThisView(shareButton, Field.showToBottom);
		}
		else{
			shareButton.setImageResource(R.drawable.share2);
			hideAct.hideThisView(shareButton, Field.hideToBottom);
		}
		
		isAlreadySharable = shareState;
	}
	
	if(isAlreadySavable != saveState)	{
		
		if (saveState){
			saveButton.setImageResource(R.drawable.save1);
			hideAct.showThisView(saveButton, Field.showToBottom);
		}
		else{
			saveButton.setImageResource(R.drawable.save2);
			hideAct.hideThisView(saveButton, Field.hideToBottom);
		}
		
		isAlreadySavable = saveState;
	}
	
	if(isAlreadyTextable != textState)	{
		if (textState){
			textButton.setImageResource(R.drawable.text1);
			hideAct.showThisView(textButton, Field.showToBottom);
		}
		else{
			textButton.setImageResource(R.drawable.text2);
			hideAct.hideThisView(textButton, Field.hideToBottom);
		}
		
		isAlreadyTextable = textState;
	}

	if(isAlreadyRetryable != retryState)	{
		if (retryState){
			retryButton.setImageResource(R.drawable.retry1);
			hideAct.showThisView(retryButton, Field.showToBottom);
		}
		else{
			retryButton.setImageResource(R.drawable.retry2);
			hideAct.hideThisView(retryButton, Field.hideToBottom);
		}
		
		isAlreadyRetryable = retryState;		
	}
		
//		if(isAlreadySharable != shareState)
//			isAlreadySharable = false;
//		if(isAlreadySavable != saveState)
//			isAlreadySavable = false;
//		if(isAlreadyTextable != textState)
//			isAlreadyTextable = false;
//		if(isAlreadyRetryable != retryState)
//			isAlreadyRetryable = false;
		
		//hideAct.hideThisView(saveButton, Field.hideToLeft);utilityLayout
//		if(utilityLayout.isShown()){
//			hideAct.hideThisViewGroup(utilityLayout, Field.hideToLeft);
//			utilityLayout.setVisibility(ViewGroup.GONE);
//		}
//		else if(!utilityLayout.isShown()){
//			hideAct.showThisViewGroup(utilityLayout, Field.showToLeft);
//			utilityLayout.setVisibility(ViewGroup.VISIBLE);
//		}
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

	public void setEditText() {

//		isTextAdded = false;
//		isTextEditable = true;
//		setButton(textButton);
		//setButtons(isSharable, isSavable, isTextEditable, isRetryable);
		// createTextFrameLayout.removeAllViews();
		// customAlertdialog();
		// textButton.setImageResource(R.drawable.text2);
		// Do the Text
		AlertDialog.Builder alertDialogBuilderCreateText = new AlertDialog.Builder(
				DualCamActivity.this);

		// set title
		// alertDialogBuilderCreateText.setTitle("Create Text");

		// set dialog message
		alertDialogBuilderCreateText.setMessage(addALabelText)
				.setCancelable(false)
				.setPositiveButton(yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						createTextFrameLayout.removeAllViews();
						customAlertdialog();

					}
				}).setNegativeButton(no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

//						isTextAdded = false;
//						isTextEditable = true;
//						setButton(textButton);
						//setButtons(isSharable, isSavable, isTextEditable,isRetryable);
					}
				});

		// create alert dialog
		AlertDialog alert = alertDialogBuilderCreateText.create();
		alert.show();
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
			focusMarker = new ImageView(getApplicationContext());
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
						if (sdk >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
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
						if (sdk >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
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

	public void setHide() {

	}

	public void setRetake(String cameraSide) {

		if (cameraSide == "BACK") {
			isBackTaken = true;
		} else if (cameraSide == "FRONT") {
			isFrontTaken = true;
		}
	}

	public void setSide(String thisside) {
		touchAction = Field.ActionStateClickable;
		cameraSide = thisside;
		seePreview(cameraSide);
	}

	public void settoBackground(View view, Bitmap bitmap) {
		BitmapDrawable bd = new BitmapDrawable(bitmap);

		if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			view.setBackgroundDrawable(bd);
		} else {
			view.setBackground(bd);
		}
	}
	
//	public void ninjaMoves(View view, MotionEvent event, Integer jutsu, final Integer act){
	public void ninjaMoves(View view, MotionEvent event, Integer jutsu){
		
		int ninjaMoves = jutsu;
		jutsuAction = jutsu;
		
		switch(ninjaMoves){
		
		case Field.singleTapCaptureJutsu:
			
			if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					// //Listen for events
					Log.i(TAG, "MotionEvent = ACTION_DOWN");
					performedAction = Field.ActionClick;
					touchAction = Field.ActionClick;
					// Initiate First point of contact
					firstPointer = event.getPointerId(0);
					firstPointerIndex = event.findPointerIndex(firstPointer);
	
	
					setFocus("UNFOCUS", performedAction, null, null, null);
					setFocus("FOCUS", performedAction,
							event.getX(firstPointerIndex),
							event.getY(firstPointerIndex), 
							view);
					
	
					if (view.getId() == R.id.cumPreviewBack) {
						topTapCount++;
						if (!isBackTaken) {
							// cameraFocusTimer.cancel();
							longClickTimer.start();
						}
					}
	
					else if (view.getId() == R.id.cumPreviewFront) {
						bottomTapCount++;
						if (!isFrontTaken) {
							// cameraFocusTimer.cancel();
							longClickTimer.start();
						}
					}

				}
			else if(event.getAction() == MotionEvent.ACTION_UP)
			{
				// Drop all events and execute if there is one
				Log.i(TAG, "MotionEvent = ACTION_UP");
	
				if (performedAction == Field.ActionClick
						|| performedAction == Field.ActionLongClick) {
					performedAction = Field.ActionClickEnd;
					touchAction = Field.ActionClickEnd;
					
					
					if (longClickTimer != null)
						longClickTimer.cancel();
	
					if (view.getId() == R.id.cumPreviewBack) {
						if (isSavable && isBackTaken) {
							bottomTapCount = 0;
							topTapCount = 0;
							retakeImage("BACK");
						}
	
					} 
					else if (view.getId() == R.id.cumPreviewFront) {
						if (isSavable && isFrontTaken) {
							bottomTapCount = 0;
							topTapCount = 0;
							retakeImage("FRONT");
						}
	
						else if (!isFrontTaken) {
							bottomTapCount = 0;
							topTapCount = 0;
							takeAShot();
						}
					}
				}
			}
				
			
		
		break;
		
		case Field.doubleTapCaptureJutsu:
			if(event.getAction() == MotionEvent.ACTION_DOWN)
			{
				//Listen for events
				Log.i(TAG, "MotionEvent = ACTION_DOWN");
				performedAction = Field.ActionClick;
				touchAction = Field.ActionClick;
				
				firstPointer = event.getPointerId(0);
				firstPointerIndex = event.findPointerIndex(firstPointer);

				if (topTapCount < 1 && bottomTapCount < 1) {
					touchAction = performedAction;

//					if (cameraFocusTimer != null)
//						cameraFocusTimer.cancel();

					setFocus("UNFOCUS", performedAction, null, null, null);
					setFocus("FOCUS", performedAction,
							event.getX(firstPointerIndex),
							event.getY(firstPointerIndex), 
							view);
				}

				if (view.getId() == R.id.cumPreviewBack) {
					topTapCount++;
					if (!isBackTaken) {
						// cameraFocusTimer.cancel();
						longClickTimer.start();
					}
				}

				else if (view.getId() == R.id.cumPreviewFront) {
					bottomTapCount++;
					if (!isFrontTaken) {
						// cameraFocusTimer.cancel();
						longClickTimer.start();
					}
				}

			}
		else if(event.getAction() == MotionEvent.ACTION_UP)
			{
				// Drop all events and execute if there is one
				Log.i(TAG, "MotionEvent = ACTION_UP");
	
				if (performedAction == Field.ActionClick
						|| performedAction == Field.ActionLongClick) {
					performedAction = Field.ActionClickEnd;
					touchAction = Field.ActionClickEnd;
					if (longClickTimer != null)
						longClickTimer.cancel();
	
					if (view.getId() == R.id.cumPreviewBack) {
						if (isSavable && isBackTaken) {
							bottomTapCount = 0;
							topTapCount = 0;
							retakeImage("BACK");
						} else if (topTapCount == 2 && !isBackTaken ) {
							bottomTapCount = 0;
							topTapCount = 0;
							takeAShot();
						}
	
					} 
					else if (view.getId() == R.id.cumPreviewFront) {
						if (isSavable && isFrontTaken) {
							bottomTapCount = 0;
							topTapCount = 0;
							retakeImage("FRONT");
						}
	
						else if (!isFrontTaken) {
							bottomTapCount = 0;
							topTapCount = 0;
							
							takeAShot();
						}
					}
				}
			}
		break;
		
		case Field.noTapCaptureJutsu:
			if(event.getAction() == MotionEvent.ACTION_DOWN)
			{
				//Listen for events
				Log.i(TAG, "MotionEvent = ACTION_DOWN");
				performedAction = Field.ActionClick;
				touchAction = Field.ActionClick;
				
				firstPointer = event.getPointerId(0);
				firstPointerIndex = event.findPointerIndex(firstPointer);

				setFocus("UNFOCUS", performedAction, null, null, null);
				
				if(view.getId() != R.id.cumPreviewFront)
				setFocus("FOCUS", performedAction,
						event.getX(firstPointerIndex),
						event.getY(firstPointerIndex), 
						view);

//				if (view.getId() == R.id.cumPreviewBack) {
//					topTapCount++;
//					if (!isBackTaken) {
//						// cameraFocusTimer.cancel();
//						longClickTimer.start();
//					}
//				}
//
//				else if (view.getId() == R.id.cumPreviewFront) {
//					
//					bottomTapCount++;
//					if (!isFrontTaken) {
//						
//						// cameraFocusTimer.cancel();
//						longClickTimer.start();
//					}
//				}

			}
			else if(event.getAction() == MotionEvent.ACTION_UP)
			{
				// Drop all events and execute if there is one
				Log.i(TAG, "MotionEvent = ACTION_UP");
	
				if (performedAction == Field.ActionClick
						|| performedAction == Field.ActionLongClick) {
					performedAction = Field.ActionClickEnd;
					touchAction = Field.ActionClickEnd;
					if (longClickTimer != null)
						longClickTimer.cancel();
	
					if (view.getId() == R.id.cumPreviewBack) {
						if (isSavable && isBackTaken) {
							bottomTapCount = 0;
							topTapCount = 0;
							retakeImage("BACK");
						} 
					} 
					
					else if (view.getId() == R.id.cumPreviewFront) {
						if (isSavable && isFrontTaken) {
							bottomTapCount = 0;
							topTapCount = 0;
							retakeImage("FRONT");
						}
					}
				}
			}
		break;
		
		case Field.longTapCaptureJutsu:
			if(event.getAction() == MotionEvent.ACTION_DOWN)
			{
				//Listen for events
				Log.i(TAG, "MotionEvent = ACTION_DOWN");
				performedAction = Field.ActionClick;
				touchAction = Field.ActionClick;
				
				firstPointer = event.getPointerId(0);
				firstPointerIndex = event.findPointerIndex(firstPointer);

				setFocus("UNFOCUS", performedAction, null, null, null);
				setFocus("FOCUS", performedAction,
						event.getX(firstPointerIndex),
						event.getY(firstPointerIndex), 
						view);

				if (view.getId() == R.id.cumPreviewBack) {
					topTapCount++;
					if (!isBackTaken) {
						// cameraFocusTimer.cancel();
						longClickTimer.start();
					}
				}

				else if (view.getId() == R.id.cumPreviewFront) {
					bottomTapCount++;
					if (!isFrontTaken) {
						// cameraFocusTimer.cancel();
						longClickTimer.start();
					}
				}

			}
			else if(event.getAction() == MotionEvent.ACTION_UP)
			{

				// Drop all events and execute if there is one
				Log.i(TAG, "MotionEvent = ACTION_UP");
	
				if (performedAction == Field.ActionClick
						|| performedAction == Field.ActionLongClick) {
					performedAction = Field.ActionClickEnd;
					touchAction = Field.ActionClickEnd;
					
					if (longClickTimer != null)
						longClickTimer.cancel();
					
					
	
					if (view.getId() == R.id.cumPreviewBack) {
						if (isSavable && isBackTaken) {
							bottomTapCount = 0;
							topTapCount = 0;
							retakeImage("BACK");
						} 
					} 
					
					else if (view.getId() == R.id.cumPreviewFront) {
						if (isSavable && isFrontTaken) {
							bottomTapCount = 0;
							topTapCount = 0;
							retakeImage("FRONT");
						}
					}
				}
			}
		break;
		
		default:
			Log.i(TAG, "Unknown Jutsu");
		break;
		
		}
			
	}

	public class setTouchMode implements View.OnTouchListener {

		@Override
		public boolean onTouch(final View view, final MotionEvent event) {
			// Toast.makeText(getApplicationContext(),
			// "Number of points = "+event.getPointerCount(),
			// Field.SHOWTIME).show();
			Log.i(TAG, "touchAction = " + touchAction + " : performedAction = " + performedAction);
			longClickTimer = cameraFocus();
			touchCount = event.getPointerCount();
			
			if (touchAction != Field.ActionStateNotClickable)
				switch (event.getActionMasked()) {
				case MotionEvent.ACTION_POINTER_DOWN:
					// Listen zoom event
					Log.i(TAG, "MotionEvent = ACTION_POINTER_DOWN");
					topTapCount = 0;
					bottomTapCount = 0;

					performedAction = Field.ActionZoom;
					touchAction = Field.ActionZoom;

					setFocus("UNFOCUS", performedAction, null, null, null);

					// Cancel all Timer
//					if (cameraFocusTimer != null)
//						cameraFocusTimer.cancel();

					if (longClickTimer != null)
						longClickTimer.cancel();

					firstPointer = event.getPointerId(0);
					secondPointer = event.getPointerId(1);
					firstPointerIndex = event.findPointerIndex(firstPointer);
					secondPointerIndex = event.findPointerIndex(secondPointer);

					// pointerDistance =
					// Math.abs(event.getX(firstPointerIndex)/event.getY(firstPointerIndex)
					// -
					// event.getX(secondPointerIndex)/event.getY(secondPointerIndex));
					Float pointX = Math.abs(event.getX(firstPointerIndex)
							- event.getX(secondPointerIndex));
					Float pointY = Math.abs(event.getY(firstPointerIndex)
							- event.getY(secondPointerIndex));
					// + Math.abs(event.getY(firstPointerIndex) -
					// event.getY(secondPointerIndex))^2
					pointerDistance = (float) Math.sqrt((pointX * pointX)
							+ (pointY * pointY));
					// Log.i(TAG,
					// "firstPointerIndex = "+firstPointerIndex+" : secondPointerIndex = "+secondPointerIndex);
					break;

				case MotionEvent.ACTION_DOWN:
					
					//Single tap capture
					ninjaMoves(view,event,movingJutsu);
					
					//Double tap capture
					//ninjaMoves(view,event,Field.doubleTapCaptureJutsu);
					
					//Double tap capture
					//ninjaMoves(view,event,Field.noTapCaptureJutsu);
					
					//Double tap capture
					//ninjaMoves(view,event,Field.longTapCaptureJutsu);
					
					
					break;

				case MotionEvent.ACTION_UP:
					//Single tap capture
					ninjaMoves(view,event,movingJutsu);
					
					//Double tap capture
					//ninjaMoves(view,event,Field.doubleTapCaptureJutsu);
					
					//Double tap capture
					//ninjaMoves(view,event,Field.noTapCaptureJutsu);
					
					//Double tap capture
					//ninjaMoves(view,event,Field.longTapCaptureJutsu);
					
					break;

				case MotionEvent.ACTION_POINTER_UP:
					// Drop zoom event
					Log.i(TAG, "MotionEvent = ACTION_POINTER_UP");
					if (touchAction == Field.ActionZoom
							|| touchAction == Field.ActionZooming) {
						performedAction = Field.ActionZoomEnd;
						touchAction = Field.ActionZoomEnd;
					}
					// End event of zoom
					// Cancel the action, there's nothing more to do
					break;

				case MotionEvent.ACTION_MOVE:
					// Execute zoom event
					Log.i(TAG, "MotionEvent = ACTION_MOVE");
					// setFocusMarker("UNFOCUS");
					if (performedAction == Field.ActionZoom
							|| performedAction == Field.ActionZooming) {
						performedAction = Field.ActionZooming;
						touchAction = Field.ActionZooming;

						if (event.getPointerCount() > 1) {

							// Log.i(TAG,
							// "Finger 1 = "+event.getX(firstPointerIndex));
							// Log.i(TAG,
							// "Finger 2 = "+event.getX(secondPointerIndex));
							// pointerDistance =
							// Math.abs(event.getX(firstPointerIndex)/event.getY(firstPointerIndex)
							// -
							// event.getX(secondPointerIndex)/event.getY(secondPointerIndex));
							Float pointXC = Math.abs(event
									.getX(firstPointerIndex)
									- event.getX(secondPointerIndex));
							Float pointYC = Math.abs(event
									.getY(firstPointerIndex)
									- event.getY(secondPointerIndex));
							// + Math.abs(event.getY(firstPointerIndex) -
							// event.getY(secondPointerIndex))^2
							changedPointerDistance = (float) Math
									.sqrt((pointXC * pointXC)
											+ (pointYC * pointYC));
							// changedPointerDistance =
							// Math.abs(event.getX(firstPointerIndex)/event.getY(firstPointerIndex)
							// -
							// event.getX(secondPointerIndex)/event.getY(secondPointerIndex));
							Log.i(TAG, "Distance of both fingers = "
									+ pointerDistance + " : "
									+ changedPointerDistance);
							if (pointerDistance > changedPointerDistance) {
								// Do zoom in
								doZoom(pointerDistance, changedPointerDistance);
								pointerDistance = changedPointerDistance;
							} else if (pointerDistance < changedPointerDistance) {
								// Do zoom out
								doZoom(pointerDistance, changedPointerDistance);
								pointerDistance = changedPointerDistance;
							}
						}

					}
					break;

				}

			Log.i(TAG, "Number of points = " + event.getPointerCount()
					+ " : Event = " + event.getAction());
			return true;
		}

	}

	public void setUntake(String cameraSide) {
		if (cameraSide == "BACK") {
			isBackTaken = false;
		} else if (cameraSide == "FRONT") {
			isFrontTaken = false;
		}
	}
	public void shareFunction() {
		
		initControlTwitter();
//		publishStory();
//		sharePho();
//		shareMe();
//		shareFacebook();
//		Uri uri = Uri.parse("file://" + fileName);
//		String shareBody = "Here is the share content body";
//		sharingIntent = new Intent(Intent.ACTION_SEND);
//		//sharingIntent.setClassName("com.facebook.katana","com.facebook.katana.ShareLinkActivity");
//		sharingIntent.setType("image/png");
//		sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
//		finish();
//		startActivity(Intent.createChooser(sharingIntent, "Share via"));
//		
		final Dialog dialog = new Dialog(DualCamActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.sharing_menu);
        dialog.setCancelable(true);
        EditText sharemessage=(EditText)dialog.findViewById(R.id.shareMessage);
        
        final CheckBox fbCB = (CheckBox)dialog.findViewById(R.id.checkBoxFacebook);
        final CheckBox tCB = (CheckBox)dialog.findViewById(R.id.checkBoxTwitter);
        
        final EditText shareMessage = (EditText)dialog.findViewById(R.id.shareMessage);
        final TextView messageCounter = (TextView)dialog.findViewById(R.id.messageCounter);
        messageCounter.setText("0/120");
        Button cancel = (Button) dialog.findViewById(R.id.shareCancelBtn);
        Button ok = (Button) dialog.findViewById(R.id.shareOkBtn);
        shareMessage.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            	String charCount=""+shareMessage.getText();
            	messageCounter.setText(""+charCount.length()+"/120");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            	Log.i("a","a2");

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            	Log.i("a","a3");


            } 

        });
        
        tCB.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
						
						if (!sharedPreferences.getBoolean(TwitterConstant.PREFERENCE_TWITTER_IS_LOGGED_IN,false))
							  {
					        	new TwitterAuthenticateTask().execute();
					            Toast.makeText(getApplicationContext(), "No log in acc. on Twitter.", Field.SHOWTIME).show();
					    }else {
					             new TwitterGetAccessTokenTask().execute("");
					             //Toast.makeText(getApplicationContext(), "Has log in acc. on Twitter.", Field.SHOWTIME).show();
					           }
					                  
				}else{
					//Toast.makeText(getApplicationContext(), "No Twitter.", Field.SHOWTIME).show(); 
			    }
			}
        	
        });
        
        
        cancel.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN){
//					if(v.getId()==R.id.shareCancelBtn)
//						v.setBackgroundResource(R.drawable.sharebuttonpressed);
					
//					pushFBRequest(shareMessage.getText().toString());
				}
				if(event.getAction()==MotionEvent.ACTION_UP){
					if(v.getId()==R.id.shareCancelBtn)
						dialog.dismiss();
				}
				return true;
			}
		});
        
        ok.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN){
//					if(v.getId()==R.id.shareOkBtn)
//						v.setBackgroundResource(R.drawable.sharebuttonpressed);
					
//					pushFBRequest(shareMessage.getText().toString());
				}
				if(event.getAction()==MotionEvent.ACTION_UP){
					if(v.getId()==R.id.shareOkBtn){
						if(fbCB.isChecked()){
							pushFBRequest(shareMessage.getText().toString());
							dialog.dismiss();
							loading.show();
						}
						else if(!fbCB.isChecked())
							Toast.makeText(getApplicationContext(), "Please choose at least 1 media.", Field.SHOWTIME).show();
							dialog.dismiss();

						if(tCB.isChecked()) {
								fileName = mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE).toString();
								String TwitText = shareMessage.getText().toString();
								String TwitStatus = TwitText + " via #DualCam";
								//Toast.makeText(getApplicationContext(), "TwitStatus" + TwitStatus, Field.SHOWTIME).show();  
								new TwitterUpdateStatusTask().execute(TwitStatus);
														  
						}else if(!tCB.isChecked()) {
						       					
							}

					}
				}
				return true;
			}
		});
        
        fbCB.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				if(mySession == null)
//					mySession = new SetMyFBSession(getApplicationContext(), DualCamActivity.this, globalBundle).startMySession();
			}
		});
        
        
        dialog.show();
        
//		
//		final SharingDialog shareDialog = new SharingDialog(DualCamActivity.this);
//		final SharingDialog shareDialog = new SharingDialog(DualCamActivity.this);
//        Button cancel = (Button) shareDialog.findViewById(R.id.shareCancelBtn);
//
//        Button ok = (Button) shareDialog.findViewById(R.id.shareOkBtn);
//        final EditText shareMessage = (EditText)shareDialog.findViewById(R.id.shareMessage);
//        cancel.setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				if(event.getAction()==MotionEvent.ACTION_DOWN){
//					if(v.getId()==R.id.shareCancelBtn)
//						v.setBackgroundResource(R.drawable.sharebuttonpressed);
//					
////					pushFBRequest(shareMessage.getText().toString());
//				}
//				if(event.getAction()==MotionEvent.ACTION_UP){
//					if(v.getId()==R.id.shareCancelBtn)
//						shareDialog.dismiss();
//				}
//				return true;
//			}
//		});
//        
//        ok.setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				if(event.getAction()==MotionEvent.ACTION_DOWN){
//					if(v.getId()==R.id.shareOkBtn)
//						v.setBackgroundResource(R.drawable.sharebuttonpressed);
//					
////					pushFBRequest(shareMessage.getText().toString());
//				}
//				if(event.getAction()==MotionEvent.ACTION_UP){
//					if(v.getId()==R.id.shareOkBtn){
//						pushFBRequest(shareMessage.getText().toString());
//						shareDialog.dismiss();
//					}
//				}
//				return true;
//			}
//		});
//        
//        shareDialog.show();
        
        
		
	}
	
	public void shareFunctionInActivity(){
		Intent intent = new Intent(DualCamActivity.this,  DualCamActivity.class); 
		//Intent intent = new Intent(DualCamActivity.this,  SharingActivity.class); 
		System.out.println(filePath.toString());
		intent.putExtra("imagePath", filePath.toString()); 
		DualCamActivity.this.startActivity(intent);	
		DualCamActivity.this.finish();
	}
	
	private void pushFBRequest(String message){
		killMe = true;
		Uri uri = Uri.parse("file://" + fileName);
		
		if(mySession == null)
			mySession		 = new SetMyFBSession(getApplicationContext(), this, globalBundle).startMySession();
		
		Request.Callback req = new PhotoReqCallback();
		try{
		List<String> permissions = mySession.getPermissions();
        if (!isSubsetOf(PERMISSIONS, permissions)) {
            pendingPublishReauthorization = true;
            Session.NewPermissionsRequest newPermissionsRequest = new Session
                    .NewPermissionsRequest(DualCamActivity.this, PERMISSIONS);
            mySession.requestNewPublishPermissions(newPermissionsRequest);

    		Log.i(TAG, "bwahahahahha");
            //return;
        }
	        
		File f = filePath;
		Log.i(TAG, "The File "+f.toString());
		Request postRequest = Request.newStatusUpdateRequest(mySession,f.toString(),req);
		Request photoRequest = Request.newUploadPhotoRequest(mySession,f/*ileBitmap*/,req);
		Log.i(TAG,"Session permission count = "+mySession.getPermissions().size());
		for(int y = 0; y < mySession.getPermissions().size(); y++){
			Log.i(TAG,"Permission "+y+" = "+mySession.getPermissions().get(y));
		}
		
//		Bundle paramsPost = postRequest.getParameters();
//		//params.putString("message", "description  goes here");
//		postRequest.setParameters(paramsPost);
//		postRequest.executeAsync();
		
		Bundle paramsPhoto = photoRequest.getParameters();
		paramsPhoto.putString("message", message);
		photoRequest.setParameters(paramsPhoto);
		photoRequest.executeAsync();
		
		}
		catch(Exception e ){
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "ERROR in Posting!!!", Field.SHOWTIME).show();
		}
	}
	
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;
	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
	    for (String string : subset) {
	        if (!superset.contains(string)) {
	            return false;
	        }
	    }
	    return true;
	}
	
	public class PhotoReqCallback implements Request.Callback {
        @Override
        public void onCompleted(Response response) {
        	String fbPhotoAddress = null;
        	if(loading != null)
            	loading.dismiss();
        	killMe = false;
            //showPublishResult(getString(R.string.photo_post), response.getGraphObject(), response.getError());
        	if(response != null){

        		Log.i(TAG, "Response is not null");
        		if(response.getError() == null){
        			Object graphResponse = response.getGraphObject().getProperty("id");
        			if (graphResponse == null || !(graphResponse instanceof String) || 
        					TextUtils.isEmpty((String) graphResponse)) { // [IF Failed upload/no results]
        				Log.d(TAG,"failed photo upload/no response");
        			} else {  // [ELSEIF successful upload]
        				fbPhotoAddress = "https://www.facebook.com/photo.php?fbid=" +graphResponse;     
        				//Toast.makeText(getApplicationContext(), "Posting SUCCESS!!! : "+fbPhotoAddress, Field.SHOWTIME).show();
        				Toast.makeText(getApplicationContext(), getResources().getString(R.string.success_upload), Field.SHOWTIME).show();
            			Log.i(TAG, "SUCCESS in posting");
        			}  
        			
        		}
        		else{
        			Log.i(TAG, "Response Error = "+response.getError().getErrorMessage());
        			Toast.makeText(getApplicationContext(), "Posting FAILED!!! : "+response.getError().getErrorMessage(), Field.SHOWTIME).show();
        		}
    			
        	}
        	
        	else {
        		Log.i(TAG, "Response is null");
    			Toast.makeText(getApplicationContext(), "FAILED in Posting!!!", Field.SHOWTIME).show();
    		}
        	
        		
            
        	//linkRESTART();
        }
    }
	
	private void publishStory() {
		Uri uri = Uri.parse("file://" + fileName);
	    Session session = mySession;

	    if (session != null){

	        // Check for publish permissions    
//	        List<String> permissions = session.getPermissions();
//	        if (!isSubsetOf(PERMISSIONS, permissions)) {
//	            pendingPublishReauthorization = true;
//	            Session.NewPermissionsRequest newPermissionsRequest = new Session
//	                    .NewPermissionsRequest(this, PERMISSIONS);
//	        session.requestNewPublishPermissions(newPermissionsRequest);
//	            return;
//	        }

	        Bundle postParams = new Bundle();
	        postParams.putString("name", "Facebook SDK for Android");
	        postParams.putString("caption", "Build great social apps and get more installs.");
	        postParams.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
	        postParams.putString("link", "https://developers.facebook.com/android");
	        postParams.putString("picture",uri.toString());

	        Request.Callback callback= new Request.Callback() {
	            public void onCompleted(Response response) {
//	                JSONObject graphResponse = response
//	                                           .getGraphObject()
//	                                           .getInnerJSONObject();
//	                String postId = null;
//	                try {
//	                    postId = graphResponse.getString("id");
//	                } catch (JSONException e) {
//	                    Log.i(TAG,
//	                        "JSON error "+ e.getMessage());
//	                }
//	                FacebookRequestError error = response.getError();
//	                if (error != null) {
//	                    Toast.makeText(getApplicationContext(),
//	                         error.getErrorMessage(),
//	                         Toast.LENGTH_SHORT).show();
//	                    } else {
//	                        Toast.makeText(getApplicationContext(), 
//	                             postId,
//	                             Toast.LENGTH_LONG).show();
//	                }
	            }
	        };

	        Request request = new Request(session, "me/feed", postParams, 
	                              HttpMethod.POST, callback);

	        RequestAsyncTask task = new RequestAsyncTask(request);
	        task.execute();
	    }

	}
	
	public void sharePho(){
		Uri uri = Uri.parse("file://" + fileName);
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		//shareIntent.setType("text/plain");
		shareIntent.setType("image/plain");
		//shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Content to share");
		PackageManager pm = getApplicationContext().getPackageManager();
		List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY);
		int i = 0;
		int c = 0;
		for (final ResolveInfo app : activityList) {
			i++;
			
		    if ((app.activityInfo.name).contains("facebook")) {
		    	c++;
		        final ActivityInfo activity = app.activityInfo;
		        final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
		        shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		        //shareIntent.setData(uri);
		        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
		        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		        shareIntent.setComponent(name);
		        getApplicationContext().startActivity(shareIntent);
		        Toast.makeText(getApplicationContext(), " : "+i +" : "+c, Field.SHOWTIME).show();
		        break;
		        //shareFacebook(activity);
				
		   }
		}
		Toast.makeText(getApplicationContext(), "Items = "+activityList.size(), Field.SHOWTIME).show();
		
	}
	
	public void shareFacebook(ActivityInfo activity) {
        String fullUrl = "https://m.facebook.com/sharer.php?u=..";
        Uri uri = Uri.parse("file://" + fileName);
        try {
        	Log.e(TAG, "Starting to share");
        	PackageManager packageManager = getPackageManager();
//        	sharingIntent = new Intent(Intent.ACTION_SEND);
//        	sharingIntent.setClassName(activity.applicationInfo.packageName.toString(),activity.applicationInfo.packageName.toString()+".ShareLinkActivity");
//        	sharingIntent.setType("image/png");
//    		sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
//    		//finish();
//    		startActivity(sharingIntent);
//        	Log.e(TAG, "Ending to share");
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setClassName("com.facebook.katana",
                    "com.facebook.katana.ShareLinkActivity");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, "your title text");
            startActivity(sharingIntent);

        } catch (Exception e) {
        	Log.e(TAG, "Error = "+e.getMessage());
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            i.setData(Uri.parse(fullUrl));
//            startActivity(i);

            
            Log.e("In Exception", "Comes here");
            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.putExtra(Intent.EXTRA_STREAM, uri);
            i.setData(Uri.parse(fullUrl));
            startActivity(i);
        }
    }
	
	public void shareMe(){
		String fullUrl = "https://m.facebook.com/sharer.php?u=..";
		Uri uri = Uri.parse("file://" + fileName);
//		Intent shareIntent = searchShareIntent(); 
//		Intent shareIntent = new Intent(Intent.ACTION_SEND);
//		shareIntent.setPackage("com.facebook.katana");
//		shareIntent.setType("image/png");
//		shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
////        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
//        startActivity(shareIntent);
//		Intent i = new Intent();
//		i.putExtra(Intent.EXTRA_STREAM, uri);
//        i.setAction(Intent.ACTION_VIEW);
//        i.setData(Uri.parse(fullUrl));
//        startActivity(i);
		
		Intent i = searchShareIntent();
		startActivity(Intent.createChooser(i, "Share via"));
	}
	
	public Intent searchShareIntent(){
		Uri uri = Uri.parse("file://" + fileName);
		final String[] sharingApps = {"com.facebook.katana"};
		Intent photoShareIntent = new Intent(Intent.ACTION_SEND);
		photoShareIntent.setType("image/png");
		photoShareIntent.putExtra(Intent.EXTRA_STREAM, uri);
		final PackageManager packageManager = getPackageManager();
	    List<ResolveInfo> list = packageManager.queryIntentActivities(
	    		photoShareIntent, PackageManager.MATCH_DEFAULT_ONLY);
	    
	    for (int i = 0; i < sharingApps.length; i++) {
	        for (ResolveInfo resolveInfo : list) {
	            String p = resolveInfo.activityInfo.packageName;
	            if (p != null && p.startsWith(sharingApps[i])) {
	            	photoShareIntent.setPackage(p);
	                return photoShareIntent;
	            }
	        }
	    }
	    return null;
	}

	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("folder/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		try {
			startActivityForResult(
					Intent.createChooser(intent, "Select a File to Upload"),
					Field.FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			// Potentially direct the user to the Market with a Dialog
			Toast.makeText(this, "Please install a File Manager.",
					Toast.LENGTH_SHORT).show();
		}
		// Intent intent = new Intent(this, DirectoryPicker.class);
		// // optionally set options here
		// startActivityForResult(intent, DirectoryPicker.PICK_DIRECTORY);
	}

	public void takeAShot() {
//		PackageManager pm = getBaseContext().getPackageManager();
//		final Parameters p = mCamera.getParameters();
//		if(isFlashSupported(pm)) {
//			if (flashStatus == Field.MODE_Flash_Auto)p.setFlashMode(Parameters.FLASH_MODE_AUTO);
//			else if (flashStatus == Field.MODE_Flash_ON)p.setFlashMode(Parameters.FLASH_MODE_ON);
//			else if (flashStatus == Field.MODE_Flash_OFF)p.setFlashMode(Parameters.FLASH_MODE_OFF);
//			mCamera.setParameters(p);
//		}
		
		
		
		if (mCamera != null && cameraAction != Field.CameraCannotCapture) {
			try {
				//added by gelo
	    		bgMusicUtility("pause");
				mCamera.setErrorCallback(ec);
				mCamera.takePicture(shutterKACHANG, null, getPic);

			} catch (Exception e) {
				Log.i(TAG, "Error at capture button : e = " + e.getCause());
				Toast.makeText(getApplicationContext(), errorMessage,
						Field.SHOWTIME).show();
			}

		}
	}

	@Override
	public void colorChanged(int color) {
		PreferenceManager.getDefaultSharedPreferences(this).edit()
				.putInt(COLOR_PREFERENCE_KEY, color).commit();
		fontColor = color;
	}
	
	private boolean isFlashSupported(PackageManager packageManager) {
		// if device support camera flash?
		if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
			return true;
		}
		return false;
	}
	
	public void playAfterSound(){
		
		if(melodyStatus == Field.MODE_Melody_ON){
			
			new CountDownTimer(500,250) {
				
				public void onTick(long millisUntilFinished) {
					
				}
				
				public void onFinish() {
				}
			}.start();
		}
		
	}
	
	public void bgMusicUtility(String action){
		
		if(action == "initialize"){
			mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.loopingmelody);
			mMediaPlayer.setLooping(true);
			
			if(melodyStatus == Field.MODE_Melody_ON)
				mMediaPlayer.start();
		}
		else if(action == "pause"){
			mMediaPlayer.pause();
		}
		else if(action == "stop"){
			mMediaPlayer.stop();
		}
		
		if(melodyStatus == Field.MODE_Melody_ON){

			if(action == "start"){
				mMediaPlayer.start();
			}
			
			else if(action == "release"){
				releaseSound();
			}
			else if(action == "captureresume"){
				new CountDownTimer(500,250) {
					
					public void onTick(long millisUntilFinished) {
						
					}
					
					public void onFinish() {
						mMediaPlayer.start();
					}
				}.start();
			}
		}
		
		
	}
	private void setSavedOrientation() {
		SharedPreferences settings = getSharedPreferences(Field.PREFS_DUALCAM, 0);
		int savedOrientation=settings.getInt(Field.PREFS_SET_ORIENTATION, 0);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		if(savedOrientation==7722)this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		else{
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
			if(savedOrientation==7723){
				this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
			else if(savedOrientation==7724){
				this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
		}
	}
	public void textFeatureUtility(String action){
		
//		if(action == "hide"){
//			hideAct.hideThisView(okButton, Field.hideToTop);
//			hideAct.hideThisView(noButton, Field.hideToTop);
//			okButton.setVisibility(View.GONE);
//			noButton.setVisibility(View.GONE);
//			
//			hideAct.showThisView(saveButton, Field.showToBottom);
//			hideAct.showThisView(textButton, Field.showToBottom);
//			hideAct.showThisView(retryButton, Field.showToBottom);
//			
//		}
//		else if(action == "show"){
//
//			hideAct.showThisView(okButton, Field.showToTop);
//			hideAct.showThisView(noButton, Field.showToTop);
//			
//			hideAct.hideThisView(saveButton, Field.hideToBottom);
//			hideAct.hideThisView(textButton, Field.hideToBottom);
//			hideAct.hideThisView(retryButton, Field.hideToBottom);
//		}
		
	}
	
	public void initiateJapWords(){
		errorMessage = getResources().getString(R.string.error_message);
		retakeMessage = getResources().getString(R.string.retake_message);
		restartMessage = getResources().getString(R.string.restart_message);
		saveMessage = getResources().getString(R.string.save_message);
		addALabelText = getResources().getString(R.string.add_a_label_text);
		typeTextHereText = getResources().getString(R.string.typehere_text);
		fontSizeText = getResources().getString(R.string.fontsize_text);
		fontColorText = getResources().getString(R.string.fontcolor_text);

		ok = getResources().getString(R.string.ok_text);
		cancel = getResources().getString(R.string.cancel_text);
		yes = getResources().getString(R.string.yes_text);
		no = getResources().getString(R.string.no_text);
	}
	
	// Twitter Log in
	class TwitterAuthenticateTask extends AsyncTask<String, String, RequestToken> {

        @Override
        protected void onPostExecute(RequestToken requestToken) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL()));
            startActivity(intent);
        }

        @Override
        protected RequestToken doInBackground(String... params) {
            return TwitterUtil.getInstance().getRequestToken();
        }
	}
	
	// Twitter Log out function
	private void twitterLogout() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TwitterConstant.PREFERENCE_TWITTER_OAUTH_TOKEN, "");
        editor.putString(TwitterConstant.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, "");
        editor.putBoolean(TwitterConstant.PREFERENCE_TWITTER_IS_LOGGED_IN, false);
        editor.commit();
        Toast.makeText(getApplicationContext(),  "Logging Out on Twitter", Toast.LENGTH_LONG).show();
	}
	
	// Twitter initial Control for getting token Data
	private void initControlTwitter() {
        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(TwitterConstant.TWITTER_CALLBACK_URL)) {
            String verifier = uri.getQueryParameter(TwitterConstant.URL_PARAMETER_TWITTER_OAUTH_VERIFIER);
            new TwitterGetAccessTokenTask().execute(verifier);
        } else
            new TwitterGetAccessTokenTask().execute("");
	}

	// Background task on getting Access token in twitter
	class TwitterGetAccessTokenTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String userName) {
            //textViewUserName.setText(Html.fromHtml("<b> Welcome " + userName + "</b>"));
        }

        @Override
        protected String doInBackground(String... params) {

            Twitter twitter = TwitterUtil.getInstance().getTwitter();
            RequestToken requestToken = TwitterUtil.getInstance().getRequestToken();
            if (!StringUtil.isNullOrWhitespace(params[0])) {
                try {

                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, params[0]);
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(TwitterConstant.PREFERENCE_TWITTER_OAUTH_TOKEN, accessToken.getToken());
                    editor.putString(TwitterConstant.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, accessToken.getTokenSecret());
                    editor.putBoolean(TwitterConstant.PREFERENCE_TWITTER_IS_LOGGED_IN, true);
                    editor.commit();
                    return twitter.showUser(accessToken.getUserId()).getName();
                } catch (TwitterException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            } else {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String accessTokenString = sharedPreferences.getString(TwitterConstant.PREFERENCE_TWITTER_OAUTH_TOKEN, "");
                String accessTokenSecret = sharedPreferences.getString(TwitterConstant.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, "");
                AccessToken accessToken = new AccessToken(accessTokenString, accessTokenSecret);
                try {
                    TwitterUtil.getInstance().setTwitterFactory(accessToken);
                    return TwitterUtil.getInstance().getTwitter().showUser(accessToken.getUserId()).getName();
                } catch (TwitterException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

	// Background task on Posting Tweet on Twitter 
    class TwitterUpdateStatusTask extends AsyncTask<String, String, Boolean> {

        @Override
        protected void onPostExecute(Boolean result) {
            if (result)
                Toast.makeText(getApplicationContext(), "Tweet successfully", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "Tweet failed", Toast.LENGTH_SHORT).show();
        }
        
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String accessTokenString = sharedPreferences.getString(TwitterConstant.PREFERENCE_TWITTER_OAUTH_TOKEN, "");
                String accessTokenSecret = sharedPreferences.getString(TwitterConstant.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, "");

                if (!StringUtil.isNullOrWhitespace(accessTokenString) && !StringUtil.isNullOrWhitespace(accessTokenSecret)) {
                    AccessToken accessToken = new AccessToken(accessTokenString, accessTokenSecret);
                    
                   // working original
                   // twitter4j.Status status = TwitterUtil.getInstance().getTwitterFactory().getInstance(accessToken).updateStatus(params[0]);
                    
                    /*File finalFile = new File("file:///sdcard/Pictures/temp.jpg");
                    Twitter twitter = null;
                    StatusUpdate stat = new StatusUpdate("awwww");
                    stat.setMedia(finalFile);
                    twitter.updateStatus(stat);*/
                    
                    Twitter twitter =  TwitterUtil.getInstance().getTwitterFactory().getInstance(accessToken);

                    // Update status

                    StatusUpdate ad=new StatusUpdate(params[0]);

                    
                    // The InputStream opens the resourceId and sends it to the buffer
                   
                    //InputStream is = getResources().openRawResource(R.drawable.ic_launcher);
                    
                    
                    //String path = file.getAbsolutePath();
                    FileInputStream xs = new FileInputStream(filePath);
                    
                    
                    
                    ad.setMedia(params[0],xs);
                    
                    twitter4j.Status response = twitter.updateStatus(ad);
                    
                    return true;
                }
                
            } catch (TwitterException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return false;  //To change body of implemented methods use File | Settings | File Templates.
            
        }
    }
    
    
}
