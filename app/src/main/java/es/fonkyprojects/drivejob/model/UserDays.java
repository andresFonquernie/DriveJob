package es.fonkyprojects.drivejob.model;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.Serializable;
import java.util.List;

public class UserDays implements Serializable, Comparable<UserDays> {

    private String userId;
    private List<Integer> days;

    public UserDays() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserDays(String userId, List<Integer> days) {
        this.userId = userId;
        this.days = days;
    }

    public String getUserId() {
        return userId;
    }

    public List<Integer> getDays() {
        return days;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDays(List<Integer> days) {
        this.days = days;
    }

    @Override
    public int compareTo(@NonNull UserDays ud) {
        Log.e("COMPARE", this.userId + " " + ud.toString());
        return ud.userId.compareTo(this.userId);
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof UserDays){
            UserDays ud = (UserDays) o;
            return ud.userId.equals(this.userId);
        }
        return false;
    }

    @Override
    public String toString() {
        return this.getUserId();
    }
}
