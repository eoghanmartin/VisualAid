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
	Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		if(!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)) //Returns false if device doesn't have an accelerometer
		{
			Log.e("accelerometer", "not found");
			Dialog close = noAccelerometer();
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
		    	if(v.getId()==R.id.macroDefine){
					//instantiate ZXing integration class
					IntentIntegrator scanIntegrator = new IntentIntegrator(a);
					//start scanning
					scanIntegrator.initiateScan();
				}
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
		if (scanningResult != null) {
			String scanContent = scanningResult.getContents();
			Intent i = new Intent(this, TCPClient.class);
			i.putExtra("KEY",scanContent);
			Global.stringToPass = scanContent;
			String scanFormat = scanningResult.getFormatName();
		}
		else{
			Toast toast = Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

}
