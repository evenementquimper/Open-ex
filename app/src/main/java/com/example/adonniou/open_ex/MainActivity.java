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
import android.content.BroadcastReceiver;
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

import org.apache.http.NameValuePair;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
    // Reference to the fragment showing events, so we can clear it with a button
    // as necessary.

    //private DialogFragment mLoginDialog;
    private UsbDevice mUsbDevise;
    private PreferenceManager mPreference;
    private String mfragment_nom;
    private Fragment mFragment;
    private SimpleTextFragment msimpleTextFragment;
    private BureauFragment mbureauFragment;
    /* private = new LoginDialog(); */
    private File jsonOutputFile;
    private File jsonFile;

    private static JSONObject json;
    private static String CSRFTOKEN;

    private static final String TAG = "EDroide";

    //private JsonFactory jsonFactory = null;
    //private JsonParser jp = null;

    public String apitest1="http://cadorb.fr/dahouet/api/api.php?action=get&var=regate";


    public String apidev3="https://dev3.libre-informatique.fr/tck.php/ticket/login?signin[username]=antoine?signin[password]=android2015@";

    public String apidev4="https://dev3.libre-informatique.fr/default.php/login"; //+parametres Marche en POST réponse 200 ok

    public String apidev7="https://dev3.libre-informatique.fr/"; //+parametres Marche en POST réponse 200 ok



    public String apidev5="https://dev3.libre-informatique.fr/tck.php/ticket/control/action?control[id]=&control[ticket_id]=2222&control[checkpoint_id]=1&control[comment]=";

    public String apidev6="https://dev3.libre-informatique.fr/tck.php/ticket/control/action";

    public String cookie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(new CookieManager());

        View UsbBouton = findViewById(R.id.usb_item);

        this.setContentView(R.layout.mainlayout);


        mfragment_nom = getIntent().getStringExtra("fragment");
        setupFragments();

        showFragment(mbureauFragment);

        if(Read_log(this)==null)
        {
            showDialog();
        }

    }


    public JSONObject Read_log(Context context)
    {
        FileInputStream fIn = null;
        InputStreamReader isr = null;

        char[] inputBuffer = new char[255];
        String data = null;

        JSONObject json=null;

        try{
            fIn = context.openFileInput("settings.txt");
            isr = new InputStreamReader(fIn);
            isr.read(inputBuffer);
            data = new String(inputBuffer);
            //affiche le contenu de mon fichier dans un popup surgissant
            Toast.makeText(context, "data: " + data, Toast.LENGTH_SHORT).show();
            json=new JSONObject(data);

        }
        catch (Exception e) {
            Toast.makeText(context, "Settings not read", Toast.LENGTH_SHORT).show();
        }
            /*finally {
               try {
                      isr.close();
                      fIn.close();
                      } catch (IOException e) {
                        Toast.makeText(context, "Settings not read",Toast.LENGTH_SHORT).show();
                      }
            } */
        return json;
    }

    private void showDialog()
    {
        //initialisation de la boite de dialog login
        DialogFragment mLoginDialog = new LoginDialog();
        mLoginDialog.show(getSupportFragmentManager(), "LoginDialog");
    }

    private void setupFragments() {
        final FragmentManager fm = getSupportFragmentManager();

        this.mbureauFragment = (BureauFragment) new BureauFragment();
        if (this.mbureauFragment == null) {
            this.mbureauFragment = new BureauFragment();
        }

        this.msimpleTextFragment = (SimpleTextFragment) new SimpleTextFragment();
        if (this.msimpleTextFragment == null) {
            this.msimpleTextFragment = new SimpleTextFragment();
        }


    }

    private void showFragment(final Fragment newfragment) {
        if (newfragment == null)
            return;

        View currentView= getCurrentFocus();


//currentView.destroyDrawingCache();
//currentView.clearFocus();
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
 // We can also animate the changing of fragment
//Fragment currentfragment=fm.findFragmentById(R.id.intro_fragment);
//currentfragment.setUserVisibleHint(false);

        ft.setCustomAnimations(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);
//ft.hide(fragment);
// ft.remove(fragment);
//ft.add(msimpleTextFragment,null);

        ft.replace(R.id.intro_fragment, newfragment);
//this.mbureauFragment;
        ft.addToBackStack(null);
//ft.detach(fragment);
//Toast.makeText(getBaseContext(), "Changement de fragment commit: ", Toast.LENGTH_SHORT).show();
//ft.remove(fragment);
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

    public void Scan_ticket_fragment(View v) {
        showFragment(this.msimpleTextFragment);
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

            //http://cadorb.fr/dahouet/api/api.php?action=get&var=regate
            case R.id.newlog:
                showDialog();
                return true;

            case R.id.bureau:
                try {
            showFragment(this.mbureauFragment);
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Erreur show fragment: ", Toast.LENGTH_SHORT).show();

                }
          //  case R.id.fetch_action:

           //     new DownloadTask().execute(apitest1);
       // try {
                    //mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                   // IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                 //   registerReceiver(mUsbReceiver, filter);
               //     Toast.makeText(getBaseContext(), "mPermissionIntent: " + mPermissionIntent.toString(), Toast.LENGTH_SHORT).show();
             //   } catch (Exception e) {
                   // e.printStackTrace();
                  //  Toast.makeText(getBaseContext(), "erreur Pending intent: " + e, Toast.LENGTH_SHORT).show();
                //}
                //InputDevice mInputDevice;
                //try {
                 //   InputManager mInputManager = (InputManager) getSystemService(this.INPUT_SERVICE);
                    //HashMap <String ,InputDevice> InpMap;

                  //  mInputManager.registerInputDeviceListener(new InputManager.InputDeviceListener() {

                      //  @Override
                    //    public void onInputDeviceAdded(int deviceId) {
                            //Log.d("Input", "InputDeviceAdded: " + deviceId);
                            //Toast.makeText(getBaseContext(), "Oninputadd: " + deviceId, Toast.LENGTH_SHORT).show();

                          //identifier le device
                            //View UsbBouton_rouge=new View();
                       // }

//                        @Override
  //                      public void onInputDeviceRemoved(int deviceId) {
                            //Log.d("Input", "InputDeviceRemoved: " + deviceId);
                           // Toast.makeText(getBaseContext(), "onInputDeviceRemoved: " + deviceId, Toast.LENGTH_SHORT).show();
    //                    }

      //                  @Override
        //                public void onInputDeviceChanged(int deviceId) {
                          //  Log.d("Input", "InputDeviceChanged: " + deviceId);
                        //    Toast.makeText(getBaseContext(), "InputDeviceChanged: " + deviceId, Toast.LENGTH_SHORT).show();
          //              }

            //        }, null);

              //  } catch (Exception e) {
                //    Toast.makeText(getBaseContext(), "erreur inputdev: " +e, Toast.LENGTH_SHORT).show();
                  //  e.printStackTrace();
                //}


              //  try {

                //    UsbManager mUsbManager = (UsbManager) getSystemService(this.USB_SERVICE);
                  //  HashMap <String ,UsbDevice> UsbMap;
                    //        UsbMap=mUsbManager.getDeviceList();

                  //  if (UsbMap != null)
                   // {
                     //   Iterator<UsbDevice> deviceIterator = UsbMap.values().iterator();
                       // while (deviceIterator.hasNext()) {
                        //mUsbDevise = deviceIterator.next();
                 //   Toast.makeText(getBaseContext(), "USb devise: " +mUsbDevise.getDeviceName(), Toast.LENGTH_SHORT).show(); ///dev/bus/usb/001/002
                  //  Toast.makeText(getBaseContext(), "USb DeviceId: " +mUsbDevise.getDeviceId(), Toast.LENGTH_SHORT).show();//1002
                  //  Toast.makeText(getBaseContext(), "USb DeviceProtocol: " +mUsbDevise.getDeviceProtocol(), Toast.LENGTH_SHORT).show();//0
                 //   Toast.makeText(getBaseContext(), "USb InterfaceCount: " +mUsbDevise.getInterfaceCount(), Toast.LENGTH_SHORT).show();//1

                   //     }
                    //}
                    //else
                    //{
                     //   Toast.makeText(getBaseContext(), "USbMap Vide: ", Toast.LENGTH_SHORT).show();
                    //}
               // } catch (Exception e) {
                 //   e.printStackTrace();
                   // Toast.makeText(getBaseContext(), "erreur Usb : " + e, Toast.LENGTH_SHORT).show();
                //}

               return true;
            // Clear the log view fragment.
            case R.id.usb_item:
        //   ControlTic controltic=new ControlTic();

                new DownloadTaskHttps().execute(apidev7);//connection e-venement
               //new ControlTaskHttps().execute(apidev6);//Controle ticket
                return true;
            case R.id.clear_action:
                //Quitter le programme
                this.finish();
                return true;
        }
        return false;
    }

    private class DownloadTaskHttps extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {

                return https_test(urls[0]);

                // return loadFromNetwork(urls[0]);
            } catch (IOException e) {
                Log.i(TAG, "Erreur connection: " + e);
                return getString(R.string.connection_error);
            }
        }

        /**
         * Uses the logging framework to display the output of the fetch
         * operation in the log fragment.
         */
        @Override
        protected void onPostExecute(String result) {
           // Log.i(TAG, result);
            //affichage du resultat dans un toast

            //try {
          //      json= new JSONObject(result);
            //   Log.i(TAG, "Json: "+json.length());
            //} catch (JSONException e) {
              //  Log.i(TAG, "Erreur Json : "+e);
            //}


        }
    }

    /**
     * Implementation of AsyncTask, to fetch the data in the background away from
     * the UI thread.
     */
    private class ControlTaskHttps extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String...urls) {
            try {

                return String.valueOf(https_control(urls[0]));
               // return loadFromNetwork(urls[0]);
            } catch (IOException e) {
                Log.i(TAG, "Erreur connection: " + e);
                return getString(R.string.connection_error);
            }
        }

        /**
         * Uses the logging framework to display the output of the fetch
         * operation in the log fragment.
         */
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Result: " + result, Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * Initiates the fetch operation.
     */
    private String loadFromNetwork(String urlString) throws IOException {
        InputStream stream = null;
        String str = "";

        try {
            stream = downloadUrl(urlString);
            str = readIt(stream, 500);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return str;
    }

    private int httptestconnect (String urlString) throws IOException {

        URL url = new URL(urlString);


        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setRequestProperty("User-Agent", "e-venement-app/");
        // Start the query
        conn.connect();
        return conn.getResponseCode();
    }

    private String https_control (String urlString) throws IOException {



        String token="";
        URL url = new URL(urlString);

        Log.i(TAG, "Protocol: " + url.getProtocol().toString());

        //if (url.getProtocol().toLowerCase().equals("https")) {
        //trustAllHosts();

        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        conn.setReadTimeout(20000 /* milliseconds */);
        conn.setConnectTimeout(25000 /* milliseconds */);
        // conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        conn.setChunkedStreamingMode(0);

        conn.setRequestProperty("User-Agent", "e-venement-app/0.1");


        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(); //On cr�e la liste qui contiendra tous nos param�tres

       //"https://dev3.libre-informatique.fr/tck.php/ticket/control/action?control[id]=&control[ticket_id]=2222&control[checkpoint_id]=1&control[comment]=";

        //Et on y rajoute nos param�tres
        nameValuePairs.add(new BasicNameValuePair("control[ticket_id]", "2222"));
        nameValuePairs.add(new BasicNameValuePair("control[checkpoint_id]", "1"));
        //nameValuePairs.add(new BasicNameValuePair("control[id]", ""));
        //nameValuePairs.add(new BasicNameValuePair("control[comment]", ""));

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer2 = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer2.write(getQuery(nameValuePairs));
        writer2.flush();
        //writer2.close();
        //os.close();

        conn.connect();

        String headerName = null;

        for (int i = 1; (headerName = conn.getHeaderFieldKey(i)) != null; i++)
        {
            Log.i(TAG, headerName + ": " + conn.getHeaderField(i));
        }


        if (conn.getInputStream()!=null)
        {
            json=  convertInputStreamToJson(conn.getInputStream());

            Log.i(TAG, json.toString());
            ControlTic mControlTic=new ControlTic();

            try {
                mControlTic.setJSONOBJET(json);
              //  Log.i(TAG, "JSON: "+mControlTic.getSUCCESS());
            } catch (Exception e) {
                Log.i(TAG, "erreur json: " + e);
            }

        }

        return token;
    }

    private String https_test (String urlString) throws IOException {



        String token="";
        URL url = new URL(urlString);

Log.i(TAG, "Protocol: " + url.getProtocol().toString());

       //if (url.getProtocol().toLowerCase().equals("https")) {
            trustAllHosts();

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();




            conn.setReadTimeout(20000 /* milliseconds */);
            conn.setConnectTimeout(25000 /* milliseconds */);
           // conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setChunkedStreamingMode(0);

            conn.setRequestProperty("User-Agent", "e-venement-app/0.1");

        //OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        //writer.getEncoding();
        //writer.write("&signin[username]=antoine");
        //writer.write("&signin[password]=android2015@");
        //writer.write("?control[id]=");
        //writer.write("&control[ticket_id]=2222");
        //writer.write("&control[checkpoint_id]=1");
        //writer.write("&control[comment]=");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(); //On cr�e la liste qui contiendra tous nos param�tres

        //Et on y rajoute nos param�tres
        nameValuePairs.add(new BasicNameValuePair("signin[username]", "antoine"));
        nameValuePairs.add(new BasicNameValuePair("signin[password]", "android2015@"));
        //nameValuePairs.add(new BasicNameValuePair("control[id]", ""));
        //nameValuePairs.add(new BasicNameValuePair("control[ticket_id]", "2222"));
        //nameValuePairs.add(new BasicNameValuePair("control[checkpoint_id]", "1"));
        //nameValuePairs.add(new BasicNameValuePair("control[comment]", ""));

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer2 = new BufferedWriter(
        new OutputStreamWriter(os, "UTF-8"));
        writer2.write(getQuery(nameValuePairs));
        writer2.flush();
        //writer2.close();
        //os.close();

       // conn.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        //writer.write("&signin[_csrf_token]="+CSRFTOKEN);
        //writer.flush();

        conn.connect();

        String headerName = null;

        for (int i = 1; (headerName = conn.getHeaderFieldKey(i)) != null; i++)
        {
            //data=data+"Header Nme : " + headerName;
            //data=data+conn.getHeaderField(i);
            // Log.i (TAG,headerName);
            Log.i(TAG, headerName + ": " + conn.getHeaderField(i));

        }

        int responseCode = conn.getResponseCode();

       if(responseCode == conn.HTTP_OK) {
           final String COOKIES_HEADER = "Set-Cookie";
           cookie = conn.getHeaderField(COOKIES_HEADER);
        }

        if (conn.getInputStream()!=null)
        {



           // token =getStringFromInputStream(conn.getInputStream());
            Log.i(TAG, readIt(conn.getInputStream(), 15000));
            token=readIt(conn.getInputStream(),15000);
            Log.i(TAG, getStringFromInputStream(conn.getInputStream()));
            //data=readIt(conn.getInputStream(),7500);
            //Log.i(TAG,token);
            //token=readIt(conn.getInputStream(),7500);
        }
            //conn.connect();
           // List<String> cookiesList = conn.getHeaderFields().get("Set-Cookie");


       // }
        //conn.disconnect();
        return token;
    }



    private String https_token (String urlString) throws IOException {



        String token=null;
        URL url = new URL(urlString);

        if (url.getProtocol().toLowerCase().equals("https")) {
            trustAllHosts();
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();


            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setChunkedStreamingMode(0);

            conn.setRequestProperty("User-Agent", "e-venement-app/");

            List<String> cookies1 = conn.getHeaderFields().get("Set-Cookie");

            for (int g = 0; g < cookies1.size(); g++) {
                Log.i(TAG, "Cookie_list: " + cookies1.get(g).toString());
                Cookie cookie;
                String[] cook = cookies1.get(g).toString().split(";");

                String[] subcook = cook[0].split("=");
                token = subcook[1];
                Log.i(TAG, "Sub Cook: " + subcook[1]);

                // subcook[1];
            }
        }
        //conn.disconnect();
        return token;
    }

    private String httpstestconnect (String urlString) throws IOException {
        CookieManager msCookieManager = new CookieManager();

        URL url = new URL(urlString);

        if (url.getProtocol().toLowerCase().equals("https")) {
            trustAllHosts();




            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            try {

                String headerName = null;

                for (int i = 1; (headerName = conn.getHeaderFieldKey(i)) != null; i++)
                {
                    //data=data+"Header Nme : " + headerName;
                    //data=data+conn.getHeaderField(i);
                    // Log.i (TAG,headerName);
                    Log.i(TAG, headerName + ": " + conn.getHeaderField(i));
                }



              //  Map<String, List<String>> headerFields = conn.getHeaderFields();
                //List<String> cookiesHeader = headerFields.get("Set-Cookie");

                //if(cookiesHeader != null)
                //{
                  //  for (String cookie : cookiesHeader)
                   // {
                     //   msCookieManager.getCookieStore().add(null,HttpCookie.parse(cookie).get(0));

                    //}
                //}


            } catch (Exception e) {
                Log.i(TAG, "Erreur Cookie" + e);
            }


            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setChunkedStreamingMode(0);

            conn.setRequestProperty("User-Agent", "e-venement-app/");

            //if(msCookieManager.getCookieStore().getCookies().size() > 0)
            //{
          //        conn.setRequestProperty("Cookie",
            //            TextUtils.join(",", msCookieManager.getCookieStore().getCookies()));
            //}

           // conn= (HttpsURLConnection) url.wait(); ;
                    //(HttpsURLConnection) url.openConnection();




            final String password="android2015@";


            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.getEncoding();
            writer.write("&signin[username]=antoine");
            writer.write("&signin[password]=android2015@");
            //writer.write("&signin[_csrf_token]="+CSRFTOKEN);
            writer.flush();
//Log.i(TAG,"Writer: "+writer.toString());

         //   conn.connect();






String data=null;

                //
                if (conn.getInputStream()!=null)
                {
                    Log.i(TAG, readIt(conn.getInputStream(), 2500));
                     data=readIt(conn.getInputStream(),7500);
                }

          //  return conn.getResponseCode();
            return data;
        //return readIt(inputStream,1028);
        }

        else
        {
            return url.getProtocol();
        }

    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    private static JSONObject convertInputStreamToJson(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";



        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        JSONObject jsonobjet=null;

        try {
            jsonobjet=new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        return jsonobjet;

    }

    /**
     * Given a string representation of a URL, sets up a connection and gets
     * an input stream.
     *
     * @param urlString A string representation of a URL.
     * @return An InputStream retrieved from a successful HttpURLConnection.
     * @throws java.io.IOException
     */
    private InputStream downloadUrl(String urlString) throws IOException {
        // BEGIN_INCLUDE(get_inputstream)
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setRequestProperty("User-Agent", "e-venement-app/");
        // Start the query
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
        // END_INCLUDE(get_inputstream)
    }

    /**
     * Reads an InputStream and converts it to a String.
     *
     * @param stream InputStream containing HTML from targeted site.
     * @param len    Length of string that this method returns.
     * @return String concatenated according to len parameter.
     * @throws java.io.IOException
     * @throws java.io.UnsupportedEncodingException
     */
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

    public static String getStringFromInputStream(InputStream stream) throws IOException
    {
        int n = 0;
        char[] buffer = new char[1024 * 4];
        InputStreamReader reader = new InputStreamReader(stream, "UTF8");
        StringWriter writer = new StringWriter();
        while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);
        return writer.toString();
    }


    private static void trustAllHosts() {

        X509TrustManager easyTrustManager = new X509TrustManager() {

            public void checkClientTrusted(
                    X509Certificate[] chain,
                    String authType) throws CertificateException {
                // Oh, I am easy!
            }

            public void checkServerTrusted(
                    X509Certificate[] chain,
                    String authType) throws CertificateException {
                // Oh, I am easy!
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

        };

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{easyTrustManager};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    /**
     * Creation d'un broadcastReceiver pour détecter la douchette en usb
     */

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

//détection d'un appareil usb qui se connecte
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //call method to set up device communication
//affichage bouton vert de présence douchette
                        }
                    } else {
                        Log.d(TAG, "permission denied for device " + device);
                    }
                }
                 //détection de débranchement d'un appareil usb
                if (ACTION_USB_DEVICE_DETACHED.equals(action)) {
                    UsbDevice deviceout = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (deviceout != null) {

                        //affichage bouton rouge indiquant qu'il n'y a pas de douchette
                        // call your method that cleans up and closes communication with the device

                    }
                }
            }
        }}

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