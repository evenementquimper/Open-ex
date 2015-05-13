package com.example.adonniou.open_ex;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by adonniou on 29/04/15.
 */
public class ControlTic {

    private static final String TAG = "EDroide_class_JSON";

    JSONObject JSONOBJET;
    JSONArray JSONARRAY_TICKETS;


    String SUCCESS;
    String MESSAGE;
    String TIMESTAMP;

    //  {
    //    "success": false,
    //      "message": "Contrôle: échec !",
    //     "timestamp": "27/04/2015 21:52:25",

    String TICKETS_ID;
    String TICKETS_GAUGE;
    String TICKETS_MANIFESTATION;
    String TICKETS_MANIFESTATION_URL;
    String TICKETS_SEAT;
    String TICKETS_PRICE;
    String TICKETS_VALUE;
    String TICKETS_VALUE_TXT;
    String TICKETS_URL;
    String TICKETS_USERS;
    String TICKETS_CANCEL;

    //   "tickets": [
    //{
    //  "id": 4090,
    //    "gauge": "Cat 1 - Parterre",
    //  "manifestation": "Summit e-venement 2015 @ lun. 27 avr. 2015 22:00",
    // "manifestation_url": "https://test.libre-informatique.fr/event.php/manifestation/1.html",
    //"seat": "C60",
//                "price": "TPLEIN",
//                "value": "50.000",
//                "value_txt": "50,00 €",
//                "url": "https://test.libre-informatique.fr/tck.php/ticket/4090",
//                "users": [
//            "vel"
//            ],
//            "cancel": null
//        }
//        ],
    JSONObject DETAILS;
    JSONObject CONTROL;

    String DETAILS_CONTROL_COMMENT;

    JSONArray ERRORS_ARRAY;
    String DETAILS_CONTROL_ERRORS;

//        "details": {
//        "control": {
//            "comment": null,
//                    "errors": [
//            "Une erreur est survenue lors du contrôle de votre billet #4090."
//            ]
//        },

    JSONObject CONTACTS;
    JSONObject CONTACT;
    //String CONTACTS;
    int CONTACTS_ID;
    String CONTACTS_CONTACT_NAME;
    String CONTACTS_CONTACT_COMMENT;
    String CONTACTS_CONTACT_URL;
    String CONTACTS_CONTACT_FLASH;

//        "contacts": {
//            "4090": {
//                "contact": {
//                    "id": 766,
//                            "name": "Dieudonné Delphine",
//                            "comment": null,
//                            "url": "https://test.libre-informatique.fr/rp.php/contact/766/edit",
//                            "flash": null
//                }
//            }
//        }
//    }
//    }



    public JSONObject getJSONOBJET() {
        return JSONOBJET;
    }

    @Override
    public String toString()
    {
        return JSONOBJET.toString();
    }

    public void setJSONOBJET(JSONObject JSONOBJET) {

        this.JSONOBJET = JSONOBJET;

        try {
            //gestion pour un échec


            if (JSONOBJET.getString("success")!=null)
                this.SUCCESS=JSONOBJET.getString("success");

            if(JSONOBJET.getString("message")!=null)
                this.MESSAGE=JSONOBJET.getString("message");

            if(JSONOBJET.getString("timestamp")!=null)
            this.TIMESTAMP=JSONOBJET.getString("timestamp");

            if(JSONOBJET.getJSONArray("tickets")!=null)
            this.JSONARRAY_TICKETS=JSONOBJET.getJSONArray("tickets");

            if(JSONOBJET.getJSONArray("tickets")!=null) {
                JSONArray json_array_tic = JSONOBJET.getJSONArray("tickets");

                if(json_array_tic.getJSONObject(0).getString("id")!=null)
                this.TICKETS_ID = json_array_tic.getJSONObject(0).getString("id");

            }

            if(JSONOBJET.getJSONObject("details")!=null)
                this.DETAILS_CONTROL_ERRORS=JSONOBJET.getJSONObject("details").getJSONObject("control").getJSONArray("errors").getString(0);



            //this.TICKETS_GAUGE=json_array_tic.getString(1);
            //this.TICKETS_MANIFESTATION=json_array_tic.getString(2);
            //this.TICKETS_MANIFESTATION_URL=json_array_tic.getString(3);
            //this.TICKETS_SEAT=json_array_tic.getString(4);
            //this.TICKETS_PRICE=json_array_tic.getString(5);
            //this.TICKETS_VALUE=json_array_tic.getString(6);
            //this.TICKETS_VALUE_TXT=json_array_tic.getString(7);
            //this.TICKETS_URL=json_array_tic.getString(8);
            //this.TICKETS_USERS=json_array_tic.getString(9);
            //this.TICKETS_CANCEL=json_array_tic.getString(10);


            //DETAILS_CONTROL_ERRORS=JSONOBJET.get("details").toString();
            this.DETAILS=JSONOBJET.getJSONObject("details");



            this.CONTROL=DETAILS.getJSONObject("control");

            this.DETAILS_CONTROL_COMMENT=CONTROL.getString("comment");
            this.ERRORS_ARRAY=CONTROL.getJSONArray("errors");
           // this.DETAILS_CONTROL_ERRORS=ERRORS_ARRAY.get(0).toString();
            //this.DETAILS_CONTROL_ERRORS=CONTROL.getString("errors").toString();
            this.CONTACTS=DETAILS.getJSONObject("contacts");
            this.CONTACTS.getJSONObject(String.valueOf(TICKETS_ID)).getJSONObject("contact");
            this.CONTACTS_ID=CONTACT.getInt("id");
            this.CONTACTS_CONTACT_NAME=CONTACT.getString("name");
            this.CONTACTS_CONTACT_COMMENT=CONTACT.getString("comment");
            this.CONTACTS_CONTACT_URL=CONTACT.getString("url");
            this.CONTACTS_CONTACT_FLASH=CONTACT.getString("flash");

            //ERRORS_ARRAY.get(0);


        } catch (JSONException e) {

           // Log.i(TAG,e.toString());
        }

    }

