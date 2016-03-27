package com.NImble.phoney_mouse;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class Menu extends Activity
{
	Context context; //Used for intents
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		if(!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)) //Returns false if device doesn't have an accelerometer
		{ //DUNNO IF WE HAVE CORRECT SENSOR FOR LINEAR ACCELERATION?
			Log.e("accelerometer", "not found");
			Dialog close = noAccelerometer(); //Inform user that the app won't work
		    close.show();
		}
		
		setContentView(R.layout.menu);
		context = this;
		
		Button btn = (Button) findViewById(R.id.main);
		final Activity a = this;

		btn.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	Intent intent = new Intent(context, Main.class);
		    	startActivity(intent);
		    }
		});
		
		btn = (Button) findViewById(R.id.macroDefine);
		
		btn.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	//Intent intent = new Intent(context, MainActivity.class);
		    	//startActivity(intent);
		    	if(v.getId()==R.id.macroDefine){
					//instantiate ZXing integration class
					IntentIntegrator scanIntegrator = new IntentIntegrator(a);
					//start scanning
					scanIntegrator.initiateScan();
				}
		    }
		});
		
		btn = (Button) findViewById(R.id.mediaTest);
		
		btn.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	Intent intent = new Intent(context, multigame.class);
		    	startActivity(intent);
		    }
		});
		
		btn = (Button) findViewById(R.id.controls);
		
		btn.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	Intent intent = new Intent(context, MediaControls.class);
		    	startActivity(intent);
		    }
		});
	}
	
	public Dialog noAccelerometer()
	{
		 AlertDialog.Builder builder = new AlertDialog.Builder(this);
		 builder.setMessage(R.string.noAccelerometer)
		    .setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() 
		    {
		    	 public void onClick(DialogInterface dialog, int id) 
		         {
		    		 finish();
		         }
		    });
		 return builder.create();
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//retrieve result of scanning - instantiate ZXing object
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		//check we have a valid result
		if (scanningResult != null) {
			//get content from Intent Result
			String scanContent = scanningResult.getContents();
			Intent i = new Intent(this, TCPClient.class);
			i.putExtra("KEY",scanContent);
			Global.stringToPass = scanContent;
			//get format name of data scanned
			String scanFormat = scanningResult.getFormatName();
			//output to UI
	//		formatTxt.setText("FORMAT: "+scanFormat);
	//		contentTxt.setText("CONTENT: "+scanContent);
		}
		else{
			//invalid scan data or scan canceled
			Toast toast = Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

}
