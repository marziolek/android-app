/**
 * 
 */
package com.project.mgr.fragments.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.Session;
import com.project.mgr.MainActivity;
import com.project.mgr.R;

public class SettingsTab4 extends Fragment {
    
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.settings_tab4, container, false);
		
		Button signOut = (Button) rootView.findViewById(R.id.sign_out);
		signOut.setOnClickListener(new View.OnClickListener() {
	    	@Override
	        public void onClick(View v) {
	    		signOut();
	    	}
		});
		
		return rootView;
	}
	
	private void signOut() {
		final Session session = Session.getActiveSession();
	    if (session != null && session.isOpened()) {
	    	if (!session.isClosed()) {
	            session.closeAndClearTokenInformation();
	            //clear your preferences if saved
	            
	            Intent intent = new Intent(getActivity(), MainActivity.class);
	            startActivity(intent);
	        }
	    }
	}
	
}