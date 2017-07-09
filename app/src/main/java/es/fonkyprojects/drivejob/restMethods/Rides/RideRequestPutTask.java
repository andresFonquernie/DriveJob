package es.fonkyprojects.drivejob.restMethods.Rides;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import es.fonkyprojects.drivejob.model.UserDays;
public class RideRequestPutTask extends AsyncTask<String, Void, String> {

    private Context context;
    private String result;
    private List<UserDays> ud;
    private List<String> id;

    public RideRequestPutTask(Context c) {
        this.context = c;
    }

    public void setRideRequestPutTask(List<UserDays> ud){
        this.ud = ud;
    }

    public void setRideRequestUserPutTask(List<String> id){
        this.id = id;
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
        JSONArray jsRequest = new JSONArray();
        for (int i=0; i<ud.size(); i++){
            JSONObject jso = new JSONObject();
            jso.put("userId", ud.get(i).getUserId());
            JSONArray days = new JSONArray(ud.get(i).getDays());
            jso.put("days",  days);
            jsRequest.put(jso);
        }
        dataToSend.put("request", jsRequest);
        JSONArray jsRequestId = new JSONArray(id);
        dataToSend.put("requestUser", jsRequestId);

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
