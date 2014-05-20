package com.project.mgr.fragments.tabs;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

public class DetectScrollView extends ScrollView {
	private OnScrollViewListener mOnScrollViewListener;
	
	public DetectScrollView(Context context) {
		super(context);
	}
	
	public DetectScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public DetectScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public interface OnScrollViewListener {
	    void onScrollChanged( DetectScrollView v, int l, int t, int oldl, int oldt );
	}
	
	public void setOnScrollViewListener(OnScrollViewListener l) {
	    this.mOnScrollViewListener = l;
	}
	
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
	    mOnScrollViewListener.onScrollChanged( this, l, t, oldl, oldt );
	    super.onScrollChanged( l, t, oldl, oldt );
	    
	}
}
