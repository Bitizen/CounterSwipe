package com.bitizen.camera.util;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import android.graphics.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import android.view.Display;
import org.opencv.core.CvType;

public class ColorDetector {
	
    // Lower and Upper bounds for range checking in HSV color space
    private Scalar mLowerBound = new Scalar(0);
    private Scalar mUpperBound = new Scalar(0);
    
    // Minimum contour area in percent for contours filtering
    private static double mMinContourArea = 0.1;
    
    // Color radius for range checking in HSV color space
    private Scalar mColorRadius = new Scalar(25,50,50,0);
    private Mat mSpectrum = new Mat();
    private List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();
    Mat img;
    int centerX, centerY, height, width;
    Display display;
    final Point size = new Point();
    private static Mat mSrc;
    
    // Cache
    Mat mPyrDownMat = new Mat();
    Mat mHsvMat = new Mat();
    Mat mMask = new Mat();
    Mat mDilatedMask = new Mat();
    Mat mHierarchy = new Mat();
    
    final String BLUE = "Blue";
    final String RED = "Red";
    final String GREEN = "Green";
    final String YELLOW = "Yellow";
    final String ORANGE = "Orange";
    
    boolean colorHit = false;
    
    public void setColorRadius(Scalar radius) {
        mColorRadius = radius;
    }

    public void setHsvColor(Scalar hsvColor) {
        double minH = (hsvColor.val[0] >= mColorRadius.val[0]) ? hsvColor.val[0]-mColorRadius.val[0] : 0;
        double maxH = (hsvColor.val[0]+mColorRadius.val[0] <= 255) ? hsvColor.val[0]+mColorRadius.val[0] : 255;

        mLowerBound.val[0] = minH;
        mUpperBound.val[0] = maxH;

        mLowerBound.val[1] = hsvColor.val[1] - mColorRadius.val[1];
        mUpperBound.val[1] = hsvColor.val[1] + mColorRadius.val[1];

        mLowerBound.val[2] = hsvColor.val[2] - mColorRadius.val[2];
        mUpperBound.val[2] = hsvColor.val[2] + mColorRadius.val[2];

        mLowerBound.val[3] = 0;
        mUpperBound.val[3] = 255;

        Mat spectrumHsv = new Mat(1, (int)(maxH-minH), CvType.CV_8UC3);

        for (int j = 0; j < maxH-minH; j++) {
            byte[] tmp = {(byte)(minH+j), (byte)255, (byte)255};
            spectrumHsv.put(0, j, tmp);
        }

        Imgproc.cvtColor(spectrumHsv, mSpectrum, Imgproc.COLOR_HSV2RGB_FULL, 4);
    }
   
    public Mat getSpectrum() {
        return mSpectrum;
    }

    public void setMinContourArea(double area) {
        mMinContourArea = area;
    }

