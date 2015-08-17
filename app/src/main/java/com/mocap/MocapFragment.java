/*
 * Copyright 2015 Mocap Project
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

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.net.Uri;
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

import com.android.vending.billing.IInAppBillingService;
import com.example.adonniou.open_ex.R;

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

/**
 * Simple fragment containing only a TextView. Used by TextPagerAdapter to create
 * tutorial-style pages for apps.
 */
public class MocapFragment extends Fragment {

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
    private static final String TAG2 = "InApp";

    private MyGLSurfaceView mGLView;

    SensorManager mSensorManager;

    float m_rotationMatrix []=new float [9];
    float m_orientation []=new float [3];

    IInAppBillingService mService;
    ServiceConnection connection;
    String inappid = "android.purchased";

    String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqisWa/NLPj12MqShV4HN4RI+iypQhgRNkS5TKh8biTt4E8bYP4KWWz6Xb5cvWKs9bOISdEFi3snNGPAgRUukP8sMPzhv1qiai91L+1lA341d6bpH2PSxDM0YEi0Y7zpjhDg0vsGhp+I80VI3qisEWFRHn4QEcqczz4r1puVbxAMqC2Fl60ACqkPjw+vq9vWyMNiEMXp38ApC7lh+BcJifqv+LJZMo6wuMxIlprRR/GzZ5Iaa4ffIkHYJH6X2vnX3WZ1Bh2OepKZlhK3wn0896Bvx68XdqVBCSRYX57qjo0JLdg+ppekwK1lBmcKQXEJFx3iihqNh0PT9mW34BVz9GwIDAQAB";

    IabHelper mHelper;

    FragmentTransaction ft;

    // Does the user have the premium upgrade?
    boolean mIsPremium = false;

    // Does the user have an active subscription to the infinite gas plan?
    boolean mSubscribedToInfiniteGas = false;

    // SKUs for our products: the premium upgrade (non-consumable) and gas (consumable)
    static final String SKU_PREMIUM = "premium";
    //static final String SKU_GAS = "gas";

    // SKU for our subscription (infinite gas)
    //static final String SKU_INFINITE_GAS = "infinite_gas";

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

    // Graphics for the gas gauge
    //static int[] TANK_RES_IDS = { R.drawable.gas0, R.drawable.gas1, R.drawable.gas2,
      //      R.drawable.gas3, R.drawable.gas4 };

    // How many units (1/4 tank is our unit) fill in the tank.
    static final int TANK_MAX = 4;

    // Current amount of gas in tank, in units
    int mTank;

    public MocapFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

/*
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                try {
                    mService = IInAppBillingService.Stub.asInterface(service);

                } catch (Exception e) {
                    Log.i(TAG2, "InappBillingservice erreur: "+e);
                }

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }
        };


        this.getActivity().bindService(new Intent("android.vending.billing.InAppBillingService.BIND"), connection, getActivity().getBaseContext().BIND_AUTO_CREATE);
        Log.i(TAG2, "InappBillingservice OK: ");

*/

       // processArguments();
fa=super.getActivity();

        View layout = inflater.inflate(R.layout.layout_opengl, container, false);//com.mocap.MyGLSurfaceView
        final MyGLSurfaceView GLView = (MyGLSurfaceView) layout.findViewById(R.id.glsurfaceview);

        final ImageButton mInitButton= (ImageButton) layout.findViewById(R.id.init);
        final ImageButton mRefreshButton= (ImageButton) layout.findViewById(R.id.refresh);
        final ImageButton mStopButton= (ImageButton) layout.findViewById(R.id.stop);
        final ImageButton mStartButton= (ImageButton) layout.findViewById(R.id.start);
        final ImageButton mSaveButton= (ImageButton) layout.findViewById(R.id.save_file);
        final ImageButton mZommPlusButton= (ImageButton) layout.findViewById(R.id.zoomplus);
        final ImageButton mZommMoinsButton= (ImageButton) layout.findViewById(R.id.zoommoins);

        mRefreshButton.setEnabled(false);
        mRefreshButton.getDrawable().setAlpha(150);
        mStopButton.setEnabled(false);
        mStopButton.getDrawable().setAlpha(150);
        mStartButton.setEnabled(false);
        mStartButton.getDrawable().setAlpha(150);

        mSaveButton.setEnabled(false);
        mSaveButton.getDrawable().setAlpha(150);
        mZommPlusButton.setEnabled(false);
        mZommPlusButton.getDrawable().setAlpha(150);
        mZommMoinsButton.setEnabled(false);
        mZommMoinsButton.getDrawable().setAlpha(150);

