package models;

import static database.SessionQueries.generateUniqueID;

/**
 * Model of the player
 */
public class Player {

    String playerID, sessionID, roleName;
    int roleID, score;

    public Player(String sessionID, int roleID) {
        System.out.println(0);
        playerID = generateUniqueID("SELECT player_id FROM session_player WHERE player_id='id_placeholder'");
        System.out.println(1);
        this.sessionID = sessionID;
        this.roleID = roleID;
        score = 0;
        System.out.println(2);
        roleName = Role.getById(roleID).name();
        System.out.println(3);
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getSessionID() {
        return sessionID;
    }

    public String getRole() {
        return roleName;
    }

    public int getRoleID() {
        return roleID;
    }

    public int getScore() {
        return score;
    }
}
