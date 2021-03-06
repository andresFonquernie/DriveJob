package es.fonkyprojects.drivejob.restMethods;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import es.fonkyprojects.drivejob.activity.R;

public class GetTask extends AsyncTask<String, Void, String> {

    private ProgressDialog progressDialog;
    private Context context;
    private String result;

    private static final String TAG = "GetTask";

    public GetTask(Context c){
        this.context = c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.updating_ride));
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return getData(params[0]);
        } catch (IOException ioe) {
            return "Network error";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        this.result = result;

        //Set data response to TextView
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    private String getData(String uriPath) throws IOException {
        StringBuilder result = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            //Initialize and config request, the connect to server
            URL url = new URL(uriPath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.connect();

            //Read data response from server
            InputStream inputStream = urlConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line).append("\n");
            }
        } finally {
            bufferedReader.close();
        }
        return result.toString();
    }
}
