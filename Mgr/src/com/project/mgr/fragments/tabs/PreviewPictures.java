package com.project.mgr.fragments.tabs;

import java.io.File;
import java.io.IOException;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;

import com.project.mgr.R;

public class PreviewPictures extends FragmentActivity {
	private String mAllFiles;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_audio);
        
        mAllFiles = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MgrApp";
    	File dir = new File(mAllFiles);
    	File[] files = dir.listFiles();
	}
}
