/**
 * 
 */
package com.project.mgr.fragments.tabs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.project.mgr.R;

public class RecordTab3 extends Fragment {
	
	 private static final int RECORDER_BPP = 16;
     private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
     private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
     private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
     private static final int RECORDER_SAMPLERATE = 44100;
     private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
     private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
     
     private AudioRecord recorder = null;
     private int bufferSize = 0;
     private Thread recordingThread = null;
     private boolean isRecording = false;
     
     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    	 View v= inflater.inflate(R.layout.crecord_tab2, container, false);
     	 	     
	     setButtonHandlers(v);
	     //enableButtons(false, v);
	     
	     bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING);
     
	     return v;
     }

     private void setButtonHandlers(View v) {
             ((Button) v.findViewById(R.id.btnStart)).setOnClickListener(btnClick);
     ((Button) v.findViewById(R.id.btnStop)).setOnClickListener(btnClick);
     }
     
     private void enableButton(int id,boolean isEnable, View v){
             ((Button) v.findViewById(id)).setEnabled(isEnable);
     }
     
     private void enableButtons(boolean isRecording, View v) {
             enableButton(R.id.btnStart,!isRecording, v);
             enableButton(R.id.btnStop,isRecording, v);
     }
     
     private String getFilename(){
             String filepath = Environment.getExternalStorageDirectory().getPath();
             File file = new File(filepath,AUDIO_RECORDER_FOLDER);
             
             if(!file.exists()){
                     file.mkdirs();
             }
             
             return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + AUDIO_RECORDER_FILE_EXT_WAV);
     }
     
     private String getTempFilename(){
             String filepath = Environment.getExternalStorageDirectory().getPath();
             File file = new File(filepath,AUDIO_RECORDER_FOLDER);
             
             if(!file.exists()){
                     file.mkdirs();
             }
             
             File tempFile = new File(filepath,AUDIO_RECORDER_TEMP_FILE);
             
             if(tempFile.exists())
                     tempFile.delete();
             
             return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
     }
     
     private void startRecording(){
             recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                                             RECORDER_SAMPLERATE, RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING, bufferSize);
             
             recorder.startRecording();
             
             isRecording = true;
             
             recordingThread = new Thread(new Runnable() {
                     
                     @Override
                     public void run() {
                             writeAudioDataToFile();
                     }
             },"AudioRecorder Thread");
             
             recordingThread.start();
     }
     
     private void writeAudioDataToFile(){
             byte data[] = new byte[bufferSize];
             String filename = getTempFilename();
             FileOutputStream os = null;
             
             try {
                     os = new FileOutputStream(filename);
             } catch (FileNotFoundException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
             }
             
             int read = 0;
             
             if(null != os){
                     while(isRecording){
                             read = recorder.read(data, 0, bufferSize);
                             
                             if(AudioRecord.ERROR_INVALID_OPERATION != read){
                                     try {
                                             os.write(data);
                                     } catch (IOException e) {
                                             e.printStackTrace();
                                     }
                             }
                     }
                     
                     try {
                             os.close();
                     } catch (IOException e) {
                             e.printStackTrace();
                     }
             }
     }
     
     private void stopRecording(){
             if(null != recorder){
                     isRecording = false;
                     
                     recorder.stop();
                     recorder.release();
                     
                     recorder = null;
                     recordingThread = null;
             }
             
             copyWaveFile(getTempFilename(),getFilename());
             deleteTempFile();
     }

     private void deleteTempFile() {
             File file = new File(getTempFilename());
             
             file.delete();
     }
     
     private void copyWaveFile(String inFilename,String outFilename){
             FileInputStream in = null;
             FileOutputStream out = null;
             long totalAudioLen = 0;
             long totalDataLen = totalAudioLen + 36;
             long longSampleRate = RECORDER_SAMPLERATE;
             int channels = 2;
             long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels/8;
             
             byte[] data = new byte[bufferSize];
             
             try {
                     in = new FileInputStream(inFilename);
                     out = new FileOutputStream(outFilename);
                     totalAudioLen = in.getChannel().size();
                     totalDataLen = totalAudioLen + 36;
                     
                     System.out.println("File size: " + totalDataLen);
                     
                     WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                                     longSampleRate, channels, byteRate);
                     
                     while(in.read(data) != -1){
                             out.write(data);
                     }
                     
                     in.close();
                     out.close();
             } catch (FileNotFoundException e) {
                     e.printStackTrace();
             } catch (IOException e) {
                     e.printStackTrace();
             }
     }

     private void WriteWaveFileHeader(
                     FileOutputStream out, long totalAudioLen,
                     long totalDataLen, long longSampleRate, int channels,
                     long byteRate) throws IOException {
             
             byte[] header = new byte[44];
             
             header[0] = 'R';  // RIFF/WAVE header
             header[1] = 'I';
             header[2] = 'F';
             header[3] = 'F';
             header[4] = (byte) (totalDataLen & 0xff);
             header[5] = (byte) ((totalDataLen >> 8) & 0xff);
             header[6] = (byte) ((totalDataLen >> 16) & 0xff);
             header[7] = (byte) ((totalDataLen >> 24) & 0xff);
             header[8] = 'W';
             header[9] = 'A';
             header[10] = 'V';
             header[11] = 'E';
             header[12] = 'f';  // 'fmt ' chunk
             header[13] = 'm';
             header[14] = 't';
             header[15] = ' ';
             header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
             header[17] = 0;
             header[18] = 0;
             header[19] = 0;
             header[20] = 1;  // format = 1
             header[21] = 0;
             header[22] = (byte) channels;
             header[23] = 0;
             header[24] = (byte) (longSampleRate & 0xff);
             header[25] = (byte) ((longSampleRate >> 8) & 0xff);
             header[26] = (byte) ((longSampleRate >> 16) & 0xff);
             header[27] = (byte) ((longSampleRate >> 24) & 0xff);
             header[28] = (byte) (byteRate & 0xff);
             header[29] = (byte) ((byteRate >> 8) & 0xff);
             header[30] = (byte) ((byteRate >> 16) & 0xff);
             header[31] = (byte) ((byteRate >> 24) & 0xff);
             header[32] = (byte) (2 * 16 / 8);  // block align
             header[33] = 0;
             header[34] = RECORDER_BPP;  // bits per sample
             header[35] = 0;
             header[36] = 'd';
             header[37] = 'a';
             header[38] = 't';
             header[39] = 'a';
             header[40] = (byte) (totalAudioLen & 0xff);
             header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
             header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
             header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

             out.write(header, 0, 44);
     }
     
     private View.OnClickListener btnClick = new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                     switch(v.getId()){
                             case R.id.btnStart:{
                                     System.out.println("*****Start Recording******");
                                     
                                     //enableButtons(true, v);
                                     startRecording();
                                                     
                                     break;
                             }
                             case R.id.btnStop:{
                            	 	System.out.println("*****Start Recording*****");
                                     
                                     //enableButtons(false, v);
                                     stopRecording();
                                     
                                     break;
                             }
                     }
             }
     }; 
	
