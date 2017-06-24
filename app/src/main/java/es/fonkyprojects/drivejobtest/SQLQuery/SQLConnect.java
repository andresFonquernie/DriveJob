package es.fonkyprojects.drivejob.SQLQuery;

import java.util.List;

import es.fonkyprojects.drivejob.model.Ride;

/**
 * Created by andre on 29/01/2017.
 */

public class SQLConnect {

    private static final String TAG = "SQL Connect";


    public void insertRide(Ride r, int engineId){

        /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
        //Search specific days
            String[] items = r.getDays().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
            String days = r.getDays();
            for (int i = 0; i < items.length; i++) {
                if (items[i].equals("true")) {
                    days = days.replaceFirst("true", String.valueOf(i));
                    Log.e(TAG, days);
                }
            }

            String sql = "INSERT INTO Ride (_id, authorID, author, timeGoing, timeReturn, placeGoing, placeReturn, latGoing, latReturn" +
                    ", lngGoing, lngReturn, price, passengers, days, carID, avSeatsDay, daysPos, engineId) VALUES" +
                    "('" + r.getID() + "', '" + r.getAuthorID() + "', '" + r.getAuthor() + "', '" + r.getTimeGoing() + "'" +
                    ", '" + r.getTimeReturn() + "', '" + r.getPlaceGoing() + "', '" + r.getPlaceReturn() + "', " + r.getLatGoing() +
                    ", " + r.getLatReturn() + ", " + r.getLngGoing() + ", " + r.getLngReturn() + ", " + r.getPrice() +
                    ", " + r.getPassengers() + ", '" + r.getDays() + "', '" + r.getCarID() + "', '" + r.getAvSeats() + "'" +
                    ", '" + days + "', " + engineId + ")";

            Log.e(TAG, sql);

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection con = DriverManager.getConnection(Constants.SQL_TABLE, Constants.SQL_USER, Constants.SQL_PASS);
            Statement st = con.createStatement();
            st.executeUpdate(sql);

            st.close();
            con.close();
        }catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }*/
    }

    public void updateRide(Ride r, int engineId){
        /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection con = DriverManager.getConnection(Constants.SQL_TABLE, Constants.SQL_USER, Constants.SQL_PASS);
            Statement st = con.createStatement();

            String sql = "UPDATE Ride SET timeGoing = '" + r.getTimeGoing() + "', timeReturn = '" + r.getTimeReturn() + "'" +
                    ", placeGoing = '" + r.getPlaceGoing() + "', placeReturn = '" + r.getPlaceReturn() + "'" +
                    ", latGoing = " + r.getLatGoing() +  ", latReturn = " + r.getLatReturn() + ", lngGoing = " + r.getLngGoing() +
                    ", lngReturn = " + r.getLngReturn() + ", price = " + r.getPrice() + ", passengers = " + r.getPassengers() +
                    ", days = '" + r.getDays() + "', carID = '" + r.getCarID() + "'" +
                    ", avSeatsDay = '" + r.getAvSeatsDay() + "', engineId = " + engineId + " " +
                    " WHERE _id = '" + r.getID() + "'";
            Log.e(TAG, sql);
            st.executeUpdate(sql);

            st.close();
            con.close();
        }catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }*/
    }

    public void deleteRide(String rideKey){
        /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
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
        }*/
    }

    public void updateAvSeatsDay(String avSeatsDay, String key){
        /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Search specific days
        String[] items = avSeatsDay.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
        String daysPos = "[";
        for (int i = 0; i < items.length; i++) {
            if(Integer.parseInt(items[i])>0)
                daysPos = daysPos + i + ", ";
            else
                daysPos = daysPos + "false" + ", ";
        }
        daysPos = daysPos.substring(0, daysPos.length()-2) + "]";

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection con = DriverManager.getConnection(Constants.SQL_TABLE, Constants.SQL_USER, Constants.SQL_PASS);
            Statement st = con.createStatement();

            String sql = "UPDATE Ride SET avSeatsDay = '" + avSeatsDay + "', daysPos = '" + daysPos + "' " +
                    "WHERE _id = '" + key + "'";
            st.executeUpdate(sql);

            st.close();
            con.close();
        }catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }*/
    }

    public List<Ride> searchRide(String authorId, double myLatGo, double myLatReturn, double myLngGo, double myLngReturn,
                                 String myTimeGo, String myTimeReturn, List<Boolean> days, int maxDistance, int maxTime) {

        /*List<Ride> rides = new ArrayList<>();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        float maxDistanceF = (float) maxDistance/1000;
        maxTime = maxTime*100;

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection con = DriverManager.getConnection(Constants.SQL_TABLE, Constants.SQL_USER, Constants.SQL_PASS);

            Statement st = con.createStatement();

            //Search specific days
            String[] items = days.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
            String searchDays = "";
            for (int i = 0; i < items.length; i++) {
                if (items[i].equals("true")) {
                    searchDays = searchDays + " OR daysPos LIKE '%" + i + "%'";
                }
            }
            searchDays = searchDays.substring(4, searchDays.length());

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
                    "AND " + searchDays;
            Log.e(TAG, sql);

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
                r.setDays(rs.getString("days"));
                r.setPrice(rs.getInt("price"));
                r.setPassengers(rs.getInt("passengers"));
                r.setAvSeatsDay(rs.getString("avSeatsDay"));
                r.setCarID(rs.getString("carID"));
                r.setEngineId(rs.getInt("engineId"));
                rides.add(r);
            }
            st.close();
            con.close();
        } catch (IllegalAccessException | InstantiationException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return rides;*/
        return null;
    }
}
