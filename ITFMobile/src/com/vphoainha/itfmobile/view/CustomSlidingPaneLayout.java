package com.vphoainha.itfmobile.view;

import android.content.Context;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomSlidingPaneLayout extends SlidingPaneLayout {
	private boolean enableSliding;   
	
	public CustomSlidingPaneLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		enableSliding=true;
	}
	
	@Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
		if(!enableSliding) return false;
        return super.onInterceptTouchEvent(event);
    }

	public boolean isEnableSliding() {
		return enableSliding;
	}

	public void setEnableSliding(boolean enableSliding) {
		this.enableSliding = enableSliding;
	}

    

}
