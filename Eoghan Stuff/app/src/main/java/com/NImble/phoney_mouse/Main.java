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
import android.widget.Toast;

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

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accSen = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

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

	public class connectTask extends AsyncTask<String,String,TCPClient> {
	    @Override
	    protected TCPClient doInBackground(String... message) {
		    mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
	            @Override
	            public void messageReceived(String message) {
	                publishProgress(message);

                    final Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

                    Log.d("Message Recieved: ", message);

                    String[] parts = message.split(":");
                    String locationArea = parts[0];
                    if(parts.length > 1) {
                        String[] coords = parts[1].split(",");
                        int y_value = Integer.parseInt(coords[1]);
                        Log.d(coords[0], coords[1]);

                        long[] pattern = genVibratorPattern(1.0f, 2000);

                        if (locationArea.equals("left")) {
                            pattern = genVibratorPattern(1.0f, 500);
                            // Left height is at 960
                            int left_height = 960;
                            if (y_value < (left_height / 3)) {
                                for (int i =0;i<2;i++) {
                                    vib.vibrate(pattern, -1);
                                    try {
                                        Log.d("Vibration wait: ", "Waiting between vibrations.");
                                        Thread.sleep(700);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (y_value < ((left_height / 3) * 2)) {
                                for (int i =0;i<1;i++) {
                                    vib.vibrate(pattern, -1);
                                    try {
                                        Thread.sleep(700);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                vib.vibrate(pattern, -1);
                            }
                        }
                        if (locationArea.equals("right")) {
                            pattern = genVibratorPattern(0.5f, 500);
                            // Right height is at 960
                            int right_height = 960;
                            if (y_value < (right_height / 3)) {
                                for (int i =0;i<2;i++) {
                                    vib.vibrate(pattern, -1);
                                    try {
                                        Thread.sleep(700);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (y_value < ((right_height / 3) * 2)) {
                                for (int i =0;i<1;i++) {
                                    vib.vibrate(pattern, -1);
                                    try {
                                        Thread.sleep(700);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                vib.vibrate(pattern, -1);
                            }
                        }
                        if (locationArea.equals("front")) {
                            pattern = genVibratorPattern(1.0f, 1000);
                            // Front height is at 640
                            int front_height = 640;
                            if (y_value < (front_height / 3)) {
                                for (int i =0;i<2;i++) {
                                    vib.vibrate(pattern, -1);
                                    try {
                                        Thread.sleep(700);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (y_value < ((front_height / 3) * 2)) {
                                for (int i =0;i<1;i++) {
                                    vib.vibrate(pattern, -1);
                                    try {
                                        Thread.sleep(700);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                vib.vibrate(pattern, -1);
                            }
                        }
                        if (locationArea.equals("top")) {
                            pattern = genVibratorPattern(0.5f, 1000);
                            // Top height is at 320
                            int top_height = 320;
                            if (y_value < (top_height / 3)) {
                                for (int i =0;i<2;i++) {
                                    vib.vibrate(pattern, -1);
                                    try {
                                        Thread.sleep(700);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else if (y_value < ((top_height / 3) * 2)) {
                                for (int i =0;i<1;i++) {
                                    vib.vibrate(pattern, -1);
                                    try {
                                        Thread.sleep(700);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                vib.vibrate(pattern, -1);
                            }
                        } else {
                            vib.vibrate(pattern, -1);
                        }
                    }
	            }
	        });
	        mTcpClient.run();
	        return null;
	    }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            arrayList.add(values[0]);
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
