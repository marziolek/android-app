/**
 * 
 */
package com.project.mgr.fragments.tabs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.LinearLayout;

import com.project.mgr.R;

public class RecordTab3 extends Fragment {
	
	 private static final int RECORDER_BPP = 16;
     private static final String AUDIO_RECORDER_FOLDER = "MgrApp/audio";
     private static final String AUDIO_RECORDER_TEMP_FILE_2 = "record_temp2.raw";
     private static final int RECORDER_SAMPLERATE = 16000;
     private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
     private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
     
     private AudioRecord recorder = null;
     private int bufferSize = 0;
     private Thread recordingThread = null;
     private boolean isRecording = false;
     
     private Button mRecordButton = null;
     Chronometer mChronometer;
     private long mChronometerPause = 0;
     
     private String mFileName = null;
     private boolean mChronometerStarted = false;
     private LinearLayout recording_status;
          
     @Override
     public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

    	 View v= inflater.inflate(R.layout.record_tab3, container, false);
     	 	     
    	 File audio = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MgrApp/audio");
         deleteFolder(audio);
         audio.mkdirs();
                  
    	 final MyAnimationView animView = new MyAnimationView(getActivity());
    	 recording_status = (LinearLayout) v.findViewById(R.id.recording_status);
    	 
    	 mChronometer = (Chronometer) v.findViewById(R.id.chronometer);
    	 mRecordButton = (Button) v.findViewById(R.id.record_button);
    	 mRecordButton.setOnTouchListener(new OnTouchListener() {
    		 @Override
    		 public boolean onTouch(View v, MotionEvent event) {
    			 mChronometer.setOnChronometerTickListener(new OnChronometerTickListener() {
    				 @Override
    				 public void onChronometerTick(Chronometer chronometer) {
    					 if (SystemClock.elapsedRealtime() - mChronometer.getBase() >= 8000 || mChronometer.getText() == "0:08") {
    						 stopRecording();
					         mRecordButton.setEnabled(false);
					         makeWave();
    					 }
    				 }
    			 });
    			 switch ( event.getAction() ) {
	             	case MotionEvent.ACTION_DOWN:
	             		startRecording(mChronometerStarted);                    	
		                int recordingStatusHeight = recording_status.getHeight();
		                animView.startAnimation(recordingStatusHeight);
	                    break;
	             	case MotionEvent.ACTION_UP:
	             		mChronometerStarted = true;
	                 	stopRecording();
	                 	int recordingStatusHeightActual = recording_status.getHeight();
	                 	recording_status.getY();
		                animView.cancelAnimation(recordingStatusHeightActual);
		                break;
	             	case MotionEvent.ACTION_CANCEL:
	             		mChronometerStarted = true;
	                 	stopRecording();
	                 	recordingStatusHeightActual = recording_status.getHeight();
	                 	recording_status.getY();
		                animView.cancelAnimation(recordingStatusHeightActual);
		                break;
    			 }
    			 return false;
    		 }
    	 });
			
	     bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING);
      
	     return v;
     }
     
     private String getTempFilename(){
             String filepath = Environment.getExternalStorageDirectory().getPath();
             File file = new File(filepath,AUDIO_RECORDER_FOLDER);
             
             if(!file.exists()){
            	 file.mkdirs();
             }
             
             return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE_2);
     }
     
     private void startRecording(boolean mChronometerStarted){
    	 try {
             recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                                             RECORDER_SAMPLERATE, RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING, bufferSize);
             
             recorder.startRecording();
             if (!mChronometerStarted) {
	             mChronometer.setBase(SystemClock.elapsedRealtime());
	         	 mChronometer.start();
             } else {
            	 mChronometer.setBase(SystemClock.elapsedRealtime() + mChronometerPause);
            	 mChronometer.start();
             }
             
             isRecording = true;
             
             recordingThread = new Thread(new Runnable() {
                     
                     @Override
                     public void run() {
                             writeAudioDataToFile();
                     }
             },"AudioRecorder Thread");
             
             recordingThread.start();
    	 } catch (Exception e) {
    		
    	 }
     }
     
     private void writeAudioDataToFile(){
             byte data[] = new byte[bufferSize];
             String filename = getTempFilename();
             FileOutputStream os = null;
             File file = new File(filename);
             
             int read = 0;
             
             try {
            	 if (!file.exists()) {
                     os = new FileOutputStream(filename);
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
            	 } else {
            		 os = new FileOutputStream(filename, true);
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
             } catch (Exception e) {
            	 e.printStackTrace();
             }
     }
     
     private void stopRecording(){
             if(null != recorder){
                     isRecording = false;
                     
                     mChronometerPause = mChronometer.getBase() - SystemClock.elapsedRealtime();
          			 mChronometer.stop();
                     
                     recorder.stop();
                     recorder.release();
                     
                     recorder = null;
                     recordingThread = null;
             }
             
             
     }
     
     private void makeWave() {
    	 copyWaveFile(getTempFilename(),getFilename());
    	 deleteTempFile();
    	 Intent intent = new Intent(getActivity(), PreviewAudio.class);
    	 startActivity(intent);
     }

     private void deleteTempFile() {
             File file = new File(getTempFilename());
             
             file.delete();
     }
     
     private void copyWaveFile(String inFilename,String outFilename){
             FileInputStream in = null;
             FileOutputStream out = null;
             long totalAudioLen = 0;
             long totalDataLen = totalAudioLen;
             long longSampleRate = RECORDER_SAMPLERATE;
             int channels = 1;
             long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels/8;
             
             byte[] data = new byte[bufferSize];
             
             try {
                     in = new FileInputStream(inFilename);
                     out = new FileOutputStream(outFilename);
                     totalAudioLen += in.getChannel().size();
                     totalDataLen += totalAudioLen;
                     
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
             deleteTempFile();
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
     
     //Animation functions
     private double elapsedTime() {
         long elapsedMillis = mChronometer.getBase();
         double elapsedTime = elapsedMillis / 1000.0;
         
         return elapsedTime;
     }
     
     private String getFilename() {
    	 mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MgrApp/audio";
         File dir = new File(mFileName);
         if (!dir.exists()) {
         	dir.mkdirs();
         }
         String currentTime = String.valueOf(System.currentTimeMillis());
         mFileName += "/audiorecord" + currentTime + ".wav";
         
         return mFileName;
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
                         recording_status.getY(), getHeight() + recordingStatusHeight).setDuration(9000);
                 yAnim.setRepeatCount(0);
                 yAnim.setRepeatMode(ValueAnimator.REVERSE);
                 yAnim.setInterpolator(new LinearInterpolator());
                 yAnim.addUpdateListener(this);
                 yAnim.addListener(this);

                 animation = new AnimatorSet();
                 ((AnimatorSet) animation).play(yAnim);
                 animation.addListener(this);
             } else {
            	 System.out.println(animation.getDuration());
            	 long duration = SystemClock.elapsedRealtime() - mChronometer.getBase();
            	 animation.end();
                 ObjectAnimator yAnim = ObjectAnimator.ofFloat(recording_status, "y",
                		 		recording_status.getY(), getHeight() + recordingStatusHeight).setDuration(9000-duration);
                 yAnim.setRepeatCount(0);
                 yAnim.setRepeatMode(ValueAnimator.REVERSE);
                 yAnim.setInterpolator(new LinearInterpolator());
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
             //createAnimation(recordingStatusHeight);
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