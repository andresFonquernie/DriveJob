package es.fonkyprojects.drivejob.SQLQuery;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import es.fonkyprojects.drivejob.model.Ride;
import es.fonkyprojects.drivejob.utils.Constants;

/**
 * Created by andre on 29/01/2017.
 */

public class SQLConnect {

    private static final String TAG = "SQL Connect";


    public String insertRide(Ride r){
        String sql = "INSERT INTO Ride VALUES('" + r.getID() + "', '" + r.getAuthorID() + "', '" + r.getAuthor() + "', '" + r.getTimeGoing() +
                "', '" + r.getTimeReturn() + "', '" + r.getPlaceGoing() + "', '" + r.getPlaceReturn() + "', " + r.getLatGoing() +
                ", " + r.getLatReturn() + ", " + r.getLngGoing() + ", " + r.getLngReturn() + ", " + r.getPrice() +
                ", " + r.getPassengers() + ", " + r.getAvSeats() + ", '" + r.getDays() + "', '" + r.getCarID() + "')";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection con = DriverManager.getConnection(Constants.SQL_TABLE, Constants.SQL_USER, Constants.SQL_PASS);
            Statement st = con.createStatement();
            st.executeUpdate(sql);

            st.close();
            con.close();
        }catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return sql;
    }

    public void updateRide(Ride r){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection con = DriverManager.getConnection(Constants.SQL_TABLE, Constants.SQL_USER, Constants.SQL_PASS);
            Statement st = con.createStatement();

            String sql = "UPDATE Ride SET timeGoing = '" + r.getTimeGoing() + "', timeReturn = '" + r.getTimeReturn() + "'" +
                    ", placeGoing = '" + r.getPlaceGoing() + "', placeReturn = '" + r.getPlaceReturn() + "'" +
                    ", latGoing = " + r.getLatGoing() +  ", latReturn = " + r.getLatReturn() + ", lngGoing = " + r.getLngGoing() +
                    ", lngReturn = " + r.getLngReturn() + ", price = " + r.getPrice() + ", passengers = " + r.getPassengers() +
                    ", avSeats = " + r.getAvSeats() + ", carID = '" + r.getCarID() + "'" +
                    " WHERE _id = '" + r.getID() + "'";
            Log.e(TAG, sql);
            st.executeUpdate(sql);

            st.close();
            con.close();
        }catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void deleteRide(String rideKey){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection con = DriverManager.getConnection(Constants.SQL_TABLE, Constants.SQL_USER, Constants.SQL_PASS);
            Statement st = con.createStatement();

            String sql = "DELETE FROM Ride WHERE _id = '" + rideKey + "'";
            st.executeUpdate(sql);

            st.close();
            con.close();
        }catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void updateAvSeats(int i, String key){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection con = DriverManager.getConnection(Constants.SQL_TABLE, Constants.SQL_USER, Constants.SQL_PASS);
            Statement st = con.createStatement();

            String sql = "UPDATE Ride SET avSeats = " + i + " WHERE _id = '" + key + "'";
            st.executeUpdate(sql);

            st.close();
            con.close();
        }catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public List<Ride> searchRide(String authorId, double myLatGo, double myLatReturn, double myLngGo, double myLngReturn, String myTimeGo, String myTimeReturn, String days, int maxDistance, int maxTime) {

        List<Ride> rides = new ArrayList<>();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        float maxDistanceF = (float) maxDistance/1000;
        maxTime = maxTime*100;

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection con = DriverManager.getConnection(Constants.SQL_TABLE, Constants.SQL_USER, Constants.SQL_PASS);

            Statement st = con.createStatement();

            String sql = "SELECT *, " +
                    "( 3959 * acos( cos( radians(" + myLatGo + ") ) * cos( radians( latGoing ) ) * cos( radians(lngGoing) - " +
                    "radians(" + myLngGo + ")) + sin(radians(" + myLatGo + ")) * sin( radians(latGoing)))) AS 'distanceGo',  " +
                    "( 3959 * acos( cos( radians("+ myLatReturn + ") ) * cos( radians( latReturn ) ) * cos( radians(lngReturn) - " +
                    "radians(" + myLngReturn + ")) +sin(radians(" + myLatReturn + ")) * sin( radians(latReturn)))) AS 'distanceReturn', " +
                    "SUBTIME(timeGoing,'" + myTimeGo + "') AS 'DifGo', " +
                    "SUBTIME(timeReturn,'" + myTimeReturn + "') AS 'DifReturn' " +
                    "FROM Ride " +
                    "HAVING (distanceGo BETWEEN -" + maxDistanceF + " AND " + maxDistanceF + ") " +
                    "AND (distanceReturn BETWEEN -" + maxDistanceF + " AND " + maxDistanceF + ") " +
                    "AND (DifGo BETWEEN -" + maxTime + " AND " + maxTime + ") AND (DifReturn BETWEEN -" + maxTime + " AND " + maxTime + ")" +
                    //"AND authorID <> '" + authorId + "' " +
                    "AND days = '" + days + "' " +
                    "AND avSeats > 0";

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                //Retrieve by column name
                Ride r = new Ride();
                r.setID(rs.getString("_id"));
                r.setAuthorID(rs.getString("authorID"));
                r.setAuthor(rs.getString("author"));
                r.setTimeGoing(rs.getString("timeGoing"));
                r.setTimeReturn(rs.getString("timeReturn"));
                r.setPlaceGoing(rs.getString("placeGoing"));
                r.setPlaceReturn(rs.getString("placeReturn"));
                r.setLatGoing(rs.getDouble("latGoing"));
                r.setLatReturn(rs.getDouble("latReturn"));
                r.setLngGoing(rs.getDouble("lngGoing"));
                r.setLngReturn(rs.getDouble("lngReturn"));
                r.setPrice(rs.getInt("price"));
                r.setPassengers(rs.getInt("passengers"));
                r.setCarID(rs.getString("carID"));
                rides.add(r);
            }
            st.close();
            con.close();
        } catch (IllegalAccessException | InstantiationException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return rides;
    }
}
