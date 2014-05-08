package com.project.mgr.fragments.tabs;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.app.SearchManager.OnCancelListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.project.mgr.R;

public class PreviewGif extends FragmentActivity {
		
	public Button uploadGif;
	
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
        
        uploadGif = (Button) findViewById(R.id.uploadGif);
        uploadGif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), FilesUploader.class);
                startActivity(intent);
            }
        });
        
        new task().execute();
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
	
	 TextView txt1,txt2,txt3;
	
	class task extends AsyncTask<String, String, Void> {
		private ProgressDialog progressDialog = new ProgressDialog(PreviewGif.this);
	    InputStream is = null ;
	    String result = "";
	    protected void onPreExecute() {
	       progressDialog.setMessage("Fetching data...");
	       progressDialog.show();
	       /*progressDialog.setOnCancelListener(new OnCancelListener() {
			 @Override
			  public void onCancel(DialogInterface arg0) {
			  task.this.cancel(true);
			    }
			 });*/
	     }
	    
	    @Override
	    protected Void doInBackground(String... params) {
	      String url_select = "http://wierzba.wzks.uj.edu.pl/~09_ziolekm/MgrApp/select.php";

	      HttpClient httpClient = new DefaultHttpClient();
	      HttpPost httpPost = new HttpPost(url_select);

	             ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

	        try {
	     httpPost.setEntity(new UrlEncodedFormEntity(param));

	     HttpResponse httpResponse = httpClient.execute(httpPost);
	     HttpEntity httpEntity = httpResponse.getEntity();

	     //read content
	     is =  httpEntity.getContent();     

	     } catch (Exception e) {

	     Log.e("log_tag", "Error in http connection "+e.toString());
	     }
	    try {
	        BufferedReader br = new BufferedReader(new InputStreamReader(is));
	     StringBuilder sb = new StringBuilder();
	     String line = "";
	     while((line=br.readLine())!=null)
	     {
	        sb.append(line+"\n");
	     }
	      is.close();
	      result=sb.toString();    

	       } catch (Exception e) {
	        // TODO: handle exception
	        Log.e("log_tag", "Error converting result "+e.toString());
	       }

	      return null;

	     }
	    protected void onPostExecute(Void v) {

	  // ambil data dari Json database
	  try {
	   JSONArray Jarray = new JSONArray(result);
	   for(int i=0;i<Jarray.length();i++)
	   {
	   JSONObject Jasonobject = null;
	   txt1 = (TextView)findViewById(R.id.txt1);
	   txt2 = (TextView)findViewById(R.id.txt2);
	   txt3 = (TextView)findViewById(R.id.txt3);
	  
	   Jasonobject = Jarray.getJSONObject(i);

	   //get an output on the screen
	   String user_id = Jasonobject.getString("user_id");
	   String created_at = Jasonobject.getString("created_at");
	   String post_number = Jasonobject.getString("post_number");
	   
	      txt1.setText(user_id);
	      txt2.setText(created_at);
	      txt3.setText(post_number);
	  
	   }
	   this.progressDialog.dismiss();

	  } catch (Exception e) {
	   // TODO: handle exception
	   Log.e("log_tag", "Error parsing data "+e.toString());
	  }
	}
	}

}