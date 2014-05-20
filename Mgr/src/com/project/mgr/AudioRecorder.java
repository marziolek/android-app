package com.project.mgr;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.AsyncTask;

public class AudioRecorder {

    public static enum Status {
        STATUS_UNKNOWN,
        STATUS_READY_TO_RECORD,
        STATUS_RECORDING,
        STATUS_RECORD_PAUSED
    }

    public static interface OnException {
        public void onException(Exception e);
    }

    public static interface OnStartListener extends OnException {
        public void onStarted();
    }

    public static interface OnPauseListener extends OnException {
        public void onPaused(String activeRecordFileName);
    }

    public static class MediaRecorderConfig {
        private final int mAudioEncodingBitRate;
        private final int mAudioChannels;
        private final int mAudioSource;
        private final int mAudioEncoder;

        public static final MediaRecorderConfig DEFAULT =
                new MediaRecorderConfig(64 * 1024,          /* 64 Kib per second */
                        2,                                  /* Stereo */
                        MediaRecorder.AudioSource.DEFAULT,  /* Default audio source.
                                                               (usually, phone microphone) */
                        ApiHelper.DEFAULT_AUDIO_ENCODER);   /* Default encoder
                                                               for target Android version */

        /**
         * Constructor.
         *
         * @param audioEncodingBitRate
         * Used for {@link android.media.MediaRecorder#setAudioEncodingBitRate}
         * @param audioChannels
         * Used for {@link android.media.MediaRecorder#setAudioChannels}
         * @param audioSource
         * Used for {@link android.media.MediaRecorder#setAudioSource}
         * @param audioEncoder
         * Used for {@link android.media.MediaRecorder#setAudioEncoder}
         */
        public MediaRecorderConfig(int audioEncodingBitRate, int audioChannels, int audioSource, int audioEncoder) {
            mAudioEncodingBitRate = audioEncodingBitRate;
            mAudioChannels = audioChannels;
            mAudioSource = audioSource;
            mAudioEncoder = audioEncoder;
        }

    }

    public class StartRecordTask extends AsyncTask<OnStartListener, Void, Exception> {

        private OnStartListener mOnStartListener;

        @Override
        protected Exception doInBackground(OnStartListener... params) {
            this.mOnStartListener = params[0];
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioEncodingBitRate(mMediaRecorderConfig.mAudioEncodingBitRate);
            mMediaRecorder.setAudioChannels(mMediaRecorderConfig.mAudioChannels);
            mMediaRecorder.setAudioSource(mMediaRecorderConfig.mAudioSource);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setOutputFile(getTemporaryFileName());
            mMediaRecorder.setAudioEncoder(mMediaRecorderConfig.mAudioEncoder);
            
            Exception exception = null;
            try {
                mMediaRecorder.prepare();
                mMediaRecorder.start();
            } catch (IOException e) {
                exception = e;
            }
            return exception;
        }

        @Override
        protected void onPostExecute(Exception e) {
            super.onPostExecute(e);
            if (e == null) {
                setStatus(AudioRecorder.Status.STATUS_RECORDING);
                mOnStartListener.onStarted();
            } else {
                setStatus(AudioRecorder.Status.STATUS_READY_TO_RECORD);
                mOnStartListener.onException(e);
            }
        }
    }

    public class PauseRecordTask extends AsyncTask<OnPauseListener, Void, Exception> {
        private OnPauseListener mOnPauseListener;

        @Override
        protected Exception doInBackground(OnPauseListener... params) {
            mOnPauseListener = params[0];
            Exception exception = null;
            try {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
            } catch (Exception e) {
                exception = e;
            }
            if ( exception == null ) {
                appendToFile(mTargetRecordFileName, getTemporaryFileName());
            }
            return exception;
        }

        @Override
        protected void onPostExecute(Exception e) {
            super.onPostExecute(e);
            if (e == null) {
                setStatus(AudioRecorder.Status.STATUS_RECORD_PAUSED);
                mOnPauseListener.onPaused(mTargetRecordFileName);
            } else {
                setStatus(AudioRecorder.Status.STATUS_READY_TO_RECORD);
                mOnPauseListener.onException(e);
            }
        }
    }

    private Status mStatus;
    private String mTargetRecordFileName;
    private MediaRecorder mMediaRecorder;
    private Context context;
    private MediaRecorderConfig mMediaRecorderConfig;

    private AudioRecorder() {
        mStatus = Status.STATUS_UNKNOWN;
    }

    /**
     * Returns the ready-to-use AudioRecorder.
     * Uses {@link com.github.lassana.recorder.AudioRecorder.MediaRecorderConfig#DEFAULT} as
     * {@link android.media.MediaRecorder} config.
     */
    public static AudioRecorder build(final Context context,
                                      final String targetFileName) {
        return build(context, targetFileName, MediaRecorderConfig.DEFAULT);
    }

    /**
     * Returns the ready-to-use AudioRecorder.
     */
    public static AudioRecorder build(Context context,
                                      final String targetFileName,
                                      final MediaRecorderConfig mediaRecorderConfig) {
        AudioRecorder rvalue = new AudioRecorder();
        rvalue.mTargetRecordFileName = targetFileName;
        rvalue.context = context;
        rvalue.mMediaRecorderConfig = mediaRecorderConfig;
        rvalue.mStatus = Status.STATUS_READY_TO_RECORD;
        return rvalue;
    }

    /**
     * Continues existing record or starts new one.
     */
    @SuppressLint("NewApi")
    public void start(final OnStartListener listener) {
        StartRecordTask task = new StartRecordTask();
        if (ApiHelper.HAS_EXECUTE_ON_EXECUTOR_METHOD) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, listener);
        } else {
            task.execute(listener);
        }
    }

    /**
     * Pauses active recording.
     */
    @SuppressLint("NewApi")
    public void pause(final OnPauseListener listener) {
        PauseRecordTask task = new PauseRecordTask();
        if (ApiHelper.HAS_EXECUTE_ON_EXECUTOR_METHOD) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, listener);
        } else {
            task.execute(listener);
        }
    }

    public Status getStatus() {
        return mStatus;
    }

    public String getRecordFileName() {
        return mTargetRecordFileName;
    }

    public boolean isRecording() {
        return mStatus == Status.STATUS_RECORDING;
    }

    public boolean isReady() {
        return mStatus == Status.STATUS_READY_TO_RECORD;
    }

    public boolean isPaused() {
        return mStatus == Status.STATUS_RECORD_PAUSED;
    }

    private void setStatus(final Status status) {
        mStatus = status;
    }

    private String getTemporaryFileName() {
        return context.getCacheDir().getAbsolutePath() + File.separator + "tmprecord";
    }

    private void appendToFile(final String targetFileName, final String newFileName) {
        Mp4ParserWrapper.append(targetFileName, newFileName);
    }
}
