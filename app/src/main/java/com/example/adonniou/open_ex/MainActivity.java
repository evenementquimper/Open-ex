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

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;


/**
 * Sample application demonstrating how to connect to the network and fetch raw
 * HTML. It uses AsyncTask to do the fetch on a background thread. To establish
 * the network connection, it uses HttpURLConnection.
 *
 * This sample uses the logging framework to display log output in the log
 * fragment (LogFragment).
 */

public class MainActivity extends FragmentActivity {

    //public static final String TAG = "Network Connect";
    public static final UsbRequest mUsbRequest=null;
    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";
    private static final String ACTION_USB_DEVICE_DETACHED =
            "com.android.example.USB_DEVICE_DETACHED";
    private PendingIntent mPermissionIntent;

    private UsbDevice mUsbDevise;
    private PreferenceManager mPreference;
    private String mfragment_nom;
    private Fragment mFragment;
    private BureauFragment mbureauFragment;

    private File jsonOutputFile;
    private File jsonFile;



    private static final String TAG = "EDroide";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.mainlayout);

        mfragment_nom = getIntent().getStringExtra("fragment");
        setupFragments();

        showFragment(mbureauFragment);

    }


    private void setupFragments() {
        final FragmentManager fm = getSupportFragmentManager();

        this.mbureauFragment = (BureauFragment) new BureauFragment();
        if (this.mbureauFragment == null) {
            this.mbureauFragment = new BureauFragment();
        }

    }

    private void showFragment(final Fragment newfragment) {
        if (newfragment == null)
            return;

        View currentView= getCurrentFocus();

        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();


        ft.setCustomAnimations(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);


        ft.replace(R.id.intro_fragment, newfragment);

        ft.addToBackStack(null);

        ft.commit();

    }

    @Override
    public void onBackPressed() {

        Log.i("ActivityInTab", "onBackPressed");

        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 1) {
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

public void Menu (View view)
{
    onBackPressed();
}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //fechview.setHovered(true);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // When the user clicks FETCH, fetch the first 500 characters of

            case R.id.newlog:

                return true;

            case R.id.bureau:
                try {
            showFragment(this.mbureauFragment);
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Erreur show fragment: ", Toast.LENGTH_SHORT).show();

                }


               return true;
            // Clear the log view fragment.
            case R.id.usb_item:
        //   ControlTic controltic=new ControlTic();

                return true;
            case R.id.clear_action:
                //Quitter le programme
                this.finish();
                return true;
        }
        return false;
    }
        ;
    /**
     * Vérification de la version de l'os
     */

    private boolean isKitkatWithStepSensor() {
        // BEGIN_INCLUDE(iskitkatsensor)
        // Require at least Android KitKat
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        // Check that the device supports the step counter and detector sensors
        PackageManager packageManager = this.getPackageManager();
        //Build.VERSION_CODES.LOLLIPOP;
        return currentApiVersion >= android.os.Build.VERSION_CODES.KITKAT
                && packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)
                && packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR);
        // END_INCLUDE(iskitkatsensor)
    }


}