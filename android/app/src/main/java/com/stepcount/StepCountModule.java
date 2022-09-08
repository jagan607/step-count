package com.stepcount;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.lang.ref.WeakReference;
import java.util.Objects;


public class StepCountModule extends ReactContextBaseJavaModule {
    StepCountModule(ReactApplicationContext context) {
        super(context);
    }

    private static WeakReference<Activity> mActivityRef;

    public static void updateActivity(Activity activity) {
        mActivityRef = new WeakReference<Activity>(activity);
    }


    @NonNull
    @Override
    public String getName() {
        return "StepModule";
    }

    @ReactMethod
    public void createStepCountEvent(Promise promise) {

        // Create the observer which updates the UI.
        final Observer<String> stepObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String steCount) {
                try {
                    promise.resolve(steCount);
                } catch (Exception e) {
                    promise.reject("Error", e);
                }

            }
        };

        MainActivity mainActivity = (MainActivity) mActivityRef.get();
        mainActivity.getStepCount().observe((LifecycleOwner) Objects.requireNonNull(getCurrentActivity()), stepObserver);



        Intent intent = new Intent(getReactApplicationContext(),MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getReactApplicationContext().startActivity(intent);
    }



}