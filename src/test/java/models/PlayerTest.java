//package models;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import static models.Role.getById;
//import static org.junit.Assert.*;
//
///**
// * Test of the player class.
// */
//public class PlayerTest {
//    private Player player;
//
//    /**
//     * Initialize the player constructor.
//     */
//    @Before
//    public void setupPlayer() {
//        player = new Player("playerID_42", "sessionID_42", "deviceID_42");
//    }
//
//    /**
//     * Test the creation of a new Player.
//     */
//    @Test
//    public void newPlayerTest() {
//        Player newPlayer = Player.newPlayer("sessionID_42", 2, "deviceID_42");
//        assertEquals(newPlayer.getDeviceID(), "deviceID_42");
//        assertEquals(newPlayer.getSessionID(), "sessionID_42");
//        assertEquals(newPlayer.getRoleID(), 2);
//    }
//
//    /**
//     * Test whether player properlyID is set properly.
//     */
//    @Test
//    public void getPlayerID() {
//        assertEquals(player.getPlayerID(), "playerID_42");
//    }
//
//    /**
//     * Test whether player sessionID is set properly.
//     */
//    @Test
//    public void getSessionID() {
//        assertEquals(player.getSessionID(), "sessionID_42");
//    }
//
//    /**
//     * Test whether player deviceID is set properly.
//     */
//    @Test
//    public void getDeviceID() {
//        assertEquals(player.getDeviceID(), "deviceID_42");
//    }
//
//    /**
//     *  Test whether player role is set properly.
//     */
//    @Test
//    public void getAndSetRoleID() {
//        player.setRoleID(0);
//        assertEquals(player.getRoleID(), 0);
//    }
//
//    /**
//     *  Test whether player role name is returned properly.
//     */
//    @Test
//    public void getRole() {
//        player.setRoleID(0);
//        assertEquals(player.getRole(), getById(0));
//    }
//
//    /**
//     *  Test whether player is an admin.
//     */
//    @Test
//    public void isAdminTrue() {
//        player.setRoleID(0);
//        assertTrue(player.isAdmin());
//    }
//
//    /**
//     * Test whether player is an admin.
//     */
//    @Test
//    public void isAdminFalse() {
//        player.setRoleID(1);
//        assertFalse(player.isAdmin());
//    }
//
//    /**
//     * Test the setting and getting of a players score.
//     */
//    @Test
//    public void getAndSetScore() {
//        player.setScore(42);
//        assertEquals(player.getScore(), 42);
//    }
//
//
//}