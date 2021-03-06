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
import android.hardware.SensorEvent;
import android.opengl.GLU;
import android.util.DisplayMetrics;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class
 * must override the OpenGL ES drawing lifecycle methods:
 * <ul>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */

//implements MyGLSurfaceView.Renderer

public class MyGLRenderer implements MyGLSurfaceView.Renderer {

    Context context;

    private OBJParser parser;
    private TDModel model;

    private Grille mGrille;
    private float []mAngle = new float[3];
    private float mtouchx;
    private float mtouchy;
    private int mCamerDist;
    private Repere mRepere;
    private DisplayMetrics ecran = new DisplayMetrics();

    private int DisWidth=250 ;
    private int DisHeight=400;

    private static final String TAG = "EDroide";

    private static float anglePyramid = 0; // Rotational angle in degree for pyramid (NEW)
    private static float angleCube = 45f;    // Rotational angle in degree for cube (NEW)
    private static float speedPyramid = 2.0f; // Rotational speed for pyramid (NEW)
    private static float speedCube = -1.5f;   // Rotational speed for cube (NEW)

    private static Map<SensorEvent, String> EventTable = new HashMap<SensorEvent, String>();

    private float x=1.0f;

    private float[] mVertices=null;

    private float[] vertices_init = {
            0.0f, 0.0f, 0.0f
    };

    private float[][] colors = {  // Colors of the 6 faces
            {1.0f, 0.5f, 0.0f, 1.0f},  // 0. orange
            {1.0f, 0.0f, 1.0f, 1.0f},  // 1. violet
            {0.0f, 1.0f, 0.0f, 1.0f},  // 2. green
            {0.0f, 0.0f, 1.0f, 1.0f},  // 3. blue
            {1.0f, 0.0f, 0.0f, 1.0f},  // 4. red
            {1.0f, 1.0f, 0.0f, 1.0f},   // 5. yellow
    };

    private int numFaces = 6;

    private FloatBuffer vertexBuffer;  // Buffer for vertex-array

    private float[] RepMatrix = {0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f};

    private FloatBuffer RepMatrixBuffer;

    private ByteBuffer mbb = ByteBuffer.allocateDirect(100);

    private ByteBuffer vbb = ByteBuffer.allocateDirect(100000);

    private ByteBuffer dlb = ByteBuffer.allocateDirect(
            // (# of coordinate values * 2 bytes per short)
            100000);

    public float[] getRepMatrix() {
        return RepMatrix;
    }

    public void setRepMatrix(float[] repMatrix) {
        RepMatrix = repMatrix;
        mbb.order(ByteOrder.nativeOrder());
        RepMatrixBuffer = mbb.asFloatBuffer();
        RepMatrixBuffer.put(RepMatrix);
        RepMatrixBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the background frame color

        //Log.i(TAG, "Surface Created");
        gl.glClearColor(0.192f, 0.192f, 0.192f, 0.0f);  // Set color's clear-value to black
        gl.glClearDepthf(1.0f);            // Set depth's clear-value to farthest
        gl.glEnable(GL10.GL_DEPTH_TEST);   // Enables depth-buffer for hidden surface removal, Ne change rien
        gl.glDepthFunc(GL10.GL_LEQUAL);    // The type of depth testing to do , peut etre mieux sans
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);  // nice perspective view, peut etre mieux sans
        gl.glShadeModel(GL10.GL_SMOOTH);   // Enable smooth shading of color , Ne change rien
        gl.glDisable(GL10.GL_DITHER);      // Disable dithering for better performance , Ne change rien

        gl.glStencilFunc(GL10.GL_ALWAYS, 1, 1);
        gl.glStencilOp(0, 0, GL10.GL_REPLACE);

               // Setup vertex-array buffer. Vertices in float. An float has 4 bytes

        vbb.order(ByteOrder.nativeOrder()); // Use native byte order
        vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float
        vertexBuffer.put(vertices_init);         // Vertices change
        vertexBuffer.position(0);           // Rewind


