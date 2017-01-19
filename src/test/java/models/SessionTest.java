package models;

import com.google.gson.JsonObject;
import database.DatabaseTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;

import static models.Session.getSession;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

/**
 * Tests the session class.
 */
public class SessionTest {
    private Session session;
    private String sessionID = "sessionID";
    private String adminID = "adminID";
    private int status = 1;
    private LocalDateTime created = LocalDateTime.now();

    /**
     * Make sure the database is clean before we do anything else.
     */
    @BeforeClass
    public static void clean() {
        DatabaseTest.clean();
    }


    /**
     * Create a default session for testing purposes.
     */
    @Before
    public void setup() {
        session = new Session(sessionID, adminID, status, created);
    }

    /**
     * Test the get method for a session that has been added to the database.
     */
    @Test
    public void getSessionTest() {
        DatabaseTest.setupDevice();
        DatabaseTest.setupSession();

        Session newSession = getSession(DatabaseTest.SESSION_ID);

        assertEquals(DatabaseTest.SESSION_ID, newSession.getSessionID());
        assertEquals(DatabaseTest.PLAYER_ID, newSession.getAdminID());
        assertEquals(1, newSession.getStatus());

        DatabaseTest.cleanDatabase();
    }

    /**
     * Test the get method for sessionID.
     */
    @Test
    public void getSessionIDTest() {
        assertEquals(sessionID, session.getSessionID());
    }

    /**
     * Test the get method for adminID.
     */
    @Test
    public void getAdminIDTest() {
        assertEquals(adminID, session.getAdminID());
    }

    /**
     * Test the get method for status.
     */
    @Test
    public void getStatusTest() {
        assertEquals(status, session.getStatus());
    }

    /**
     * Test the return of a sessions attributes as a JsonObject.
     */
    @Test
    public void toJsonTest() {
        JsonObject jsonObject = session.toJson();

        assertEquals(sessionID, jsonObject.get("sessionID").getAsString());
        assertEquals(adminID, jsonObject.get("adminID").getAsString());
        assertEquals(status, jsonObject.get("status").getAsInt());
        assertEquals(created.toString(), jsonObject.get("created").getAsString());
    }

    /**
     * Verify that a session equal to itself.
     */
    @Test
    public void equalsSelfTest() {
        assertEquals(session, session);
    }

    /**
     * Verify that a session is the same if all fields are the same.
     */
    @Test
    public void equalsSameTest() {
        Session session2 = new Session(sessionID, adminID, status, created);
        assertEquals(session, session2);
    }

    /**
     * Verify that a session is not the same if the sessionID is different.
     */
    @Test
    public void equalsOtherTest() {
        Session session2 = new Session("other", adminID, status, created);
        assertNotEquals(session, session2);
    }

    /**
     * Verify that a session is not the same if the adminID is different.
     */
    @Test
    public void equalsOtherTest2() {
        Session session2 = new Session(sessionID, "other", status, created);
        assertNotEquals(session, session2);
    }

    /**
     * Verify that a session is not the same if the status is different.
     */
    @Test
    public void equalsOtherTest3() {
        Session session2 = new Session(sessionID, adminID, Integer.MAX_VALUE, created);
        assertNotEquals(session, session2);
    }

    /**
     * Verify that a session is not the same if the date is different.
     */
    @Test
    public void equalsOtherTest4() {
        Session session2 = new Session(sessionID, adminID, status, LocalDateTime.MAX);
        assertNotEquals(session, session2);
    }

    /**
     * Verify that a session does not equal null.
     */
    @Test
    public void equalsOtherTest5() {
        assertFalse(session == null);
    }

    /**
     * Verify that a session does not equal a string.
     */
    @Test
    public void equalsOtherTest6() {
        assertFalse(session.equals(""));
    }

    /**
     * Verify that the hashcode for two the same sessions is the same.
     */
    @Test
    public void hashCodeSelfTest() {
        assertEquals(session.hashCode(), session.hashCode());
    }

    /**
     * Verify that the hashcode for two identical sessions is the same.
     */
    @Test
    public void hashCodeSameTest() {
        Session session2 = new Session(sessionID, adminID, status, created);
        assertEquals(session.hashCode(), session2.hashCode());
    }

    /**
     * Verify that the hashcode for two different sessions is NOT the same.
     */
    @Test
    public void hashCodeTest() {
        Session session2 = new Session("other", adminID, status, created);
        assertNotEquals(session.hashCode(), session2.hashCode());
    }

}