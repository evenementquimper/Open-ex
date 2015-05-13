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

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Simple fragment containing only a TextView. Used by TextPagerAdapter to create
 * tutorial-style pages for apps.
 */
public class SimpleTextFragment extends Fragment {

    // Contains the text that will be displayed by this Fragment
    String mText;
    // Contains a resource ID for the text that will be displayed by this fragment.
    int mTextId = -1;


    // Keys which will be used to store/retrieve text passed in via setArguments.
    public static final String TEXT_KEY = "text";
    public static final String TEXT_ID_KEY = "text_id";

    private static JSONObject json;

    private static final String TAG = "EDroide";

    public String apidev6="https://dev3.libre-informatique.fr/tck.php/ticket/control/action";

    public String scan_controle1="";
    // For situations where the app wants to modify text at Runtime, exposing the TextView.

    View createdView;

    private TextView mTextView;

    private String message="";

    public SimpleTextFragment() {
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

    private class ControlTaskHttps extends AsyncTask<String, Void, ControlTic> {
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
         //   final ProgressDialog progressBar= (ProgressDialog)
        }

        @Override
        protected ControlTic doInBackground(String...urls) {
            try {

                return https_control(urls[0]);
            } catch (IOException e) {
                Log.i(TAG, "Erreur connection: " + e);
                //return getString(R.string.connection_error);
                ControlTic tic=null;
                return tic;
            }
        }
        /**
         * Uses the logging framework to display the output of the fetch
         * operation in the log fragment.
         */
        @Override
        protected void onPostExecute(ControlTic result) {
            message=result.getMESSAGE();

            final TextView numtic_controle =(TextView) createdView.findViewById(R.id.numtic_controle);
            final TextView success_controle =(TextView) createdView.findViewById(R.id.success_controle);
            final TextView message_controle =(TextView) createdView.findViewById(R.id.message_controle);
            final TextView errors_controle =(TextView) createdView.findViewById(R.id.errors_controle);

            numtic_controle.setText("Numéro: ");
            success_controle.setText("Success: ");
            message_controle.setText("Message: ");
            errors_controle.setText("Erreurs: ");

            numtic_controle.setText("Numéro: "+result.TICKETS_ID);
            success_controle.setText("Success: "+result.SUCCESS);
            message_controle.setText("Message: "+result.MESSAGE);
            errors_controle.setText("Détails: " + result.DETAILS_CONTROL_ERRORS);


            Toast.makeText(getActivity(), "Resultat: " + result.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private ControlTic https_control (String urlString) throws IOException {

        ControlTic mControlTic=new ControlTic();
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
        nameValuePairs.add(new BasicNameValuePair("control[ticket_id]", scan_controle1));
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


            try {

                mControlTic.setJSONOBJET(json);

                //Log.i(TAG, "JSON: "+mControlTic.getSUCCESS());
            } catch (Exception e) {
                Log.i(TAG, "erreur json: " + e);
            }

        }
        else
        {
            mControlTic=null;
            Log.i(TAG, "No InputStream: ");
        }

        return mControlTic;
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

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
            Bundle savedInstanceState) {
        // Before initializing the textView, check if any arguments were provided via setArguments.
        processArguments();

        createdView = inflater.inflate(R.layout.sample_main, container, false);

        final EditText scan_controle =(EditText) createdView.findViewById(R.id.code_contole);


        scan_controle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan_controle1=scan_controle.getText().toString();
                new ControlTaskHttps().execute(apidev6);
            }
        });


        // Create a new TextView and set its text to whatever was provided.

        if (message != null) {
            try {
                //mTextView = new TextView(getActivity());
                //mTextView.setGravity(Gravity.CENTER);
                //mTextView.setText(message);
               // container.addView(mTextView);
                Log.i("SimpleTextFragment", message);
            } catch (Exception e) {
                Log.i(TAG, "Erreur affichage:" + e);
            }
        }
        return createdView;
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