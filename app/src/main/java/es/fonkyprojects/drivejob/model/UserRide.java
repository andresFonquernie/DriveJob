package es.fonkyprojects.drivejob.model;

/**
 * Created by andre on 05/02/2017.
 */

public class UserRide {

    private String _id;
    private String userId;
    private String rideId;

    public UserRide() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserRide(String userId, String rideId) {
        this.userId = userId;
        this.rideId = rideId;
    }

    public String toString(){
        return userId + " " + rideId;
    }

    public String get_id() { return _id; }

    public String getUserId() {
        return userId;
    }

    public String getRideId() {
        return rideId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }
}
// [END user_clas
