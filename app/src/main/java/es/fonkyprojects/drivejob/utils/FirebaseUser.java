package es.fonkyprojects.drivejob.utils;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by andre on 07/12/2016.
 */

public class FirebaseUser {

    public static String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
