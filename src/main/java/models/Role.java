package models;

/**
 * Created by Ben on 19/12/16.
 */
public enum Role {
    Admin(0),moderator(1),user(2);
    private int id;

    Role(int id) {
        this.id = id;
    }
    public static Role getById(int id) {
        for (Role r:Role.values()) {
            if (r.id == id) {
                return r;
            }
        }
        return null;
    }
}
