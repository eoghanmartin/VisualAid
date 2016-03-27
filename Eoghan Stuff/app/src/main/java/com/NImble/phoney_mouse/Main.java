package com.NImble.phoney_mouse;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Thread.sleep;

public class Main  extends Activity /*implements SensorEventListener*/ implements AccelerometerListener
{
	Context context; //Used for intents
	private TCPClient mTcpClient;

    private float moved = 0;
    private float speed = 0;
    private float accel = 0;
    private float accelDiff = 0;

    private float[] a;

    private long lastUpdate = 0;
    private float curAccel = 0;
    private int accelUpdates = 0;
	
	float width, height;
	float x1, x2, y1, y2;
	long timeDiff = 0; //Not implemented just now (just here as an option)
	//TOUCH THING HAS LIMIT ERROR!
	
	int degreesInSingleSwipe = 120;
	float degreesPerHeight;
	boolean endSwipe = true;
	
	//Sensor Stuffs
    public SensorManager mSensorManager;
    public Sensor accSen;
	public static int delay = SensorManager.SENSOR_DELAY_GAME;
	double accuracy=100.0;
	/////////////////
	
	public int sendDelay=15;
	public int flap=0;
	
	public String forward="w",leanBack="s",left="a",right="d";
	public String btnA="q",btnB="e";

	public boolean paused=false;
    Thread thread;


    //REDO BUTTONS
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		////// remove title & Full Screen	///////
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ///////////////////////////////////////////

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
		//mSensorManager.registerListener(this, accSen, delay);
		////////////////////////////
		
		////Read Settings
		String mStringArray[] = { ",\"", "\\" };

		JSONArray mJSONArray = new JSONArray(Arrays.asList(mStringArray));
		String json = mJSONArray.toString();
		Log.d("fileio", json);
		try {
			JSONObject myjson = new JSONObject(json);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//TCP, connect to server
		new connectTask().execute("");
		Button start = (Button) findViewById(R.id.macro2);
		
		final Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

		start.setOnTouchListener(new View.OnTouchListener() //Haz bugz
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

    public void onAccelerationChanged(float x, float y, float z) {
        // TODO Auto-generated method stub

    }

    public void onShake(float force) {

        // Do your stuff here
        String keys=" ";
        String mode="k";
        if (mTcpClient != null && !paused) {
            mTcpClient.sendMessage(mode+keys);
            Log.d("myInfo", "Send: " + mode + ":" + keys);
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

        //Check device supported Accelerometer senssor or not
        if (AccelerometerManager.isSupported(this)) {

            //Start Accelerometer Listening
            AccelerometerManager.startListening(this);
        }
    }
	
	/*protected void onResume() {
		super.onResume();
		//mSensorManager.registerListener(this, accSen, delay);
	}*/

	//From TCP
	public class connectTask extends AsyncTask<String,String,TCPClient> {
	
	    @Override
	    protected TCPClient doInBackground(String... message) {
	
	        //we create a TCPClient object and
	        mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
	            @Override
	            //here the messageReceived method is implemented
	            public void messageReceived(String message) {
	                //this method calls the onProgressUpdate
	                publishProgress(message);
	            }
	        });
	        mTcpClient.run();
	
	        return null;
	    }
	}
}
