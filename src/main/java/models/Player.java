package models;

import com.google.gson.JsonObject;
import database.PlayerQueries;

import static database.CreateUniqueIDs.generateUniqueID;
import static database.PlayerQueries.addNewPlayer;

/**
 * Model of the player.
 */
public class Player {

    private String playerID;
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
     */
    public Player(String playerID, Session session, Device device, Role role, Integer score) {
        this.playerID = playerID;
        this.session = session;
        this.device = device;
        this.role = role;
        this.score = score;
        // TODO: 23/12/16 Add username
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
     * Creates a newPlayer.
     *
     * @param session   Players session.
     * @param roleID    Players role.
     * @param device    Players device.
     * @param score     The score of the player.
     * @return A new player.
     */
    public static Player newPlayer(Session session, int roleID, Device device, int score) {
        Player player = new Player(generateUniqueID("session_player", "player_id"),
                session, device, Role.getById(roleID), score);
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
        jsonObject.addProperty("sessionID", session.toJson().toString());
        jsonObject.addProperty("role", role.name());
        jsonObject.addProperty("score", score);
        return jsonObject;
    }
}
