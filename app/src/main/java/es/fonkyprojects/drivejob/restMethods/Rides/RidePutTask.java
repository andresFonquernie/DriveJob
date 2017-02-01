package es.fonkyprojects.drivejob.restMethods.Rides;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import es.fonkyprojects.drivejob.model.Ride;

/**
 * Created by andre on 29/01/2017.
 */

public class RidePutTask extends AsyncTask<String, Void, String> {

    ProgressDialog progressDialog;
    Context context;
    String result;

    private Ride ride;

    public RidePutTask(Context c) {
        this.context = c;
    }

    public void setRidePost(Ride r){
        this.ride = r;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Updating data");
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return putData(params[0]);
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

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private String putData(String uriPath) throws IOException, JSONException {

        BufferedWriter bufferedWriter = null;

        //Create data to send to server
        JSONObject dataToSend = new JSONObject();
        dataToSend.put("timeGoing", ride.getTimeGoing());
        dataToSend.put("timeReturn", ride.getTimeReturn());
        dataToSend.put("placeGoing", ride.getPlaceGoing());
        dataToSend.put("placeReturn", ride.getPlaceReturn());
        dataToSend.put("latGoing", ride.getLatGoing());
        dataToSend.put("latReturn", ride.getLatReturn());
        dataToSend.put("lngGoing", ride.getLngGoing());
        dataToSend.put("lngReturn", ride.getLngReturn());
        dataToSend.put("price", ride.getPrice());
        dataToSend.put("passengers", ride.getPassengers());

        try {
            //Initialize and config request, the connect to server
            URL url = new URL(uriPath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setRequestMethod("PUT");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.connect();

            //Write data into server
            OutputStream outputStream = urlConnection.getOutputStream();
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(dataToSend.toString());
            bufferedWriter.flush();

            //Check update successful or not
            if (urlConnection.getResponseCode() == 200) {
                return "Update";
            } else {
                return "Update failed " + urlConnection.getResponseCode();
            }

        } finally {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        }
    }
}
