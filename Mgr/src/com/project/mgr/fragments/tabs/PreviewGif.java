package com.project.mgr.fragments.tabs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;

import com.project.mgr.R;

public class PreviewGif extends FragmentActivity {
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.preview_gif);
		
		FileOutputStream outStream = null;
        try{
            outStream = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/MgrApp/test.gif");
            outStream.write(generateGIF());
            outStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }
	}
		
	public byte[] generateGIF() {
		ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
		File picturesDir = new File(Environment.getExternalStorageDirectory().getPath() + "/MgrApp/pictures");
		File pictures[] = picturesDir.listFiles();
		for (int i=0; i < pictures.length; i++) {
			Bitmap myBitmap = BitmapFactory.decodeFile(pictures[i].getAbsolutePath());
			//ArrayList<Bitmap> bitmaps = adapter.getBitmapArray();
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
}