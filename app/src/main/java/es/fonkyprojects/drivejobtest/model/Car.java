package es.fonkyprojects.drivejob.model;

import com.google.firebase.database.IgnoreExtraProperties;

import es.fonkyprojects.drivejob.activity.R;
import es.fonkyprojects.drivejob.utils.MyApp;

@IgnoreExtraProperties
public class Car {

    private String _id;
    private String authorID;
    private String brand;
    private String model;
    private int engineID;


    public Car() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Car(String authorID, String brand, String model, int engineID) {
        this.authorID = authorID;
        this.brand = brand;
        this.model = model;
        this.engineID = engineID;
    }

    @Override
    public String toString() {
        String[] list = MyApp.getAppContext().getResources().getStringArray(R.array.engineType);
        return brand + " " + model + " (" + list[engineID] + ")";
    }

    public String getId() { return _id; }

    public String getAuthorID() { return authorID; }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public int getEngineID() {
        return engineID;
    }

    public void setId(String _id) { this._id = _id; }

    public void setAuthorID(String authorID) { this.authorID =  authorID; }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setEngineId(int engineID) {
        this.engineID = engineID;
    }
}