        // Create the helper, passing it our context and the public key to verify signatures with
        Log.i(TAG2, "Creating IAB helper.");
        mHelper = new IabHelper(getActivity(), base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.i(TAG2, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.i(TAG2, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.i(TAG2, "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });



        mInitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();

                final View alertDialogView = inflater.inflate(R.layout.init_dialog, null);
                builder.setView(alertDialogView);
                builder.setMessage(R.string.Init_phrase);
                builder.setTitle(R.string.Init_titre);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        try {
                            GLView.onInitGravity();
                            Toast.makeText(getActivity(), "Init Gravity: ", Toast.LENGTH_SHORT).show();
                            mStartButton.setEnabled(true);
                            mStartButton.getDrawable().setAlpha(255);
                            mZommMoinsButton.setEnabled(true);
                            mZommMoinsButton.getDrawable().setAlpha(255);
                            mZommPlusButton.setEnabled(true);
                            mZommPlusButton.getDrawable().setAlpha(255);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Erreur save login: " + e, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton(R.string.Annuler, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        try {


                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Erreur save login: " + e, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();
//activation des sensors


            }
        });
        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//activation des sensors
                GLView.onRefresh();
                Toast.makeText(getActivity(), "Refresh: ", Toast.LENGTH_SHORT).show();
                mStartButton.setEnabled(true);
                mStartButton.getDrawable().setAlpha(255);
                mStopButton.setEnabled(false);
                mStopButton.getDrawable().setAlpha(150);
                mSaveButton.setEnabled(false);
                mSaveButton.getDrawable().setAlpha(150);
                mZommMoinsButton.setEnabled(true);
                mZommMoinsButton.getDrawable().setAlpha(255);
                mZommPlusButton.setEnabled(true);
                mZommPlusButton.getDrawable().setAlpha(255);


            }
        });

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//activation des sensors
                GLView.onSensorsStart();
                mStartButton.setEnabled(false);
                mStartButton.getDrawable().setAlpha(150);
                mStopButton.setEnabled(true);
                mStopButton.getDrawable().setAlpha(255);
                mInitButton.setEnabled(false);
                mInitButton.getDrawable().setAlpha(150);
                Toast.makeText(getActivity(), "Start: ", Toast.LENGTH_SHORT).show();
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//pause des sensor
                GLView.onSensorsPause();
                mStartButton.setEnabled(true);
                mStartButton.getDrawable().setAlpha(255);
                mStopButton.setEnabled(false);
                mStopButton.getDrawable().setAlpha(150);
                mSaveButton.setEnabled(true);
                mSaveButton.getDrawable().setAlpha(255);
                mInitButton.setEnabled(true);
                mInitButton.getDrawable().setAlpha(255);
                mRefreshButton.setEnabled(true);
                mRefreshButton.getDrawable().setAlpha(250);

                Toast.makeText(getActivity(), "Stop: ", Toast.LENGTH_SHORT).show();


//enregistrer le fichier .obj
                //SaveOBJ(getActivity(),GLView);
          }
        });
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUpgradeAppButtonClicked(v);


