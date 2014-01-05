package com.ideasimproved.responable;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Context ctx;
	private Camera mCamera;
	private CameraPreview mPreview;
	private WebView webView;

	LogTask mLogTask = null;

	private ListView mTraceView;
    private ArrayAdapter<String> mTraceArrayAdapter;

    String[] reportArray = {
    		"Fireman 1: Fire at Old Navy",
    		"Fireman 2: Fire next to Barnes and Noble",
    		"Policeman 1: Evacuate civilians through entrance near Pink",
    		"Fireman 1: Blaze intensifying at Old Navy - Need more men",
    		"Fireman 2: Send EMT to Barnes and Noble - 3 burn victims need attention",
    		"Policeman 2: Need assistance clearing vehicles from lot 3",
    		"Fireman 1: Fire contained to Old Navy - all civilians evacuated",
    		"Fireman 2: EMT has arrived, transporting burn victims out Pink exit",
    		"Policeman 1: Ambulance arriving at Pink exit"
 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx = this;
		 
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.webView1);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
		webView.loadUrl("http://placenous.com/attdemo/");

		try {
			mCamera = Camera.open();
		} catch (Exception e) {
			//
		}
		mPreview = new CameraPreview(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_feed);
		preview.addView(mPreview);

		CharSequence text = "Idle";
		int duration = Toast.LENGTH_SHORT;

		// start logging
		mTraceArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
		mTraceView = (ListView) findViewById(R.id.in);
		mTraceView.setAdapter(mTraceArrayAdapter);
		mTraceArrayAdapter.clear();

		mLogTask = new LogTask();
		Integer[] strobeArray = {reportArray.length, 100, 8};
		mLogTask.execute(strobeArray);
	}

	@Override
	protected void onResume() {
		super.onResume();

		//		if (mCamera == null) {
		//			try {
		//				mCamera = Camera.open();
		//			} catch (Exception e) {
		//				//
		//			}
		//		}
		//		if (mPreview == null) {
		//		mPreview.
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mLogTask != null) {
			mLogTask.cancel(false);
			mLogTask = null;
		}

		//		if (mCamera != null) {
		//			mPreview.setCamera(null);
		//			mCamera.release();
		//			mCamera = null;
	}

	//	@Override
	//	public boolean onCreateOptionsMenu(Menu menu) {
	//		// Inflate the menu; this adds items to the action bar if it is present.
	//		getMenuInflater().inflate(R.menu.main, menu);
	//		return true;
	//	}

	public class LogTask extends AsyncTask<Integer, Integer, Void> {

		int i = 0;
		boolean runLoop = true;

		@Override
		protected Void doInBackground(Integer... strobeArray) {
			while (runLoop) {
				for (i=0; i<strobeArray[0]; i++) {
					// read string array
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (isCancelled()) runLoop = false;
					publishProgress(i);
				}
			}
			return null;
		}

		@Override
		protected void onCancelled(){
			runLoop = false;
			super.onCancelled();
		}

		protected void onProgressUpdate (Integer... reportCount) {
//			Toast toast = Toast.makeText(ctx, Integer.toString(reportCount[0]), Toast.LENGTH_SHORT);
//			toast.show();
			mTraceArrayAdapter.add(reportArray[reportCount[0]]);
			
//			webView.loadUrl("http://placenous.com/attdemo/");
		}
	}
}

class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder mHolder;
	private Camera mCamera;
	private String TAG = "preview";

	public CameraPreview(Context context, Camera camera) {
		super(context);
		mCamera = camera;

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, now tell the camera where to draw the preview.
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
		} catch (IOException e) {
			Log.d(TAG, "Error setting camera preview: " + e.getMessage());
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// empty. Take care of releasing the Camera preview in your activity.
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.

		if (mHolder.getSurface() == null){
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		try {
			mCamera.stopPreview();
		} catch (Exception e){
			// ignore: tried to stop a non-existent preview
		}

		// set preview size and make any resize, rotate or
		// reformatting changes here

		// start preview with new settings
		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();

		} catch (Exception e){
			Log.d(TAG, "Error starting camera preview: " + e.getMessage());
		}
	}
}