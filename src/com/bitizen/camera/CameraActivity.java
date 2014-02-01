package com.bitizen.camera;

import static com.bitizen.camera.util.CameraHelper.cameraAvailable;
import static com.bitizen.camera.util.CameraHelper.getCameraInstance;
import static com.bitizen.camera.util.MediaHelper.getOutputMediaFile;
import static com.bitizen.camera.util.MediaHelper.saveToFile;

import java.io.File;

import com.bitizen.camera.FromXML;
import com.bitizen.camera.CameraPreview;
import com.bitizen.camera.util.Log;
import com.bitizen.counterswipe.R;
import com.bitizen.counterswipe.ResultsActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class CameraActivity extends Activity implements PictureCallback {

	public static final String EXTRA_IMAGE_PATH = "com.bitizen.camera.CameraActivity.EXTRA_IMAGE_PATH";

	private Camera camera;
	private CameraPreview cameraPreview;
	
	private final Context CONTEXT = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		setResult(RESULT_CANCELED);
		// Camera may be in use by another activity or the system or not available at all
		camera = getCameraInstance();
		if(cameraAvailable(camera)){
			initCameraPreview();
		} else {
			finish();
		}
	}

	// Show the camera view on the activity
	private void initCameraPreview() {
		cameraPreview = (CameraPreview) findViewById(R.id.camera_preview);
		cameraPreview.init(camera);
	}

	@FromXML
	public void onCaptureClick(View button){
		// Take a picture with a callback when the photo has been created
		// Here you can add callbacks if you want to give feedback when the picture is being taken
		//camera.takePicture(null, null, this);
	}

	@FromXML
	public void onMenuClick(View button){
		//Intent intent = new Intent(this, ResultsActivity.class);
    	//startActivity(intent);
		Toast.makeText(CONTEXT, "Menu selected.", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		/*Log.d("Picture taken");
		String path = savePictureToFileSystem(data);
		setResult(path);
		finish();
		*/

		Toast.makeText(CONTEXT, "Hit test.", Toast.LENGTH_SHORT).show();
	}

	private static String savePictureToFileSystem(byte[] data) {
		File file = getOutputMediaFile();
		saveToFile(data, file);
		return file.getAbsolutePath();
	}

	private void setResult(String path) {
		Intent intent = new Intent();
		intent.putExtra(EXTRA_IMAGE_PATH, path);
		setResult(RESULT_OK, intent);
	}

	// ALWAYS remember to release the camera when you are finished
	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera();
	}

	private void releaseCamera() {
		if(camera != null){
			camera.release();
			camera = null;
		}
	}
}
