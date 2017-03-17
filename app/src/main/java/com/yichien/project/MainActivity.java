package com.yichien.project;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    static final int MIN_TIME = 5000;
    static final float MIN_DIST = 5;
    LocationManager mgr;
    TextView txv;
    TextView textInfo, textX, textY, textZ;


    SensorManager sensorManager;
    boolean accelerometerPresent;
    Sensor accelerometerSensor;



    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txv=(TextView)findViewById(R.id.txv);
        mgr=(LocationManager)getSystemService(LOCATION_SERVICE);


        textInfo = (TextView)findViewById(R.id.info);
        textX = (TextView)findViewById(R.id.textx);
        textY = (TextView)findViewById(R.id.texty);
        textZ = (TextView)findViewById(R.id.textz);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

        if(sensorList.size() > 0){
            accelerometerPresent = true;
            accelerometerSensor = sensorList.get(0);

        }
        else{
            accelerometerPresent = false;
        }

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        String best=mgr.getBestProvider(new Criteria(),true);
        if(best!=null){
            txv.setText("取得定位資訊中...");
            mgr.requestLocationUpdates(best,MIN_TIME,MIN_DIST,this);
        }
        else
            txv.setText("請確認已開啟定位功能");

        if(accelerometerPresent){
            sensorManager.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Toast.makeText(this, "Register accelerometerListener", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected  void onPause(){
        super.onPause();
        mgr.removeUpdates(this);
    }

    public void onLocationChanged(Location location){
        String str="定位提供者:"+location.getProvider();
        str+=String.format("\n緯度:%.5f\n經度:%.5f\n高度:%.2f公尺",
                location.getLatitude(),
                location.getLongitude(),
                location.getAltitude());
        txv.setText(str);
    }


    @Override
    public void onProviderDisabled(String provider){}

    @Override
    public void onProviderEnabled(String provider){}

    @Override
    public void onStatusChanged(String provider,int status,Bundle extras){}

    public void setup(View v){
        Intent it=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(it);
    }
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();

        if(accelerometerPresent){
            sensorManager.unregisterListener(accelerometerListener);
            Toast.makeText(this, "Unregister accelerometerListener", Toast.LENGTH_LONG).show();
        }
    }

    private SensorEventListener accelerometerListener = new SensorEventListener(){

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub
            textX.setText("X: " + String.valueOf(event.values[0]));
            textY.setText("Y: " + String.valueOf(event.values[1]));
            textZ.setText("Z: " + String.valueOf(event.values[2]));
            if (Math.abs(event.values[0])+Math.abs(event.values[1])+Math.abs(event.values[2])>18){
                textInfo.setText("shakingggg");
            }
            else
                textInfo.setText("");
        }
    };
}

