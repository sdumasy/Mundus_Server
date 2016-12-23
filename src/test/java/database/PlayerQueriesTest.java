package database;

import org.junit.Test;

/**
 * Created by Thomas on 23-12-2016.
 */
public class PlayerQueriesTest {
    @Test
    public void playerExists() throws Exception {

    }

    @Test
    public void playerIDExists() throws Exception {

    }

    @Test
    public void addNewPlayer() throws Exception {

    }

    @Test
    public void getPlayer() throws Exception {

    }

//    /**
//     * Test adding an extra player to a session and verifying its values.
//     */
//    @Test
//    public void setGetPlayer() {
//        try {
//            Player player = addNewPlayerSetup();
//
//            Map<String, Object> map = PlayerQueries.getPlayerData(player);
//            assertEquals("playerID_42", (String) map.get("player_id"));
//            assertEquals(sessionID, (String) map.get("session_id"));
//            assertEquals(localDevice, (String) map.get("device_id"));
//        } finally {
//            addNewPlayerTearDown();
//        }
//    }
//
//
//
//    /**
//     * Creates a session setup with one extra player added.
//     * @return The extra player
//     */
//    public Player addNewPlayerSetup() {
//        Validation.createToken(localDevice);
//        createSessionSetup();
//        Player player = new Player("playerID_42", sessionID, localDevice);
//        player.setRoleID(0);
//        player.setScore(42);
//        PlayerQueries.addNewPlayer(player);
//        return player;
//    }
//
//    /**
//     * Deletes the extra player and tears down the session.
//     */
//    private void addNewPlayerTearDown() {
//        Database.executeManipulationQuery("DELETE FROM session_player WHERE player_id='" + "playerID_42" + "';");
//        createSessionTearDown();
//        Database.executeManipulationQuery("DELETE FROM device WHERE device_id='" + localDevice + "';");
//    }
//    /**
//     * Test getting the role of a player.
//     */
//    @Test
//    public void getRoleIdTest() {
//        try {
//            Player player = addNewPlayerSetup();
//            assertEquals(0, (int) getRoleId(player));
//        } finally {
//            addNewPlayerTearDown();
//        }
//    }
//
//    /**
//     * Test getting the score of a player.
//     */
//    @Test
//    public void getScoreTest() {
//        try {
//            Player player = addNewPlayerSetup();
//            assertEquals(42, (int) getScore(player));
//        } finally {
//            addNewPlayerTearDown();
//        }
//    }

}