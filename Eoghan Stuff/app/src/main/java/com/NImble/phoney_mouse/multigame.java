package com.NImble.phoney_mouse;

/**
 * Created by eoghanmartin on 19/08/2014.
 */
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
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class multigame  extends Activity implements SensorEventListener
{
    Context context; //Used for intents
    private TCPClient mTcpClient;

    private final float NOISE = (float) 2.0;
    private float mLastX, mLastY, mLastZ;
    private boolean mInitialized;

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


    //REDO BUTTONS
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mInitialized = false;

        ////// remove title & Full Screen	///////
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ///////////////////////////////////////////

        setContentView(R.layout.balance);
        context = this;

        //Sensor init
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accSen = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, accSen, delay);
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

       // Button button_a = (Button) findViewById(R.id.left);
        //Button button_b = (Button) findViewById(R.id.right);
        Button start = (Button) findViewById(R.id.macro2);

        final Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

       /* button_b.setOnTouchListener(new View.OnTouchListener() //Haz bugz
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean vibr = false;
                String mode="k";

                Button b = (Button) findViewById(R.id.right);
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    b.setBackgroundResource(R.drawable._b);
                    vibr = true;
                }
                else if(event.getAction() == MotionEvent.ACTION_UP) {
                    b.setBackgroundResource(R.drawable.b_not);
                }

                //sends the message to the server
                if (mTcpClient != null && !paused) {
                    mTcpClient.sendMessage(mode+btnB);
                }
                if (vibr == true){
                    vib.vibrate(100);
                }
                return false;

            }
        });


        button_a.setOnTouchListener(new View.OnTouchListener() //Haz bugz
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean vibr = false;
                String mode="k";

                Button b = (Button) findViewById(R.id.left);
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    b.setBackgroundResource(R.drawable.a_down);
                    vibr = true;
                }
                else if(event.getAction() == MotionEvent.ACTION_UP) {
                    b.setBackgroundResource(R.drawable.a_not);
                }
                //sends the message to the server
                if (mTcpClient != null && !paused) {
                    mTcpClient.sendMessage(mode+btnA);
                }
                if (vibr == true){
                    vib.vibrate(100);
                }
                return false;
            }
        });*/

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
            Log.d("myInfo", "Send: "+mode+":"+keys );
        }

        // Called when Motion Detected
        Toast.makeText(context, "Motion detected",
                Toast.LENGTH_SHORT).show();

    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
    }

  /*  @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(getBaseContext(), "onResume Accelerometer Started",
                Toast.LENGTH_SHORT).show();

        //Check device supported Accelerometer senssor or not
        if (AccelerometerManager.isSupported(this)) {

            //Start Accelerometer Listening
            AccelerometerManager.startListening(this);
        }
    }*/

	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, accSen, delay);
	}

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

	@Override
	public void onSensorChanged(SensorEvent event) {
		analysisAcceleration(event.values);}
        /*String keys="";
        String mode="k";
        int sensorType = event.sensor.getType();

        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            a = event.values;
            accel = (float) (Math.sqrt((a[0]*a[0]+a[1]*a[1]+a[2]*a[2]))-SensorManager.GRAVITY_EARTH)-accelDiff;
            curAccel += accel;
            accelUpdates++;

            long curTime = event.timestamp - lastUpdate;
            if (curTime > 1000000000) {
                curAccel = curAccel/accelUpdates;
                speed += curAccel*curTime*10e-10;
                moved += speed*curTime*10e-10;

                lastUpdate = event.timestamp;

                //xLabel.setText(String.format("Moved: %+2.20f", moved));
                //yLabel.setText(String.format("Speed: %+2.20f", speed));
                //zLabel.setText(String.format("Accel: %+2.20f Time:  %+2.3f", curAccel, curTime*10e-10));

                //if(moved>500){
                    keys+=" ";
                    TextView t = (TextView) findViewById(R.id.lst_cmd);
                    String gotString = "space";
                    t.setText(Float.toString(moved));
                //}


                curAccel = 0;
                accelUpdates = 0;
            }

            if (mTcpClient != null && keys.length()>0 && !paused) {
                mTcpClient.sendMessage(mode+keys);
                Log.d("myInfo", "Send: "+mode+":"+keys );
            }
        }
	}*/

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	public void analysisAcceleration(float[] axis){
		float	x=axis[0],
				y=axis[1],
				z=axis[2];
		String keys="";
		String mode="k";
		String forward="w",leanBack="s",left="a",right="d";
		float	rLim= 8,//RIGHT		Y>4
				lLim=-8,//LEFT		Y<-4
				bLim=-6,//LEAN BACK	Z<-2
				fLim= 8;//FORWARD	Z>7
		float speedLim=2;

		flap=flap>0?flap-1:0;

       /* if (!mInitialized) {
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            mInitialized = true;
        } else {
		/*if(Math.max(x, Math.max(y, z)) > 14){
			//if(flap==0){
				flap=sendDelay;
				keys+=" ";
			//}
			return;
		}

            float deltaX = Math.abs(mLastX - x);
            float deltaY = Math.abs(mLastY - y);
            float deltaZ = Math.abs(mLastZ - z);
            if (deltaX < NOISE) deltaX = (float) 0.0;
            if (deltaY < NOISE) deltaY = (float) 0.0;
            if (deltaZ < NOISE) deltaZ = (float) 0.0;

            if (mLastX > x) {
                keys += right;
            }
            if (mLastX < x) {
                keys += left;
            }
            if (deltaY > deltaX) {
                keys += forward;
            }
            mLastX = x;
            mLastY = y;
            mLastZ = z;*/

		if(y>rLim){
			keys+=right;
		}
		else if(y<lLim){
			keys+=left;
		}if(z<bLim){
			keys+=leanBack;
		}else if(z>fLim){
			keys+=forward;
		}

        //}

		if (mTcpClient != null && keys.length()>0 && !paused) {
			mTcpClient.sendMessage(mode+keys);
			Log.d("myInfo", "Send: "+mode+":"+keys );
        }
	}

}
