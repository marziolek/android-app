package com.project.mgr.fragments.tabs;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import android.content.Intent;
import android.graphics.Movie;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.project.mgr.R;

public class PreviewGif extends FragmentActivity {
		
	public Button uploadGif;
	public String user_id = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.preview_gif);

        final PreviewGifPlayer gif1 = (PreviewGifPlayer) findViewById(R.id.gif1);
        try {
        	InputStream is = new FileInputStream(Environment.getExternalStorageDirectory().getPath() + "/MgrApp/"+fileName());
            byte[] array = streamToBytes(is);
            Log.d("filename", fileName());
    	    Movie movie = Movie.decodeByteArray(array, 0, array.length);
    	    gif1.setMovie(movie);
	    }
        catch(Exception e) {
        }
        uploadGif = (Button) findViewById(R.id.uploadGif);
        uploadGif.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
                Intent intent = new Intent(PreviewGif.this, FilesUploader.class);
                Bundle b = new Bundle();
                b.putString("fileName", fileName());
                intent.putExtras(b);
                startActivity(intent);
            }
        });	    
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
	
	public String fileName() {
		Intent extras = this.getIntent();
    	if (extras != null) {
    	    String fileName = extras.getStringExtra("fileName");
    	    return fileName;
    	} else {
    		return null;
    	}
	}
}