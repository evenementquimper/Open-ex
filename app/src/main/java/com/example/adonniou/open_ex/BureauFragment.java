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
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
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

       final ImageButton mStopButton= (ImageButton) layout.findViewById(R.id.stop);
        final ImageButton mStartButton= (ImageButton) layout.findViewById(R.id.start);
        final ImageButton mCleanVerticesButton= (ImageButton) layout.findViewById(R.id.save_file);
        final ImageButton mZommPlusButton= (ImageButton) layout.findViewById(R.id.zoomplus);
        final ImageButton mZommMoinsButton= (ImageButton) layout.findViewById(R.id.zoommoins);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//activation des sensors
                GLView.onSensorsStart();
                Toast.makeText(getActivity(), "Start: ", Toast.LENGTH_SHORT).show();
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//pause des sensor
                GLView.onSensorsPause();

                Toast.makeText(getActivity(), "Stop: ", Toast.LENGTH_SHORT).show();


//enregistrer le fichier .obj
                //SaveOBJ(getActivity(),GLView);
          }
        });
        mCleanVerticesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//pause des sensor

                SaveOBJ(getActivity(), GLView);


                //GLView.onNoVertices();

                Toast.makeText(getActivity(), "Refresh: ", Toast.LENGTH_SHORT).show();

                //GLView.getRootView();
                //GLView.clearFocus();
                //GLView.clearAnimation();
                //GLView.onResume();


//enregistrer le fichier .obj
                //SaveOBJ(getActivity(),GLView);
            }
        });
        mZommPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GLView.onChangeCam(1);
    }
        });
        mZommMoinsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GLView.onChangeCam(-1);
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

        File mFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"mocap.obj");

        // On crée un fichier qui correspond à l'emplacement extérieur




        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            try {

                Log.i(TAG, "Documents dir: "+Environment.DIRECTORY_DOCUMENTS);
                fOut=new FileOutputStream(mFile);
                osw = new OutputStreamWriter(fOut);
                osw.write("# *.obj file (Generate by Mocap 3D)\n");
                osw.flush();

                for (int i =0; i < sVertices.length-3; i++) {

                    try {
                        String data = "v "+Float.toString(sVertices[i])+" "+Float.toString(sVertices[i+1])+" "+Float.toString(sVertices[i+2])+"\n";
                       Log.i(TAG, data);
                        osw.write(data);
                        osw.flush();
                        i=i+2;
                    } catch (Exception e) {
                        Toast.makeText(context, "erreur d'écriture: "+e, Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "Erreur: "+e);
                    }

                }

                osw.write("# lignes:\n");
                osw.write("l ");
                osw.flush();;
                for (int i = 1; i < (-1+sVertices.length/3); i++)
                {
                    osw.write(i+" ");
                    osw.flush();
                }
                //popup surgissant pour le résultat
                Toast.makeText(context, "Settings saved", Toast.LENGTH_SHORT).show();

                //lancement d'un explorateur de fichiers vers le fichier créer
                //systeme des intend
                try {
                    File root = new File(Environment.DIRECTORY_DOCUMENTS);
                    Uri uri = Uri.fromFile(mFile);

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setData(uri);


                    // Verify that the intent will resolve to an activity
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        Log.i(TAG,"intent pk: ");
                        getActivity().startActivityForResult(intent, 1);
                    }
                } catch (Exception e) {
                    Log.i(TAG,"Erreur intent: "+e);
                }
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

    public void Read_File(Context context)
    {
        FileInputStream fIn = null;
        InputStreamReader isr = null;

        char[] inputBuffer = new char[255];
        String data = null;

        JSONObject json=null;

        try{
            fIn = context.openFileInput("objet_1.obj");
            isr = new InputStreamReader(fIn);
            isr.read(inputBuffer);
            data = new String(inputBuffer);
            //affiche le contenu de mon fichier dans un popup surgissant
            //Log.i(TAG, "Data: " + data);
            //Toast.makeText(context, "data: " + data, Toast.LENGTH_SHORT).show();
            //json=new JSONObject(data);

        }
        catch (Exception e) {
            Toast.makeText(context, "Objet not read", Toast.LENGTH_SHORT).show();
        }
            /*finally {
               try {
                      isr.close();
                      fIn.close();
                      } catch (IOException e) {
                        Toast.makeText(context, "Settings not read",Toast.LENGTH_SHORT).show();
                      }
            } */
        //return json;
    }

    private String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {

        //  BufferedReader r = new BufferedReader(new InputStreamReader(stream));
        //StringBuilder total = new StringBuilder();
        //String line;
        //while ((line = r.readLine()) != null) {
        //  total.append(line);
        //}
        //return total.toString();


        Reader reader = null;
        //stream.available();
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
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