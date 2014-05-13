/**
 * 
 */
package com.project.mgr.fragments.tabs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.project.mgr.R;

public class Stream_tab1 extends Fragment {
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {

    		View rootView = inflater.inflate(R.layout.stream_tab1, container, false);
    		
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
    		    	            String[] params = {user_id};
    		    	            
    		    	            new RetrivePosts().execute(params);
    		    	    	}   
    		    	    }   
    	    	    }   
        	    }); 
        	    Request.executeBatchAsync(request);
        	}
        	
        	
        	
        	return rootView;
    }
	
	TextView txt1,txt2,txt3,txt4;
	
	class RetrivePosts extends AsyncTask<String, String, Void> {
		private ProgressDialog progressDialog = new ProgressDialog(getActivity());
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
	    	param.add(new BasicNameValuePair("user_id",params[0]));
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
			    while((line=br.readLine())!=null) {
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
	   LinearLayout posts = (LinearLayout) getActivity().findViewById(R.id.posts);
	   String[] fields = {"user_id","created_at","gif","audio"};
	   for(int i=0;i<Jarray.length();i++) {
		   JSONObject Jasonobject = null;
		   Jasonobject = Jarray.getJSONObject(i);
		   
		   for(int j=0;j<fields.length;j++) {
		   
		   TextView post = new TextView(getActivity());
		   post.setText(Jasonobject.getString(fields[j]));
		   post.setId(i);
		   posts.addView(post);
		   
		   /*txt1 = (TextView) getActivity().findViewById(R.id.txt1);
		   txt2 = (TextView) getActivity().findViewById(R.id.txt2);
		   txt3 = (TextView) getActivity().findViewById(R.id.txt3);
		   txt4 = (TextView) getActivity().findViewById(R.id.txt4);
	
		   Jasonobject = Jarray.getJSONObject(i);
	
		   //get an output on the screen
		   String user_id = Jasonobject.getString("user_id");
		   String created_at = Jasonobject.getString("created_at");
		   String gif_name = Jasonobject.getString("gif");
		   String audio_name = Jasonobject.getString("audio");

		   txt1.setText(user_id);
		   txt2.setText(created_at);
		   txt3.setText(gif_name);
           txt4.setText(audio_name);*/
		   }
	   }
	   this.progressDialog.dismiss();

	  } catch (Exception e) {
	   // TODO: handle exception
	   Log.e("log_tag", "Error parsing data "+e.toString());
	  }
	}
	}
	
	public void downloadPosts() {
		try {
	        //set the download URL, a url that points to a file on the internet
	        //this is the file to be downloaded
	        URL url = new URL("http://somewhere.com/some/webhosted/file");

	        //create the new connection
	        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

	        //set up some things on the connection
	        urlConnection.setRequestMethod("GET");
	        urlConnection.setDoOutput(true);

	        //and connect!
	        urlConnection.connect();

	        //set the path where we want to save the file
	        //in this case, going to save it on the root directory of the
	        //sd card.
	        File SDCardRoot = Environment.getExternalStorageDirectory();
	        //create a new file, specifying the path, and the filename
	        //which we want to save the file as.
	        File file = new File(SDCardRoot,"somefile.ext");

	        //this will be used to write the downloaded data into the file we created
	        FileOutputStream fileOutput = new FileOutputStream(file);

	        //this will be used in reading the data from the internet
	        InputStream inputStream = urlConnection.getInputStream();

	        //this is the total size of the file
	        int totalSize = urlConnection.getContentLength();
	        //variable to store total downloaded bytes
	        int downloadedSize = 0;

	        //create a buffer...
	        byte[] buffer = new byte[1024];
	        int bufferLength = 0; //used to store a temporary size of the buffer

	        //now, read through the input buffer and write the contents to the file
	        while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
	                //add the data in the buffer to the file in the file output stream (the file on the sd card
	                fileOutput.write(buffer, 0, bufferLength);
	                //add up the size so we know how much is downloaded
	                downloadedSize += bufferLength;
	                //this is where you would do something to report the prgress, like this maybe
	                //updateProgress(downloadedSize, totalSize);

	        }
	        //close the output stream when done
	        fileOutput.close();

		//catch some possible errors...
		} catch (MalformedURLException e) {
		        e.printStackTrace();
		} catch (IOException e) {
		        e.printStackTrace();
		}
	}
}