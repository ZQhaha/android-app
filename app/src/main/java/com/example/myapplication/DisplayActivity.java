package com.example.myapplication;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class DisplayActivity extends AppCompatActivity {
    private static final String TAG = "";
    private Predictor predictor = new Predictor();
    private final int sampleRate = 40;
    private final float T = 1;//测试周期s
    Timer timer = null;
    TimerTask timerTask = null;
    Handler handler = null;
    private SensorEventListener sensorEventListener;
    SensorManager sensorManager;
    Sensor accSensor, gyroSensor;
    private WaveView waveViewAcc;
    private WaveView waveViewGyro;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        waveViewAcc = findViewById(R.id.wave_view1);
        waveViewGyro = findViewById(R.id.wave_view2);
        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                switch (sensorEvent.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        waveViewAcc.addDataArray(sensorEvent.values);
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        waveViewGyro.addDataArray(sensorEvent.values);
                        break;
                }

            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
                Log.d(TAG, "onAccuracyChanged:" + sensor.getType() + "->" + i);
            }
        };
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.i("seek_bar progress is", i + "");
                waveViewAcc.setWaveLineWidth(i + 6);
                waveViewGyro.setWaveLineWidth(i + 6);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        predictor.init(this, getString(R.string.model_name));

    }

    public void startPredictor(View view) {
        if (timer != null) {
            return;
        }
        sensorManager.registerListener(sensorEventListener, accSensor, (int) (1e6 / sampleRate));
        sensorManager.registerListener(sensorEventListener, gyroSensor, (int) (1e6 / sampleRate));
        handler = new Handler();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                predictor.setData(waveViewAcc.getDataArray(), waveViewGyro.getDataArray());
                String out = predictor.run();
                handler.post(() -> {
                    TextView text = findViewById(R.id.out_View);
                    text.setText(out);
                    text.setVisibility(View.VISIBLE);
                });
            }
        };
        //500表示调用schedule方法后等待500ms后调用run方法，50表示以后调用run方法的时间间隔
        timer.schedule(timerTask, 500, (int) (T * 1000));
    }

    public void EndPredictor(View view) {
        release();
    }


    @Override
    protected void onPause() {
        super.onPause();
        release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
    }

    private void release() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        if (null != timerTask) {
            timerTask.cancel();
            timerTask = null;
        }
        TextView text = findViewById(R.id.out_View);
        text.setText(R.string.out_view);
        text.setVisibility(View.INVISIBLE);
        sensorManager.unregisterListener(sensorEventListener);
        waveViewAcc.clearDataArray();
        waveViewGyro.clearDataArray();
    }
}