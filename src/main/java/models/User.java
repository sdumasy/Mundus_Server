package models;

/**
 * Created by macbookpro on 05/12/2016.
 */
public class User {
    private String id;
    private String name;
    private String GameSessionId;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getGameSessionId() {
        return GameSessionId;
    }
}
