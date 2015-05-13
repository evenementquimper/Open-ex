package com.example.adonniou.open_ex;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by adonniou on 16/04/15.
 */
public class LoginDialog extends DialogFragment {

    private PreferenceManager mPreference;
    private JSONObject jsonLog_save;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View alertDialogView = inflater.inflate(R.layout.login_dialog, null);
        final EditText login = (EditText) alertDialogView.findViewById(R.id.username);
        final EditText hote = (EditText) alertDialogView.findViewById(R.id.hote);
        final EditText pass =(EditText) alertDialogView.findViewById(R.id.password);
        final CheckBox checkBox_save = (CheckBox) alertDialogView.findViewById(R.id.save_log);

        builder.setView(alertDialogView);

        if (Read_log(getActivity()) != null) {

            try {
                JSONObject js=Read_log(getActivity());

                login.setText(js.getString("login"),null);
                pass.setText(js.getString("pass"),null);
                hote.setText(js.getString("hote"), null);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        builder.setTitle("Login");


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        AlertDialog.Builder builder1 = builder.setView(alertDialogView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        try {
//récuperer le login + trim + minuscule
                            String log = login.getText().toString();
                            log = log.trim();
                            log = log.toLowerCase();

//récuperer le pass + trim + minuscule
                            String pas = pass.getText().toString();
                            pas = pas.trim();
                            pas = pas.toLowerCase();

//récuperer l'hote + trim + minuscule
                            String hot= hote.getText().toString();
                            hot = hot.trim();
                            hot =hot.toLowerCase();

                            JSONObject JSONLogin=new JSONObject();


//sauvegarde en local du login
                            //
                            DownloadTask requete = new DownloadTask();

                         if (checkBox_save.isChecked()) {
                                jsonLog_save = new JSONObject();
                                jsonLog_save.put("login",log).toString();
                                jsonLog_save.put("pass",pas).toString();
                                jsonLog_save.put("hote",hot).toString();
                                Save_log(getActivity(), jsonLog_save);
                                //signin[remember]
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Erreur save login: " + e, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.Annuler, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    /**
     * Implementation of AsyncTask, to fetch the login data in the background away from
     * the UI thread.
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {

                return loadFromNetwork(urls[0]);
            } catch (IOException e) {
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
           Toast.makeText(getActivity(), "Result: " + result, Toast.LENGTH_SHORT).show();

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
        //conn.setRequestProperty("User-Agent", "e-venement-app/");
        conn.setDoInput(true);
        // Start the query
        //Toast.makeText(getActivity(), "Connection Code: " + conn.getResponseCode(), Toast.LENGTH_SHORT).show();

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
        char[] buffer = new char[0];
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            buffer = new char[len];
            reader.read(buffer);
        return new String(buffer);
    }

    private void Save_log (Context context,JSONObject sav_log)
    {
        FileOutputStream fOut = null;
        OutputStreamWriter osw = null;

        if(Read_log(context)!=null)
        {
            Toast.makeText(context, "Compte existant: ", Toast.LENGTH_SHORT).show();

        }
        else {
            try {
                fOut = context.openFileOutput("settings.txt", Context.MODE_APPEND);
                osw = new OutputStreamWriter(fOut);

                osw.write(sav_log.toString());
                osw.flush();
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

}
