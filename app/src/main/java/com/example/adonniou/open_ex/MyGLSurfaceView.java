/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.adonniou.open_ex;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView implements SensorEventListener {

    static private Sensor mAccelerometer;
    //static private Sensor mGravity;
    static private Sensor mGyroscope;
    static private Sensor mLinearAcceleration;
    static private Sensor mRotationVector;
    static private Sensor mOrientation;
    static private Sensor mMagneticField;
    static private Sensor mProximity;
    static private Sensor mPressure;
    static private Sensor mLight;
    SensorManager mSensorManager;

    private static Map<SensorEvent, String> EventTable = new HashMap<SensorEvent, String>();

    private static final String TAG = "EDroide";

    private MyGLRenderer mRenderer;
    private MyGLRenderer myGLRenderer;

    public MyGLSurfaceView(Context context) {
        super(context);
        EventTable.clear();

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer();
        setRenderer(mRenderer);

        mRenderer.setmCamerDist(-7);

        mSensorManager = (SensorManager) this.getContext().getSystemService(Context.SENSOR_SERVICE);
        // Render the view only when there is a change in the drawing data
        List<Sensor> msensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticField=mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        //mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //mLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        //mRotationVector = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        //mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        // mOrientation= mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        // float m_result[]=mSensorManager.getOrientation(m_rotationMatrix,m_orientation);

        //Log.i(TAG,"azimuth: "+m_result[0]);
        //Log.i(TAG,"pith: "+m_result[1]);
        //Log.i(TAG,"roll_angle: "+m_result[2]);

        //mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        //mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        //mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        for (int k=0;k<msensorList.size();k++){
           // Log.i(TAG, "Sensor: " + msensorList.get(k).getName());
        }

           //SensorManager.SENSOR_DELAY_UI
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mMagneticField, SensorManager.SENSOR_DELAY_UI);
        //mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_UI);//marche pas sur ma tab
        //mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_UI);//marche pas sur ma tab
        //mSensorManager.registerListener(this, mLinearAcceleration, SensorManager.SENSOR_DELAY_UI);//marche pas sur ma tab
        //mSensorManager.registerListener(this, mRotationVector, SensorManager.SENSOR_DELAY_UI);//marche pas sur ma tab
        //mSensorManager.registerListener(this, mOrientation,SensorManager.SENSOR_DELAY_UI);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    float[] mGravity;
    float[] mGeomagnetic;

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mMagneticField, SensorManager.SENSOR_DELAY_UI);
        //mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_UI);//marche pas sur tab
        //mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_UI);//marche pas sur tab
        //mSensorManager.registerListener(this, mLinearAcceleration, SensorManager.SENSOR_DELAY_UI);//marche pas sur tab
        //mSensorManager.registerListener(this, mRotationVector, SensorManager.SENSOR_DELAY_UI);//marche pas sur tab
        //mSensorManager.registerListener(this, mOrientation,SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);

    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, we are only
        // interested in events where the touch position changed.




        float x = e.getX();
        float y = e.getY();

        //Log.i(TAG,"Coord x: "+x);
        //Log.i(TAG,"Coord y: "+y);


        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                float[] vertices = mRenderer.getVertices();
                int tailletab=vertices.length;

                float[] vertices2 = new float[vertices.length];

                for (int i=0;i<vertices.length;i++)
                {
                   // Log.i(TAG,i+" Vertice: "+vertices[i]);
                    if(vertices[i]<=0)
                    {
                        vertices2[i]=vertices[i]+0.1f;
                    }
                    if(vertices[i]>0)
                    {
                        vertices2[i]=vertices[i]-0.11f;
                    }

                }
                vertices=vertices2;


                //myGLRenderer = new MyGLRenderer();
                //myGLRenderer.setVertices(vertices2);
                //setRenderer(myGLRenderer);
                mRenderer.setVertices(vertices2);

                //this.setRenderer(mRenderer);

                //mRenderer.onDrawFrame();
//this.refreshDrawableState();
                // reverse direction of rotation above the mid-line
                if (y > getHeight() / 2) {
                    dx = dx * -1 ;
                }

                // reverse direction of rotation to left of the mid-line
                if (x < getWidth() / 2) {
                    dy = dy * -1 ;
                }
                mRenderer.setMtouchx(x / 1000);
                mRenderer.setMtouchy(y / 1000);
                mRenderer.setAngle(
                        mRenderer.getAngle() +
                                ((dx + dy) * TOUCH_SCALE_FACTOR));  // = 180.0f / 320


                requestRender();
        }

        mPreviousX = x;
        mPreviousY = y;

        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Log.i(TAG, "Sensor name: " + event.sensor.getName());
        //Log.i(TAG, "Time_event: " + event.timestamp);

        //switch (event.sensor.getType()) {
            //case Sensor.TYPE_ACCELEROMETER:
               // System.arraycopy(event.values, 0, 0, 0, 3);
              //  break;
            //case Sensor.TYPE_MAGNETIC_FIELD:
              //  System.arraycopy(event.values, 0, 0, 0, 3);
              //  break;
        //récupérer le tableau de coordonnées
        float [] vert = mRenderer.getVertices();
        float [] vert2 = new float [vert.length+3];
        Log.i(TAG, "Long vertices: "+vert.length);
        vert2=vert;
        //Utiliation des valeurs du dernier point
        float x_vert = vert [vert.length-2];
        float y_vert = vert [vert.length-1];
        float z_vert = vert [vert.length];

        //Calcul du nouveau point
        vert2[vert.length+1]=x_vert+1.0f;
        vert2[vert.length+2]=y_vert+1.0f;
        vert2[vert.length+3]=z_vert+1.0f;

        //Ajouter au vertice le point calcul
        mRenderer.setVertices(vert2);

        //Ajouter le sensoreven à la table des event
        mRenderer.getEventTable().put(event,"");


            //default:
              //  return;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {

                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);

                //recup azimut
                float azimut = orientation[0]; // orientation contains: azimut, pitch and roll
               // Log.i(TAG, "Azimut: "+azimut);
                //Log.i(TAG,"Pitch: "+orientation[1]);
                //Log.i(TAG,"roll: "+orientation[2]);
            }
        }

        //}
//Log.i(TAG, "Event timestamp: "+event.timestamp); //ex:1249672200000 nanosecondes soit 124,967.. secondes
       // Log.i(TAG, "grav"+event.values[2]);
        float x=event.values[0];
        float y=event.values[1];
        float z=event.values[2];
        int x1=Math.round(x);

        int distance =mRenderer.getmCamerDist();

        //  mRenderer.setmCamerDist(distance+x1);


        mRenderer.setAngle(
                mRenderer.getAngle() +
                        ((z) * TOUCH_SCALE_FACTOR));  // = 180.0f / 320
        requestRender();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
