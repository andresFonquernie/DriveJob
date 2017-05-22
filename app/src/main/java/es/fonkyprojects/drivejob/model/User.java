package es.fonkyprojects.drivejob.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by andre on 04/12/2016.
 */
// [START user_class]
@IgnoreExtraProperties
public class User implements Serializable {

    private String _id;
    private String userId;
    private String username;
    private String surname;
    private String email;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String _id, String userId, String username, String surname, String email) {
        this._id = _id;
        this.userId = userId;
        this.username = username;
        this.surname = surname;
        this.email = email;
    }

    public User(String userId, String username, String surname, String email) {
        this.userId = userId;
        this.username = username;
        this.surname = surname;
        this.email = email;
    }

    public String get_id() { return _id; }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public void set_id(String _id) { this._id = _id; }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
// [END user_class]


