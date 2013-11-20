package com.bitizen.cameratest;

import static com.bitizen.cameratest.util.CameraHelper.cameraAvailable;
import static com.bitizen.cameratest.util.CameraHelper.getCameraInstance;
import static com.bitizen.cameratest.util.MediaHelper.getOutputMediaFile;
import static com.bitizen.cameratest.util.MediaHelper.saveToFile;

import java.io.File;

import com.bitizen.cameratest.FromXML;
import com.bitizen.cameratest.CameraPreview;
import com.bitizen.cameratest.util.Log;
import com.bitizen.counterswipe.R;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.view.View;

public class CameraActivity extends Activity implements PictureCallback {

	protected static final String EXTRA_IMAGE_PATH = "com.bitizen.cameratest.CameraActivity.EXTRA_IMAGE_PATH";

	private Camera camera;
	private CameraPreview cameraPreview;

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
		camera.takePicture(null, null, this);
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		Log.d("Picture taken");
		String path = savePictureToFileSystem(data);
		setResult(path);
		finish();
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
