package es.fonkyprojects.drivejob.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by andre on 04/12/2016.
 */
// [START user_class]
@IgnoreExtraProperties
public class User {

    public String username;
    public String email;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

}
// [END user_class]


