package com.NImble.phoney_mouse;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MediaMode extends Activity
{

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE); //Do we want a title?
		
	    setContentView(R.layout.media_mode);
	    
	    final Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		
		Button button_down = (Button) findViewById(R.id.button5);
		Button button_right = (Button) findViewById(R.id.button4);
		Button button_center = (Button) findViewById(R.id.button3);
		Button button_left = (Button) findViewById(R.id.button2);
		Button button_up = (Button) findViewById(R.id.button1);
		Button button_secondary = (Button) findViewById(R.id.button6);
		
		button_down.setOnTouchListener(new View.OnTouchListener() //Haz bugz
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				boolean vibr = false;
				
				Button b = (Button) findViewById(R.id.button5);
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					b.setBackgroundResource(R.drawable.down_dn);
					vibr = true;
				}
				else if(event.getAction() == MotionEvent.ACTION_UP) {
					b.setBackgroundResource(R.drawable.down);
				}
				if (vibr == true){
					vib.vibrate(100);
				}
				return false;
				}
		});
		
		button_up.setOnTouchListener(new View.OnTouchListener() //Haz bugz
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				boolean vibr = false;
				
				Button b = (Button) findViewById(R.id.button1);
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					b.setBackgroundResource(R.drawable.up_dn);
					vibr = true;
				}
				else if(event.getAction() == MotionEvent.ACTION_UP) {
					b.setBackgroundResource(R.drawable.up);
				}
				if (vibr == true){
					vib.vibrate(100);
				}
				return false;
				}
		});
		
		button_right.setOnTouchListener(new View.OnTouchListener() //Haz bugz
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				boolean vibr = false;
				
				Button b = (Button) findViewById(R.id.button4);
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					b.setBackgroundResource(R.drawable.right_dn);
					vibr = true;
				}
				else if(event.getAction() == MotionEvent.ACTION_UP) {
					b.setBackgroundResource(R.drawable.right);
				}
				if (vibr == true){
					vib.vibrate(100);
				}
				return false;
				}
		});
		
		button_left.setOnTouchListener(new View.OnTouchListener() //Haz bugz
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				boolean vibr = false;
				
				Button b = (Button) findViewById(R.id.button2);
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					b.setBackgroundResource(R.drawable.left_dn);
					vibr = true;
				}
				else if(event.getAction() == MotionEvent.ACTION_UP) {
					b.setBackgroundResource(R.drawable.left);
				}
				if (vibr == true){
					vib.vibrate(100);
				}
				return false;
				}
		});
		
		button_center.setOnTouchListener(new View.OnTouchListener() //Haz bugz
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				boolean vibr = false;
				
				Button b = (Button) findViewById(R.id.button3);
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					b.setBackgroundResource(R.drawable.center_dn);
					vibr = true;
				}
				else if(event.getAction() == MotionEvent.ACTION_UP) {
					b.setBackgroundResource(R.drawable.center);
				}
				if (vibr == true){
					vib.vibrate(100);
				}
				return false;
				}
		});
		
		button_secondary.setOnTouchListener(new View.OnTouchListener() //Haz bugz
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				boolean vibr = false;
				
				Button b = (Button) findViewById(R.id.button6);
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					b.setBackgroundResource(R.drawable.secondary_dn);
					vibr = true;
				}
				else if(event.getAction() == MotionEvent.ACTION_UP) {
					b.setBackgroundResource(R.drawable.secondary);
				}
				if (vibr == true){
					vib.vibrate(100);
				}
				return false;
				}
		});
	    
	}
}