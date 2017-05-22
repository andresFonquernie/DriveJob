package es.fonkyprojects.drivejob.model;

import java.io.Serializable;

/**
 * Created by andre on 21/05/2017.
 */

public class UserDays implements Serializable {

    private String userId;
    private String username;
    private String days;

    public UserDays() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserDays(String userId, String username, String days) {
        this.userId = userId;
        this.username = username;
        this.days = days;
    }

    public String getUserId() { return userId; }

    public String getUsername() {
        return username;
    }

    public String getDays() {
        return days;
    }

    public void setUserId(String userId) { this.userId = userId; }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDays(String days) {
        this.days = days;
    }
}
