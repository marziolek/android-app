package com.project.mgr.fragments.tabs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import android.support.v4.app.NavUtils;
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

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.project.mgr.R;

public class TakePhotos extends FragmentActivity {
    private ResizableCameraPreview mPreview;
    private ArrayAdapter<String> mAdapter;
    private RelativeLayout mLayout;
    private int mCameraId = 0;
    private Boolean makingGif = false;
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
        deleteFolder(pictures);
        
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
		    	final Session session = Session.getActiveSession();
		    	if (session != null && session.isOpened()) {
		    		// If the session is open, make an API call to get user data
		    	    // and define a new callback to handle the response
		    	    Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
			    	    @Override
			    	    public void onCompleted(GraphUser user, Response response) {
			    	               // If the response is successful
				    	    if (session == Session.getActiveSession()) {
				    	    	if (user != null) {
				    	    		Bundle extras = new Bundle();
				    	    		extras.putString("user_id", user.getId());
				    	    		extras.putString("fileName", fileName);
				    	    		intent.putExtras(extras);
				    	    		startingActivity.removeDialog(TakePhotos.PLEASE_WAIT_DIALOG);
				    		        Toast.makeText(startingActivity, "GIF created!", Toast.LENGTH_SHORT).show();
				    		        startActivity(intent);
				    	    	}   
				    	    }   
			    	    }   
		    	    }); 
		    	    Request.executeBatchAsync(request);
		    	} 
		    	
		    }
		    
		    @Override
		    protected void onCancelled() {
		    	//TODO some activity restart needed 
		    	//Intent intent = new Intent(getBaseContext(), TakePhotos.class);
		    	//startActivity(intent);
		    	createCameraPreview();
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