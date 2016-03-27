package com.NImble.phoney_mouse;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class Main  extends Activity implements AccelerometerListener
{
	Context context;
	private TCPClient mTcpClient;
	
    public SensorManager mSensorManager;
    public Sensor accSen;

	public boolean paused=false;
    Thread thread;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		////// remove title & Full Screen	///////
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);

        thread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(300);
                    }
                } catch (InterruptedException ex) {
                }

                // TODO
            }
        };
        thread.start();
		
		setContentView(R.layout.main);
		context = this;

		//Sensor init
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accSen = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		//TCP, connect to server
		new connectTask().execute("");
		Button start = (Button) findViewById(R.id.macro2);
		
		final Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

		start.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				boolean vibr = false;
				Button b = (Button) findViewById(R.id.macro2);
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					b.setBackgroundResource(R.drawable.start_down);
				}
				else if(event.getAction() == MotionEvent.ACTION_UP) {
					paused = !paused;
					Log.d("myInfo", "Paused:"+paused);
					b.setBackgroundResource(R.drawable.start_not);
					vibr = true;
				}

				if (vibr == true){
					vib.vibrate(100);
				}

				return false;
				}
		});
	}

    public void onAccelerationChanged(float x, float y, float z) {}

    public void onShake(float force) {
        String mode="capture";
        if (mTcpClient != null && !paused) {
            mTcpClient.sendMessage(mode);
            Log.d("myInfo", "Send: " + mode);
            synchronized (thread) {
                thread.notifyAll();
            }
        }
    }

    protected void onPause() {
        super.onPause();
        AccelerometerManager.stopListening();
    }

    protected void onDestroy() {
        super.onDestroy();
        AccelerometerManager.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();

        //Check device supported Accelerometer sensor or not
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
    }

	//From TCP
	public class connectTask extends AsyncTask<String,String,TCPClient> {
	    @Override
	    protected TCPClient doInBackground(String... message) {
		    mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
	            @Override
	            //here the messageReceived method is implemented
	            public void messageReceived(String message) {
	                publishProgress(message);
                    // TODO IMPLEMENT VIBRATIONS BASED ON RECIEVED VALUE...

                    TextView tv1 = (TextView)findViewById(R.id.location);
                    tv1.setText(message);

                    String test_str = "test";
                    final Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                    Log.d("message recieved", test_str);
                    vib.vibrate(700);
	            }
	        });
	        mTcpClient.run();
	        return null;
	    }
	}
}
