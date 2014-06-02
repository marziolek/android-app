package com.project.mgr;

import java.io.File;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.project.mgr.fragments.tabs.SwipeTabs;

public class MainActivity extends Activity {
    
	private String TAG = "Facebook";
	private UiLifecycleHelper uiHelper;
	public static String fbId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	   
	    setContentView(R.layout.main);
	    
	    File mMainDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MgrApp");
	    deleteFolder(mMainDir);
        File mMainDirAudio = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MgrApp/audio");
        File mMainDirPictures = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MgrApp/pictures");
        if (!mMainDir.isDirectory()) {
        	mMainDir.mkdir();	
        }
        if (!mMainDirAudio.isDirectory()) {
        	mMainDirAudio.mkdir();	
        }
        if (!mMainDirPictures.isDirectory()) {
        	mMainDirPictures.mkdir();	
        }
                
	    uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
	    
	    LoginButton authButton = (LoginButton) findViewById(R.id.fbLoginBtn);
	    authButton.setReadPermissions(Arrays.asList("user_status"));
	   
	}
	
	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
	    if (state.isOpened()) {
	    	LoginButton authButton = (LoginButton) findViewById(R.id.fbLoginBtn);
	    	authButton.setVisibility(View.GONE);
	        //final Session session = Session.getActiveSession();
	    	if (session != null && session.isOpened()) {
	    		// If the session is open, make an API call to get user data
	    	    // and define a new callback to handle the response
	    	    Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
		    	    @Override
		    	    public void onCompleted(GraphUser user, Response response) {
		    	               // If the response is successful
			    	    if (session == Session.getActiveSession()) {
			    	    	if (user != null) {
			    	    		String fbId = user.getId();
			    	    		Log.i(TAG, "Logged in...");
			    	    		Intent intent = new Intent(MainActivity.this, SwipeTabs.class);
			    		        intent.putExtra("fbId", fbId);
			    		        startActivity(intent);
			    	    	}
			    	    }   
		    	    }   
	    	    });
	    	    Request.executeBatchAsync(request);
	    	} 	
	        
	    } else if (state.isClosed()) {
	        Log.i(TAG, "Logged out...");
	    }
	}
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	@Override
	public void onResume() {
	    super.onResume();
	    Session session = Session.getActiveSession();
	    if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
	        onSessionStateChange(session, session.getState(), null);
	    }

	    uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	
	public static void deleteFolder(File folder) {
	    File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolder(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	    folder.delete();
	}
	
}
/*
 * DB 
 drop table if exists posts;
 create table posts 
 	(id int not null auto_increment,
 	 user_id varchar(15) not null,
 	 created_at timestamp not null, 
 	 gif varchar(27) not null, 
 	 audio varchar(35) not null, 
 	 geo varchar(20) null,
 	 likes int null default 0, 
 	 primary key (id));
 	 
 drop table if exists likes;
 create table likes
 	(id int not null auto_increment,
 	 post_id int not null,
 	 user_id varchar(15) not null,
 	 primary key (id));

 drop table if exists gcm_users;
 CREATE TABLE IF NOT EXISTS `gcm_users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `gcm_regid` text,
  `fbId` varchar(15) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

*/