package es.fonkyprojects.drivejob.model;

public class Messaging {

    private String _id;
    private String usernameFrom;
    private String useridTo;
    private String key;
    private int value;


    public Messaging() {
    }

    public Messaging(String usernameFrom, String useridTo, String key, int value) {
        this.usernameFrom = usernameFrom;
        this.useridTo = useridTo;
        this.key = key;
        this.value = value;
    }

    public String get_id() {
        return _id;
    }

    public String getUsernameFrom() {
        return usernameFrom;
    }

    public String getUseridTo() {
        return useridTo;
    }

    public String getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setUsernameFrom(String usernameFrom) {
        this.usernameFrom = usernameFrom;
    }

    public void setUseridTo(String useridTo) {
        this.useridTo = useridTo;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
