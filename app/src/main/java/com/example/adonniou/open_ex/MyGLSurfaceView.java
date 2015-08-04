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
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
    static private Sensor mGravity;
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



    private float[] sVertices;

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer();
        //this.setRenderMode(RENDERMODE_WHEN_DIRTY);
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
            Log.i(TAG, "Sensor: " + msensorList.get(k).getName());
        }

           //SensorManager.SENSOR_DELAY_UI
       //Ne pas enregistrer le listener au debut
        //mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        //mSensorManager.registerListener(this, mMagneticField, SensorManager.SENSOR_DELAY_UI);
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
    private float mPreviousZ;
    private double mPreviousD;

    float[] mAcc;
    float[] mGeomagnetic;
    float[] mLinear;
    long lastime=0;
    //float[] gravity={0,0,0};
    float[] linear_acceleration;

    int var=0;

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

    public void onSensorsStart(){
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mMagneticField, SensorManager.SENSOR_DELAY_UI);
    }


    public void onSensorsPause(){
        mSensorManager.unregisterListener(this);
    }

    public void onNoVertices(){
        float[] novertice={0.0f,0.0f,0.0f};
        mRenderer.clearVertex();
        mRenderer.setVertices(novertice);
        requestRender();

    }

    public void onChangeCam(int zoom){
        int cam_distance = mRenderer.getmCamerDist();
        cam_distance=cam_distance+zoom;
        mRenderer.setmCamerDist(cam_distance);
        requestRender();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, we are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();


int action = e.getAction();
int historySize = e.getHistorySize();
        if(e.getPointerCount()>1&&e.getPointerCount()<3)
        {
            //identité du pointer et de l'evenement
            int actionPointerId = action & MotionEvent.ACTION_POINTER_INDEX_MASK;
            int actionEvent= action & MotionEvent.ACTION_MASK;
            int pointerIndex = e.findPointerIndex(actionPointerId);

            //Log.i(TAG,"Action pointer id: "+actionPointerId);
            //Log.i(TAG,"Action history size: "+historySize);

            float [] x_pos=new float[3];
            float [] y_pos=new float[3];
            float [] delta=new float[3];
            double doub_delta=0.00;

            for (int i=0;i<historySize;i++) {

                if (i < 2) {
                    //coordonnées x et y de chaques points
                    x_pos[i] = e.getHistoricalX(pointerIndex, i);
                    //Log.i(TAG, "Action pointer x: " + x_pos);
                    y_pos[i] = e.getHistoricalY(pointerIndex, i);
                  //  Log.i(TAG, "Action pointer y: " + y_pos);
                }
                else
                {

                }
            }
            try {
                //Log.i(TAG,"x1: "+x_pos[0]+" x2: "+x_pos[1]);

                if(x_pos[0]!=0.00d&&x_pos[1]!=0.00d&&y_pos[0]!=0.00d&&y_pos[1]!=0.00d) {
                    delta[0] = Math.abs(x_pos[0] - x_pos[1]);
                    delta[1] = Math.abs(y_pos[0] - y_pos[1]);


                    double doub__x = (double) delta[0];
                    double doub__y = (double) delta[1];

//distance entre les 2 points
                    doub_delta = Math.sqrt(Math.pow(doub__x, 2) + Math.pow(doub__y, 2));
                    //Log.i(TAG, "Distance: " + doub_delta);
                }
                else
                {

                }
            } catch (Exception e1) {
                Log.i(TAG, "Erreur : "+e1);
            }
            if(mPreviousD!=0.00d)
            {
                double delta_distance=doub_delta-mPreviousD;
                int cam_distance = mRenderer.getmCamerDist();
          if(delta_distance<0)
                {
                    cam_distance=cam_distance+1;
                }
                if(delta_distance>=0)
                {
cam_distance=cam_distance-1;
                }
                Log.i(TAG, "Cam_Dist: "+cam_distance+", distanceD: "+delta_distance);

                //cam_distance = cam_distance + (int)delta_distance/10;
                    //augmentation de la distance de la caméra
                mRenderer.setmCamerDist(cam_distance);
                requestRender();
            }
            mPreviousD=doub_delta;
        }

        else {

            switch (e.getAction()) {

                case MotionEvent.ACTION_MOVE:

                    float dx = x - mPreviousX;
                    float dy = y - mPreviousY;
                    double doub_dz = 0.00;


                    double doub_dx = (double) dx;
                    double doub_dy = (double) dy;


                    doub_dz = Math.sqrt(Math.pow(doub_dx, 2) + Math.pow(doub_dy, 2));

                    // reverse direction of rotation above the mid-line
                    if (y > getHeight() / 2) {
                        dx = dx * -1;
                    }

                    // reverse direction of rotation to left of the mid-line
                    if (x < getWidth() / 2) {
                        dy = dy * -1;
                    }

                    float mAngle[] = mRenderer.getAngle();

                    mAngle[0] = mAngle[0] + (float) doub_dx * TOUCH_SCALE_FACTOR;
                    mAngle[1] = 0.0f;//mAngle[1] + (float) doub_dy * TOUCH_SCALE_FACTOR;
                    mAngle[2] = 0.0f;//mAngle[2] + (float) doub_dz * TOUCH_SCALE_FACTOR;

                    mRenderer.setAngle(mAngle);

                    requestRender();

            }

            mPreviousX = x;
            mPreviousY = y;
        }

            return true;

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float [] vert = mRenderer.getVertices();
        float global_linear_acceleration [] = new float[3];
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                long global_deltatime=0;

                if(lastime!=0) {
                    global_deltatime = event.timestamp - lastime;

                    mAcc = event.values;

                    //calcul de l'acceleration lineaire dans le repère global - la gravité en z
                    global_linear_acceleration[0] = mAcc[0];
                    global_linear_acceleration[1] = mAcc[1];
                    global_linear_acceleration[2] = mAcc[2] - 9.807f;

                    float global_nanotosec = global_deltatime / 1000000000f;

                    float global_linear_vect[] = new float[3];
                    global_linear_vect[0] = global_linear_acceleration[0] * global_nanotosec;
                    global_linear_vect[1] = global_linear_acceleration[1] * global_nanotosec;
                    global_linear_vect[2] = global_linear_acceleration[2] * global_nanotosec;

                    //Suppression de la gravitée -9.55 ou -9.807
                    //linear_acceleration[0]=mAcc[0]-9.807f*R[6];
                    //linear_acceleration[1]=mAcc[1]-9.807f*R[7];
                    //linear_acceleration[2]=mAcc[2]-9.807f*R[8];

                    if (global_linear_vect[0] > 0.005 || global_linear_vect[1] > 0.005 || global_linear_vect[2] > 0.005 || global_linear_vect[0] < -0.005 || global_linear_vect[1] < -0.005 || global_linear_vect[2] < -0.005) {

                        float global_nVertices[] = new float[vert.length + 3];


                        try {
                            for (int g = 0; g < vert.length; g++) {
                                global_nVertices[g] = vert[g];
                            }

                            Log.i(TAG, "linear x," + global_linear_vect[0] + " linear y: " + global_linear_vect[1] + ", Linear vect:z " + global_linear_vect[2]);

                            global_nVertices[global_nVertices.length - 1] = vert[vert.length - 1] + global_linear_vect[2];
                            global_nVertices[global_nVertices.length - 2] = vert[vert.length - 2] + global_linear_vect[1];
                            global_nVertices[global_nVertices.length - 3] = vert[vert.length - 3] + global_linear_vect[0];

                        } catch (Exception e) {
                            Log.i(TAG, "erreur ajout");
                        }
                        //Log.i(TAG, "Log vert: " + nVertices.length + " x," + nVertices[nVertices.length - 3] + " y," + nVertices[nVertices.length - 2]+ ", Linear vect:z " + nVertices[nVertices.length - 1] );
                        sVertices = global_nVertices;
                        mRenderer.setVertices(global_nVertices);
                    } else {

                    }
                }
                else
                {

                }
                lastime=event.timestamp;
            }


