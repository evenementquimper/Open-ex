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

package com.mocap;

import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbRequest;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.adonniou.open_ex.R;

import org.json.JSONException;
import org.json.JSONObject;

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
    private MocapFragment mbureauFragment;

    private File jsonOutputFile;
    private File jsonFile;

    String inappid = "android.purchased";

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

        this.mbureauFragment = (MocapFragment) new MocapFragment();
        if (this.mbureauFragment == null) {
            this.mbureauFragment = new MocapFragment();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001)
        {
            String purchaseData =data.getStringExtra("INAPP_PURCHASE_DATA");
            if (resultCode == RESULT_OK)
            {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString(inappid);
                    Toast.makeText(this,"You have bought the "+sku,Toast.LENGTH_LONG).show();
                    ;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}