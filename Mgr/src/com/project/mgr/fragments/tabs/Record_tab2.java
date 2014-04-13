/**
 * 
 */
package com.project.mgr.fragments.tabs;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.project.mgr.R;

public class Record_tab2 extends Fragment {
	
	        private static final String LOG_TAG = "Record_tab2";
	        private String mFileName = null;
	        private Button mRecordButton = null;
	        private MediaRecorder mRecorder = null;
	        private Button mPlayButton = null;
	        private MediaPlayer mPlayer = null;
	        private boolean mStartPlaying = false;
	        private boolean mStartRecording = false;
	        private AudioRecorderResultListener audioRecorderResultListener;
	        private boolean maxDuration = false;
	        
	        public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                    Bundle savedInstanceState) {

	                View v= inflater.inflate(R.layout.record_tab2, container, false);
	                
	                	mRecordButton = (Button) v.findViewById(R.id.record_button);
	                    mRecordButton.setOnTouchListener(recordListener);
	                        
	                    mPlayButton = (Button) v.findViewById(R.id.play_button);
	                    mPlayButton.setEnabled(false);
	                    mPlayButton.setOnTouchListener(playListener);             
	                    
	                return v;
	        }
	        
	        final Timer timer = new Timer();
	        
	        private OnTouchListener recordListener = new OnTouchListener(){
                public boolean onTouch(View v, MotionEvent event) {
                	switch ( event.getAction() ) {
                    	case MotionEvent.ACTION_DOWN: 
                    		timer.schedule(new TimerTask() {    	
	                    		@Override
	                    		public void run() {
	                    			stopRecording();
	                    			Log.v("xinxin**", "mRecord and mstartRecording=recording ended");
	                    			if (!maxDuration) {
	                    				maxDuration = true;
	                    			}
	                    		}
                    		}, 10000);
                    		startRecording();
			                if (mStartRecording) {
			                	mPlayButton.setEnabled(false);
			                }
                    		return true;
                    	case MotionEvent.ACTION_UP:
                    		if (!maxDuration) {
                    			stopRecording();
                    			maxDuration = true;
                    			Log.v("xinxin**", "mRecord and mstartRecording=recording ended");
                    		}
	                        mPlayButton.setEnabled(true);
	                        return true;
	                    }
                   return false;
                }
            };
	        
            private OnTouchListener playListener = new OnTouchListener(){
                public boolean onTouch(View v, MotionEvent event) {
                	switch ( event.getAction() ) {
                    	case MotionEvent.ACTION_DOWN: 
                    		mStartPlaying = !mStartPlaying;
                            startPlaying();
                            if (mStartPlaying) {
                                    mRecordButton.setEnabled(false);
                            } else {
                                    mRecordButton.setEnabled(true);
                            }
                    		return true;
                    	case MotionEvent.ACTION_UP:
                    		mStartPlaying = !mStartPlaying;
                            stopPlaying();
                            if (mStartPlaying) {
                                    mRecordButton.setEnabled(false);
                            } else {
                                    mRecordButton.setEnabled(true);
                            }
                    		return true;
	                    }
                   return false;
                }
            };
            
	        /**
	         * recording and playing
	         */
	        public void setAudioRecorderResultListener(AudioRecorderResultListener audioRecorderResultListener){
	                this.audioRecorderResultListener = audioRecorderResultListener;
	        }
	        
	        private void startPlaying() {
	                mPlayer = new MediaPlayer();
	                try {
	                        mPlayer.setDataSource(mFileName);
	                        mPlayer.prepare();
	                        mPlayer.start();
	                } catch (IOException e) {
	                        Log.e(LOG_TAG, "prepare() failed when trying to play recorded audio");
	                }
	        }

	        private void stopPlaying() {
	                mPlayer.release();
	                mPlayer = null;
	        }

	        private void startRecording() {
	                mRecorder = new MediaRecorder();
	                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	                
	                mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
	                mFileName += "/audiorecordtest.3gpp";
	    	        
	                mFileName = mFileName.replaceAll("<audio.*src=.*/>","");
	                mRecorder.setOutputFile(mFileName);
	                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

	                try {
	                        mRecorder.prepare();
	                } catch (IOException e) {
	                        Log.e(LOG_TAG, "prepare() failed when trying to start recording");
	                }

	                mRecorder.start();
	        }

	        private void stopRecording() {
	                mRecorder.stop();
	                mRecorder.reset();
	                mRecorder.release();
	                mRecorder = null;
	        }
	        
	        public static interface AudioRecorderResultListener {
		        void onReceiveAudio();
		    }
	        
	        
	}