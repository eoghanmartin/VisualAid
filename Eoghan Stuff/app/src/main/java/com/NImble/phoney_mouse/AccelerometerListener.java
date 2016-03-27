package com.NImble.phoney_mouse;

/**
 * Created by eoghanmartin on 19/08/2014.
 */
public interface AccelerometerListener {

    public void onAccelerationChanged(float x, float y, float z);

    public void onShake(float force);

}