package es.fonkyprojects.drivejob.SQLQuery;

import java.util.List;

import es.fonkyprojects.drivejob.model.Ride;

/**
 * Created by andre on 29/01/2017.
 */

public class SQLConnect {

    //private Connection con;
    //private Statement st;

    public SQLConnect(){
        /*try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://104.199.51.125:3306/calculateKM", "root", "heidelbE18");
            st = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/
    }

    public void closeConnect(){
        /*try {
            st.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
    }

    public void insertRide(Ride r){
        /*try {
            String sql;
            sql = "INSERT INTO Ride VALUES('" + r.getID() + "', '" + r.getAuthor() + "', '" + r.getAuthor() + "', '" + r.getTimeGoing() +
                    "', '" + r.getTimeReturn() + "', '" + r.getPlaceGoing() + "', '" + r.getPlaceReturn() + "', " + r.getLatGoing() +
                    ", " + r.getLatReturn() + ", " + r.getLngGoing() + ", " + r.getLngReturn() + ", " + r.getPrice() + ", " + r.getPassengers();
            ResultSet rs = null;
            rs = st.executeQuery(sql);
            Log.e("INSERT RIDE", rs.toString());
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
    }

    public List<Ride> searchRide(float myLatGo, float myLatReturn, float myLngGo, float myLngReturn, String myTimeGo, String myTimeReturn){
        return null;
    }

    public void updateRide(Ride r){

    }

    public void deleteRide(Ride r){

    }

}