/*	private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;

    private RecordButton mRecordButton = null;
    private MediaRecorder mRecorder = null;

    private PlayButton   mPlayButton = null;
    private MediaPlayer   mPlayer = null;

    private void onRecord(boolean start) {
        if (start) {
            record.start();
        } else {
            stopRecord.start();
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
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    
    class RecordButton extends Button {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    //setText("Stop recording");
                	System.out.println("started");
                } else {
                    //setText("Start recording");
                	System.out.println("stopped");
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            setText("Start recording");
            setOnClickListener(clicker);
        }
    }
    
    class PlayButton extends Button {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setText("Stop playing");
                } else {
                    setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        public PlayButton(Context ctx) {
            super(ctx);
            setText("Start playing");
            setOnClickListener(clicker);
        }
    }
    
    Thread record = new Thread(new Runnable() {
        @Override
        public void run() { 
        	//Your recording portion of the code goes here.
        	startRecording();
		}
	});
    
    Thread stopRecord = new Thread(new Runnable() {
        @Override
        public void run() { 
        	//Your recording portion of the code goes here.
        	stopRecording();
		}
	});
    
    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }
    
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    	View v= inflater.inflate(R.layout.crecord_tab2, container, false);
        
    	   mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
           mFileName += "/audiorecordtest.3gp";
       
    	
        LinearLayout ll = new LinearLayout(getActivity());
        mRecordButton = new RecordButton(getActivity());
        ll.addView(mRecordButton,
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0));
        mPlayButton = new PlayButton(getActivity());
        ll.addView(mPlayButton,
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0));
        LinearLayout cre = (LinearLayout) v.findViewById(R.id.cre);
        
        cre.addView(ll);
        
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
	
	
	*/	
	
	        /*private String mFileName = null;
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
	        
	        **
	         * recording and playing
	         *
	        
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
	        	mAudioRecorder.start(new AudioRecorder.OnStartListener() {
	        	    @Override
	        	    public void onStarted() {
	        	        // started
	        	    	Log.v("**rec**", "new recorder started");
	        	    }

	        	    @Override
	        	    public void onException(Exception e) {
	        	        // error
	        	    	Log.v("**rec**", "error!!!!!!!!!!!!");
	        	    }
	        	});
	        }

	        private void stopRecording() {
	        	mAudioRecorder.pause(new AudioRecorder.OnPauseListener() {
	        	    @Override
	        	    public void onPaused(String activeRecordFileName) {
	        	        // paused
	        	    	Log.v("**rec**", "new recorder paused");
	        	    }

	        	    @Override
	        	    public void onException(Exception e) {
	        	        // error
	        	    	Log.v("**rec**", "PUASE ERROR!!!!!");
	        	    }
	        	});
	        }
	        
	        public static interface AudioRecorderResultListener {
		        void onReceiveAudio();
		    }
	        
	        
	      **
	         * Animating recording progress
	         *
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

	    	 class Recording extends AsyncTask<Void, Void, Void> {
	 			
	 		    protected void onPreExecute() {
	 		    	
	 		    }
	 		    
	 		    @Override
	 		    protected Void doInBackground(Void... arg0) {
	 		    	
	 		    	return null;
	 		    }

	 		    protected void onPostExecute(Void v) {
	 			   
	 		   	}
	    	 }*/
	    }	