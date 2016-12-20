package models;

import database.SessionQueries;

import static database.SessionQueries.generateUniqueID;

/**
 * Model of the player
 */
public class Player {

    private String playerID, sessionID, deviceID, roleName;
    private Integer roleID, score;

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

    public String getPlayerID() {
        return playerID;
    }

    public String getSessionID() {
        return sessionID;
    }

    public String getDeviceID() {
        return deviceID;
    }

    protected void setRoleID(Integer roleID) {
        this.roleID = roleID;
    }

    public int getRoleID() {
        if (roleID == null) {
            roleID = SessionQueries.getRoleId(this);
        }
        return roleID;
    }

    public String getRole() {
        if (roleName == null) {
            roleName = Role.getById(getRoleID()).name();
        }
        return roleName;
    }

    public boolean isAdmin() {
        return getRoleID()==0;
    }

    protected void setScore(Integer score) {
        this.score = score;
    }

    public int getScore() {
        if (score == null) {
            score = SessionQueries.getScore(this);
        }
        return score;
    }
}
