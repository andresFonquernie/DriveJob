package es.fonkyprojects.drivejob.utils;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseUser {

    public static String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
