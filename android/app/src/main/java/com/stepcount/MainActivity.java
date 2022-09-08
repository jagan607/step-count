package com.stepcount;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.Bundle;

import android.preference.PreferenceManager;
import android.util.AttributeSet;

import android.view.View;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;


import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;



import org.jetbrains.annotations.NotNull;



public class MainActivity extends ReactActivity implements SensorEventListener {


  private long steps = 0;
  private TextView textViewSteps;
  private SensorManager sensorManager;
  private Intent intent;
  private static SharedPreferences sharedPreferences;
  private MutableLiveData<String> stepCount;




  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    textViewSteps = findViewById(R.id.textViewSteps);
    stepCount = new MutableLiveData<String>();


    sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    intent = new Intent(getApplicationContext(), NotificationService.class);
    intent.putExtra("steps", steps);

    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    textViewSteps.setText(String.format("%d", sharedPreferences.getLong("steps", 0)));
    StepCountModule.updateActivity(this);


    startService(intent);
  }

  @Override
  protected void onPause() {
    super.onPause();

    sharedPreferences.edit().putLong("steps", steps).apply();
  }

  @NotNull
  public static final String RECEIVER_TAG = "RECEIVER_TAG";



  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  @Override
  protected String getMainComponentName() {
    return "main";
  }


  @Override
  protected void onResume() {
    super.onResume();

    Sensor stepsSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

    if (stepsSensor == null) {
      Toast.makeText(this, "No Step Counter Sensor !", Toast.LENGTH_SHORT).show();
    } else {
      sensorManager.registerListener(this, stepsSensor, SensorManager.SENSOR_DELAY_UI);
    }
  }

  public MutableLiveData<String> getStepCount() {
    if (stepCount == null) {
      stepCount = new MutableLiveData<String>();
    }
    return stepCount;
  }

  /**
   * Returns the instance of the {@link ReactActivityDelegate}. There the RootView is created and
   * you can specify the renderer you wish to use - the new renderer (Fabric) or the old renderer
   * (Paper).
   */
  @Override
  protected ReactActivityDelegate createReactActivityDelegate() {
    return new MainActivityDelegate(this, getMainComponentName());
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
    return super.onCreateView(name, context, attrs);

  }

  @Override
  public void onSensorChanged(SensorEvent event) {

    Sensor sensor = event.sensor;
    float[] values = event.values;
    int value = -1;

    if (values.length > 0) {
      value = (int) values[0];
    }


    if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
      steps++;
      textViewSteps.setText(""+steps);
      intent.putExtra("steps", steps);
      stepCount.setValue(String.valueOf(steps));
      startService(intent);
    }

  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {

  }




  public static class MainActivityDelegate extends ReactActivityDelegate {
    public MainActivityDelegate(ReactActivity activity, String mainComponentName) {
      super(activity, mainComponentName);
    }

  }


}

