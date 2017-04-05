package es.fonkyprojects.drivejob.model;

import java.io.Serializable;

/**
 * Created by andre on 02/02/2017.
 */

public class RideSearch implements Serializable {

    private String authorID;
    private String timeGoing;
    private String timeReturn;
    private double latGoing;
    private double latReturn;
    private double lngGoing;
    private double lngReturn;
    private String days;

    public RideSearch() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public RideSearch(String authorID, String timeGoing, String timeReturn, double latGoing, double latReturn,
                      double lngGoing, double lngReturn, String days) {
        this.authorID = authorID;
        this.timeGoing = timeGoing;
        this.timeReturn = timeReturn;
        this.latGoing = latGoing;
        this.latReturn = latReturn;
        this.lngGoing = lngGoing;
        this.lngReturn = lngReturn;
        this.days = days;
    }

    public String getAuthorID() {
        return authorID;
    }

    public String getTimeGoing() {
        return timeGoing;
    }

    public String getTimeReturn() {
        return timeReturn;
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

    public String getDays() {
        return days;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public void setTimeGoing(String timeGoing) {
        this.timeGoing = timeGoing;
    }

    public void setTimeReturn(String timeReturn) {
        this.timeReturn = timeReturn;
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

    public void setDays (String days) {
        this.days = days;
    }

}
