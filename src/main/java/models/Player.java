package models;

import com.google.gson.JsonObject;
import database.PlayerQueries;

import static database.CreateUniqueIDs.generateUniqueID;
import static database.PlayerQueries.addNewPlayer;

/**
 * Model of the player.
 */
public class Player {

    private String playerID, username;
    private Session session;
    private Device device;
    private Role role;
    private Integer score;

    /**
     * Constructor for player.
     *
     * @param playerID  The id for the player.
     * @param session The session of the player.
     * @param device    The device of the player.
     * @param role      The role of the player.
     * @param score     The score of the player.
     * @param username  The username of the player.
     */
    public Player(String playerID, Session session, Device device, Role role, Integer score, String username) {
        this.playerID = playerID;
        this.session = session;
        this.device = device;
        this.role = role;
        this.score = score;
        this.username = username;
    }

    /**
     * Gets a player from the database.
     *
     * @param playerID The player ID.
     * @return The Player.
     */
    public static Player getPlayer(String playerID) {
        return PlayerQueries.getPlayer(playerID);
    }

    /**
     * Gets a player from the database based of session and username.
     *
     * @param sessionID The sessionID.
     * @param username  The username.
     * @return The player.
     */
    public static Player getPlayer(String sessionID, String username) {
        return PlayerQueries.getPlayer(sessionID, username);
    }

    /**
     * Creates a newPlayer.
     *
     * @param session   Players session.
     * @param roleID    Players role.
     * @param device    Players device.
     * @param score     The score of the player.
     * @param username  The username of the player.
     * @return A new player.
     */
    public static Player newPlayer(Session session, int roleID, Device device, int score, String username) {
        Player player = new Player(generateUniqueID("session_player", "player_id"),
                session, device, Role.getById(roleID), score, username);
        if (addNewPlayer(player)) {
            return player;
        }
        return null;
    }

    /**
     * Gets the player ID.
     *
     * @return The player ID.
     */
    public String getPlayerID() {
        return playerID;
    }

    /**
     * Gets the session.
     *
     * @return The session.
     */
    public Session getSession() {
        return session;
    }

    /**
     * Gets the device ID.
     *
     * @return The device ID.
     */
    public Device getDevice() {
        return device;
    }

    /**
     * Gets the role ID.
     *
     * @return The role ID.
     */
    public int getRoleID() {
        return role.getId();
    }

    /**
     * Allows the role of the player to be set.
     *
     * @param roleID The role ID.
     */
    protected void setRoleID(Integer roleID) {
        this.role = Role.getById(roleID);
    }

    /**
     * Gets the username.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the role ID by name.
     *
     * @return The role.
     */
    protected Role getRole() {
        return role;
    }

    /**
     * Returns whether this player is an admin or not.
     *
     * @return The boolean value.
     */
    public boolean isAdmin() {
        return getRoleID() == 0;
    }

    /**
     * Returns whether this player is an admin or not.
     *
     * @return The boolean value.
     */
    public boolean isModerator() {
        return getRoleID() == 1;
    }

    /**
     * Gets the score.
     *
     * @return The score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Allows the score of the player to be set.
     *
     * @param score The amount of points this player has scored.
     */
    protected void setScore(Integer score) {
        this.score = score;
    }

    /**
     * Return a JsonObject with all the player attributes.
     *
     * @return A JsonObject with all the player attributes.
     */
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("playerID", getPlayerID());
        jsonObject.addProperty("sessionID", session.getSessionID());
        jsonObject.addProperty("role", role.name());
        jsonObject.addProperty("score", score);
        jsonObject.addProperty("username", username);
        return jsonObject;
    }

    /**
     * The equals method of player.
     *
     * @param o The other object to compare with.
     * @return Whether they are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Player player = (Player) o;

        if (!playerID.equals(player.playerID)) {
            return false;
        }
        if (!username.equals(player.username)) {
            return false;
        }
        if (!session.equals(player.session)) {
            return false;
        }
        if (!device.equals(player.device)) {
            return false;
        }
        if (role != player.role) {
            return false;
        }
        return score.equals(player.score);
    }

    /**
     * Hashcode method of player.
     *
     * @return The hashcode.
     */
    @SuppressWarnings("checkstyle:magicnumber") //31 is defined by intellij.
    @Override
    public int hashCode() {
        int result = playerID.hashCode();
        result = 31 * result + username.hashCode();
        result = 31 * result + session.hashCode();
        result = 31 * result + device.hashCode();
        result = 31 * result + role.hashCode();
        result = 31 * result + score.hashCode();
        return result;
    }
}