    public String getSUCCESS() {
        return SUCCESS;
    }

    public void setSUCCESS(String SUCCESS) {
        this.SUCCESS = SUCCESS;
    }

    public String getMESSAGE() {
        return MESSAGE;
    }

    public void setMESSAGE(String MESSAGE) {
        this.MESSAGE = MESSAGE;
    }

    public String getTIMESTAMP() {
        return TIMESTAMP;
    }

    public void setTIMESTAMP(String TIMESTAMP) {
        this.TIMESTAMP = TIMESTAMP;
    }

    public String getTICKETS_ID() {
        return TICKETS_ID;
    }

    public void setTICKETS_ID(String TICKETS_ID) {
        this.TICKETS_ID = TICKETS_ID;
    }

    public String getTICKETS_GAUGE() {
        return TICKETS_GAUGE;
    }

    public void setTICKETS_GAUGE(String TICKETS_GAUGE) {
        this.TICKETS_GAUGE = TICKETS_GAUGE;
    }

    public String getTICKETS_MANIFESTATION() {
        return TICKETS_MANIFESTATION;
    }

    public void setTICKETS_MANIFESTATION(String TICKETS_MANIFESTATION) {
        this.TICKETS_MANIFESTATION = TICKETS_MANIFESTATION;
    }

    public String getTICKETS_MANIFESTATION_URL() {
        return TICKETS_MANIFESTATION_URL;
    }

    public void setTICKETS_MANIFESTATION_URL(String TICKETS_MANIFESTATION_URL) {
        this.TICKETS_MANIFESTATION_URL = TICKETS_MANIFESTATION_URL;
    }

    public String getTICKETS_SEAT() {
        return TICKETS_SEAT;
    }

    public void setTICKETS_SEAT(String TICKETS_SEAT) {
        this.TICKETS_SEAT = TICKETS_SEAT;
    }

    public String getTICKETS_PRICE() {
        return TICKETS_PRICE;
    }

    public void setTICKETS_PRICE(String TICKETS_PRICE) {
        this.TICKETS_PRICE = TICKETS_PRICE;
    }

    public String getTICKETS_VALUE() {
        return TICKETS_VALUE;
    }

    public void setTICKETS_VALUE(String TICKETS_VALUE) {
        this.TICKETS_VALUE = TICKETS_VALUE;
    }

    public String getTICKETS_VALUE_TXT() {
        return TICKETS_VALUE_TXT;
    }

    public void setTICKETS_VALUE_TXT(String TICKETS_VALUE_TXT) {
        this.TICKETS_VALUE_TXT = TICKETS_VALUE_TXT;
    }

    public String getTICKETS_URL() {
        return TICKETS_URL;
    }

    public void setTICKETS_URL(String TICKETS_URL) {
        this.TICKETS_URL = TICKETS_URL;
    }

    public String getTICKETS_USERS() {
        return TICKETS_USERS;
    }

    public void setTICKETS_USERS(String TICKETS_USERS) {
        this.TICKETS_USERS = TICKETS_USERS;
    }

    public String getTICKETS_CANCEL() {
        return TICKETS_CANCEL;
    }

    public void setTICKETS_CANCEL(String TICKETS_CANCEL) {
        this.TICKETS_CANCEL = TICKETS_CANCEL;
    }

    public String getDETAILS_CONTROL_COMMENT() {
        return DETAILS_CONTROL_COMMENT;
    }

    public void setDETAILS_CONTROL_COMMENT(String DETAILS_CONTROL_COMMENT) {
        this.DETAILS_CONTROL_COMMENT = DETAILS_CONTROL_COMMENT;
    }

    public String getDETAILS_CONTROL_ERRORS() {
        return DETAILS_CONTROL_ERRORS;
    }

    public void setDETAILS_CONTROL_ERRORS(String DETAILS_CONTROL_ERRORS) {
        this.DETAILS_CONTROL_ERRORS = DETAILS_CONTROL_ERRORS;
    }

    public int getCONTACTS_ID() {
        return CONTACTS_ID;
    }

    public void setCONTACTS_ID(int CONTACTS_ID) {
        this.CONTACTS_ID = CONTACTS_ID;
    }

    public String getCONTACTS_CONTACT_NAME() {
        return CONTACTS_CONTACT_NAME;
    }

    public void setCONTACTS_CONTACT_NAME(String CONTACTS_CONTACT_NAME) {
        this.CONTACTS_CONTACT_NAME = CONTACTS_CONTACT_NAME;
    }

    public String getCONTACTS_CONTACT_COMMENT() {
        return CONTACTS_CONTACT_COMMENT;
    }

    public void setCONTACTS_CONTACT_COMMENT(String CONTACTS_CONTACT_COMMENT) {
        this.CONTACTS_CONTACT_COMMENT = CONTACTS_CONTACT_COMMENT;
    }

    public String getCONTACTS_CONTACT_URL() {
        return CONTACTS_CONTACT_URL;
    }

    public void setCONTACTS_CONTACT_URL(String CONTACTS_CONTACT_URL) {
        this.CONTACTS_CONTACT_URL = CONTACTS_CONTACT_URL;
    }

    public String getCONTACTS_CONTACT_FLASH() {
        return CONTACTS_CONTACT_FLASH;
    }

    public void setCONTACTS_CONTACT_FLASH(String CONTACTS_CONTACT_FLASH) {
        this.CONTACTS_CONTACT_FLASH = CONTACTS_CONTACT_FLASH;
    }

}
