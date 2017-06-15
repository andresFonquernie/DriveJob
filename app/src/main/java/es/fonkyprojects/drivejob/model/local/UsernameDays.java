package es.fonkyprojects.drivejob.model.local;

import java.io.Serializable;
import java.util.List;

/**
 * Created by andre on 11/06/2017.
 */

public class UsernameDays implements Serializable {

    private String userId;
    private String username;
    private List<Integer> days;

    public UsernameDays() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UsernameDays(String userId, String username, List<Integer> days) {
        this.userId = userId;
        this.username = username;
        this.days = days;
    }

    public String getUserId() { return userId; }

    public String getUsername() {
        return username;
    }

    public List<Integer> getDays() {
        return days;
    }

    public void setUserId(String userId) { this.userId = userId; }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDays(List<Integer> days) {
        this.days = days;
    }
}

