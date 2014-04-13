/**
 * 
 */
package com.project.mgr.fragments.tabs;

import java.io.IOException;

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

	        private Button mReturnButton = null;
	        
	        private boolean mStartPlaying = false;
	        private boolean mStartRecording = false;
	        
	        private AudioRecorderResultListener audioRecorderResultListener;
	        
	        /*
	        public boolean onTouchEvent(MotionEvent me) {
	        	switch (me.getAction()) {
	            	case MotionEvent.ACTION_DOWN:
	            		startRecording();
	            	case MotionEvent.ACTION_UP:
	            		stopRecording();
	            }
	            return true;
	        }
	        */
	        
	        public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                    Bundle savedInstanceState) {

	                View v= inflater.inflate(R.layout.record_tab2, container, false);
	                
	                	mRecordButton = (Button) v.findViewById(R.id.record_button);
	                    mRecordButton.setOnTouchListener(recordListener);
	                        
	                    mPlayButton = (Button) v.findViewById(R.id.play_button);
	                    mPlayButton.setEnabled(false);
	                    mPlayButton.setOnTouchListener(playListener);
	                    
	                    mReturnButton = (Button) v.findViewById(R.id.return_button) ;
	                    mReturnButton.setEnabled(false);
	                    //mReturnButton.setOnClickListener(Record_tab2.this);	                    
	                    
	                return v;
	        }
	        
	        private OnTouchListener recordListener = new OnTouchListener(){
                public boolean onTouch(View v, MotionEvent event) {
                	switch ( event.getAction() ) {
                    	case MotionEvent.ACTION_DOWN: 
                    		mStartRecording = !mStartRecording;
	                        Log.v("xinxin**", "mRecord and mstartRecording=" + mStartRecording);
	                        onRecord(mStartRecording);
	                        if (mStartRecording) {
	                                //mRecordButton.setImageResource(R.drawable.recorder_stop);
	                                mPlayButton.setEnabled(false);
	                                mReturnButton.setEnabled(false);
	                                
	                        }
                    		return true;
                    	case MotionEvent.ACTION_UP:
                    		mStartRecording = !mStartRecording;
                    		onRecord(mStartRecording);
                    		mPlayButton.setEnabled(true);
                    		Log.v("xinxin**", "mRecord and mstartRecording=ju¿ siê zrobi³o");
    	            		return true;
	                    }
                   return false;
                }
            };
	        
            private OnTouchListener playListener = new OnTouchListener(){
                public boolean onTouch(View v, MotionEvent event) {
                	switch ( event.getAction() ) {
                    	case MotionEvent.ACTION_DOWN: 
                    		startPlaying();
                    		return true;
                    	case MotionEvent.ACTION_UP:
                    		stopPlaying();
                    		return true;
	                    }
                   return false;
                }
            };
	        
	        public void setAudioRecorderResultListener(AudioRecorderResultListener audioRecorderResultListener){
	                this.audioRecorderResultListener = audioRecorderResultListener;
	        }
	        
	        public void onClick(View v) {
	                /*if (v == mRecordButton) {
	                        
	                        mStartRecording = !mStartRecording;
	                        Log.v("xinxin**", "mRecord and mstartRecording=" + mStartRecording);
	                        onRecord(mStartRecording);
	                        if (mStartRecording) {
	                                //mRecordButton.setImageResource(R.drawable.recorder_stop);
	                                mPlayButton.setEnabled(false);
	                                mReturnButton.setEnabled(false);
	                                
	                        } else {
	                                //mRecordButton.setImageResource(R.drawable.recorder_record);
	                                mPlayButton.setEnabled(true);
	                                mReturnButton.setEnabled(true);
	                        }
	                }*/
	                if(v == mPlayButton){
	                        mStartPlaying = !mStartPlaying;
	                        onPlay(mStartPlaying);
	                        if (mStartPlaying) {
	                                //mPlayButton.setImageResource(R.drawable.recorder_pause);
	                                mRecordButton.setEnabled(false);
	                                mReturnButton.setEnabled(false);
	                        } else {
	                                //mPlayButton.setImageResource(R.drawable.recorder_play);
	                                mRecordButton.setEnabled(true);
	                                mReturnButton.setEnabled(true);
	                        }
	                }
	               /*
	                if(v == mReturnButton){
	                        Log.v("xinxin**", "mPlayButton");
	                        audioRecorderResultListener.onReceiveAudio();
	                        //getDialog().dismiss();
	                }
	          */     
	        } 
	        
	            
	           

	        private void onRecord(boolean start) {
	                if (start) {
	                        startRecording();
	                } else {
	                        stopRecording();
	                }
	        }

	        private void onPlay(boolean start) {
	                if (start) {
	                        startPlaying();
	                } else {
	                        stopPlaying();
	                }
	        }

	        private void startPlaying() {
	                mPlayer = new MediaPlayer();
	                try {
	                        mPlayer.setDataSource(mFileName);
	                        mPlayer.prepare();
	                        mPlayer.start();
	                        Log.v("asd","asd");
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