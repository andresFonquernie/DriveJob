package es.fonkyprojects.drivejob.restMethods;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by andre on 03/02/2017.
 */

public class DeleteTask extends AsyncTask<String, Void, String> {

    Context context;
    String result;

    public DeleteTask(Context c){
        this.context = c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return deleteData(params[0]);
        } catch (IOException ioe) {
            return "Network error";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        this.result = result;
    }

    private String deleteData(String uriPath) throws IOException {

        //Initialize and config request, the connect to server
        URL url = new URL(uriPath);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setReadTimeout(10000);
        urlConnection.setConnectTimeout(10000);
        urlConnection.setRequestMethod("DELETE");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.connect();

        //Check update successful or not
        if (urlConnection.getResponseCode() == 204) {
            return "Ok";
        } else {
            return "Delete failed " + urlConnection.getResponseCode();
        }
    }
}