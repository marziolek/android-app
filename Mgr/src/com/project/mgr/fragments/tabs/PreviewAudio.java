package com.project.mgr.fragments.tabs;

import java.io.File;
import java.io.IOException;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.project.mgr.R;

public class PreviewAudio extends FragmentActivity {
	private String mAllFiles;
	private MediaPlayer mPlayer = null;
	private ImageButton mPlay;
	private boolean mStartPlaying = true;
	private ImageButton mTakePhoto;
	private String user_id;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_audio);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
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
		    	    		user_id = user.getId();
		    	    	}   
		    	    }   
	    	    }   
    	    }); 
    	    Request.executeBatchAsync(request);
    	} 
        
        mTakePhoto = (ImageButton) findViewById(R.id.take_photo);
        mTakePhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent(getBaseContext(), TakePhotos.class);
            	Bundle extras = new Bundle();
            	extras.putString("user_id", user_id);
	    		intent.putExtras(extras);
                startActivity(intent);
            }
        });
        mPlay = (ImageButton) findViewById(R.id.play); 
        
        mPlay.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch ( event.getAction() ) {
                	case MotionEvent.ACTION_DOWN: 
                		mStartPlaying = !mStartPlaying;
                        startPlaying();
                    	return true;
                    case MotionEvent.ACTION_UP:
                    	mStartPlaying = !mStartPlaying;
                    	stopPlaying();
                    	return true;
				}
                return false;
			}
		});
	}
    
    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
        	mAllFiles = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MgrApp/audio";
        	File dir = new File(mAllFiles);
        	File[] files = dir.listFiles();
                mPlayer.setDataSource(files[files.length - 1].toString());
                mPlayer.prepare();
                mPlayer.start();
        } catch (IOException e) {
                Log.d("asdasd", "prepare() failed when trying to play recorded audio");
        }
    }
    
    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }
    
    @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	     switch (item.getItemId()) {
	     // Respond to the action bar's Up/Home button
	     case android.R.id.home:
	         NavUtils.navigateUpFromSameTask(this);
	         return true;
	     }
	     return super.onOptionsItemSelected(item);
	 }
}
