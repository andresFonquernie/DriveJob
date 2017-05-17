package es.fonkyprojects.drivejob.model;

/**
 * Created by andre on 08/05/2017.
 */

public class Messaging {

    private String username;
    private String userIdDestination;
    private String key;
    private int value;


    public Messaging() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Messaging(String username, String userId, String key, int value) {
        this.username = username;
        this.userIdDestination = userId;
        this.key = key;
        this.value = value;
    }

    public String getUsername() {
        return username;
    }

    public String getUserIdDestination() {
        return userIdDestination;
    }

    public String getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserIdDestination(String userIdDestination) {
        this.userIdDestination = userIdDestination;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