//pause des sensor
/*
                ArrayList skuList = new ArrayList();
                skuList.add(inappid);
                Bundle querySkus = new Bundle();
                querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
                Bundle skuDetails;

                try {
                    Log.i(TAG, "Pas Erreur div: " + mService.toString());

                    skuDetails = mService.getSkuDetails(3, getActivity().getPackageName(),
                            "inapp", querySkus);

                    int response = skuDetails.getInt("RESPONSE_CODE");

                    if(response == 0)
                    {
                        ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");

                        for (String thisResponse : responseList)
                        {
                            JSONObject object = new JSONObject(thisResponse);
                            String sku = object.getString("productId");
                            String price = object.getString("price");
                            if (sku.equals(inappid)){
                                Log.i(TAG, "Price: "+price);
                                Bundle buyIntentBundle = mService
                                        .getBuyIntent(3, getActivity().getPackageName()
                                                , sku, "inapp"
                                                , "test");
                                PendingIntent pendingIntent = buyIntentBundle
                                        .getParcelable("BUY_INTENT");
                                getActivity().startIntentSenderForResult(
                                        pendingIntent.getIntentSender(),1001,
                                        new Intent(), Integer.valueOf(0),
                                                Integer.valueOf(0), Integer.valueOf(0));


                            }

                        }
                    }

                //} catch (RemoteException e) {
                 //  Log.i(TAG, "Erreur remote: "+e);
               // }
             //catch (JSONException e) {
             //    Log.i(TAG, "Erreur json: " + e);
            }
         catch (Exception e) {
             Log.i(TAG, "Erreur div: "+ e);
        }
*/


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
        Log.i(TAG, "DIR: ");
        float sVertices[] = glview.getsVertices();
        FileOutputStream fOut = null;
        OutputStreamWriter osw = null;

        File mFile;

        if(Environment.DIRECTORY_PICTURES!=null) {
            try {
                mFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES ),"mocap.obj");

                Log.i(TAG, "Long Vertices: "+sVertices.length);
                fOut=new FileOutputStream(mFile);
                osw = new OutputStreamWriter(fOut);
                osw.write("# *.obj file (Generate by Mocap 3D)\n");
                osw.flush();

                for (int i =0; i < sVertices.length-4; i=i+3) {

                    try {
                        String data = "v "+Float.toString(sVertices[i])+" "+Float.toString(sVertices[i+1])+" "+Float.toString(sVertices[i+2])+"\n";
                       Log.i(TAG, i + ": " + data);
                        osw.write(data);
                        osw.flush();

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
                Toast.makeText(getActivity(), "Save : "+Environment.DIRECTORY_PICTURES+"/mocap.obj ", Toast.LENGTH_SHORT).show();

                //lancement d'un explorateur de fichiers vers le fichier créer
                //systeme des intend
                try {
                    File root = new File(Environment.DIRECTORY_PICTURES);
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

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (connection != null)
        {
            getActivity().unbindService(connection);
        }
    }


    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.i(TAG2, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.i(TAG2, "Query inventory was successful."+result.getMessage());

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             *verifyDeveloperPayload().
             */



            // Do we have the premium upgrade?
            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
            Log.i(TAG2, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));

            // Do we have the infinite gas plan?
           // Purchase infiniteGasPurchase = inventory.getPurchase(SKU_INFINITE_GAS);
           // mSubscribedToInfiniteGas = (infiniteGasPurchase != null &&
              //      verifyDeveloperPayload(infiniteGasPurchase));
            //Log.i(TAG2, "User " + (mSubscribedToInfiniteGas ? "HAS" : "DOES NOT HAVE")
              //      + " infinite gas subscription.");
            //if (mSubscribedToInfiniteGas) mTank = TANK_MAX;

            // Check for gas delivery -- if we own gas, we should fill up the tank immediately
            //Purchase gasPurchase = inventory.getPurchase(SKU_GAS);
            //if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
              //  Log.i(TAG2, "We have gas. Consuming it.");
               // mHelper.consumeAsync(inventory.getPurchase(SKU_GAS), mConsumeFinishedListener);
                //return;
            //}

            updateUi();
            setWaitScreen(false);
            Log.i(TAG2, "Initial inventory query finished; enabling main UI.");
        }
    };

    // User clicked the "Buy Gas" button
    public void onBuyGasButtonClicked(View arg0) {
        Log.i(TAG2, "Buy gas button clicked.");

        if (mSubscribedToInfiniteGas) {
            complain("No need! You're subscribed to infinite gas. Isn't that awesome?");
            return;
        }

        if (mTank >= TANK_MAX) {
            complain("Your tank is full. Drive around a bit!");
            return;
        }

        // launch the gas purchase UI flow.
        // We will be notified of completion via mPurchaseFinishedListener
        setWaitScreen(true);
        Log.i(TAG2, "Launching purchase flow for gas.");

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = "";

        //mHelper.launchPurchaseFlow(getActivity(), SKU_GAS, RC_REQUEST,
          //      mPurchaseFinishedListener, payload);
    }

    // User clicked the "Upgrade to Premium" button.
    public void onUpgradeAppButtonClicked(View arg0) {
        Log.i(TAG2, "Upgrade button clicked; launching purchase flow for upgrade.");
        setWaitScreen(true);

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = "";

        mHelper.launchPurchaseFlow(getActivity(), SKU_PREMIUM, RC_REQUEST,
                mPurchaseFinishedListener, payload);
    }

    // "Subscribe to infinite gas" button clicked. Explain to user, then start purchase
    // flow for subscription.
    public void onInfiniteGasButtonClicked(View arg0) {
        if (!mHelper.subscriptionsSupported()) {
            complain("Subscriptions not supported on your device yet. Sorry!");
            return;
        }

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = "";

        setWaitScreen(true);
        Log.i(TAG2, "Launching purchase flow for infinite gas subscription.");
        //mHelper.launchPurchaseFlow(getActivity(),
          //      SKU_INFINITE_GAS, IabHelper.ITEM_TYPE_SUBS,
            //    RC_REQUEST, mPurchaseFinishedListener, payload);
    }


    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.i(TAG2, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                setWaitScreen(false);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                setWaitScreen(false);
                return;
            }

            Log.i(TAG2, "Purchase successful.");

            //if (purchase.getSku().equals(SKU_GAS)) {
                // bought 1/4 tank of gas. So consume it.
              //  Log.i(TAG2, "Purchase is gas. Starting gas consumption.");
               // mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            //}
            if (purchase.getSku().equals(SKU_PREMIUM)) {
                // bought the premium upgrade!
                Log.i(TAG2, "Purchase is premium upgrade. Congratulating user.");
                alert("Thank you for upgrading to premium!");
                mIsPremium = true;
                updateUi();
                setWaitScreen(false);
            }
            //else if (purchase.getSku().equals(SKU_INFINITE_GAS)) {
                // bought the infinite gas subscription
              //  Log.i(TAG2, "Infinite gas subscription purchased.");
                //alert("Thank you for subscribing to infinite gas!");
                //mSubscribedToInfiniteGas = true;
                //mTank = TANK_MAX;
                //updateUi();
                //setWaitScreen(false);
            //}
        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.i(TAG2, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.i(TAG2, "Consumption successful. Provisioning.");
                mTank = mTank == TANK_MAX ? TANK_MAX : mTank + 1;
                saveData();
                alert("You filled 1/4 tank. Your tank is now " + String.valueOf(mTank) + "/4 full!");
            }
            else {
                complain("Error while consuming: " + result);
            }
            updateUi();
            setWaitScreen(false);
            Log.i(TAG2, "End consumption flow.");
        }
    };

    // Drive button clicked. Burn gas!
    public void onDriveButtonClicked(View arg0) {
        Log.d(TAG, "Drive button clicked.");
        if (!mSubscribedToInfiniteGas && mTank <= 0) alert("Oh, no! You are out of gas! Try buying some!");
        else {
            if (!mSubscribedToInfiniteGas) --mTank;
            saveData();
            alert("Vroooom, you drove a few miles.");
            updateUi();
            Log.i(TAG2, "Vrooom. Tank is now " + mTank);
        }
    }


    // updates UI to reflect model
    public void updateUi() {
        // update the car color to reflect premium status or lack thereof
        //((ImageView)findViewById(R.id.free_or_premium)).setImageResource(mIsPremium ? R.drawable.premium : R.drawable.free);

        // "Upgrade" button is only visible if the user is not premium
        //findViewById(R.id.upgrade_button).setVisibility(mIsPremium ? View.GONE : View.VISIBLE);

        // "Get infinite gas" button is only visible if the user is not subscribed yet
        //findViewById(R.id.infinite_gas_button).setVisibility(mSubscribedToInfiniteGas ?
              //  View.GONE : View.VISIBLE);

        // update gas gauge to reflect tank status
        if (mSubscribedToInfiniteGas) {
            //((ImageView)findViewById(R.id.gas_gauge)).setImageResource(R.drawable.gas_inf);
        }
        else {
           // int index = mTank >= TANK_RES_IDS.length ? TANK_RES_IDS.length - 1 : mTank;
         //   ((ImageView)findViewById(R.id.gas_gauge)).setImageResource(TANK_RES_IDS[index]);
        }
    }

    // Enables or disables the "please wait" screen.
    void setWaitScreen(boolean set) {
    //    findViewById(R.id.screen_main).setVisibility(set ? View.GONE : View.VISIBLE);
      //  findViewById(R.id.screen_wait).setVisibility(set ? View.VISIBLE : View.GONE);
    }

    void complain(String message) {
        Log.i(TAG2, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(getActivity());
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.i(TAG2, "Showing alert dialog: " + message);
        bld.create().show();
    }

    void saveData() {

        /*
         * WARNING: on a real application, we recommend you save data in a secure way to
         * prevent tampering. For simplicity in this sample, we simply store the data using a
         * SharedPreferences.
         */

        //SharedPreferences.Editor spe = getActivity().getPreferences(MODE_PRIVATE).edit();
        //spe.putInt("tank", mTank);
        //spe.commit();
        Log.i(TAG2, "Saved data: tank = " + String.valueOf(mTank));
    }

    private void saveDialog(final MyGLSurfaceView GLView) {

    try {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage(R.string.save_valid);
        builder1.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                try {

                    SaveOBJ(getActivity(), GLView);
                    //mSaveButton.setEnabled(false);
                    //mSaveButton.getDrawable().setAlpha(150);
                    //mStopButton.setEnabled(false);
                    //mStopButton.getDrawable().setAlpha(150);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Erreur save login: " + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder1.setNegativeButton(R.string.Annuler, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                try {


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Erreur save login: " + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder1.show();
    } catch (Exception e) {
        Log.i(TAG,"Erreur dialog: "+e);
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