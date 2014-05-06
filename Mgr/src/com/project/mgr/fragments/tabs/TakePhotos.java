package com.project.mgr.fragments.tabs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.Toast;

import com.project.mgr.R;

public class TakePhotos extends FragmentActivity implements AdapterView.OnItemSelectedListener {
    private ResizableCameraPreview mPreview;
    private ArrayAdapter<String> mAdapter;
    private RelativeLayout mLayout;
    private int mCameraId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide status-bar
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Hide title-bar, must be before setContentView
        // requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.take_photos);
        
        // Spinner for preview sizes
        Spinner spinnerSize = (Spinner) findViewById(R.id.spinner_size);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSize.setAdapter(mAdapter);
        spinnerSize.setOnItemSelectedListener(this);
        
        mLayout = (RelativeLayout) findViewById(R.id.layout);		
		mLayout.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	//Camera.takePicture(null, null, photoCallback);
		    	mPreview.takePictureFromPreview();
		    }
		});
    }

    @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    new MenuInflater(this).inflate(R.menu.options, menu);

	    return(super.onCreateOptionsMenu(menu));
	  }

	  @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == R.id.see_pictures) {
	    	new GIF(this).execute();
	    	//Intent intent = new Intent(getBaseContext(), PreviewGif.class);
	    	//startActivity(intent);
	    }

	    return(super.onOptionsItemSelected(item));
	  }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.w("CameraPreviewTestActivity", "onItemSelected invoked");
        Log.w("CameraPreviewTestActivity", "position: " + position);
        Log.w("CameraPreviewTestActivity", "parent.getId(): " + parent.getId());
        switch (parent.getId()) {
            case R.id.spinner_size:
            Rect rect = new Rect();
            mLayout.getDrawingRect(rect);

            if (0 == position) { // "Auto" selected
                mPreview.surfaceChanged(null, 0, rect.width(), rect.height());
            } else {
                mPreview.setPreviewSize(position - 1, rect.width(), rect.height());
            }
            break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    @Override
    protected void onResume() {
        super.onResume();
        createCameraPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
        mLayout.removeView(mPreview);
        mPreview = null;
    }
    
    private void createCameraPreview() {
        // Set the second argument by your choice.
        // Usually, 0 for back-facing camera, 1 for front-facing camera.
        // If the OS is pre-gingerbreak, this does not have any effect.
        mPreview = new ResizableCameraPreview(this, mCameraId, CameraPreview.LayoutMode.FitToParent, false);
        LayoutParams previewLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mLayout.addView(mPreview, 0, previewLayoutParams);

        mAdapter.clear();
        mAdapter.add("Auto");
        List<Camera.Size> sizes = mPreview.getSupportedPreivewSizes();
        for (Camera.Size size : sizes) {
            mAdapter.add(size.width + " x " + size.height);
        }
    }
    
    /**
     * Async GIF creation before previewing
     */
    
	
	
	public byte[] generateGIF() {
		ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
		File picturesDir = new File(Environment.getExternalStorageDirectory().getPath() + "/MgrApp/pictures");
		File pictures[] = picturesDir.listFiles();
		for (int i=0; i < pictures.length; i++) {
			Bitmap myBitmap = BitmapFactory.decodeFile(pictures[i].getAbsolutePath());
			bitmaps.add(myBitmap);
		}
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    GIFEncoder encoder = new GIFEncoder();
	    encoder.start(bos);
	    for (Bitmap bitmap : bitmaps) {
	        encoder.addFrame(bitmap);
	    }
	    encoder.finish();
	    return bos.toByteArray();
	}
	
    
    public static final int PLEASE_WAIT_DIALOG = 1;
	
	@Override
    public Dialog onCreateDialog(int dialogId) {
        switch (dialogId) {
        case PLEASE_WAIT_DIALOG:
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Creating GIF");
            dialog.setMessage("Please wait...");
            dialog.setCancelable(true);
            return dialog;
        default:
            break;
        }
        return null;
    }

	class GIF extends AsyncTask<Void, Void, Void> {
		Activity startingActivity;

		public GIF(Activity startingActivity) {
			this.startingActivity = startingActivity;
		}
		 
		@Override
		protected void onPreExecute() {
			startingActivity.showDialog(TakePhotos.PLEASE_WAIT_DIALOG);
		}
				 
		@Override
		protected Void doInBackground(Void... arg0) {
			FileOutputStream outStream = null;
	        try{
	            outStream = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/MgrApp/GIFfromPictures.gif");
	            outStream.write(generateGIF());
	            outStream.close();
	        }catch(Exception e){
	            e.printStackTrace();
	        }
			/*
			try {
				Thread.sleep(5000);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }*/
	        return null;
	    }
		 
		    @Override
		    protected void onPostExecute(Void result) {
		    	startingActivity.removeDialog(TakePhotos.PLEASE_WAIT_DIALOG);
		        Toast.makeText(startingActivity, "GIF created!", Toast.LENGTH_SHORT).show();
		        Intent intent = new Intent(getBaseContext(), PreviewGif.class);
		    	startActivity(intent);
		    }
	}


}