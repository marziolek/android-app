/**
 * 
 */
package com.project.mgr.fragments.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.project.mgr.R;

public class Tab2Fragment extends Fragment {
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.tab_frag2_layout, container, false);
		
		//final Intent intent = new Intent(getActivity(), AudioRecorder.class);
		final Button button = (Button) rootView.findViewById(R.id.loginButton);
/*
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(intent);
            }
        });
  */      
        return rootView;
	}
}