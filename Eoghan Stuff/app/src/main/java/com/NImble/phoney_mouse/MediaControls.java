package com.NImble.phoney_mouse;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class MediaControls extends Activity
{

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE); //Do we want a title?
		
	    setContentView(R.layout.config_menu);
	    
	}
}