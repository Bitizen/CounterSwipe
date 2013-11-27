package com.bitizen.camera.util;

import android.hardware.Camera;

public class CameraHelper {

	public static boolean cameraAvailable(Camera camera) {
		return camera != null;
	}

	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();
		} catch (Exception e) {
			Log.d("getCamera failed", e);
		}
		return c;
	}

}
