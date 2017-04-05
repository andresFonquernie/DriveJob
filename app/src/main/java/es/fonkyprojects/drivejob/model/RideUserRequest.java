package es.fonkyprojects.drivejob.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by andre on 04/12/2016.
 */
// [START user_class]
@IgnoreExtraProperties
public class RideUserRequest {

    private String _id;
    private String rideId;
    private String userId;

    public RideUserRequest() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public RideUserRequest(String rideId, String userId) {
        this.rideId = rideId;
        this.userId = userId;
    }

    public String toString(){
        return rideId + " " + userId;
    }

    public String get_id() { return _id; }

    public String getRideId() {
        return rideId;
    }

    public String getUserId() {
        return userId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
// [END user_class]