/**
 * 
 */
package com.project.mgr.fragments.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.mgr.R;

public class Tab1Fragment extends Fragment {
    
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {

    		View rootView = inflater.inflate(R.layout.tab_frag1_layout, container, false);
    		return rootView;
    }
	
	
}