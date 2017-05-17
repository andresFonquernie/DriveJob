package es.fonkyprojects.drivejob.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

// [START ride_class]
@IgnoreExtraProperties
public class Ride implements Serializable {

    private String _id;
    private String authorID;
    private String author;
    private String timeGoing;
    private String timeReturn;
    private String placeGoing;
    private String placeReturn;
    private double latGoing;
    private double latReturn;
    private double lngGoing;
    private double lngReturn;
    private String days;
    private int price;
    private int passengers;
    private int avSeats;
    private String carID;

    public Ride() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Ride(String authorID, String author, String timeGoing, String timeReturn, String placeGoing, String placeReturn, double latGoing,
                double latReturn, double lngGoing, double lngReturn, String days, int price, int passengers, int avSeats, String carID) {
        this.authorID = authorID;
        this.author = author;
        this.timeGoing = timeGoing;
        this.timeReturn = timeReturn;
        this.placeGoing = placeGoing;
        this.placeReturn = placeReturn;
        this.latGoing = latGoing;
        this.latReturn = latReturn;
        this.lngGoing = lngGoing;
        this.lngReturn = lngReturn;
        this.days = days;
        this.price = price;
        this.passengers = passengers;
        this.avSeats = avSeats;
        this.carID = carID;
    }

    public Ride(String _id, String authorID, String author, String timeGoing, String timeReturn, String placeGoing, String placeReturn,
                double latGoing, double latReturn, double lngGoing, double lngReturn, String days, int price, int passengers, int avSeats,
                String carID) {
        this._id = _id;
        this.authorID = authorID;
        this.author = author;
        this.timeGoing = timeGoing;
        this.timeReturn = timeReturn;
        this.placeGoing = placeGoing;
        this.placeReturn = placeReturn;
        this.latGoing = latGoing;
        this.latReturn = latReturn;
        this.lngGoing = lngGoing;
        this.lngReturn = lngReturn;
        this.days = days;
        this.price = price;
        this.passengers = passengers;
        this.avSeats = avSeats;
        this.carID = carID;
    }

    public String toString(){
        return placeGoing + " " + placeReturn + " " + author;
    }

    public String getID(){
        return _id;
    }

    public String getAuthorID() {
        return authorID;
    }

    public String getAuthor() {
        return author;
    }

    public String getTimeGoing() {
        return timeGoing;
    }

    public String getTimeReturn() {
        return timeReturn;
    }

    public String getPlaceGoing() {
        return placeGoing;
    }

    public String getPlaceReturn() {
        return placeReturn;
    }

    public double getLatGoing() {
        return latGoing;
    }

    public double getLatReturn() {
        return latReturn;
    }

    public double getLngGoing() {
        return lngGoing;
    }

    public double getLngReturn() { return lngReturn; }

    public String getDays() { return days; }

    public int getPrice() {
        return price;
    }

    public int getPassengers() {
        return passengers;
    }

    public int getAvSeats() {
        return avSeats;
    }

    public String getCarID() { return carID; }

    public void setID(String id){
        this._id = id;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTimeGoing(String timeGoing) {
        this.timeGoing = timeGoing;
    }

    public void setTimeReturn(String timeReturn) {
        this.timeReturn = timeReturn;
    }

    public void setPlaceGoing(String placeGoing) {
        this.placeGoing = placeGoing;
    }

    public void setPlaceReturn(String placeReturn) {
        this.placeReturn = placeReturn;
    }

    public void setLatGoing(double latGoing) {
        this.latGoing = latGoing;
    }

    public void setLatReturn(double latReturn) {
        this.latReturn = latReturn;
    }

    public void setLngGoing(double lngGoing) {
        this.lngGoing = lngGoing;
    }

    public void setLngReturn(double lngReturn) {
        this.lngReturn = lngReturn;
    }

    public void  setDays(String days) { this.days = days; }

    public void setPrice(int price) { this.price = price; }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }

    public void setAvSeats(int avSeats) {
        this.avSeats = avSeats;
    }

    public void setCarID(String carID) { this.carID = carID; }
}
// [END ride_class]
