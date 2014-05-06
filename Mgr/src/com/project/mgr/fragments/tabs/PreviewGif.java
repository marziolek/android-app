package com.project.mgr.fragments.tabs;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import android.graphics.Movie;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.project.mgr.R;

public class PreviewGif extends FragmentActivity {
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.preview_gif);
		
		
        
        final PreviewGifPlayer gif1 = (PreviewGifPlayer) findViewById(R.id.gif1);
        
        try {
        	InputStream is = new FileInputStream(Environment.getExternalStorageDirectory().getPath() + "/MgrApp/GIFfromPictures.gif");
            byte[] array = streamToBytes(is);
	        Movie movie = Movie.decodeByteArray(array, 0, array.length);
	        gif1.setMovie(movie);
	    }
        catch(Exception e) {
        }        
	}
	
	private static byte[] streamToBytes(InputStream is) {
        ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = is.read(buffer)) >= 0) {
                os.write(buffer, 0, len);
            }
        } catch (java.io.IOException e) {
        }
        return os.toByteArray();
    }
	
	
	public void onGifClick(View v) {
		PreviewGifPlayer gif = (PreviewGifPlayer) v;
		gif.setPaused(!gif.isPaused());
		
	}
}