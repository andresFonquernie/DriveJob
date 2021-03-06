package es.fonkyprojects.drivejob.restMethods.Messaging;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import es.fonkyprojects.drivejob.model.Messaging;

public class MessagingPostTask extends AsyncTask<String, Void, String> {

    private Context context;
    private String result;
    private Messaging messaging;

    private static final String TAG = "RidePostTask";

    public MessagingPostTask(Context c) {
        this.context = c;
    }

    public void setMessaging(Messaging messaging){
        this.messaging = messaging;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return postData(params[0]);
        } catch (IOException ioe) {
            return "Network error";
        } catch (JSONException js) {
            return "Data invalid";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        this.result = result;
    }

    private String postData(String uriPath) throws IOException, JSONException {

        StringBuilder result = new StringBuilder();
        BufferedWriter bufferedWriter = null;
        BufferedReader bufferedReader = null;

        JSONObject data = new JSONObject();

        data.put("username", messaging.getUsernameFrom());
        data.put("key", messaging.getKey());
        data.put("value", messaging.getValue());
        data.put("topic", messaging.getUseridTo());

        try {
            //Initialize and config request, the connect to server
            URL url = new URL(uriPath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.connect();

            //Write data into server
            OutputStream outputStream = urlConnection.getOutputStream();
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(data.toString());
            bufferedWriter.flush();

            //Read data response from server
            InputStream inputStream = urlConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line).append("\n");
            }

            return result.toString();
        } finally {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
    }
}
