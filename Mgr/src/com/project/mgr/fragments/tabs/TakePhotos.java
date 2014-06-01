package com.project.mgr.fragments.tabs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.project.mgr.R;

public class TakePhotos extends FragmentActivity {
    private ResizableCameraPreview mPreview;
    private RelativeLayout mLayout;
    private ImageView mSwitchCam;
    private int mCameraId = 0;
    private Boolean makingGif = false, photosTaken = false;
    private String fileName = generateFileName();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide status-bar
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Hide title-bar, must be before setContentView
        // requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.take_photos);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        File pictures = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MgrApp/pictures");
        pictures.delete();
        deleteFolder(pictures);
        pictures.mkdirs();
        
        mLayout = (RelativeLayout) findViewById(R.id.layout);		
		mLayout.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	mPreview.takePictureFromPreview();
		    	photosTaken = true;
		    }
		});
		
		mSwitchCam = (ImageView) findViewById(R.id.switchCamera);
		mSwitchCam.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	mPreview.stop();
		    	mLayout.removeView(mPreview);
		    	if (mCameraId == 0) {
		    		mCameraId = 1;
		    	} else {
		    		mCameraId = 0;
		    	}
		    	mPreview = null;
		    	createCameraPreview();
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
	    	if (photosTaken) {
	    		new GIF(this).execute();
	    	} else {
	    		Toast.makeText(this, "You need to take some photos", 2000).show();
	    	}
	    }
	    switch (item.getItemId()) {
  	     // Respond to the action bar's Up/Home button
  	    	case android.R.id.home:
  	    		NavUtils.navigateUpFromSameTask(this);
  	    		return true;
  	    }
	    return(super.onOptionsItemSelected(item));
	  }

    @Override
    protected void onResume() {
        super.onResume();
        createCameraPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!makingGif) {
        	mPreview.stop();
            mLayout.removeView(mPreview);
            mPreview = null;	
        }
    }
    
    private void createCameraPreview() {
        // Set the second argument by your choice.
        // Usually, 0 for back-facing camera, 1 for front-facing camera.
        // If the OS is pre-gingerbreak, this does not have any effect.
        mPreview = new ResizableCameraPreview(this, mCameraId, CameraPreview.LayoutMode.FitToParent, false);
        LayoutParams previewLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        /*Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int centerX = width/2;
        mPreview.setCenterPosition(centerX, -1);*/
        mLayout.addView(mPreview, 0, previewLayoutParams);
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
	    encoder.setFrameRate(6);
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
			mPreview.stop();
	        mLayout.removeView(mPreview);
	        mPreview = null;
			makingGif = true;
		}
				 
		@Override
		protected Void doInBackground(Void... arg0) {
			FileOutputStream outStream = null;
	        try{
	        	File gifDir = new File(Environment.getExternalStorageDirectory().getPath() + "/MgrApp/GIF/");
	        	if (gifDir.exists()) {
	        		deleteFolder(gifDir);
	        		gifDir.mkdirs();
	        	} else {
	        		gifDir.mkdirs();
	        	}
	            outStream = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/MgrApp/GIF/"+fileName);
	            outStream.write(generateGIF());
	            outStream.close();
	        }catch(Exception e){
	            //e.printStackTrace();
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
		    	final Intent intent = new Intent(TakePhotos.this, PreviewGif.class);
				Bundle extras = new Bundle();
				extras.putString("user_id", userId());
				extras.putString("fileName", fileName);
				intent.putExtras(extras);
		    	startingActivity.removeDialog(TakePhotos.PLEASE_WAIT_DIALOG);
		    	Toast.makeText(startingActivity, "GIF created!", Toast.LENGTH_SHORT).show();
				startActivity(intent);    	
		    }
		    
		    @Override
		    protected void onCancelled() {
		    	//TODO some activity restart needed 
		    	//Intent intent = new Intent(getBaseContext(), TakePhotos.class);
		    	//startActivity(intent);
		    	createCameraPreview();
		    }
	}

	public String userId() {
		Intent extras = this.getIntent();
    	if (extras != null) {
    	    String user_id = extras.getStringExtra("user_id");
    	    return user_id;
    	} else {
    		return null;
    	}
	}
	
	public String generateFileName() {
		Date now = new Date();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
	    String fileName = "GIF_"+formatter.format(now) + ".gif";
	    return fileName;
	}
	
	public static void deleteFolder(File folder) {
	    File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolder(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	    folder.delete();
	}

}