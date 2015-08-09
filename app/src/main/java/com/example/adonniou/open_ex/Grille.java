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

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * A two-dimensional triangle for use as a drawn object in OpenGL ES 1.0/1.1.
 */
public class Grille {

    private static final String TAG = "EDroide";
    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;

    static float GrilleCoords[] = {
            -10.0f, -10.0f, 0.0f,//lignes // x
             10.0f, -10.0f, 0.0f,
            -10.0f, -9.0f, 0.0f,
             10.0f, -9.0f, 0.0f,
            -10.0f, -8.0f, 0.0f,
             10.0f, -8.0f, 0.0f,
            -10.0f, -7.0f, 0.0f,
             10.0f, -7.0f, 0.0f,
            -10.0f, -6.0f, 0.0f,
             10.0f, -6.0f, 0.0f,
            -10.0f, -5.0f, 0.0f,
             10.0f, -5.0f, 0.0f,
            -10.0f, -4.0f, 0.0f,
             10.0f, -4.0f, 0.0f,
            -10.0f, -3.0f, 0.0f,
             10.0f, -3.0f, 0.0f,
            -10.0f, -2.0f, 0.0f,
             10.0f, -2.0f, 0.0f,
            -10.0f, -1.0f, 0.0f,
             10.0f, -1.0f, 0.0f,
            -10.0f,  0.0f, 0.0f,
             10.0f,  0.0f, 0.0f,
            -10.0f,  1.0f, 0.0f,
             10.0f,  1.0f, 0.0f,
            -10.0f,  2.0f, 0.0f,
             10.0f,  2.0f, 0.0f,
            -10.0f,  3.0f, 0.0f,
             10.0f,  3.0f, 0.0f,
            -10.0f,  4.0f, 0.0f,
             10.0f,  4.0f, 0.0f,
            -10.0f,  5.0f, 0.0f,
             10.0f,  5.0f, 0.0f,
            -10.0f,  6.0f, 0.0f,
             10.0f,  6.0f, 0.0f,
            -10.0f,  7.0f, 0.0f,
             10.0f,  7.0f, 0.0f,
            -10.0f,  8.0f, 0.0f,
             10.0f,  8.0f, 0.0f,
            -10.0f,  9.0f, 0.0f,
             10.0f,  9.0f, 0.0f,
            -10.0f, 10.0f, 0.0f,
             10.0f, 10.0f, 0.0f,

            -10.0f, -10.0f, 0.0f,//lignes // y
            -10.0f,  10.0f, 0.0f,
            -9.0f, -10.0f, 0.0f,
            -9.0f, 10.0f, 0.0f,
            -8.0f, -10.0f, 0.0f,
            -8.0f, 10.0f, 0.0f,
            -7.0f, -10.0f, 0.0f,
            -7.0f, 10.0f, 0.0f,
            -6.0f, -10.0f, 0.0f,
            -6.0f, 10.0f, 0.0f,
            -5.0f, -10.0f, 0.0f,
            -5.0f, 10.0f, 0.0f,
            -4.0f, -10.0f, 0.0f,
            -4.0f, 10.0f, 0.0f,
            -3.0f, -10.0f, 0.0f,
            -3.0f, 10.0f, 0.0f,
            -2.0f, -10.0f, 0.0f,
            -2.0f, 10.0f, 0.0f,
            -1.0f, -10.0f, 0.0f,
            -1.0f, 10.0f, 0.0f,
            0.0f,  -10.0f, 0.0f,
            0.0f,  10.0f, 0.0f,
            1.0f,  -10.0f, 0.0f,
            1.0f,  10.0f, 0.0f,
            2.0f,  -10.0f, 0.0f,
            2.0f,  10.0f, 0.0f,
            3.0f,  -10.0f, 0.0f,
            3.0f,  10.0f, 0.0f,
            4.0f,  -10.0f, 0.0f,
            4.0f,  10.0f, 0.0f,
            5.0f,  -10.0f, 0.0f,
            5.0f,  10.0f, 0.0f,
            6.0f,  -10.0f, 0.0f,
            6.0f,  10.0f, 0.0f,
            7.0f,  -10.0f, 0.0f,
            7.0f,  10.0f, 0.0f,
            8.0f,  -10.0f, 0.0f,
            8.0f,  10.0f, 0.0f,
            9.0f,  -10.0f, 0.0f,
            9.0f,  10.0f, 0.0f,
            10.0f, -10.0f, 0.0f,
            10.0f, 10.0f, 0.0f,


    };


    private final short drawOrder[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
                                          31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42,
                                        43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84}; // order to draw vertices


    float color[] = { 0.105f, 0.138f, 0.898039216f, 0.170f }; //bleu clair
   // float color[] = { 0.0f, 1.0f, 0.0f, 1.0f };

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public Grille() {

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                GrilleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();

        vertexBuffer.put(GrilleCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param gl - The OpenGL ES context in which to draw this shape.
     */
    public void draw(GL10 gl,float []mAngle) {
                Log.i(TAG,"Angle grille: "+mAngle[1]);
        // Since this shape uses vertex arrays, enable them
        gl.glEnableClientState(GL10.GL_LINES);
        // draw the shape
        gl.glColor4f(       // set color:
                color[0], color[1],
                color[2], color[3]);
        gl.glVertexPointer( // point to vertex data:
                COORDS_PER_VERTEX,
                GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glRotatef(mAngle[1], 0f, mAngle[1], 0f);
        gl.glRotatef(mAngle[0],mAngle[0],0f,0f);
        gl.glDrawArrays(    // draw shape:
                GL10.GL_LINES, 0,
                GrilleCoords.length / COORDS_PER_VERTEX);
        gl.glLineWidth(1);
        gl.glDrawElements(GL10.GL_LINES,drawOrder.length, GL10.GL_UNSIGNED_SHORT,
                drawListBuffer);

        // Disable vertex array drawing to avoid
        // conflicts with shapes that don't use it
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }


}
