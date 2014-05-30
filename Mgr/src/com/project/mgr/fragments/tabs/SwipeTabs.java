package com.project.mgr.fragments.tabs;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.project.mgr.Config;
import com.project.mgr.Controller;
import com.project.mgr.R;

public class SwipeTabs extends FragmentActivity implements ActionBar.TabListener {
	private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    // Tab titles
    private String[] tabs = { "Stream", "Record", "Settings", "My stream" };
    
    Controller aController;
	AsyncTask<Void, Void, Void> mRegisterTask;
        
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(4);
        
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
 
        viewPager.setAdapter(mAdapter);
        //actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        final Intent i = getIntent();
        final String fbId = i.getStringExtra("fbId");
 
        // Adding Tabs
        for (String tab_name : tabs) {
            if (tab_name == "Stream") {
            	actionBar.addTab(actionBar.newTab().setTabListener(this).setIcon(R.drawable.users));
            }
            if (tab_name == "My stream") {
            	actionBar.addTab(actionBar.newTab().setTabListener(this).setIcon(R.drawable.user));
            }
            if (tab_name == "Record") {
            	actionBar.addTab(actionBar.newTab().setTabListener(this).setIcon(R.drawable.mic));
            }
            if (tab_name == "Settings") {
            	actionBar.addTab(actionBar.newTab().setTabListener(this).setIcon(R.drawable.settings));
            }
        }
 
        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
 
            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }
 
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
 
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        
        /*
         * GCM
         */
        
            	//Get Global Controller Class object (see application tag in AndroidManifest.xml)
      		aController = (Controller) getApplicationContext();
      		
      		System.out.println(aController);
      		
      		// Check if Internet present
      		if (!aController.isConnectingToInternet()) {
      			
      			// Internet Connection is not present
      			aController.showAlertDialog(SwipeTabs.this,
      					"Internet Connection Error",
      					"Please connect to Internet connection", false);
      			// stop executing code by return
      			return;
      		}
      		
      		// Getting name, email from intent
      		
      		
      		//name = i.getStringExtra("name");
      		//email = i.getStringExtra("email");
      				
      		
      		// Make sure the device has the proper dependencies.
      		GCMRegistrar.checkDevice(this);

      		// Make sure the manifest permissions was properly set 
      		GCMRegistrar.checkManifest(this);

      		//lblMessage = (TextView) findViewById(R.id.lblMessage);
      		
      		// Register custom Broadcast receiver to show messages on activity
      		registerReceiver(mHandleMessageReceiver, new IntentFilter(
      				Config.DISPLAY_MESSAGE_ACTION));
      		
      		// Get GCM registration id
      		final String regId = GCMRegistrar.getRegistrationId(this);
      		System.out.println(regId);
      		System.out.println(fbId);
      		// Check if regid already presents
      		if (regId.equals("")) {
      			
      			// Register with GCM			
      			GCMRegistrar.register(this, Config.GOOGLE_SENDER_ID);
      			
      		} else {
      			
      			// Device is already registered on GCM Server
      			if (GCMRegistrar.isRegisteredOnServer(this)) {
      				
      				// Skips registration.				
      				//Toast.makeText(getApplicationContext(), "Already registered with GCM Server", Toast.LENGTH_LONG).show();
      			
      			} else {
      				
      				// Try to register again, but not in the UI thread.
      				// It's also necessary to cancel the thread onDestroy(),
      				// hence the use of AsyncTask instead of a raw thread.
      				
      				final Context context = this;
      				mRegisterTask = new AsyncTask<Void, Void, Void>() {

      					@Override
      					protected Void doInBackground(Void... params) {
      						
      						// Register on our server
      						// On server creates a new user
      						
      						aController.register(context, fbId, regId);
      						
      						return null;
      					}

      					@Override
      					protected void onPostExecute(Void result) {
      						mRegisterTask = null;
      					}

      				};
      				
      				// execute AsyncTask
      				mRegisterTask.execute(null, null, null);
      			}
      		}
    }
    
    // Create a broadcast receiver to get message and show on screen 
 	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
 		
 		@Override
 		public void onReceive(Context context, Intent intent) {
 			
 			String newMessage = intent.getExtras().getString(Config.EXTRA_MESSAGE);
 			
 			// Waking up mobile if it is sleeping
 			aController.acquireWakeLock(getApplicationContext());
 			
 			// Display message on the screen
 			//lblMessage.append(newMessage + "\n");			
 			
 			Toast.makeText(getApplicationContext(), newMessage, Toast.LENGTH_LONG).show();
 			
 			// Releasing wake lock
 			aController.releaseWakeLock();
 		}
 	};
    
    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }
 
    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }
 
    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }
    
    @Override
	protected void onDestroy() {
		// Cancel AsyncTask
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		try {
			// Unregister Broadcast Receiver
			unregisterReceiver(mHandleMessageReceiver);
			
			//Clear internal resources.
			GCMRegistrar.onDestroy(this);
			
		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}

}
