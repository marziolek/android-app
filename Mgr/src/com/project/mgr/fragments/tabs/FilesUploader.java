package com.project.mgr.fragments.tabs;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;



public class FilesUploader extends Activity {
	
	HttpURLConnection connection = null;
    DataOutputStream outputStream = null;
    DataInputStream inputStream = null;
    String gifPath = Environment.getExternalStorageDirectory().getPath() + "/MgrApp/GIFfromPictures.gif";
    String urlServer = "http://wierzba.wzks.uj.edu.pl/~09_ziolekm/MgrApp/upload.php";
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary =  "*****";
     
    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;
    int maxBufferSize = 1*1024*1024;
     
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	
		new Upload().execute();
		
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
	                       String user_ID = user.getId();//user id
	                       String profileName = user.getName();//user's profile name
	                       // userNameView.setText(user.getName());
	                       Log.d("id", user_ID);
	                       Log.d("prof", profileName);
	                    }   
	                }   
	            }   
	        }); 
	        Request.executeBatchAsync(request);
	    } 
    }
    
    class Upload extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				UploadFile();
			}
			catch(Exception e) {
				Log.d("err", e.toString());
			}
			return null;
		}
    }
    
    public void UploadFile(){
    	try
    	{
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
    }
   
}