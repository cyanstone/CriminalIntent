package com.example.criminalintent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Size;
//import android.graphics.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class CrimeCameraFragment extends Fragment {
	private static final String TAG = "CrimeCameraFragment";
	public static final String EXTRA_PHOTO_FILENAME = "com.example.criminalintent.photo_filename";
	
	private Camera mCamera;
	private SurfaceView mSurfaceView;
	private View mProgressContainer;
	
	private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
		
		@Override
		public void onShutter() {
			// TODO Auto-generated method stub
			mProgressContainer.setVisibility(View.VISIBLE);
		}
	};
	
	private Camera.PictureCallback  mJpegCallback = new Camera.PictureCallback() {
		
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			String filename = UUID.randomUUID().toString() + ".jpg";
			FileOutputStream os = null;
			boolean success = true;
			
			try{
				os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
				os.write(data);
			}catch(Exception e){
				Log.e(TAG,"Error writing to file " + filename,e);
				success = false;
			}finally{
				if(os != null){
					try {
						os.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						Log.e(TAG,"Error closing file " + filename,e);
						success = false;
					}
				}
			}
			
			if(success == true){
				Log.i(TAG,"JPEG saved at " + filename);
				Intent i = new Intent();
				i.putExtra(EXTRA_PHOTO_FILENAME, filename);
				getActivity().setResult(Activity.RESULT_OK,i);
			}else{
				getActivity().setResult(Activity.RESULT_CANCELED);
			}
			getActivity().finish();
		}
	};
	
	@SuppressWarnings({ "deprecated", "deprecation" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_crime_camera,container, false);
		
		mProgressContainer = v.findViewById(R.id.crime_camera_progressContainer);
		mProgressContainer.setVisibility(View.INVISIBLE);
		Button takePictureButton = (Button) v.findViewById(R.id.crime_camera_takePictureButton);
		takePictureButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			//	getActivity().finish();
				if(mCamera != null){
					mCamera.takePicture(mShutterCallback, null, mJpegCallback);
				}
			}
		});
		
		mSurfaceView = (SurfaceView) v.findViewById(R.id.crime_camera_surfaceView);
		SurfaceHolder holder = mSurfaceView.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		holder.addCallback(new SurfaceHolder.Callback() {
			
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				if(mCamera != null){
					mCamera.stopPreview();
				}
			}
			
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				try{
					if(mCamera != null){
						mCamera.setPreviewDisplay(holder);
					}
				}catch(IOException e){
					Log.e(TAG, "Error setting up preview display",e);
				}
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,
					int height) {
				// TODO Auto-generated method stub
				if(mCamera  == null) return;
				Camera.Parameters parameters = mCamera.getParameters();
				//Size s = null;
				//Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), width, height);
				Size s = getOptimalPreviewSize(parameters.getSupportedPreviewSizes(), width, height);
				parameters.setPreviewSize(s.width, s.height);   //某些手机调用该函数时如果width和height出现奇数情况下，则会出现黑屏
				s = getOptimalPreviewSize(parameters.getSupportedPictureSizes(), width, height);
				parameters.setPictureSize(s.width, s.height);
				mCamera.setParameters(parameters);
				try{
					mCamera.startPreview();
				} catch(Exception e){
					Log.e(TAG,"Could not start preview",e);
					mCamera.release();
					mCamera = null;
				}
			}
		});
		
		return v;
	}
	
	private Size getBestSupportedSize(List<Size> sizes,int width,int height){
		Size bestSize = sizes.get(0);
		int largestArea = bestSize.height * bestSize.width;
		for(Size s : sizes){
			int area = s.width * s.height;
			if(area > largestArea){
				bestSize = s;
				largestArea = area;
			}
		}
		return bestSize;
	}
	
	/**
	 * getOptinalPreviewSize()，解决部分手机黑屏
	 */
	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		  final double ASPECT_TOLERANCE = 0.05;
		  double targetRatio = (double) w / h;
		  if (sizes == null)
		   return null;

		  Size optimalSize = null;
		  double minDiff = Double.MAX_VALUE;

		  int targetHeight = h;

		  // Try to find an size match aspect ratio and size
		  for (Size size : sizes) {
		   double ratio = (double) size.width / size.height;
		   if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
		    continue;
		   if (Math.abs(size.height - targetHeight) < minDiff) {
		    optimalSize = size;
		    minDiff = Math.abs(size.height - targetHeight);
		   }
		  }

		  // Cannot find the one match the aspect ratio, ignore the requirement
		  if (optimalSize == null) {
		   minDiff = Double.MAX_VALUE;
		   for (Size size : sizes) {
		    if (Math.abs(size.height - targetHeight) < minDiff) {
		     optimalSize = size;
		     minDiff = Math.abs(size.height - targetHeight);
		    }
		   }
		  }
		  return optimalSize;
		 }
	/**
	 * getOptinalPreviewSize()
	 */
	
	
	@TargetApi(9)
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
			mCamera = Camera.open(0);
		}else{
			mCamera = Camera.open();
		}
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		if(mCamera != null){
			mCamera.release();
			mCamera = null;
		}
	}
}
