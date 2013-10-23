package com.cam.dualcam.utility;

public class Field {
	
	//Crop
	public static final int SET_AS = 1;
	public static final int CROP = 2;
	public static final int ROT_LEFT = 3;
	public static final int ROT_RIGHT = 4;
	
	//Basic Requests
	public static final int CAMERA_REQUEST = 100;
	public static final int VIDEO_REQUEST = 200;
	public static final int GALLERY_REQUEST = 300;
	public static final int CAMERA_CROP_REQUEST = 500;
	public static final int NON_CAMERA_CROP_REQUEST = 200;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	
	//Tint
  	public static final double PI = 3.14159d;
  	public static final double FULL_CIRCLE_DEGREE = 360d;
  	public static final double HALF_CIRCLE_DEGREE = 180d;
  	public static final double RANGE = 256d;
  	//Sharpen from blog
  	final static int KERNAL_WIDTH = 3;
  	final static int KERNAL_HEIGHT = 3;
  	int[][] kernalBlur = {
  	   {0, -1, 0},
  	   {-1, 5, -1},
  	   {0, -1, 0}
  	};
  	
  	//Toast showtime
  	public static final int SHOWTIME = 5000;
	public static final int FILE_SELECT_CODE = 0;

	
	public static Integer ActionClick = 1001;
	public static Integer ActionClicking = 1002;
	public static Integer ActionClickEnd = 1000;
	
	public static Integer ActionZoom = 2001;
	public static Integer ActionZooming = 2002;
	public static Integer ActionZoomEnd = 2000;
	
	public static Integer ActionLongClick = 3001;
	public static Integer ActionLongClicking = 3002;
	public static Integer ActionLongClickEnd = 3000;
	
	public static Integer ActionMove = 4001;
	public static Integer ActionMoving = 4002;
	public static Integer ActionMoveEnd = 4000;
	
	public static Integer ActionDoubleTap = 5001; 	
	public static Integer ActionDoubleTaping = 5002; 	
	public static Integer ActionDoubleTapEnd = 5000;
	
	public static Integer ActionStateNotClickable = 9000;
	public static Integer ActionStateClickable = 9001;
	public static Integer ActionStateNothing = 9999;
	
	
	public static Integer CameraAutoFocus = 1101;
	public static Integer CameraFocusing = 1102;
	public static Integer CameraFocusingEnd = 1100;
	
	public static Integer CameraCanCapture = 2001;
	public static Integer CameraCannotCapture = 2000;
	
	public static final int standbyToCaptureJutsu	= 0000;
	public static final int singleTapCaptureJutsu 	= 0001;
	public static final int doubleTapCaptureJutsu	= 0002;
	public static final int noTapCaptureJutsu		= 0003;
	public static final int longTapCaptureJutsu 	= 0004;
	public static final int onceTapCaptureJutsu 	= 0005;
	
	
	public static final int hideToLeft 	= 1000;
	public static final int hideToTop  	= 0100;
	public static final int hideToRight 	= 0010;
	public static final int hideToBottom	= 0001;
	
	public static final int showToLeft 	= 2000;
	public static final int showToTop  	= 0200;
	public static final int showToRight 	= 0020;
	public static final int showToBottom	= 0002;
	
	public static final int MODE_ManualAF_and_Capture 	= 7700;
	public static final int MODE_SingleAF_and_Capture	= 7701;
	public static final int MODE_DoubleAF_and_Capture	= 7702;
	public static final int MODE_DefaultAF_and_Capture 	= MODE_ManualAF_and_Capture;
	
	public static final int MODE_Flash_OFF 				= 7710;
	public static final int MODE_Flash_ON 				= 7711;
	public static final int MODE_Flash_Auto 			= 7712;
	public static final int MODE_Flash_Default			= MODE_Flash_Auto;

	public static final int MODE_Melody_OFF 			= 7720;
	public static final int MODE_Melody_ON 				= 7721;
	public static final int MODE_Melody_DEFAULT			= MODE_Melody_OFF;
	
	public static final int MODE_Portrait	 			= 7722;
	public static final int MODE_Landscape 				= 7723;
	public static final int MODE_Orientation_DEFAULT	= MODE_Portrait;
	
	

	public static final int SET_AutoFocus				= 7770;
	public static final int SET_Flash 					= 7771;
	public static final int SET_Melody					= 7772;
	public static final int SET_Orientation				= 7773;
	//public static final int MODE_Flash 					= 7771;
	
	//For the sharedPrefs
	public static final String PREFS_DUALCAM			= "DualCamSettings";
	public static final String PREFS_SET_AUTOFOCUS		= "AutoFocus";
	public static final String PREFS_SET_FLASH			= "Flash";
	public static final String PREFS_SET_MELODY			= "Melody";
	public static final String PREFS_SET_ORIENTATION	= "Orientation";
	

	public static final String PREFS_SET_MELODY2			= "Melody";
}

