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
import android.hardware.SensorEvent;
import android.hardware.display.DisplayManager;
import android.opengl.GLU;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Map;
import android.content.Context;


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
    private Iphone mIphone;
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

    private float[] sqare_vertices = {  // Vertices of the 6 faces
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

    private ShortBuffer drawListBuffer;
    private short drawOrder[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22}; // order to draw vertices

    private int numFaces = 6;

    private FloatBuffer vertexBuffer;  // Buffer for vertex-array

    private float[] RepMatrix = {0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f};

    private ByteBuffer vbb = ByteBuffer.allocateDirect(4000);

    private ByteBuffer dlb = ByteBuffer.allocateDirect(
            // (# of coordinate values * 2 bytes per short)
            2000);

    public float[] getRepMatrix() {
        return RepMatrix;
    }

    public void setRepMatrix(float[] repMatrix) {
        RepMatrix = repMatrix;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the background frame color

        //parser=new OBJParser(context);
        //model=parser.parseOBJ("C:/Helicoptere.obj");


        Log.i(TAG, "Surface Created");
        gl.glClearColor(0.192f, 0.192f, 0.192f, 0.0f);  // Set color's clear-value to black
        gl.glClearDepthf(1.0f);            // Set depth's clear-value to farthest
        gl.glEnable(GL10.GL_DEPTH_TEST);   // Enables depth-buffer for hidden surface removal, Ne change rien
        gl.glDepthFunc(GL10.GL_LEQUAL);    // The type of depth testing to do , peut etre mieux sans
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);  // nice perspective view, peut etre mieux sans
        gl.glShadeModel(GL10.GL_SMOOTH);   // Enable smooth shading of color , Ne change rien
        gl.glDisable(GL10.GL_DITHER);      // Disable dithering for better performance , Ne change rien


       // mVolume= new Volume();

               // Setup vertex-array buffer. Vertices in float. An float has 4 bytes

        //vbb = ByteBuffer.allocateDirect(4000);
        vbb.order(ByteOrder.nativeOrder()); // Use native byte order
        vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float
        vertexBuffer.put(vertices_init);         // Vertices change
        vertexBuffer.position(0);           // Rewind


        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        // Enabled the vertex buffer for writing and to be used during rendering.
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        //mRepere= new Repere();
        mGrille=new Grille();
        //mIphone=new Iphone();

    }


    @Override
    public void onDrawFrame(GL10 gl) {
        //Log.i(TAG, "Surface Ondraw");

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
//GLU.gluPerspective(gl,40.0f,1.5f,3.0f,20.0f);
        //gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
       mGrille.draw(gl,mAngle);
        //mRepere.draw(gl, mAngle);
        //mIphone.draw(gl, mAngle);
        gl.glTranslatef(mtouchx, mtouchy, 0);
       // Log.i(TAG, "Angle objet: " + mAngle[1]);
        gl.glRotatef(mAngle[1], 0f, mAngle[1], 0f);
        //gl.glRotatef(mAngle[0],mAngle[0],0f,0f);

        gl.glShadeModel(GL10.GL_FLAT);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, 1);

        //vertexBuffer.clear();

        if (mVertices!=null)
        {

        }
        else {
            mVertices=vertices_init;

        }

        if(vertexBuffer.capacity()<mVertices.length)
        {

            ByteBuffer vbb1 = ByteBuffer.allocateDirect(mVertices.length * 4);
            vbb1.order(ByteOrder.nativeOrder()); // Use native byte order

            //vertexBuffer.clear();

            //vertexBuffer.limit(mVertices.length);

            vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float

            //Log.i(TAG, "Vertex Buffer: " + vertexBuffer.capacity());
            try {
            //    vbb = ByteBuffer.allocateDirect(mVertices.length * 4);
                vertexBuffer.put(mVertices);
            } catch (Exception e) {
                Log.i(TAG,"erreur put"+e);
            }

            //recalcul du drawOrder
            drawOrder=null;
            drawOrder=new short[mVertices.length];
            for(int i=0; i<mVertices.length;i++){
                drawOrder[i]=(short)i;
            }

            // initialize byte buffer for the draw list
            //ByteBuffer dlb = ByteBuffer.allocateDirect(
                    // (# of coordinate values * 2 bytes per short)
              //      drawOrder.length * 2);
            //dlb.order(ByteOrder.nativeOrder());
            //drawListBuffer = dlb.asShortBuffer();
//drawListBuffer.limit(mVertices.length);
            drawListBuffer.put(drawOrder);
            drawListBuffer.position(0);

        }
        else
        {

        }
        //for(int i=0;i<mVertices.length;i++)
        //{
        //    Log.i(TAG, "vertices "+i+": "+mVertices[i]);
        //}

        vertexBuffer.position(0);           // Rewind
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);//indispensable
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);


        // Render all the faces
        //for (int face = 0; face < numFaces; face++) {
            // Set the color for each of the faces
            gl.glColor4f(colors[2][0], colors[2][1], colors[2][2], colors[2][3]);
            // Draw the primitive from the vertex-array directly

        //}
//GL_LINEAR affiche rien
        //GL_LINES lignes en discontinues
        //GL_LINESMOOTH_HINT, GL_LINE_SMOOTH affiche rien
        //GL_LINE_LOOP affiche ligne + ligne erreurs
            gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, mVertices.length); //affige plusieurs lignes
        gl.glLineWidth(3);
        //gl.glDrawElements(GL10.GL_LINE_STRIP, drawOrder.length, GL10.GL_UNSIGNED_SHORT, //GL_UNSIGNED_SHORT
          //      drawListBuffer);

        // Disable vertex array drawing to avoid
        // conflicts with shapes that don't use it
        //efface la grille bleu
        //gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

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

//        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
  //      vbb.order(ByteOrder.nativeOrder()); // Use native byte order
    //    vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float


        vertexBuffer.put(mVertices);         // vertex change
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

        gl.glViewport(0, 0, width, height);

        // make adjustments for screen ratio
        float ratio = (float) width / height;
        gl.glMatrixMode(GL10.GL_PROJECTION);        // set matrix to projection mode
        gl.glLoadIdentity();                        // reset the matrix to its default state
        gl.glFrustumf(-ratio, ratio, -1, 1, 3, 7);  // apply the projection matrix

    }

    public void onIntro (GL10 gl)
    {

    }

    public void onObjectModif(GL10 gl)
    {

    }

    /**
     * Returns the rotation angle of the triangle shape (mTriangle).
     *
     * @return - A float representing the rotation angle.
     */

    public Map<SensorEvent, String> getEventTable() {
        return EventTable;
    }

    public void clearVertex(){
        this.vertexBuffer=null;
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices_init.length * 4);
        //vbb.order(ByteOrder.nativeOrder()); // Use native byte order
        this.vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float
        //this.vertexBuffer.put(vertices_init);

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
    }


}