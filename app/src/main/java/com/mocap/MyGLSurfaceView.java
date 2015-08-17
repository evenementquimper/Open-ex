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
package com.mocap;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */

//com.mocap.MyGLSurfaceView
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
        setRenderer(mRenderer);

        mRenderer.setmCamerDist(-7);

        mSensorManager = (SensorManager) this.getContext().getSystemService(Context.SENSOR_SERVICE);

        // Render the view only when there is a change in the drawing data
        List<Sensor> msensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mMagneticField=mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;
    private float mPreviousZ;
    private double mPreviousD;
    private double mPrevious_doub_dx;
    private double mPrevious_doub_dy;

    float[] mAcc;
    float[] mGeomagnetic;
    float[] mLinear;
    long lastime=0;
    float[] linear_acceleration;

    private float[] CalibreGravity=null;
    private float[] CalibreR=new float[9];
    private float[] CalibreI=new float[9];
    private float[] CalibreOrientation=new float[3];
    int var=0;

    @Override
    public void onResume() {
        super.onResume();

        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mMagneticField, SensorManager.SENSOR_DELAY_UI);

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
        //mRenderer.clearVertex();
        mRenderer.setVertices(novertice);
        requestRender();

    }

    public void onChangeCam(int zoom){
        int cam_distance = mRenderer.getmCamerDist();
        cam_distance=cam_distance+zoom;
        mRenderer.setmCamerDist(cam_distance);
        //Log.i(TAG, "Erreur GL: " + gl.glGetError());
        requestRender();
    }

    public void onInitGravity()
    {
//Lecture du fichier d'initialisation
        //si vide :
        CalibreGravity=new float[3];
        CalibreGravity [0] = 0.0f;
        CalibreGravity [1] = 0.0f;
        CalibreGravity [2] = 0.0f;
//Ne plus bouger l'appareil
//debut d'initialisation
        onSensorsStart();


    }

    public void onRefresh()
    {
        mRenderer.refreshVertex();
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

            switch (e.getAction()) {

                case MotionEvent.ACTION_MOVE:

                    float dx = x - mPreviousX;
                    float dy = y - mPreviousY;
                    double doub_dz = 0.00;


                    double doub_dx = (double) dx;
                    double doub_dy = (double) dy;


                    // reverse direction of rotation above the mid-line
                    if (y > getHeight() / 2) {
                        dx = dx * -1;
                    }

                    // reverse direction of rotation to left of the mid-line
                    if (x < getWidth() / 2) {
                        dy = dy * -1;
                    }

                    float mAngle[] = mRenderer.getAngle();

                    mAngle[0] = mAngle[0] - (float) doub_dy * TOUCH_SCALE_FACTOR;
                    mAngle[1] = mAngle[1] + (float) doub_dx * TOUCH_SCALE_FACTOR;
                    mAngle[2] = 0.0f;//mAngle[2] + (float) doub_dy * TOUCH_SCALE_FACTOR;

                    mRenderer.setAngle(mAngle);

                    requestRender();
            }

            mPreviousX = x;
            mPreviousY = y;


            return true;

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] vert = mRenderer.getVertices();
        float global_linear_acceleration[] = new float[3];
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mAcc = event.values;
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;



        if (mAcc != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            float linear_acceleration [] = new float[3];
            boolean success = SensorManager.getRotationMatrix(R, I, mAcc, mGeomagnetic);
            if (success) {
                long deltatime=0;
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);

                if(CalibreGravity[0]==0.0f&&CalibreGravity[1]==0.0f&&CalibreGravity[2]==0.0f)
                {

                    CalibreR=R;
                    CalibreI=I;
                    CalibreOrientation=orientation;

                    CalibreGravity[0]=mAcc[0];//mAcc[0]*CalibreR[0]+mAcc[1]*CalibreR[3]+mAcc[2]*CalibreR[6];
                    CalibreGravity[1]=mAcc[1];//mAcc[0]*CalibreR[1]+mAcc[1]*CalibreR[4]+mAcc[2]*CalibreR[7];
                    CalibreGravity[2]=mAcc[2];//mAcc[0]*CalibreR[2]+mAcc[1]*CalibreR[5]+mAcc[2]*CalibreR[8];


                    //Log.i(TAG,"Acc lineaire X: "+mAcc[0]+" Y: "+mAcc[1]+" Z:"+mAcc[2]);
                    //Log.i(TAG,"GETorientation X: "+orientation[0]+" Y: "+orientation[1]+" Z:"+orientation[2]);
                    onSensorsPause();
                }

                if(lastime!=0 && CalibreGravity!=null) {

                    float[] angleChange = new float[3];
                   SensorManager.getAngleChange(angleChange, R, CalibreR);

                    //Log.i(TAG, "Change Angle X: "+angleChange[0]+" y: "+angleChange[1]+" z: "+angleChange[2]);

                    deltatime = event.timestamp - lastime;

                    float[] LocalGravity= new float [3];

                    for (int i=0;i<orientation.length;i++)
                    {
                        if (orientation[i]<0.01f)
                        {
                          orientation[i]=-1.0f;
                        }
                    }

                    try {
                        //LocalGravity[0] = CalibreGravity[0]*orientation[0];
                        //LocalGravity[1] = CalibreGravity[1]*orientation[1];
                        //LocalGravity[2] = CalibreGravity[2]*orientation[2];

                        //Log.i(TAG,"Local orientation: x: "+orientation[0]+" y: "+orientation[1]+" z: "+orientation[2]);
                        LocalGravity[0] = CalibreGravity[0]*R[0]+CalibreGravity[1]*R[3]+CalibreGravity[2]*R[6];
                        LocalGravity[1] = CalibreGravity[0]*R[1]+CalibreGravity[1]*R[4]+CalibreGravity[2]*R[7];
                        LocalGravity[2] = CalibreGravity[0]*R[2]+CalibreGravity[1]*R[5]+CalibreGravity[2]*R[8];
                    } catch (Exception e) {
                        Log.i(TAG, "Erreur calcul"+e);
                    }
                    //Log.i(TAG,"Acc brut X: "+mAcc[0]+" Y: "+mAcc[1]+" Z:"+mAcc[2]);
                    //Suppression de la gravitée -9.55 ou -9.807 calibr
                    //linear_acceleration[0]=mAcc[0]-CalibreGravity[0]*R[0]-CalibreGravity[1]*R[3]-CalibreGravity[2]*R[6];
                    //linear_acceleration[1]=mAcc[1]-CalibreGravity[0]*R[1]-CalibreGravity[1]*R[4]-CalibreGravity[2]*R[7];
                    //linear_acceleration[2]=mAcc[2]-CalibreGravity[0]*R[2]-CalibreGravity[1]*R[5]-CalibreGravity[2]*R[8];

                    //Log.i(TAG,"Local gravity X: "+LocalGravity[0]+" Y: "+LocalGravity[1]+" Z:"+LocalGravity[2]);
                    //linear_acceleration[0]=mAcc[0]*R[0]+mAcc[1]*R[3]+mAcc[2]*R[6]-LocalGravity[0];
                    //linear_acceleration[1]=mAcc[0]*R[1]+mAcc[1]*R[4]+mAcc[2]*R[7]-LocalGravity[1];
                    //linear_acceleration[2]=mAcc[0]*R[2]+mAcc[1]*R[5]+mAcc[2]*R[8]-LocalGravity[2];

                    linear_acceleration[0]=mAcc[0]-LocalGravity[0];
                    linear_acceleration[1]=mAcc[1]-LocalGravity[1];
                    linear_acceleration[2]=mAcc[2]-LocalGravity[2];


                    //calcul du vecteur linéaire dans le repere !!! LOCAL !!! c.a.d suivant les axes x,y,z du téléphone
                    float nanotosec = deltatime/1000000000f;

                    float linear_vect[]=new float [3];

                    linear_vect[0] = (linear_acceleration[0])*nanotosec;
                    linear_vect[1] = (linear_acceleration[1])*nanotosec;
                    linear_vect[2] = (linear_acceleration[2])*nanotosec;


                    if(linear_vect[0]>0.01|| linear_vect[1]>0.01||linear_vect[2]>0.01||linear_vect[0]<-0.01||linear_vect[1]<-0.01||linear_vect[2]<-0.01) {


                        float nVertices[] = new float[vert.length+3];


                        try {
                            for (int g = 0; g < vert.length; g++) {
                                nVertices[g] = vert[g];
                            }


                            if(deltatime >500000000)
                            {
                                nVertices[nVertices.length - 1] = nVertices[nVertices.length - 4];
                                nVertices[nVertices.length - 2] = nVertices[nVertices.length - 5];
                                nVertices[nVertices.length - 3] = nVertices[nVertices.length - 6];


                            }
                            else {
                                nVertices[nVertices.length - 1] = vert[vert.length - 1] + linear_vect[2];
                                nVertices[nVertices.length - 2] = vert[vert.length - 2] + linear_vect[1];
                                nVertices[nVertices.length - 3] = vert[vert.length - 3] + linear_vect[0];
                            }


                        } catch (Exception e) {
                            Log.i(TAG, "erreur ajout");
                        }
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
            }
        }
        requestRender();
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
