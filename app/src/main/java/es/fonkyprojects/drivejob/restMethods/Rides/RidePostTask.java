package es.fonkyprojects.drivejob.restMethods.Rides;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
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

import es.fonkyprojects.drivejob.model.Ride;

public class RidePostTask extends AsyncTask<String, Void, String> {

    private ProgressDialog progressDialog;
    private Context context;
    private String result;
    private Ride ride;

    private static final String TAG = "RidePostTask";


    public RidePostTask(Context c) {
        this.context = c;
    }

    public void setRidePost(Ride r){
        this.ride = r;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Inserting data");
        progressDialog.show();
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

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private String postData(String uriPath) throws IOException, JSONException {

        StringBuilder result = new StringBuilder();
        BufferedWriter bufferedWriter = null;
        BufferedReader bufferedReader = null;

        //Create data to send to server
        JSONObject dataToSend = new JSONObject();
        dataToSend.put("authorID", ride.getAuthorID());
        dataToSend.put("author", ride.getAuthor());
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
        dataToSend.put("carID", ride.getCarID());

        JSONArray jsDays = new JSONArray(ride.getDays());
        dataToSend.put("days", jsDays);
        JSONArray jsSeats = new JSONArray(ride.getAvSeats());
        dataToSend.put("avSeats", jsSeats);

        JSONArray jsRequest = new JSONArray();
        for (int i=0; i<ride.getRequest().size(); i++){
            JSONObject jso = new JSONObject();
            jso.put("userId", ride.getRequest().get(i).getUserId());
            JSONArray days = new JSONArray(ride.getRequest().get(i).getDays());
            jso.put("days",  days);
            jsRequest.put(jso);
        }
        dataToSend.put("request", jsRequest);

        JSONArray jsJoin = new JSONArray();
        for (int i=0; i<ride.getJoin().size(); i++){
            JSONObject jso = new JSONObject();
            jso.put("userId", ride.getJoin().get(i).getUserId());
            JSONArray days = new JSONArray(ride.getJoin().get(i).getDays());
            jso.put("days",  days);
            jsRequest.put(jso);
        }
        dataToSend.put("join", jsJoin);

        JSONArray jsRequestId = new JSONArray(ride.getRequestUser());
        dataToSend.put("requestUser", jsRequestId);
        JSONArray jsJoinId = new JSONArray(ride.getJoinUser());
        dataToSend.put("joinUser", jsJoinId);

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
            bufferedWriter.write(dataToSend.toString());
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
            if (bufferedWriter != null) { bufferedWriter.close(); }
            if (bufferedReader != null) { bufferedReader.close(); }
        }
    }
}
