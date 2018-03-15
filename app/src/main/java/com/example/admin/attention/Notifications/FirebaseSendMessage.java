package com.example.admin.attention.Notifications;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class  FirebaseSendMessage  extends AsyncTask<String, Integer, Double> {
    private final static String AUTH_KEY = "AIzaSyDu0RJmk7tVQo4cqdVuX47qkLdwn1WBUrw";


    protected Double doInBackground(String... params) {
        try {
            sendRequest(params);
        } catch (Exception ignored) {
        }
        return null;
    }

    protected void onPostExecute(Long l) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }


    public void sendRequest(String... params) {
        try {
            String urlString = "https://fcm.googleapis.com/fcm/send";
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "key=" + AUTH_KEY);
            String postJsonData = "{\"to\": \"/topics/"+params[0]+"\", \"notification\": {\"title\": \"Attention Please\",\"body\":\" "
                    +params[1]+"\"}}";

            //String postJsonData = "{\"to\": \"/topics/"+params[0]+"\", \"notification\": {\"title\": \"Attention Please\",\"body\":\""+params[1]+"\"}}";
            con.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postJsonData);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            Log.i("POST Response Code :: " , String.valueOf(responseCode));

            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.i("notification","succeeded");

            }
        } catch (IOException e) {
            Log.d("exception thrown: ", e.toString());

        }
    }
}