package edu.harding.acornmaze;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MazeRunnable implements Runnable, SensorEventListener {

    private Context mContext;
    private Maze mMaze;
    private MazeView mMazeView;
    //Not needed?
    //private Sensor mAccelerometer;
    private float mCurrentXAccel = 0;
    private float mCurrentYAccel = 0;
    private boolean mIsPaused = false;
    private boolean mStop = false;
    
    public MazeRunnable(Context context, Maze maze, MazeView mazeView) {
        mContext = context;
        mMaze = maze;
        mMazeView = mazeView;
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }
    
    @Override
    public void run() {
        while (!mStop) {
            mMaze.moveAvatar(mCurrentXAccel, mCurrentYAccel);
            mMazeView.postInvalidate();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (mIsPaused) {
                Thread.yield();
            }
        }
    }
    
    public void setPaused(boolean isPaused) {
        mIsPaused = isPaused;
    }
    
    public void stop() {
        mStop = true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mCurrentXAccel = -event.values[0]/(SensorManager.GRAVITY_EARTH*5);
        mCurrentYAccel = event.values[1]/(SensorManager.GRAVITY_EARTH*5);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
