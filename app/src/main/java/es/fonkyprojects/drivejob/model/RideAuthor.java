package es.fonkyprojects.drivejob.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by andre on 04/12/2016.
 */

// [START user_class]
@IgnoreExtraProperties
public class RideAuthor {

    public String userID;


    public RideAuthor() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public RideAuthor(String userID) {
        this.userID = userID;
    }

}
// [END user_class]