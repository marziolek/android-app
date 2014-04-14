/**
 * 
 */
package com.project.mgr.fragments.tabs;

import java.io.IOException;
import java.util.Timer;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.project.mgr.R;

public class Record_tab2 extends Fragment implements AnimationListener {
		
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
	        private LinearLayout mLayout = null;
	        Chronometer mChronometer;
	        private ImageView imgLogo;
	        
	        // Animation
	    	Animation animMove;
	        
	        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

	        	View v= inflater.inflate(R.layout.record_tab2, container, false);    
				mRecordButton = (Button) v.findViewById(R.id.record_button);
				mRecordButton.setOnTouchListener(recordListener);
				
				mPlayButton = (Button) v.findViewById(R.id.play_button);
				mPlayButton.setEnabled(false);
				mPlayButton.setOnTouchListener(playListener);             
				
				mChronometer = (Chronometer) v.findViewById(R.id.chronometer);
				
				imgLogo = (ImageView) v.findViewById(R.id.imgLogo);
				// load the animation
				animMove = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
						R.anim.move);

				// set animation listener
				animMove.setAnimationListener(this);
				
				return v;
	        }
	        
	        
	        final Timer timer = new Timer();
	        
	        private OnTouchListener recordListener = new OnTouchListener(){
                public boolean onTouch(View v, MotionEvent event) {
                	switch ( event.getAction() ) {
                    	case MotionEvent.ACTION_DOWN: 
                    		if (!maxDuration) {
                    			mChronometer.setBase(SystemClock.elapsedRealtime());
                    			mChronometer.start();
                    			startRecording();
                    			imgLogo.startAnimation(animMove);
                    			mPlayButton.setEnabled(false);
	                    		if (elapsedTime() >= 5.0) {
                    				 stopRecording();
                    				 mChronometer.stop();
                    				 maxDuration = true;
                    				 mPlayButton.setEnabled(true);
                    				 Log.d("wiece", "niz 5 sekund kurdeee");
                    			 }
                    		}
                    		return true;
                    	case MotionEvent.ACTION_UP:
                    		if (!maxDuration) {
                    			stopRecording();
                    			maxDuration = true;
                    			mPlayButton.setEnabled(true);
                    		}
                    		mChronometer.stop();
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
	                Log.v("**rec**", "mRecord and mstartRecording=recording started");
	        }

	        private void stopRecording() {
	                mRecorder.stop();
	                mRecorder.reset();
	                mRecorder.release();
	                mRecorder = null;
	                Log.v("**rec**", "mRecord and mstartRecording=recording ended");
	        }
	        
	        public static interface AudioRecorderResultListener {
		        void onReceiveAudio();
		    }
	        
	        /**
	         * Drawing recording progress
	         */
	        private double elapsedTime() {
	            long elapsedMillis = SystemClock.elapsedRealtime() - mChronometer.getBase();
	            double elapsedTime = elapsedMillis / 1000.0;
	            
	            return elapsedTime;
	        }
	        

	    	@Override
	    	public void onAnimationEnd(Animation animation) {
	    		// Take any action after completing the animation

	    		// check for zoom in animation
	    		if (animation == animMove) {
	    		}

	    	}

	    	@Override
	    	public void onAnimationRepeat(Animation animation) {
	    		// TODO Auto-generated method stub

	    	}

	    	@Override
	    	public void onAnimationStart(Animation animation) {
	    		// TODO Auto-generated method stub

	    	}

	}