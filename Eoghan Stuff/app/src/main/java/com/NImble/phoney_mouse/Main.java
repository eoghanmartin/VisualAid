package com.NImble.phoney_mouse;

import java.util.ArrayList;

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
import android.widget.ListView;

public class Main  extends Activity implements AccelerometerListener
{
	Context context;
    private ListView mList;
    private ArrayList<String> arrayList;
    private ObjectLocationUpdater mAdapter;
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

        /*thread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(300);
                    }
                } catch (InterruptedException ex) {
                }
            }
        };
        thread.start();*/
		
		setContentView(R.layout.main);
		context = this;

        arrayList = new ArrayList<String>();

		//Sensor init
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accSen = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //relate the listView from java to the one created in xml
        mList = (ListView)findViewById(R.id.list);

        mAdapter = new ObjectLocationUpdater(this, arrayList);
        mList.setAdapter(mAdapter);
		
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
                    //sends the message to the server
                    if (mTcpClient != null) {
                        mTcpClient.sendMessage("capture");
                    }
                    //refresh the list
                    mAdapter.notifyDataSetChanged();
				}
				else if(event.getAction() == MotionEvent.ACTION_UP) {
					//paused = !paused;
					//Log.d("myInfo", "Paused:"+paused);
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
        /*if (mTcpClient != null && !paused) {
            //mTcpClient.sendMessage(mode);
            //Log.d("myInfo", "Send: " + mode);
            synchronized (thread) {
                thread.notifyAll();
            }
        }*/
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

                    //TextView locationVal = (TextView)findViewById(R.id.location);
                    //locationVal.setText(message);

                    final Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

                    Log.d("Message Recieved: ", message);

                    //vib.vibrate(700);

                    long[] pattern = genVibratorPattern(0.9f, 1000);
                    vib.vibrate(pattern, -1);

	            }
	        });
	        mTcpClient.run();
	        return null;
	    }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            //in the arrayList we add the messaged received from server
            arrayList.add(values[0]);
            // notify the adapter that the data set has changed. This means that new message received
            // from server was added to the list
            mAdapter.notifyDataSetChanged();
        }
        public long[] genVibratorPattern( float intensity, long duration )
        {
            float dutyCycle = Math.abs( ( intensity * 2.0f ) - 1.0f );
            long hWidth = (long) ( dutyCycle * ( duration - 1 ) ) + 1;
            long lWidth = dutyCycle == 1.0f ? 0 : 1;

            int pulseCount = (int) ( 2.0f * ( (float) duration / (float) ( hWidth + lWidth ) ) );
            long[] pattern = new long[ pulseCount ];

            for( int i = 0; i < pulseCount; i++ )
            {
                pattern[i] = intensity < 0.5f ? ( i % 2 == 0 ? hWidth : lWidth ) : ( i % 2 == 0 ? lWidth : hWidth );
            }

            return pattern;
        }
	}
}
