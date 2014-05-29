package com.project.mgr.fragments.tabs;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Movie;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.project.mgr.R;

public class PreviewGif extends FragmentActivity {
	
	private static final String TAG = "Publish and share";
	
	public Button uploadGif;
	public final String user_id = null;
	private Button shareButton;
	private final Session session = Session.getActiveSession();
	 
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.preview_gif);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		
        final PreviewGifPlayer gif1 = (PreviewGifPlayer) findViewById(R.id.gif1);
        try {
        	InputStream is = new FileInputStream(Environment.getExternalStorageDirectory().getPath() + "/MgrApp/GIF/"+fileName());
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
        
        shareButton = (Button) findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				publishStory();
				Intent intent = new Intent(PreviewGif.this, FilesUploader.class);
	            Bundle b = new Bundle();
	            b.putString("fileName", fileName());
	            intent.putExtras(b);
	            startActivity(intent);
			}
		});
        
        if (savedInstanceState != null) {
            pendingPublishReauthorization = 
                savedInstanceState.getBoolean(PENDING_PUBLISH_KEY, false);
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
	
	public String fileName() {
		Intent extras = this.getIntent();
    	if (extras != null) {
    	    String fileName = extras.getStringExtra("fileName");
    	    return fileName;
    	} else {
    		return null;
    	}
	}
	
	public String userId() {
		Intent extras = this.getIntent();
    	if (extras != null) {
    	    String user_id = extras.getStringExtra("user_id");
    	    return user_id;
    	} else {
    		return null;
    	}
	}
	
	private void publishStory() {
		if (session != null){

	        /* Check for publish permissions    
	        List<String> permissions = session.getPermissions();
	        if (!isSubsetOf(PERMISSIONS, permissions)) {
	            pendingPublishReauthorization = true;
	            Session.NewPermissionsRequest newPermissionsRequest = new Session
	                    .NewPermissionsRequest(this, PERMISSIONS);
	        session.requestNewPublishPermissions(newPermissionsRequest);
	            return;
	        }*/

	        Bundle postParams = new Bundle();
	        postParams.putString("name", "MgrApp - make your gifs");
	        postParams.putString("caption", "Record sounds around you and make some GIFs!");
	        postParams.putString("description", "Check out my new post on MgrApp!");
	        postParams.putString("link", "http://wierzba.wzks.uj.edu.pl/~09_ziolekm/MgrApp/uploadedFiles/"+userId()+"/"+fileName());
	        postParams.putString("picture", "http://wierzba.wzks.uj.edu.pl/~09_ziolekm/MgrApp/uploadedFiles/"+userId()+"/"+fileName());

	        Request.Callback callback= new Request.Callback() {
	            public void onCompleted(Response response) {
	                JSONObject graphResponse = response
	                                           .getGraphObject()
	                                           .getInnerJSONObject();
	                String postId = null;
	                String successMsg = "It's now on Facebook!";
	                try {
	                    postId = graphResponse.getString("id");
	                } catch (JSONException e) {
	                    Log.i(TAG,
	                        "JSON error "+ e.getMessage());
	                }
	                FacebookRequestError error = response.getError();
	                if (error != null) {
	                    Toast.makeText(getApplicationContext(),
	                         error.getErrorMessage(),
	                         Toast.LENGTH_SHORT).show();
	                    } else {
	                        Toast.makeText(getApplicationContext(), 
	                        		successMsg,
	                             Toast.LENGTH_LONG).show();
	                }
	            }
	        };

	        Request request = new Request(session, "me/feed", postParams, 
	                              HttpMethod.POST, callback);

	        RequestAsyncTask task = new RequestAsyncTask(request);
	        task.execute();
	    }
	}
	
	@Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	     switch (item.getItemId()) {
	     // Respond to the action bar's Up/Home button
	     case android.R.id.home:
	         NavUtils.navigateUpFromSameTask(this);
	         return true;
	     }
	     return super.onOptionsItemSelected(item);
	 }
	
}