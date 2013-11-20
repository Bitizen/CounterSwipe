package com.bitizen.cameratest.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;


public class MediaHelper {

	public static File getOutputMediaFile(){
	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Spike");
	    
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("failed to create directory");
	            return null;
	        }
	    }

	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile = new File(mediaStorageDir.getPath() + File.separator +"IMG_"+ timeStamp +".jpg");

	    return mediaFile;
	}

	public static boolean saveToFile(byte[] bytes, File file){
		boolean saved = false;
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(bytes);
			fos.close();
			saved = true;
		} catch (FileNotFoundException e) {
			Log.e("FileNotFoundException", e);
		} catch (IOException e) {
			Log.e("IOException", e);
		}
		return saved;
	}

}
