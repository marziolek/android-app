/**
 * 
 */
package com.project.mgr.fragments.tabs;

import java.io.File;
import java.io.IOException;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.project.mgr.R;

public class Record_tab2 extends Fragment {
		
	        private static final String LOG_TAG = "Record_tab2";
	        private String mFileName = null;
	        private String mAllFiles = null;
	        private Button mRecordButton = null;
	        private MediaRecorder mRecorder = null;
	        private Button mPlayButton = null;
	        private MediaPlayer mPlayer = null;
	        private boolean mStartPlaying = false;
	        private boolean mStartRecording = false;
	        private AudioRecorderResultListener audioRecorderResultListener;
	        private boolean mMaxDuration = false;
	        Chronometer mChronometer;
	        private LinearLayout recording_status;
	    	Animation animMove;
	    	private long mChronometerPause = 0;
	        
	        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

	        	View v= inflater.inflate(R.layout.record_tab2, container, false);
	        	
	        	final MyAnimationView animView = new MyAnimationView(getActivity());
	        	mPlayButton = (Button) v.findViewById(R.id.play_button);
				mPlayButton.setEnabled(false);             
				mChronometer = (Chronometer) v.findViewById(R.id.chronometer);
				recording_status = (LinearLayout) v.findViewById(R.id.recording_status);
				mRecordButton = (Button) v.findViewById(R.id.record_button);
				mRecordButton.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (elapsedTime() >= 10.000) {
                   			stopRecording();
                   			mChronometer.stop();
                   			mMaxDuration = true;
                   			int recordingStatusHeightActual = recording_status.getHeight();
                    		animView.cancelAnimation(recordingStatusHeightActual);
                    		mPlayButton.setEnabled(true);
                   			mRecordButton.setEnabled(false);
                   			Log.d("more than", "10 sec");
                   			return false;
                		}
						if (!mMaxDuration) {
		                	switch ( event.getAction() ) {
		                    	case MotionEvent.ACTION_DOWN:
		                    		mChronometer.setBase(SystemClock.elapsedRealtime() + mChronometerPause);
			                    	mChronometer.start();
			                    	startRecording();
			                    	int recordingStatusHeight = recording_status.getHeight();
			                    	animView.startAnimation(recordingStatusHeight);
		                    		break;
		                    	case MotionEvent.ACTION_UP:
	                    			mChronometerPause = mChronometer.getBase() - SystemClock.elapsedRealtime();
	                    			mChronometer.stop();
	                    			stopRecording();
	                    			mPlayButton.setEnabled(true);
	                    			int recordingStatusHeightActual = recording_status.getHeight();
		                    		animView.cancelAnimation(recordingStatusHeightActual);
			                        break;
		                	}
		                } 
						if (mMaxDuration) {
							concatenateAudioFiles();
						}
						return false;
					}
				});
				mPlayButton.setOnTouchListener(new OnTouchListener() {
					@Override
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
				});
				
				return v;
	        }
	                
	        protected void onStart(Bundle savedInstanceState) {
	        	super.onStart();
	        	
	        	final LayoutParams params = new LayoutParams(
        				LayoutParams.MATCH_PARENT,      
    			        LayoutParams.MATCH_PARENT
    		    );
    		    int recordingStatusHeight = recording_status.getHeight();
    		    params.setMargins(0, recordingStatusHeight, 0, 0);
    	    	recording_status.setLayoutParams(params);
    	    	Toast.makeText(getActivity(), "asd", Toast.LENGTH_SHORT).show();
	        }
	        
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
	                
	                String currentTime = String.valueOf(System.currentTimeMillis());
	                mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MgrApp";
	                File dir = new File(mFileName);
	                if (!dir.exists()) {
	                	dir.mkdir();
	                }	             
	                mFileName += "/audiorecord" + currentTime + ".3gpp";
	                mRecorder.setOutputFile(mFileName);
	                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

	                try {
	                        mRecorder.prepare();
	                } catch (IOException e) {
	                        Log.e(LOG_TAG, "prepare() failed when trying to start recording "+currentTime );
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
	        
	        public void concatenateAudioFiles() {
	        	mAllFiles = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MgrApp";
	        	File dir = new File(mAllFiles);
	        	for (final File fileEntry : dir.listFiles()) {
	        		System.out.println(fileEntry.getName());
	        	}
	        }
	        
	        /**
	         * Animating recording progress
	         */
	        //Animation functions
	        private double elapsedTime() {
	            long elapsedMillis = SystemClock.elapsedRealtime() - mChronometer.getBase();
	            double elapsedTime = elapsedMillis / 1000.0;
	            
	            return elapsedTime;
	        }

	    	public class MyAnimationView extends View implements Animator.AnimatorListener,
        	ValueAnimator.AnimatorUpdateListener {
	    		
	    		public MyAnimationView(Context context) {
					super(context);	
				}

				Animator animation;
	            boolean endImmediately = false;
	
	            private void createAnimation(int recordingStatusHeight) {
	                if (animation == null) {
	                    ObjectAnimator yAnim = ObjectAnimator.ofFloat(recording_status, "y",
	                            recording_status.getY(), getHeight() - recordingStatusHeight).setDuration(10000);
	                    yAnim.setRepeatCount(0);
	                    yAnim.setRepeatMode(ValueAnimator.REVERSE);
	                    yAnim.setInterpolator(new AccelerateInterpolator(1f));
	                    yAnim.addUpdateListener(this);
	                    yAnim.addListener(this);
	
	                    animation = new AnimatorSet();
	                    ((AnimatorSet) animation).play(yAnim);
	                    animation.addListener(this);
	                }
	            }
	
	            public void startAnimation(int recordingStatusHeight) {
	                createAnimation(recordingStatusHeight);
	                animation.start();
	            }
	
	            public void cancelAnimation(int recordingStatusHeight) {
	                createAnimation(recordingStatusHeight);
	                animation.cancel();
	            }

	            public void endAnimation(int recordingStatusHeight) {
	                createAnimation(recordingStatusHeight);
	                animation.end();
	            }
	            
	            @Override
	            protected void onDraw(Canvas canvas) {
	                canvas.save();
	                canvas.translate(recording_status.getX(), recording_status.getY());
	                
	                canvas.restore();
	            }
	
	            public void onAnimationUpdate(ValueAnimator animation) {
	                invalidate();
	            }
	
	            public void onAnimationStart(Animator animation) {
	                if (animation instanceof AnimatorSet) {
	                    
	                } else {
	                    
	                }
	                if (endImmediately) {
	                    animation.end();
	                }
	            }
	
	            public void onAnimationEnd(Animator animation) {
	                if (animation instanceof AnimatorSet) {
	                    
	                } else {
	                    
	                }
	            }
	
	            public void onAnimationCancel(Animator animation) {
	                if (animation instanceof AnimatorSet) {
	                    
	                } else {
	                    
	                }
	            }
	
	            public void onAnimationPause(Animator animation) {
	                if (animation instanceof AnimatorSet) {
	                    
	                } else {
	                    
	                }
	            }
	            
	            public void onAnimationRepeat(Animator animation) {
	                if (animation instanceof AnimatorSet) {
	                    
	                } else {
	                    
	                }
	            }
	    	}
	    	
	    	/* removing files after closing activity / should remove files after closing whole app
	    	public void onDestroy() {
	        	super.onDestroy();
	        	
	        	mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MgrApp";
	            File dir = new File(mFileName);
	            if (dir.isDirectory()) {
	            	String[] children = dir.list();
	                for (int i = 0; i < children.length; i++) {
	                	new File(dir, children[i]).delete();
	                	String a = String.valueOf(i);
	                	Log.d("removed file: ", a);
	                }
	            }
	        }*/
	    }	