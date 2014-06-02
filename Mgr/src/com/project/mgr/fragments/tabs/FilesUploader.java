package com.project.mgr.fragments.tabs;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.project.mgr.MainActivity;



public class FilesUploader extends Activity {
	
	HttpURLConnection connection = null;
    DataOutputStream outputStream = null;
    DataInputStream inputStream = null;
    String urlServer = "http://wierzba.wzks.uj.edu.pl/~09_ziolekm/MgrApp/upload.php";
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary =  "*****";
     
    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;
    int maxBufferSize = 10*1024*1024;
     
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	
		final Session session = Session.getActiveSession();
    	if (session != null && session.isOpened()) {
    		// If the session is open, make an API call to get user data
    	    // and define a new callback to handle the response
    	    Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
	    	    @Override
	    	    public void onCompleted(GraphUser user, Response response) {
	    	               // If the response is successful
		    	    if (session == Session.getActiveSession()) {
		    	    	if (user != null) {
		    	    		String user_id = user.getId();//user id
		    	            String gifName = fileName();
		    	            String audioName = fileNameAudio();
		    	            String[] params = {user_id,gifName,audioName};
		    	            
		    	            new Task(FilesUploader.this).execute(params);
		    	    	}   
		    	    }   
	    	    }   
    	    }); 
    	    Request.executeBatchAsync(request);
    	}  
    }
        
    public static final int PLEASE_WAIT_DIALOG = 1;

    @Override
    public Dialog onCreateDialog(int dialogId) {
        switch (dialogId) {
        case PLEASE_WAIT_DIALOG:
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Uploading files");
            dialog.setMessage("Please wait...");
            dialog.setCancelable(true);
            return dialog;
        default:
            break;
        }
        return null;
    }
    
	class Task extends AsyncTask<String, String, Void> {
		InputStream is = null ;
	    String result = "";
	    
	    Activity startingActivity;

		public Task(Activity startingActivity) {
			this.startingActivity = startingActivity;
		}
		 
		@Override
		protected void onPreExecute() {
			startingActivity.showDialog(FilesUploader.PLEASE_WAIT_DIALOG);
		}
				
	    
	    @Override
	    protected Void doInBackground(String... params) {
	    	try
	    	{
	    		Log.d("uploadFile", params[0]);
	    		String gifPath = Environment.getExternalStorageDirectory().getPath() + "/MgrApp/GIF/"+fileName();
	    	    FileInputStream fileInputStream = new FileInputStream(new File(gifPath) );
	    	 
	    	    URL url = new URL(urlServer);
	    	    connection = (HttpURLConnection) url.openConnection();
	    	 
	    	    // Allow Inputs &amp; Outputs.
	    	    connection.setDoInput(true);
	    	    connection.setDoOutput(true);
	    	    connection.setUseCaches(false);
	    	 
	    	    // Set HTTP method to POST.
	    	    connection.setRequestMethod("POST");
	    	 
	    	    connection.setRequestProperty("Connection", "Keep-Alive");
	    	    connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
	    	    connection.setRequestProperty("UserId", params[0]);
	    	 
	    	    outputStream = new DataOutputStream( connection.getOutputStream() );
	    	    outputStream.writeBytes(twoHyphens + boundary + lineEnd);
	    	    outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + gifPath +"\"" + lineEnd);
	    	    outputStream.writeBytes(lineEnd);
	    	    
	    	    bytesAvailable = fileInputStream.available();
	    	    bufferSize = Math.min(bytesAvailable, maxBufferSize);
	    	    buffer = new byte[bufferSize];
	    	 
	    	    // Read file
	    	    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	    	 
	    	    while (bytesRead > 0)
	    	    {
	    	        outputStream.write(buffer, 0, bufferSize);
	    	        bytesAvailable = fileInputStream.available();
	    	        bufferSize = Math.min(bytesAvailable, maxBufferSize);
	    	        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	    	    }
	    	 
	    	    outputStream.writeBytes(lineEnd);
	    	    outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
	    	 
	    	    // Responses from the server (code and message)
	    	    Integer serverResponseCode = connection.getResponseCode();
	    	    String serverResponseMessage = connection.getResponseMessage();
	    	 
	    	    Log.d("response code", serverResponseCode.toString());
	    	    Log.d("response msg", serverResponseMessage.toString());
	    	    
	    	    fileInputStream.close();
	    	    outputStream.flush();
	    	    outputStream.close();
	    	}
	    	catch (Exception ex)
	    	{
	    	    //Exception handling
	    		Log.d("upload error", ex.toString());
	    	}
	    	
	    	try
	    	{
	    		Log.d("uploadFile", params[0]);
	    		String audioPath = Environment.getExternalStorageDirectory().getPath() + "/MgrApp/audio/"+fileNameAudio();
	    	    FileInputStream fileInputStreamAudio = new FileInputStream(new File(audioPath) );
	    	 
	    	    URL url = new URL(urlServer);
	    	    connection = (HttpURLConnection) url.openConnection();
	    	 
	    	    // Allow Inputs &amp; Outputs.
	    	    connection.setDoInput(true);
	    	    connection.setDoOutput(true);
	    	    connection.setUseCaches(false);
	    	 
	    	    // Set HTTP method to POST.
	    	    connection.setRequestMethod("POST");
	    	 
	    	    connection.setRequestProperty("Connection", "Keep-Alive");
	    	    connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
	    	    connection.setRequestProperty("UserId", params[0]);
	    	    
	    	    outputStream = new DataOutputStream( connection.getOutputStream() );
	    	    outputStream.writeBytes(twoHyphens + boundary + lineEnd);
	    	    outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + audioPath +"\"" + lineEnd);
	    	    outputStream.writeBytes(lineEnd);
	    	 
	    	    bytesAvailable = fileInputStreamAudio.available();
	    	    bufferSize = Math.min(bytesAvailable, maxBufferSize);
	    	    buffer = new byte[bufferSize];
	    	 
	    	    // Read file
	    	    bytesRead = fileInputStreamAudio.read(buffer, 0, bufferSize);
	    	 
	    	    while (bytesRead > 0)
	    	    {
	    	        outputStream.write(buffer, 0, bufferSize);
	    	        bytesAvailable = fileInputStreamAudio.available();
	    	        bufferSize = Math.min(bytesAvailable, maxBufferSize);
	    	        bytesRead = fileInputStreamAudio.read(buffer, 0, bufferSize);
	    	    }
	    	 
	    	    outputStream.writeBytes(lineEnd);
	    	    outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
	    	 
	    	    // Responses from the server (code and message)
	    	    Integer serverResponseCode = connection.getResponseCode();
	    	    String serverResponseMessage = connection.getResponseMessage();
	    	 
	    	    Log.d("response code", serverResponseCode.toString());
	    	    Log.d("response msg", serverResponseMessage.toString());
	    	    
	    	    fileInputStreamAudio.close();
	    	    outputStream.flush();
	    	    outputStream.close();
	    	}
	    	catch (Exception ex)
	    	{
	    	    //Exception handling
	    		Log.d("upload error", ex.toString());
	    	}

	    	String url_select = "http://wierzba.wzks.uj.edu.pl/~09_ziolekm/MgrApp/insert.php";

	      	HttpClient httpClient = new DefaultHttpClient();
	      	HttpPost httpPost = new HttpPost(url_select);
	      	ArrayList<NameValuePair> param = new ArrayList<NameValuePair>(1);
	      	param.add(new BasicNameValuePair("user_id", params[0]));
	      	param.add(new BasicNameValuePair("gif", params[1]));
	      	param.add(new BasicNameValuePair("audio", params[2]));
	      	
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
	    	startingActivity.removeDialog(FilesUploader.PLEASE_WAIT_DIALOG);
	    	Toast.makeText(getApplicationContext(), "Your post is now online!", 2000).show();
	    	Intent stream = new Intent(FilesUploader.this, MainActivity.class);
	    	startActivity(stream);
	    }
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
	public String fileNameAudio() {
		File audio = new File(Environment.getExternalStorageDirectory().getPath() + "/MgrApp/audio");
		if (audio.isDirectory()) {
			File[] audioFile = audio.listFiles();
			return audioFile[0].getName();
		} else {
			return null;
		}
	}
}