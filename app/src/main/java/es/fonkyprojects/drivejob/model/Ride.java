package es.fonkyprojects.drivejob.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by andre on 07/12/2016.
 */
// [START trip_class]
@IgnoreExtraProperties
public class Ride {

    public String author;
    public String authorID;
    public String timeGoing;
    public String placeGoing;
    public String timeReturn;
    public String placeReturn;
    public int price;
    public int passengers;


    public Ride() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Ride(String authorID, String author, String timeGoing, String placeGoing, String timeReturn, String placeReturn, int price, int passengers ) {
        this.authorID = authorID;
        this.author = author;
        this.timeGoing = timeGoing;
        this.placeGoing = placeGoing;
        this.timeReturn = timeReturn;
        this.placeReturn = placeReturn;
        this.price = price;
        this.passengers = passengers;
    }

    public String toString(){
        return placeGoing + " " + placeReturn + " " + author;
    }

}
// [END ride_class]