/*

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
  */

    /*
        if (mAcc != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            float linear_acceleration [] = new float[3];
            boolean success = SensorManager.getRotationMatrix(R, I, mAcc, mGeomagnetic);
            if (success) {
                long deltatime=0;

                if(lastime!=0) {
                    deltatime = event.timestamp - lastime;

                    //Suppression de la gravitée -9.55 ou -9.807
                    linear_acceleration[0]=mAcc[0]-9.807f*R[6];
                    linear_acceleration[1]=mAcc[1]-9.807f*R[7];
                    linear_acceleration[2]=mAcc[2]-9.807f*R[8];

                    //calcul du vecteur linéaire dans le repere !!! LOCAL !!! c.a.d suivant les axes x,y,z du téléphone
                    float nanotosec = deltatime/1000000000f;

                    float linear_vect[]=new float [3];
                    linear_vect[0] = linear_acceleration[0]*nanotosec;
                    linear_vect[1] = linear_acceleration[1]*nanotosec;
                    linear_vect[2] = linear_acceleration[2]*nanotosec;


                    if(linear_vect[0]>0.005|| linear_vect[1]>0.005||linear_vect[2]>0.005||linear_vect[0]<-0.005||linear_vect[1]<-0.005||linear_vect[2]<-0.005) {

                        float nVertices[] = new float[vert.length+3];


                        try {
                            for (int g = 0; g < vert.length; g++) {
                                nVertices[g] = vert[g];
                            }

                            Log.i(TAG, "linear x," +linear_vect[0]+ " linear y: "+linear_vect[1]+", Linear vect:z " +linear_vect[2]);

                            nVertices[nVertices.length - 1] = vert[vert.length-1]+linear_vect[2];
                            nVertices[nVertices.length - 2] = vert[vert.length-2]+linear_vect[1];
                            nVertices[nVertices.length - 3] = vert[vert.length-3]+linear_vect[0];

                            //nVertices[nVertices.length - 1] = nVertices[nVertices.length - 4]+linear_vect[2];
                            //nVertices[nVertices.length - 2] = nVertices[nVertices.length - 5]+linear_vect[1];
                            //nVertices[nVertices.length - 3] = nVertices[nVertices.length - 6]+linear_vect[0];


                            //nVertices[nVertices.length - 4] = vert[vert.length-1]+linear_vect[0];
                            //nVertices[nVertices.length - 5] = vert[vert.length-2]+linear_vect[1];
                            //nVertices[nVertices.length - 6] = vert[vert.length-3]+linear_vect[2];

                        } catch (Exception e) {
                            Log.i(TAG, "erreur ajout");
                        }
                        //Log.i(TAG, "Log vert: " + nVertices.length + " x," + nVertices[nVertices.length - 3] + " y," + nVertices[nVertices.length - 2]+ ", Linear vect:z " + nVertices[nVertices.length - 1] );

                        sVertices=nVertices;
                        mRenderer.setVertices(nVertices);
                    }
                    else
                    {

                    }
                }
                else
                {

                }
                lastime=event.timestamp;

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
        //float x=event.values[0];
        //float y=event.values[1];
        //float z=event.values[2];
        //int x1=Math.round(x);

        //int distance =mRenderer.getmCamerDist();

        //  mRenderer.setmCamerDist(distance+x1);


        //mRenderer.setAngle(
          //      mRenderer.getAngle() +
            //            ((z) * TOUCH_SCALE_FACTOR));  // = 180.0f / 320


        //requestRender();
*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public float[] getsVertices() {
        return sVertices;
    }

    public void setsVertices(float[] sVertices) {
        this.sVertices = sVertices;
    }


}
