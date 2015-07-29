/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.adonniou.open_ex;

import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * Simple fragment containing only a TextView. Used by TextPagerAdapter to create
 * tutorial-style pages for apps.
 */
public class BureauFragment extends Fragment {

    // Contains the text that will be displayed by this Fragment
    String mText;
    Drawable mDrawable;
    // Contains a resource ID for the text that will be displayed by this fragment.
    int mTextId = -1;
    int mDrawableId=-1;
    // Keys which will be used to store/retrieve text passed in via setArguments.
    public static final String TEXT_KEY = "text";
    public static final String TEXT_ID_KEY = "text_id";

    public static final Drawable DRAWABLE_KEY=null;
    public static final Drawable DRAWABLE_ID_KEY=null;
    private Layout ll;
    private FragmentActivity fa;
    // For situations where the app wants to modify text at Runtime, exposing the TextView.
    private TextView mTextView;
    private static final String TAG = "EDroide";

    private MyGLSurfaceView mGLView;

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

    float m_rotationMatrix []=new float [9];
    float m_orientation []=new float [3];

    //private ImageButton mImageButton;
    //public ViewGroup mViewGroup=null;
    FragmentTransaction ft;

    public BureauFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Before initializing the textView, check if any arguments were provided via setArguments.


        processArguments();
fa=super.getActivity();

        View layout = inflater.inflate(R.layout.bureaulayout_opengl, container, false);
        final MyGLSurfaceView GLView = (MyGLSurfaceView) layout.findViewById(R.id.glsurfaceview);

       final ImageButton mImageButton= (ImageButton) layout.findViewById(R.id.lauchscan);
        final ImageButton mResumeButton= (ImageButton) layout.findViewById(R.id.resume);
        final ImageButton mCleanVerticesButton= (ImageButton) layout.findViewById(R.id.cleanvertices);
        mResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//activation des sensors
                GLView.onSensorsStart();
                Toast.makeText(getActivity(), "Start: ", Toast.LENGTH_SHORT).show();
                //GLView.getRootView();
                //GLView.clearFocus();
                //GLView.clearAnimation();
                //GLView.onResume();


//enregistrer le fichier .obj
                //SaveOBJ(getActivity(),GLView);
            }
        });

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//pause des sensor
                GLView.onSensorsPause();

                Toast.makeText(getActivity(), "Stop: ", Toast.LENGTH_SHORT).show();
                //GLView.getRootView();
                //GLView.clearFocus();
                //GLView.clearAnimation();
                //GLView.onResume();


//enregistrer le fichier .obj
                //SaveOBJ(getActivity(),GLView);
          }
        });
        mCleanVerticesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//pause des sensor
                GLView.onNoVertices();

                Toast.makeText(getActivity(), "Refresh: ", Toast.LENGTH_SHORT).show();
                //GLView.getRootView();
                //GLView.clearFocus();
                //GLView.clearAnimation();
                //GLView.onResume();


//enregistrer le fichier .obj
                //SaveOBJ(getActivity(),GLView);
            }
        });

        if (mText != null) {
            mTextView.setText(mText);
            Log.i("SimpleTextFragment", mText);
        }
        return layout;
    }


    public void SaveOBJ(Context context, MyGLSurfaceView glview) {
        float sVertices[] = glview.getsVertices();
        FileOutputStream fOut = null;
        OutputStreamWriter osw = null;



        // On crée un fichier qui correspond à l'emplacement extérieur




        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            try {

                //File mFile = new File(Environment.getExternalStorageDirectory().getPath() + "/Android/data/ " + getActivity().getPackageName() + "/files/objet.obj");
                //String filename = "/sdcard/objet.obj";
                //File sdCard = Environment.getExternalStorageDirectory();
                //filename = filename.replace("/sdcard", sdCard.getAbsolutePath());
                //File tempFile = new File(filename);

                // Flux interne
                //output = context.openFileOutput("objet.obj", Context.MODE_PRIVATE);
                fOut = context.openFileOutput("objet.obj", Context.MODE_APPEND);
                //output=
                //FileOutputStream output = new FileOutputStream(mFile);

                osw = new OutputStreamWriter(fOut);

                osw.write("# *.obj file (Generate by Open-ex 3D)\n");
                osw.flush();
                for (int i = 0; i < sVertices.length; i++) {

                    try {
                        String data = "v "+Float.toString(sVertices[i])+" "+Float.toString(sVertices[i+1])+" "+Float.toString(sVertices[i+2])+"\n";
                       Log.i(TAG, data);
                        osw.write(data);
                        osw.flush();
                        i=i+3;
                    } catch (Exception e) {
                        Toast.makeText(context, "erreur d'écriture: "+e, Toast.LENGTH_SHORT).show();
                    }

                }

                osw.write("# lignes:\n");
                osw.write("l ");
                osw.flush();;
                for (int i = 1; i < sVertices.length; i++)
                {
                    osw.write(i+" ");
                    osw.flush();
                    i=i+1;
                }
                //popup surgissant pour le résultat
                Toast.makeText(context, "Settings saved", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(context, "Settings not saved", Toast.LENGTH_SHORT).show();
            } finally {
                try {
                    osw.close();
                    fOut.close();
                } catch (IOException e) {
                    Toast.makeText(context, "Settings not saved", Toast.LENGTH_SHORT).show();
                }

            }
        }
        else
        {
            Toast.makeText(context, "Pas de carte ext", Toast.LENGTH_SHORT).show();
        }
    }


    public TextView getTextView() {
        return mTextView;
    }

    /**
     * Changes the text for this TextView, according to the resource ID provided.
     * @param stringId A resource ID representing the text content for this Fragment's TextView.
     */
    public void setText(int stringId) {
        getTextView().setText(getActivity().getString(stringId));
    }

    /**
     * Processes the arguments passed into this Fragment via setArguments method.
     * Currently the method only looks for text or a textID, nothing else.
     */
    public void processArguments() {
        // For most objects we'd handle the multiple possibilities for initialization variables
        // as multiple constructors.  For Fragments, however, it's customary to use
        // setArguments / getArguments.
        if (getArguments() != null) {
            Bundle args = getArguments();
            if (args.containsKey(TEXT_KEY)) {
                mText = args.getString(TEXT_KEY);
                Log.d("Constructor", "Added Text.");
            } else if (args.containsKey(TEXT_ID_KEY)) {
                mTextId = args.getInt(TEXT_ID_KEY);
                mText = getString(mTextId);
            }
        }
    }

}