package es.fonkyprojects.drivejob.utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by andre on 14/05/2017.
 */

public class MyApp extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyApp.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApp.context;
    }
}
