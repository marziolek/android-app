package com.project.mgr.fragments.tabs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.project.mgr.R;

public class PreviewGif extends FragmentActivity {
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.preview_gif);
		
		FileOutputStream outStream = null;
        try{
            outStream = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/MgrApp/GIFfromPictures.gif");
            outStream.write(generateGIF());
            outStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        final PreviewGifPlayer gif1 = (PreviewGifPlayer) findViewById(R.id.gif1);
        
        try {
        	InputStream is = new FileInputStream(Environment.getExternalStorageDirectory().getPath() + "/MgrApp/GIFfromPictures.gif");
            byte[] array = streamToBytes(is);
	        Movie movie = Movie.decodeByteArray(array, 0, array.length);
	        gif1.setMovie(movie);
	    }
        catch(Exception e) {
        	Log.d("Asd", e.toString());
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
	
	public byte[] generateGIF() {
		ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
		File picturesDir = new File(Environment.getExternalStorageDirectory().getPath() + "/MgrApp/pictures");
		File pictures[] = picturesDir.listFiles();
		for (int i=0; i < pictures.length; i++) {
			Bitmap myBitmap = BitmapFactory.decodeFile(pictures[i].getAbsolutePath());
			bitmaps.add(myBitmap);
		}
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    GIFEncoder encoder = new GIFEncoder();
	    encoder.start(bos);
	    for (Bitmap bitmap : bitmaps) {
	        encoder.addFrame(bitmap);
	    }
	    encoder.finish();
	    return bos.toByteArray();
	}
	
	public void onGifClick(View v) {
		PreviewGifPlayer gif = (PreviewGifPlayer) v;
		gif.setPaused(!gif.isPaused());
	}
}

