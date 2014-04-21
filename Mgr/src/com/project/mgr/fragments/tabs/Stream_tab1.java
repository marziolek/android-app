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

import com.project.mgr.R;

public class Stream_tab1 extends Fragment {
    
	private Button button;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {

    		View rootView = inflater.inflate(R.layout.stream_tab1, container, false);
    		
    		button = (Button) rootView.findViewById(R.id.merge);
    		button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), TakePhotos.class);
                    startActivity(intent);
                }
            });
    		
    		return rootView;
    }
	
	
}