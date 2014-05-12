/**
 * 
 */
package com.project.mgr.fragments.tabs;

import java.io.BufferedReader;
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
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.project.mgr.R;

public class Stream_tab1 extends Fragment {
    
	private Button button;
	
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
	   for(int i=0;i<Jarray.length();i++)
	   {
	   JSONObject Jasonobject = null;
	   txt1 = (TextView) getActivity().findViewById(R.id.txt1);
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
	      txt3.setText(audio_name);
	   }
	   this.progressDialog.dismiss();

	  } catch (Exception e) {
	   // TODO: handle exception
	   Log.e("log_tag", "Error parsing data "+e.toString());
	  }
	}
	}
}