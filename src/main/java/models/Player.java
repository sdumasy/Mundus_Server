package models;

import com.google.gson.JsonObject;
import database.PlayerQueries;

import static database.CreateUniqueIDs.generateUniqueID;
import static database.PlayerQueries.addNewPlayer;

/**
 * Model of the player.
 */
public class Player {

    private String playerID, sessionID;
    private Device device;
    private Role role;
    private Integer score;

    public Player(String playerID, String sessionID, Device device, Role role, Integer score) {
        this.playerID = playerID;
        this.sessionID = sessionID;
        this.device = device;
        this.role = role;
        this.score = score;
    }

    /**
     * Gets a player from the database.
     * @param playerID The player ID.
     * @return The Player.
     */
    public static Player getPlayer(String playerID) {
        return PlayerQueries.getPlayer(playerID);
    }

    /**
     * Creates a newPlayer.
     * @param sessionID Players session.
     * @param roleID Players role.
     * @param device Players device.
     * @return A new player.
     */
    public static Player newPlayer(String sessionID, int roleID, Device device, int score) {
        Player player = new Player(generateUniqueID("session_player", "player_id"),
                sessionID, device, Role.getById(roleID), score);
        if (addNewPlayer(player)) {
            return player;
        }
        return null;
    }

    /**
     * Gets the player ID.
     * @return The player ID.
     */
    public String getPlayerID() {
        return playerID;
    }

    /**
     * Gets the session ID.
     * @return The session ID.
     */
    public String getSessionID() {
        return sessionID;
    }

    /**
     * Gets the device ID.
     * @return The device ID.
     */
    public Device getDevice() {
        return device;
    }

    /**
     * Allows the role of the player to be set.
     * @param roleID The role ID.
     */
    public void setRoleID(Integer roleID) {
        this.role = Role.getById(roleID);
    }

    /**
     * Gets the role ID.
     * @return The role ID.
     */
    public int getRoleID() {
        return role.id;
    }

    /**
     * Gets the role ID by name.
     * @return The role.
     */
    public Role getRole() {
        return role;
    }

    /**
     * Returns whether this player is an admin or not.
     * @return The boolean value.
     */
    public boolean isAdmin() {
        return getRoleID() == 0;
    }

    /**
     * Allows the score of the player to be set.
     * @param score The amount of points this player has scored.
     */
    public void setScore(Integer score) {
        this.score = score;
    }

    /**
     * Gets the score.
     * @return The score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Return a JsonObject with all the player attributes.
     * @return A JsonObject with all the player attributes.
     */
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("playerID",getPlayerID());
        jsonObject.addProperty("sessionID",sessionID);
        jsonObject.addProperty("role",role.name());
        jsonObject.addProperty("score",score);
        return jsonObject;
    }
}