    public void process(Mat rgbaImage) {
        Imgproc.pyrDown(rgbaImage, mPyrDownMat);
        Imgproc.pyrDown(mPyrDownMat, mPyrDownMat);

        Imgproc.cvtColor(mPyrDownMat, mHsvMat, Imgproc.COLOR_RGB2HSV_FULL);

        Core.inRange(mHsvMat, mLowerBound, mUpperBound, mMask);
        Imgproc.dilate(mMask, mDilatedMask, new Mat());

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Imgproc.findContours(mDilatedMask, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Find max contour area
        double maxArea = 0;
        Iterator<MatOfPoint> each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint wrapper = each.next();
            double area = Imgproc.contourArea(wrapper);
            if (area > maxArea)
                maxArea = area;
        }

        // Filter contours by area and resize to fit the original image size
        mContours.clear();
        each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
            if (Imgproc.contourArea(contour) > mMinContourArea*maxArea) {
                Core.multiply(contour, new Scalar(4,4), contour);
                mContours.add(contour);
            }
        }
    }

    public List<MatOfPoint> getContours() {
        return mContours;
    }
    
    public boolean checkColor(Scalar hsvColor, String color){
        if(hsvColor.val[0] >= 106 && hsvColor.val[0] <= 160 && hsvColor.val[1] >= 60 && hsvColor.val[1] <= 255 && hsvColor.val[2] >= 90 && hsvColor.val[2]<= 255){        	
        	colorHit = true;
        	color = BLUE;
        }else if(hsvColor.val[0] >= 90 && hsvColor.val[0] <= 140 && hsvColor.val[1] >= 120 && hsvColor.val[1] <= 255 && hsvColor.val[2] >= 80 && hsvColor.val[2] <= 160){
        	colorHit = true;
        	color = GREEN;
        }else if(hsvColor.val[0] >= 5 && hsvColor.val[0] <= 15 && hsvColor.val[1] >= 50 && hsvColor.val[1] <= 255 && hsvColor.val[2] >= 50 && hsvColor.val[2] <= 255){
        	colorHit = true;
        	color = RED;
        }else if(hsvColor.val[0] >= 20 && hsvColor.val[0] <= 30 && hsvColor.val[1] >= 0 && hsvColor.val[1] <= 255 && hsvColor.val[2] >= 0 && hsvColor.val[2] <= 255){
        	colorHit = true;
        	color = ORANGE;
        }else if(hsvColor.val[0] >= 20 && hsvColor.val[0] <= 45 && hsvColor.val[1] >= 100 && hsvColor.val[1] <= 255 && hsvColor.val[2] >= 100 && hsvColor.val[2] <= 255){
        	colorHit = true;
        	color = YELLOW;
        }else{
        	colorHit = false;
        }
        return colorHit;
    }
    
    //converts an input image from YUV to RGB to HSV color space
    public static void cvt_YUVtoRGBtoHSV (Mat src, Mat dst){
    	mSrc = new Mat();
    	src.copyTo(mSrc);
    	Imgproc.cvtColor(mSrc, dst, Imgproc.COLOR_YUV420sp2RGB);
    	Imgproc.cvtColor(dst, dst, Imgproc.COLOR_RGB2HSV);
    }
    
    public static void getRedMat(Mat src, Mat dst){
    	Core.inRange(src, new Scalar(5, 50, 50), new Scalar(15, 255, 255), dst);
    }
    
    public static void getOrangeMat(Mat src, Mat dst){
    	Core.inRange(src, new Scalar(20, 0, 0), new Scalar(30, 255, 255), dst);
    }
    
    public static void getYellowMat(Mat src, Mat dst){
    	Core.inRange(src, new Scalar(20, 100, 100), new Scalar(30, 255, 255), dst);
    }       
    
    public static void getGreenMat(Mat src, Mat dst){
    	Core.inRange(src, new Scalar(90, 120, 80), new Scalar(140, 255, 160), dst);
    }
    
    public static void getBlueMat(Mat src, Mat dst){
    	Core.inRange(src, new Scalar(100, 100, 100), new Scalar(120, 255, 255), dst);
    }
    
    public static void detectSingleBlob(Mat src, Mat image, String text, Mat dst){
    	List<MatOfPoint> contours = new ArrayList<MatOfPoint>(); //vector<vector<Point>> contours;
    	Mat hierarchy = new Mat();
    	src.copyTo(dst);
    	
    	Imgproc.findContours(image, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
    	int k = getBiggestContourIndex(contours);
    	Rect boundRect = setContourRect(contours, k);
		Point center = new Point();
    	getCenterPoint(boundRect.tl(), boundRect.br(), center);
    	Core.rectangle(dst, boundRect.tl(), boundRect.br(), new Scalar(255, 255, 0), 2, 8, 0);
    	Core.putText(dst, text, boundRect.tl(), 0/*font*/, 1, new Scalar(255, 0, 0, 255), 3);    	
    }
    
    
    public static void getCenterPoint(org.opencv.core.Point point, org.opencv.core.Point point2, Point dst){
    	dst.x = (int) ((point.x + point2.x)/2);
    	dst.y = (int) ((point.y + point2.y)/2);
    }
    
    public static int getBiggestContourIndex(List<MatOfPoint> contours){
    	double maxArea = 0;
    	Iterator<MatOfPoint> each = contours.iterator();
    	int j = 0;
    	int k = -1;
    	while (each.hasNext()){
    		MatOfPoint wrapper = each.next();
    		double area = Imgproc.contourArea(wrapper);
    		if(area > maxArea){
    			maxArea = area;
    			k = j;
    		}
    		j++;
    	}
    	return k;
    }
    
    public static Rect setContourRect(List<MatOfPoint> contours, int k){
    	Rect boundRect = new Rect();
    	Iterator<MatOfPoint> each = contours.iterator();
    	int j = 0;
    	while (each.hasNext()){
    		MatOfPoint wrapper= each.next();
    		if(j == k){
    			return Imgproc.boundingRect(wrapper);
    		}
    		j++;
    	}
    	return boundRect;
    }
    
    
}
