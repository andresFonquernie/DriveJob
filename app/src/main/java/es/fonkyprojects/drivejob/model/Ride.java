package es.fonkyprojects.drivejob.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.List;

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
    private List<Boolean> days;
    private List<Integer> avSeats;
    private int price;
    private int passengers;
    private String carID;
    private int engineId;
    private List<UserDays> request;
    private List<UserDays> join;
    private List<String> requestUser;
    private List<String> joinUser;

    public Ride() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    //Complete constructor
    public Ride(String _id, String authorID, String author, String timeGoing, String timeReturn, String placeGoing, String placeReturn,
                double latGoing, double latReturn, double lngGoing, double lngReturn, List<Boolean> days, List<Integer> avSeats,
                int price, int passengers, String carID, int engineId, List<UserDays> request, List<UserDays> join,
                List<String> requestUser, List<String> joinUser) {
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
        this.engineId = engineId;
        this.request = request;
        this.join = join;
        this.requestUser = requestUser;
        this.joinUser = joinUser;
    }

    //Create ride constructor
    public Ride(String authorID, String author, String timeGoing, String timeReturn, String placeGoing, String placeReturn,
                double latGoing, double latReturn, double lngGoing, double lngReturn, List<Boolean> days, List<Integer> avSeats,
                int price, int passengers, String carID, List<UserDays> request, List<UserDays> join,
                List<String> requestUser, List<String> joinUser) {
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
        this.request = request;
        this.join = join;
        this.requestUser = requestUser;
        this.joinUser = joinUser;
    }

    //Edit ride constructor
    public Ride(String timeGoing, String timeReturn, String placeGoing, String placeReturn, double latGoing, double latReturn,
                double lngGoing, double lngReturn, List<Boolean> days, List<Integer> avSeats, int price, int passengers,
                String carID){
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

    //Search ride constructor
    public Ride(String authorID, String timeGoing, String timeReturn, double latGoing, double latReturn,
                      double lngGoing, double lngReturn, List<Boolean> days) {
        this.authorID = authorID;
        this.timeGoing = timeGoing;
        this.timeReturn = timeReturn;
        this.latGoing = latGoing;
        this.latReturn = latReturn;
        this.lngGoing = lngGoing;
        this.lngReturn = lngReturn;
        this.days = days;
    }

    @Override
    public String toString(){
        String s;
        s = "_id: " + _id + "\n";
        s = s + "authorID: " + authorID + "\n";
        s = s + "author: " + author + "\n";
        s = s + "timeGoing: " + timeGoing + "\n";
        s = s + "timeReturn: " + timeReturn + "\n";
        s = s + "placeGoing: " + placeGoing + "\n";
        s = s + "placeReturn: " + placeReturn + "\n";
        s = s + "latGoing: " + latGoing + "\n";
        s = s + "latReturn: " + latReturn + "\n";
        s = s + "lngGoing: " + lngGoing + "\n";
        s = s + "lngReturn: " + lngReturn + "\n";
        s = s + "days: " +  days + "\n";
        s = s + "avSeats: " + avSeats + "\n";
        s = s + "price: " + price + "\n";
        s = s + "passengers: " + passengers + "\n";
        s = s + "carID: " + carID + "\n";
        s = s + "engineId: " + engineId + "\n";
        s = s + "request: " + request + "\n";
        s = s + "join: " + join + "\n";
        return s;
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

    public double getLngReturn() {
        return lngReturn;
    }

    public List<Boolean> getDays() {
        return days;
    }

    public int getPrice() {
        return price;
    }

    public int getPassengers() {
        return passengers;
    }

    public List<Integer> getAvSeats() {
        return avSeats;
    }

    public String getCarID() {
        return carID;
    }

    public int getEngineId() {
        return engineId;
    }

    public List<UserDays> getRequest() {
        return request;
    }

    public List<UserDays> getJoin() {
        return join;
    }

    public List<String> getRequestUser() {
        return requestUser;
    }

    public List<String> getJoinUser() {
        return joinUser;
    }

    public void setID (String _id){
        this._id = _id;
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

    public void setDays(List<Boolean> days) {
        this.days = days;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }

    public void setAvSeats(List<Integer> avSeats) {
        this.avSeats = avSeats;
    }

    public void setCarID(String carID) { this.carID = carID; }

    public void setEngineId(int engineId) {
        this.engineId = engineId;
    }

    public void setJoin(List<UserDays> join) {
        this.join = join;
    }

    public void setRequest(List<UserDays> request) {
        this.request = request;
    }

    public void setRequestUser(List<String> requestUser) {
        this.requestUser = requestUser;
    }

    public void setJoinUser(List<String> joinUser) {
        this.joinUser = joinUser;
    }
}
// [END ride_class]
