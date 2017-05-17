package es.fonkyprojects.drivejob.restMethods.Car;

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

import es.fonkyprojects.drivejob.model.Car;

public class CarPutTask extends AsyncTask<String, Void, String> {

    Context context;
    String result;

    private Car car;

    public CarPutTask(Context c) {
        this.context = c;
    }

    public void setCarPut(Car car){
        this.car = car;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
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
    }

    private String putData(String uriPath) throws IOException, JSONException {

        BufferedWriter bufferedWriter = null;

        //Create data to send to server
        JSONObject dataToSend = new JSONObject();
        dataToSend.put("brand", car.getBrand());
        dataToSend.put("model", car.getModel());
        dataToSend.put("engineID", car.getEngineID());

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