        // Enabled the vertex buffer for writing and to be used during rendering.
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        mGrille=new Grille();

    }



    @Override
    public void onDrawFrame(GL10 gl) {
        //Log.i(TAG, "Surface Ondraw");
        //gl.glLoadMatrixf(RepMatrixBuffer);
        gl.glStencilFunc(GL10.GL_ALWAYS, 1, 1);
        gl.glStencilOp(0, 0, GL10.GL_REPLACE);
        // Draw background color
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        //gl.glMatrixMode(GL10.GL_PROJECTION); // Change PROJECTION matrix
        gl.glLoadIdentity();

        float aspect_ratio = (float) DisWidth / DisHeight;
        GLU.gluPerspective(gl, 67, aspect_ratio, 1, 100);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        // Set GL_MODELVIEW transformation mode
        gl.glMatrixMode(GL10.GL_MODELVIEW);

        gl.glLoadIdentity();   // reset the matrix to its default state

        // When using GL_MODELVIEW, you must set the view point

        GLU.gluLookAt(gl, 0, 0, mCamerDist, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        mGrille.draw(gl, mAngle);

        gl.glRotatef(mAngle[1], 0f, mAngle[1], 0f);
        gl.glRotatef(mAngle[0], mAngle[0], 0f, 0f);

        gl.glShadeModel(GL10.GL_FLAT);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, 1);


        if (mVertices!=null)
        {

        }
        else {
            mVertices=vertices_init;

        }

        if(vertexBuffer.capacity()<mVertices.length/4)
        {


            vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float


            try {
                vbb = ByteBuffer.allocateDirect(mVertices.length * 4);

                //if(mVertices[])
                vertexBuffer.put(mVertices);
            } catch (Exception e) {
                Log.i(TAG,"erreur put"+e);
            }

        }
        else
        {

        }
      //  Log.i(TAG, "Vertex Buffer<mVertices: " + mVertices.length+"Capacity: "+vertexBuffer.capacity());
        vertexBuffer.position(0);           // Rewind
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);//indispensable
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

        gl.glColor4f(colors[2][0], colors[2][1], colors[2][2], colors[2][3]);
        gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, mVertices.length); //affige plusieurs lignes
        gl.glLineWidth(3);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        Log.i(TAG, "Surface Changed");
        // Adjust the viewport based on geometry changes
        // such as screen rotations


        vertexBuffer.clear();

        if (mVertices!=null)
        {

        }
        else {
            mVertices=vertices_init;

        }

        vertexBuffer.put(mVertices);         // vertex change
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

        gl.glViewport(0, 0, width, height);

        // make adjustments for screen ratio
        float ratio = (float) width / height;
        gl.glMatrixMode(GL10.GL_PROJECTION);        // set matrix to projection mode
        gl.glLoadIdentity();                        // reset the matrix to its default state
      //  gl.glFrustumf(-ratio, ratio, -1, 1, 3, 7);  // apply the projection matrix

    }



    public void clearVertex(){
        this.vertexBuffer.clear();
    }

    public void refreshVertex(){
        this.vertexBuffer.clear();
        mVertices=null;
    }

    public void setEventTable(Map<SensorEvent, String> eventTable) {
        EventTable = eventTable;
    }

    public float[] getAngle() {
        return mAngle;
    }

    /**
     * Sets the rotation angle of the triangle shape (mTriangle).
     */
    public void setAngle(float[] angle) {
        mAngle = angle;
    }

    public int getmCamerDist() {
        return mCamerDist;
    }

    public void setmCamerDist(int mCamerDist) {
        this.mCamerDist = mCamerDist;
    }

    public float getMtouchy() {
        return mtouchy;
    }

    public void setMtouchy(float mtouchy) {
        this.mtouchy = mtouchy;
    }

    public float getMtouchx() {
        return mtouchx;
    }

    public void setMtouchx(float mtouchx) {
        this.mtouchx = mtouchx;
    }
    public float[] getVertices() {
        return mVertices;
    }

    public void setVertices(float[] vertices) {
        this.clearVertex();
        this.mVertices = vertices;
        this.vertexBuffer.put(mVertices);
    }


}