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

import com.project.mgr.R;

public class PreviewAudio extends FragmentActivity {
	private String mAllFiles;
	private MediaPlayer mPlayer = null;
	private Button mPlay;
	private boolean mStartPlaying = true;
	private Button mTakePhoto;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_audio);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        mTakePhoto = (Button) findViewById(R.id.take_photo);
        mTakePhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), TakePhotos.class);
                startActivity(intent);
            }
        });
        mPlay = (Button) findViewById(R.id.play); 
        
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
