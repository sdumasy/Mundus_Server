package models;

/**
 * Role of a player.
 */
public enum Role {
    Admin(0), Moderator(1), User(2);
    public int id;

    /**
     * The role constructor.
     * @param id The wanted role ID.
     */
    Role(int id) {
        this.id = id;
    }

    /**
     * Get a Role by id.
     * @param id The id.
     * @return The corresponding role.
     */
    public static Role getById(int id) {
        for (Role r:Role.values()) {
            if (r.id == id) {
                return r;
            }
        }
        return null;
    }
}
