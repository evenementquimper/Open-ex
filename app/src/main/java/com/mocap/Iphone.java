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

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * A two-dimensional triangle for use as a drawn object in OpenGL ES 1.0/1.1.
 */
public class Iphone {

    private static final String TAG = "EDroide";
    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;

    static float IphoneCoords[] = {

             -2.860800f, 0.933815f, -0.473113f,
            -2.114505f, 0.933815f, -0.473113f,
            -2.114505f, 0.096368f, -0.473113f,
            -2.802845f, 0.654855f, -0.473113f,
            -2.114505f, 0.452098f, -0.473113f,
            -2.506045f, 0.933815f, -0.473113f,
            -2.114505f, 0.096368f, -0.316292f,
            -2.802845f, 0.654856f, -0.316292f,
            -2.440519f, 0.197572f, -0.316292f,
            -2.709433f, 0.320699f, -0.316292f,
            -2.874141f, 0.624608f, -0.316292f,
            -2.114505f, 0.027480f, -0.010023f,
            -2.470767f, 0.132756f, -0.010023f,
            -2.657581f, 0.374713f, -0.473113f,
            -2.440519f, 0.197572f, -0.473113f,
            -2.468353f, 0.510312f, -0.473113f,
            -2.657581f, 0.374713f, -0.316292f,
            -2.860800f, 0.933815f, -0.316292f,
            -2.914813f, 0.933815f, -0.316292f,
            -2.114505f, 0.027479f, -0.316292f,
            -2.470767f, 0.132756f, -0.316292f,
            -2.709433f, 0.320699f, -0.010023f,
            -2.914813f, 0.933815f, -0.010023f,
            -2.874141f, 0.624608f, -0.010023f,
            2.657581f, 0.374713f, -0.473113f,
            -0.000000f, 0.096368f, -0.473113f,
            2.114505f, 0.933815f, -0.473113f,
            2.114505f, 0.096368f, -0.473113f,
            -0.000000f, 0.452098f, -0.473113f,
            2.114505f, 0.452098f, -0.473113f,
            2.506045f, 0.933815f, -0.473113f,
            2.657581f, 0.374713f, -0.316292f,
            2.114505f, 0.096368f, -0.316292f,
            2.440519f, 0.197572f, -0.316292f,
            2.709433f, 0.320699f, -0.316292f,
             -0.000000f, 0.031857f, -0.316292f,
             2.914813f, 0.933815f, -0.316292f,
             2.874141f, 0.624608f, -0.316292f,
             -0.000000f, 0.031857f, -0.010023f,
             2.914813f, 0.933815f, -0.010023f,
             2.874141f, 0.624608f, -0.010023f,
             2.860800f, 0.933815f, -0.473113f,
             -0.000000f, 0.933815f, -0.473113f,
             2.802845f, 0.654855f, -0.473113f,
             2.440519f, 0.197572f,-0.473113f,
             2.468353f, 0.510312f, -0.473113f,
             -0.000000f, 0.096368f, -0.316292f,
             2.860800f, 0.933815f, -0.316292f,
             2.802845f, 0.654856f, -0.316292f,
             2.114505f, 0.027479f, -0.316292f,
             2.470767f, 0.132756f, -0.316292f,
             2.709433f, 0.320699f, -0.010023f,
             2.114505f, 0.027480f, -0.010023f,
             2.470767f, 0.132756f, -0.010023f,
             -2.114505f, 10.619629f, -0.473114f,
             -2.114505f, 5.787911f, -0.473113f,
             -2.802845f, 10.898589f, -0.473114f,
             -2.468353f, 11.043133f, -0.473114f,
             -2.860800f, 5.787911f, -0.316292f,
             -2.860800f, 10.619629f, -0.316293f,
             -2.114505f, 11.457075f, -0.316293f,
             -2.802845f, 10.898589f, -0.316293f,
             -2.709433f, 11.232744f, -0.316293f,
             -2.913912f, 5.787911f, -0.316292f,
             -2.914813f, 10.619629f, -0.316293f,
             -2.874141f, 10.928836f, -0.316293f,
             -2.709433f, 11.232744f, -0.010023f,
             -2.470767f, 11.420689f, -0.010023f,
             -2.657581f, 11.178733f, -0.473114f,
             -2.860800f, 5.787911f, -0.473113f,
             -2.860800f, 10.619629f, -0.473114f,
             -2.114505f, 11.457075f, -0.473114f,
             -2.114505f, 11.101347f, -0.473114f,
             -2.506045f, 10.619629f, -0.473114f,
             -2.506045f, 5.787911f, -0.473113f,
             -2.440519f, 11.355873f, -0.473114f,
             -2.657581f, 11.178733f, -0.316293f,
             -2.440519f, 11.355873f, -0.316293f,
             -2.114505f, 11.525963f, -0.316293f,
             -2.470767f, 11.420689f, -0.316293f,
             -2.913912f, 5.787911f, -0.010023f,
             -2.914813f, 10.619629f, -0.010023f,
             -2.114505f, 11.525963f, -0.010023f,
             -2.874141f, 10.928836f, -0.010023f,
             -0.000000f, 5.787911f, -0.473113f,
             2.860800f, 10.619629f, -0.473114f,
             2.114505f, 10.619629f, -0.473114f,
             2.114505f, 5.787911f, -0.473113f,
             -0.000000f, 11.101347f, -0.473114f,
             2.114505f, 11.101347f, -0.473114f,
             2.506045f, 10.619629f, -0.473114f,
             2.506045f, 5.787911f, -0.473113f,
             2.468353f, 11.043133f, -0.473114f,
             2.860800f, 5.787911f, -0.316292f,
             2.802845f, 10.898589f, -0.316293f,
             2.709433f, 11.232744f, -0.316293f,
             -0.000000f, 11.521588f, -0.316293f,
             2.913912f, 5.787911f, -0.316292f,
             2.114505f, 11.525963f, -0.316293f,
             2.470767f, 11.420689f, -0.316293f,
             2.709433f, 11.232744f, -0.010023f,
             -0.000000f, 11.521588f, -0.010023f,
             2.913912f, 5.787911f, -0.010023f,
             2.914813f, 10.619629f, -0.010023f,
             2.874141f, 10.928836f, -0.010023f,
             2.470767f, 11.420689f, -0.010023f,
             2.657581f, 11.178733f, -0.473114f,
             -0.000000f, 11.457075f, -0.473114f,
             2.860800f, 5.787911f, -0.473113f,
             -0.000000f, 10.619629f, -0.473114f,
             2.114505f, 11.457075f, -0.473114f,
             2.802845f, 10.898589f, -0.473114f,
             2.440519f, 11.355873f, -0.473114f,
             2.657581f, 11.178733f, -0.316293f,
             -0.000000f, 11.457075f, -0.316293f,
             2.860800f, 10.619629f, -0.316293f,
             2.114505f, 11.457075f, -0.316293f,
             2.440519f, 11.355873f, -0.316293f,
             2.914813f, 10.619629f, -0.316293f,
             2.874141f, 10.928836f, -0.316293f,
             2.114505f, 11.525963f, -0.010023f,
             2.657581f, 0.374713f, 0.460126f,
             2.860800f, 0.933815f, 0.460126f,
             2.114505f, 0.933815f, 0.460126f,
             2.114505f, 0.096368f, 0.460126f,
             2.802845f, 0.654856f, 0.460126f,
             2.114505f, 0.452098f, 0.460126f,
             2.506045f, 0.933815f, 0.460126f,
             2.440519f, 0.197572f, 0.460126f,
             2.468353f, 0.510312f, 0.460126f,
             2.657581f, 0.374713f, 0.303306f,
             2.860800f, 0.933815f, 0.303306f,
             2.114505f, 0.096368f, 0.303305f,
             2.802845f, 0.654856f, 0.303306f,
             2.440519f, 0.197572f, 0.303306f,
             2.709433f, 0.320699f, 0.303306f,
             2.914813f, 0.933815f, 0.303306f,
             2.114505f, 0.027480f, 0.303305f,
             2.874141f, 0.624608f, 0.303306f,
             2.470767f, 0.132756f, 0.303306f,
             -2.657581f, 0.374713f, 0.460125f,
             -0.000000f, 0.096368f, 0.460126f,
             -2.860800f, 0.933815f, 0.460125f,
             -0.000000f, 0.933815f, 0.460126f,
             -2.114505f, 0.933815f, 0.460125f,
             -2.114505f, 0.096368f, 0.460125f,
             -2.802845f, 0.654856f, 0.460125f,
             -0.000000f, 0.452098f, 0.460126f,
             -2.114505f, 0.452098f, 0.460125f,
             -2.506045f, 0.933815f, 0.460125f,
             -2.440519f, 0.197572f, 0.460125f,
             -2.468353f, 0.510312f, 0.460125f,
             -2.657581f, 0.374713f, 0.303305f,
             0.000000f, 0.096368f, 0.303305f,
             -2.860800f, 0.933815f, 0.303305f,
             -2.114505f, 0.096368f, 0.303305f,
             -2.802845f, 0.654856f, 0.303305f,
             -2.440519f, 0.197572f, 0.303305f,
             -2.709433f, 0.320699f, 0.303305f,
             0.000000f, 0.031857f, 0.303305f,
             -2.914813f, 0.933815f, 0.303305f,
             -2.114505f, 0.027480f, 0.303305f,
             -2.874141f, 0.624608f, 0.303305f,
             -2.470767f, 0.132756f, 0.303305f,
             2.657581f, 11.178733f, 0.460127f,
             2.860800f, 5.787910f, 0.460126f,
             2.860800f, 10.619629f, 0.460127f,
             2.114505f, 10.619629f, 0.460127f,
             2.114505f, 5.787910f, 0.460126f,
             2.114505f, 11.457075f, 0.460127f,
             2.802845f, 10.898589f, 0.460127f,
             2.114505f, 11.101347f, 0.460127f,
             2.506045f, 10.619629f, 0.460127f,
             2.506045f, 5.787910f, 0.460126f,
             2.440519f, 11.355873f, 0.460127f,
             2.468353f, 11.043133f, 0.460127f,
             2.657581f, 11.178733f, 0.303306f,
             2.860800f, 5.787910f, 0.303306f,
             2.860800f, 10.619629f, 0.303306f,
             2.114505f, 11.457075f, 0.303306f,
             2.802845f, 10.898589f, 0.303306f,
             2.440519f, 11.355873f, 0.303306f,
             2.709433f, 11.232744f, 0.303306f,
             2.913912f, 5.787910f, 0.303306f,
             2.914813f, 10.619629f, 0.303306f,
             2.114505f, 11.525963f, 0.303306f,
             2.874141f, 10.928836f, 0.303306f,
             2.470767f, 11.420689f, 0.303306f,
             -2.657581f, 11.178733f, 0.460126f,
             -0.000000f, 11.457075f, 0.460126f,
            -0.000000f, 5.787910f, 0.460126f,
            -2.860800f, 5.787910f, 0.460125f,
            -2.860800f, 10.619629f, 0.460126f,
            -0.000000f, 10.619629f, 0.460126f,
            -2.114505f, 10.619629f, 0.460126f,
            -2.114505f, 5.787910f, 0.460125f,
            -2.114505f, 11.457075f, 0.460126f,
            -2.802845f, 10.898589f,0.460126f,
            -0.000000f, 11.101347f, 0.460127f,
            -2.114505f, 11.101347f, 0.460126f,
            -2.506045f, 10.619629f, 0.460126f,
            -2.506045f, 5.787910f, 0.460125f,
            -2.440519f, 11.355873f, 0.460126f,
            -2.468353f, 11.043133f, 0.460126f,
            -2.657581f, 11.178733f, 0.303306f,
            0.000000f, 11.457075f, 0.303306f,
            -2.860800f, 5.787910f, 0.303305f,
            -2.860800f, 10.619629f, 0.303306f,
            -2.114505f, 11.457075f, 0.303306f,
            -2.802845f, 10.898589f, 0.303306f,
            -2.440519f, 11.355873f, 0.303306f,
            -2.709433f, 11.232744f, 0.303306f,
            0.000000f, 11.521588f, 0.303306f,
            -2.913912f, 5.787910f, 0.303305f,
            -2.914813f, 10.619629f, 0.303306f,
            -2.114505f, 11.525963f, 0.303306f,
            -2.874141f, 10.928836f, 0.303306f,
            -2.470767f, 11.420689f, 0.303306f,
    };


    private final short drawOrder[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
                                          31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42,
                                        43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,
                                        91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,
                                        131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,162,163,164,165,166,167,168,169,170,
                                        171,172,173,174,175,176,177,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,199,200,201,202,203,204,205,206,207,208,209,210}
            ; // order to draw vertices


    float color[] = {  0.105f, 0.138f, 0.898039216f, 0.170f }; //rouge clair
   // float color[] = { 0.0f, 1.0f, 0.0f, 1.0f };

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public Iphone() {

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                IphoneCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();

        vertexBuffer.put(IphoneCoords);
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
                IphoneCoords.length / COORDS_PER_VERTEX);
        gl.glLineWidth(2);
        gl.glDrawElements(GL10.GL_LINES,drawOrder.length, GL10.GL_UNSIGNED_SHORT,
                drawListBuffer);

        // Disable vertex array drawing to avoid
        // conflicts with shapes that don't use it
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }


}
