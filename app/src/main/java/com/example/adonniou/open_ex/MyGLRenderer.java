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

import android.hardware.SensorEvent;
import android.opengl.GLU;
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

    private Triangle mTriangle;
    private Square mSquare;
    private float mAngle;
    private float mtouchx;
    private float mtouchy;
    private int mCamerDist;
    private Ligne mLigne;
    private Volume mVolume;

    private static final String TAG = "EDroide";

    private static float anglePyramid = 0; // Rotational angle in degree for pyramid (NEW)
    private static float angleCube = 45f;    // Rotational angle in degree for cube (NEW)
    private static float speedPyramid = 2.0f; // Rotational speed for pyramid (NEW)
    private static float speedCube = -1.5f;   // Rotational speed for cube (NEW)

    private static Map<SensorEvent, String> EventTable = new HashMap<SensorEvent, String>();

    private float x=1.0f;

    private float[] mVertices=null;

    private float[] vertices = {  // Vertices of the 6 faces
            // FRONT
            -1.0f, -1.0f,  1.0f,  // 0. left-bottom-front
            x, -1.0f,  1.0f,  // 1. right-bottom-front
            -1.0f,  1.0f,  1.0f,  // 2. left-top-front
            1.0f,  x,  1.0f,  // 3. right-top-front
            // BACK
            1.0f, -1.0f, -1.0f,  // 6. right-bottom-back
            -1.0f, -1.0f, -1.0f,  // 4. left-bottom-back
            1.0f,  1.0f, -1.0f,  // 7. right-top-back
            -1.0f,  1.0f, -1.0f,  // 5. left-top-back
            // LEFT
            -1.0f, -1.0f, -1.0f,  // 4. left-bottom-back
            -1.0f, -1.0f,  1.0f,  // 0. left-bottom-front
            -1.0f,  1.0f, -1.0f,  // 5. left-top-back
            -1.0f,  1.0f,  1.0f,  // 2. left-top-front
            // RIGHT
            1.0f, -1.0f,  1.0f,  // 1. right-bottom-front
            1.0f, -1.0f, -1.0f,  // 6. right-bottom-back
            1.0f,  1.0f,  1.0f,  // 3. right-top-front
            1.0f,  1.0f, -1.0f,  // 7. right-top-back
            // TOP
            -1.0f,  1.0f,  1.0f,  // 2. left-top-front
            1.0f,  1.0f,  1.0f,  // 3. right-top-front
            -1.0f,  1.0f, -1.0f,  // 5. left-top-back
            1.0f,  1.0f, -1.0f,  // 7. right-top-back
            // BOTTOM
            -1.0f, -1.0f, -1.0f,  // 4. left-bottom-back
            1.0f, -1.0f, -1.0f,  // 6. right-bottom-back
            -1.0f, -1.0f,  1.0f,  // 0. left-bottom-front
            1.0f, -1.0f,  1.0f   // 1. right-bottom-front

    };
    private float[] vertices_init = {
            0.0f, 0.0f, 0.0f
    };

    private float[] lastvertices={-1.0f, -1.0f, -1.0f,  // 4. left-bottom-back
                                   1.0f, -1.0f, -1.0f,  // 6. right-bottom-back
                                  -1.0f, -1.0f,  1.0f,  // 0. left-bottom-front
                                   1.0f, -1.0f,  1.0f   // 1. right-bottom-front
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

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the background frame color

        Log.i(TAG, "Surface Created");
        gl.glClearColor(0.192f, 0.192f, 0.192f, 0.0f);  // Set color's clear-value to black
        gl.glClearDepthf(1.0f);            // Set depth's clear-value to farthest
        //gl.glEnable(GL10.GL_DEPTH_TEST);   // Enables depth-buffer for hidden surface removal, Ne change rien
        //gl.glDepthFunc(GL10.GL_LEQUAL);    // The type of depth testing to do , peut etre mieux sans
        //gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);  // nice perspective view, peut etre mieux sans
        //gl.glShadeModel(GL10.GL_SMOOTH);   // Enable smooth shading of color , Ne change rien
        //gl.glDisable(GL10.GL_DITHER);      // Disable dithering for better performance , Ne change rien


       // mVolume= new Volume();

               // Setup vertex-array buffer. Vertices in float. An float has 4 bytes

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder()); // Use native byte order
        vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float
        vertexBuffer.put(vertices_init);         // Vertices change
        //vertexBuffer.put(vertices);
        vertexBuffer.position(0);           // Rewind

        // Enabled the vertex buffer for writing and to be used during rendering.
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        //mTriangle = new Triangle();
        //mSquare = new Square();
        //mLigne= new Ligne();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //Log.i(TAG, "Surface Ondraw");

        // Draw background color
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // Set GL_MODELVIEW transformation mode
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();   // reset the matrix to its default state

        // When using GL_MODELVIEW, you must set the view point

        GLU.gluLookAt(gl, 0, 0, mCamerDist, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        //gl.glDrawArrays(GL10.GL_LINES, 0, 2);
        //Draw ligne
        //mLigne.draw(gl);

        gl.glTranslatef(mtouchx, mtouchy, 0);

        gl.glRotatef(mAngle, mAngle, 0.0f, 1.0f);

        gl.glShadeModel(GL10.GL_FLAT);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, 1);

        vertexBuffer.clear();

        if (mVertices!=null)
        {

        }
        else {
            mVertices=vertices_init;
           Log.i(TAG, "lg mVert: "+mVertices.length);

        }
        ByteBuffer vbb = ByteBuffer.allocateDirect(mVertices.length * 4);
        vbb.order(ByteOrder.nativeOrder()); // Use native byte order
        vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float
        vertexBuffer.put(mVertices);

            vertexBuffer.position(0);           // Rewind
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);//indispensable
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);


        // Render all the faces
        //for (int face = 0; face < numFaces; face++) {
            // Set the color for each of the faces
            gl.glColor4f(colors[2][0], colors[2][1], colors[2][2], colors[2][3]);
            // Draw the primitive from the vertex-array directly

           // gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, face*4, mVertices.length/12);
            //gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0,mVertices.length/3);
        //}
        gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, mVertices.length/2); //affige plusieurs lignes
        gl.glLineWidth(3);
        //gl.glDrawArrays(GL10.GL_POINTS, 0, mVertices.length);//affiche rien
        //gl.glDrawArrays(GL10.GL_LINE_SMOOTH, 0, mVertices.length/2);//affiche rien
        //Disable the client state before leaving
        //gl.glDisableClientState(GL10.GL_VERTEX_ARRAY); change rien
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
           // Log.i(TAG, "lg mVert: "+mVertices.length);

        }

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder()); // Use native byte order
        vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float


        vertexBuffer.put(mVertices);         // vertex change
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        //mVolume.setEventTable(EventTable);

        //Log.i(TAG, "width: " + width);
        //Log.i(TAG, "width: " + height);

        gl.glViewport(0, 0, width, height);

        // make adjustments for screen ratio
        float ratio = (float) width / height;
        gl.glMatrixMode(GL10.GL_PROJECTION);        // set matrix to projection mode
        gl.glLoadIdentity();                        // reset the matrix to its default state
        gl.glFrustumf(-ratio, ratio, -1, 1, 3, 7);  // apply the projection matrix

    }

    /**
     * Returns the rotation angle of the triangle shape (mTriangle).
     *
     * @return - A float representing the rotation angle.
     */

    public Map<SensorEvent, String> getEventTable() {
        return EventTable;
    }

    public void setEventTable(Map<SensorEvent, String> eventTable) {
        EventTable = eventTable;
    }

    public float getAngle() {
        return mAngle;
    }

    /**
     * Sets the rotation angle of the triangle shape (mTriangle).
     */
    public void setAngle(float angle) {
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
        this.mVertices = vertices;
    }


}