package com.bitizen.camera;


import java.io.IOException;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bitizen.R;
import com.bitizen.camera.util.ColorDetector;

public class CSCameraActivity extends Activity implements CvCameraViewListener2, OnClickListener{ //OnTouchListener
    private static final String TAG = "OCVSample::Activity";
    private boolean mIsColorSelected = false;
    private Mat mRgba;
    private Scalar mBlobColorRgba;
    private Scalar mBlobColorHsv;
    private ColorDetector mDetector;
    private Mat mSpectrum;
    private Scalar CONTOUR_COLOR;
    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean mIsJavaCamera = true;
    private MenuItem mItemSwitchCamera = null;
    private EditText coordinates;
    private Button hitBtn;
    Mat img;
    int centerX, centerY, height, width;
    Display display;
    final Point size = new Point();
    String color;
    //private Size SPECTRUM_SIZE;
   
    
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    //mOpenCvCameraView.setOnTouchListener(CSCameraActivity.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    
    public CSCameraActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());        
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.tutorial1_surface_view);
        coordinates = (EditText) findViewById(R.id.etDisplayCoords);
        //coordinates.setVisibility()
        hitBtn = (Button) findViewById(R.id.btnHit);
        hitBtn.setOnClickListener(this);
        
        if (mIsJavaCamera)
            mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);
        else
            mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_native_surface_view);
        
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        try {
			getCenterCoordinates();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void getCenterCoordinates() throws IOException{
    	display = getWindowManager().getDefaultDisplay();
    	display.getSize(size);
    	height = size.y;
    	width = size.x;
    	centerY = height/2;
    	centerX = width/2;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        mItemSwitchCamera = menu.add("Toggle Native/Java camera");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String toastMesage = new String();
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);

        if (item == mItemSwitchCamera) {
            mOpenCvCameraView.setVisibility(SurfaceView.GONE);
            mIsJavaCamera = !mIsJavaCamera;

            if (mIsJavaCamera) {
                mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);
                toastMesage = "Java Camera";
            } else {
                mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_native_surface_view);
                toastMesage = "Native Camera";
            }

            mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
            mOpenCvCameraView.setCvCameraViewListener(this);
            mOpenCvCameraView.enableView();
            Toast toast = Toast.makeText(this, toastMesage, Toast.LENGTH_LONG);
            toast.show();
        }

        return true;
    }

    
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mDetector = new ColorDetector();
        mSpectrum = new Mat();
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
        //SPECTRUM_SIZE = new Size(200, 64);
        CONTOUR_COLOR = new Scalar(255,0,0,255);
    }

    public void onCameraViewStopped() {
    	mRgba.release();
    }

    
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        if (mIsColorSelected) {
            mDetector.process(mRgba);
            List<MatOfPoint> contours = mDetector.getContours();
            Log.e(TAG, "Contours count: " + contours.size());
            Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);

            Mat colorLabel = mRgba.submat(4, 68, 4, 68);
            colorLabel.setTo(mBlobColorRgba);

            //Mat spectrumLabel = mRgba.submat(4, 4 + mSpectrum.rows(), 70, 70 + mSpectrum.cols());
            //mSpectrum.copyTo(spectrumLabel);
        }
        
        return mRgba;
    }
	
	private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }

	
	public void onClick(View v) {
	    if(v.getId() == R.id.btnHit){
	      	    	
	      //locates the center of the camera input frame
	      int cols = mRgba.cols(); //800
	      int rows = mRgba.rows(); //480
          int xOffset = (mOpenCvCameraView.getWidth() - cols) / 2; //0
          int yOffset = (mOpenCvCameraView.getHeight() - rows) / 2; //0
          coordinates.setText( String.valueOf(centerX) + " : " + String.valueOf(centerY) + " :: " +  String.valueOf(xOffset) + " " + String.valueOf(yOffset)); 
          int x = centerX - xOffset; //400
          int y = centerY - yOffset; //240
          Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");
          
          //sets size of touched region in the form of a rectangle
          Rect touchedRect = new Rect(); //empty Rectangle
          touchedRect.x = (x>45) ? x-45 : 0; 
	      touchedRect.y = (y>45) ? y-45 : 0; 
          touchedRect.width = (x+ 50 < cols) ? x + 50 - touchedRect.x : cols - touchedRect.x; 
	      touchedRect.height = (y+ 50 < rows) ? y + 50 - touchedRect.y : rows - touchedRect.y; 
	      
	      //extracts a rectangular submatrix (touchedRect) from the camera inputFrame(mRgba)
	      Mat touchedRegionRgba = mRgba.submat(touchedRect);
	        
          //stores an image (Mat object) in memory 
          Mat touchedRegionHsv = new Mat();
        
	      //converts an input image from RGB color space to HSV color space
	      Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);
          
          //shows touched region's average color
          mDetector.process(touchedRegionRgba);
          List<MatOfPoint> contours = mDetector.getContours();
          Log.e(TAG, "Contours count: " + contours.size());
          Imgproc.drawContours(touchedRegionRgba, contours, -1, CONTOUR_COLOR);
          Mat colorLabel = touchedRegionRgba;
          colorLabel.setTo(mBlobColorRgba);

          // Calculate average color of touched region
          mBlobColorHsv = Core.sumElems(touchedRegionHsv);
          int pointCount = touchedRect.width * touchedRect.height;
          
          for (int i = 0; i < mBlobColorHsv.val.length; i++)
              mBlobColorHsv.val[i] /= pointCount;

          mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);
          
          
          Log.i(TAG, "Touched rgba color: (" + mBlobColorRgba.val[0] + ", " + mBlobColorRgba.val[1] +
                  ", " + mBlobColorRgba.val[2] + ", " + mBlobColorRgba.val[3] + ")");
          
          coordinates.setText( String.valueOf(mBlobColorHsv.toString()));
          mDetector.setHsvColor(mBlobColorHsv);
          	          
          boolean trueColor = mDetector.checkColor(mBlobColorHsv, color);
          
          if (trueColor == true){
            if(color != null){
              coordinates.setText("HIT : " + color);
            }else{
              coordinates.setText("HIT");
            }
          }else
            coordinates.setText("MISS");
          mIsColorSelected = true;
          
          touchedRegionRgba.release();
          touchedRegionHsv.release();      
		}
	}
}
