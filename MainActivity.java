package com.example.jeffery.sheep;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.HashMap;


public class MainActivity extends ActionBarActivity implements SensorEventListener {

    ImageButton sheepButton;
    Button scaredButton;

    public static final int S1 = R.raw.baa;
    private static SoundPool soundPool;
    private static HashMap soundPoolMap;

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sheepButton = (ImageButton) findViewById(R.id.sheepButton);
        scaredButton = (Button) findViewById(R.id.scaredButton);
        setUpButtonListeners();

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
    }

    public static void initSounds(Context context) {

        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);

        soundPoolMap = new HashMap(1);

        soundPoolMap.put(S1, soundPool.load(context, com.example.jeffery.sheep.R.raw.baa, 1));
    }

    public static void playSound(Context context, int soundID) {

        if(soundPool == null || soundPoolMap == null){

            initSounds(context);

        }

        float volume = 1;

        soundPool.play((Integer) soundPoolMap.get(soundID), volume, volume, 1, 0, 1f);

    }

    private void setUpButtonListeners() {
        sheepButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                playSound(getApplicationContext(), S1);
                Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vib.vibrate(500);
            }
        });

        scaredButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                sheepButton.setVisibility(v.VISIBLE);
                scaredButton.setVisibility(v.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.example.jeffery.sheep.R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {

                    sheepButton.setVisibility(View.INVISIBLE);
                    scaredButton.setVisibility(View.VISIBLE);

                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
