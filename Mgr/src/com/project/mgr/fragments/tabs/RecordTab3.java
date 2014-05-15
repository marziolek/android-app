/**
 * 
 */
package com.project.mgr.fragments.tabs;

import java.io.File;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
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

import com.project.mgr.AudioRecorder;
import com.project.mgr.R;

public class RecordTab3 extends Fragment {
		
	        private String mFileName = null;
	        private Button mRecordButton = null;
	        private boolean mMaxDuration = false;
	        Chronometer mChronometer;
	        private LinearLayout recording_status;
	    	Animation animMove;
	    	private long mChronometerPause = 0;
	    	private AudioRecorder mAudioRecorder;
	        
	        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

	        	View v= inflater.inflate(R.layout.record_tab2, container, false);
	        	
	        	final MyAnimationView animView = new MyAnimationView(getActivity());
				mChronometer = (Chronometer) v.findViewById(R.id.chronometer);
				recording_status = (LinearLayout) v.findViewById(R.id.recording_status);
				mAudioRecorder = AudioRecorder.build(getActivity(), getNextFileName());
				mRecordButton = (Button) v.findViewById(R.id.record_button);
				mRecordButton.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (elapsedTime() >= 10.000) {
                   			stopRecording();
                   			mChronometer.stop();
                   			mRecordButton.setEnabled(false);
                   			mMaxDuration = true;
                   			int recordingStatusHeightActual = recording_status.getHeight();
                    		animView.cancelAnimation(recordingStatusHeightActual);
                    		Log.d("more than", "10 sec");
                   			Intent intent = new Intent(getActivity(), PreviewAudio.class);
							startActivity(intent);
                   			return true;
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
	                    			int recordingStatusHeightActual = recording_status.getHeight();
		                    		animView.cancelAnimation(recordingStatusHeightActual);
			                        break;
		                	}
							
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
	        }
	        
	        /**
	         * recording and playing
	         */
	        
	        private String getNextFileName() {
	        	mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MgrApp/audio";
                File dir = new File(mFileName);
                if (!dir.exists()) {
                	dir.mkdir();
                }
                String currentTime = String.valueOf(System.currentTimeMillis());
                mFileName += "/audiorecord" + currentTime + ".mp4";
                
                return mFileName;
	        }

	        private void startRecording() {
	                /*
	                mRecorder = new MediaRecorder();
	                 
	                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
	                String currentTime = String.valueOf(System.currentTimeMillis());
	                mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MgrApp";
	                File dir = new File(mFileName);
	                if (!dir.exists()) {
	                	dir.mkdir();
	                }	             
	                mFileName += "/audiorecord" + currentTime + ".3gpp";
	                mRecorder.setOutputFile(mFileName);
	                

	                try {
	                        mRecorder.prepare();
	                } catch (IOException e) {
	                        Log.e(LOG_TAG, "prepare() failed when trying to start recording "+currentTime );
	                }

	                mRecorder.start();
	                Log.v("**rec**", "mRecord and mstartRecording=recording started");
	                */
	        	mAudioRecorder.start(new AudioRecorder.OnStartListener() {
	        	    @Override
	        	    public void onStarted() {
	        	        // started
	        	    	//Log.v("**rec**", "new recorder started");
	        	    }

	        	    @Override
	        	    public void onException(Exception e) {
	        	        // error
	        	    	//Log.v("**rec**", "error!!!!!!!!!!!!");
	        	    }
	        	});
	        }

	        private void stopRecording() {
	                /*
	                mRecorder.stop(); 
	                mRecorder.reset();
	                mRecorder.release();
	                mRecorder = null;
	                Log.v("**rec**", "mRecord and mstartRecording=recording ended");
	                */
	        	mAudioRecorder.pause(new AudioRecorder.OnPauseListener() {
	        	    @Override
	        	    public void onPaused(String activeRecordFileName) {
	        	        // paused
	        	    	//Log.v("**rec**", "new recorder paused");
	        	    }

	        	    @Override
	        	    public void onException(Exception e) {
	        	        // error
	        	    	//Log.v("**rec**", "PUASE ERROR!!!!!");
	        	    }
	        	});
	        }
	        
	        public static interface AudioRecorderResultListener {
		        void onReceiveAudio();
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

	    }	