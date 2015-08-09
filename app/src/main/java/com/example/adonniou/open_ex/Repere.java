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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * A two-dimensional triangle for use as a drawn object in OpenGL ES 1.0/1.1.
 */
public class Repere {

    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = {
            // in counterclockwise order:
            0.0f,  0.622008459f, 0.0f,// top
           -0.5f, -0.311004243f, 0.0f,// bottom left
            0.5f, -0.311004243f, 0.0f // bottom right
    };

    static float RepereCoords[] = {
            0.0f,  0.0f, 0.0f,   //
            0.5f, 0.0f, 0.0f,   // x
            0.0f,  0.5f, 0.0f,  // x
            0.0f,  0.0f, 0.5f, }; // z

    private final short drawOrder[] = { 0, 1, 0, 2, 0, 3 }; // order to draw vertices

    static float ligneCoords[] = {
            // in counterclockwise order:
            0.0f,  0.0f, 0.0f,// top
            -0.9f, -0.7f, 0.0f,// bottom left
             // bottom right
    };

    float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };
   // float color[] = { 0.0f, 1.0f, 0.0f, 1.0f };

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public Repere() {

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                RepereCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();

        vertexBuffer.put(RepereCoords);
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


        // Since this shape uses vertex arrays, enable them
        gl.glEnableClientState(GL10.GL_LINES);
        // draw the shape
        gl.glColor4f(       // set color:
                color[0], color[1],
                color[2], color[3]);
        gl.glVertexPointer( // point to vertex data:
                COORDS_PER_VERTEX,
                GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glRotatef(mAngle[0],mAngle[0],0.0f,0.0f);
        //gl.glDrawArrays(    // draw shape:
          //      GL10.GL_LINES, 0,
            //    RepereCoords.length / COORDS_PER_VERTEX);

        gl.glDrawElements(GL10.GL_LINES,drawOrder.length, GL10.GL_UNSIGNED_SHORT,
                drawListBuffer);

        // Disable vertex array drawing to avoid
        // conflicts with shapes that don't use it
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
}
