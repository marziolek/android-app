package com.project.mgr;

import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.project.mgr.fragments.tabs.SwipeTabs;

public class MainActivity extends Activity {
    
	private String TAG = "Facebook";
	private UiLifecycleHelper uiHelper;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    setContentView(R.layout.main);
	    
	    uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
	    
	    LoginButton authButton = (LoginButton) findViewById(R.id.fbLoginBtn);
	    authButton.setReadPermissions(Arrays.asList("user_status")); 
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    if (state.isOpened()) {
	        Log.i(TAG, "Logged in...");
	        Intent intent = new Intent(this, SwipeTabs.class);
	        startActivity(intent);
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
 	 likes int null, 
 	 primary key (id));
 	 
 drop table if exists likes;
 create table likes
 	(id int not null auto_increment,
 	 post_id int not null,
 	 user_id varchar(15) not null,
 	 primary key (id));
 	 
*/