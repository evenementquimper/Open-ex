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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private static final String TAG = "Frag_bureau";

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

        //View layout = inflater.inflate(R.layout.bureaulayout, container, false);

        mGLView = new MyGLSurfaceView(getActivity());

        View layout =mGLView;

       // final ImageButton mImageButton= (ImageButton) layout.findViewById(R.id.lauchscan);

     //   mImageButton.setOnClickListener(new View.OnClickListener() {
           // @Override
         //   public void onClick(View v) {
       //         Toast toast3 = Toast.makeText(getActivity(), "click", Toast.LENGTH_LONG);
     //           toast3.show();
   //SimpleTextFragment bureauFragment = (SimpleTextFragment) getFragmentManager().findFragmentById(R.id.bureau_fragment);
               // this.showFragment();
       //   }
     //   });

        if (mText != null) {
            mTextView.setText(mText);
            Log.i("SimpleTextFragment", mText);
        }
        //return inflater.inflate(R.layout.bureaulayout,container,false);
        //return mDrawableView;
        return layout;
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