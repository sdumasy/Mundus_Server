package models;

import database.SessionQueries;

import static database.SessionQueries.generateUniqueID;

/**
 * Model of the player.
 */
public class Player {

    private String playerID, sessionID, deviceID, roleName;
    private Integer roleID, score;

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
        Player player = new Player(generateUniqueID(
                "SELECT player_id FROM session_player WHERE player_id='id_placeholder'"), sessionID, deviceID);
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
    protected void setRoleID(Integer roleID) {
        this.roleID = roleID;
    }


    /**
     * Gets the role ID.
     * @return The role ID.
     */
    public int getRoleID() {
        if (roleID == null) {
            roleID = SessionQueries.getRoleId(this);
        }
        return roleID;
    }

    /**
     * Gets the role ID by name.
     * @return The role name.
     */
    public String getRole() {
        if (roleName == null) {
            roleName = Role.getById(getRoleID()).name();
        }
        return roleName;
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
    protected void setScore(Integer score) {
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
