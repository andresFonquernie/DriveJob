package es.fonkyprojects.drivejob.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by andre on 04/12/2016.
 */
// [START user_class]
@IgnoreExtraProperties
public class RideUser {

    public String userID;
    public String rideID;


    public RideUser() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public RideUser(String userID, String rideID) {
        this.userID = userID;
        this.rideID = rideID;
    }

}
// [END user_class]