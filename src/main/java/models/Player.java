package models;

import database.SessionQueries;

import static database.SessionQueries.generateUniqueID;

/**
 * Model of the player.
 */
public class Player {

    private String playerID, sessionID, deviceID;
    private Role role;
    private Integer score;

    /**
     * Constructor for the player class.
     * @param playerID A player ID.
     * @param sessionID A session ID.
     * @param deviceID A device ID.
     */
    public Player(String playerID, String sessionID, String deviceID) {
        this.playerID = playerID;
        this.sessionID = sessionID;
        this.deviceID = deviceID;
    }

    /**
     * Creates a newPlayer.
     * @param sessionID Players session.
     * @param roleID Players role.
     * @param deviceID Players deviceID.
     * @return A new player.
     */
    public static Player newPlayer(String sessionID, int roleID, String deviceID) {
        Player player = new Player(generateUniqueID("session_player", "player_id"), sessionID, deviceID);
        player.setRoleID(roleID);
        player.setScore(0);
        return player;
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
    public String getDeviceID() {
        return deviceID;
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
        if (role == null) {
            getRole();
        }
        return role.id;
    }

    /**
     * Gets the role ID by name.
     * @return The role.
     */
    public Role getRole() {
        if (role == null) {
            setRoleID(SessionQueries.getRoleId(this));
        }
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
        if (score == null) {
            score = SessionQueries.getScore(this);
        }
        return score;
    }
}
