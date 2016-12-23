package models;

import com.google.gson.JsonObject;
import database.DatabaseTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;

import static models.Session.getSession;
import static org.junit.Assert.assertEquals;

/**
 * Tests the session class.
 */
public class SessionTest {
    private Session session;
    private String sessionID = "sessionID";
    private String adminID = "adminID" ;
    private int status = 1;
    private LocalDateTime created = LocalDateTime.now();

    @BeforeClass
    public static void clean() {
        DatabaseTest.cleanDatabase();
    }

    @Before
    public void setup() throws Exception {
        session = new Session(sessionID, adminID, status, created);
    }

    @Test
    public void getSessionTest() throws Exception {
        DatabaseTest.setupDevice();
        DatabaseTest.setupSession();

        Session newSession = getSession(DatabaseTest.SESSION_ID);

        assertEquals(DatabaseTest.SESSION_ID, newSession.getSessionID());
        assertEquals(DatabaseTest.PLAYER_ID, newSession.getAdminID());
        assertEquals(1, newSession.getStatus());

        DatabaseTest.cleanDatabase();
    }

    @Test
    public void getSessionIDTest() throws Exception {
        assertEquals(sessionID, session.getSessionID());
    }

    @Test
    public void getAdminIDTest() throws Exception {
        assertEquals(adminID, session.getAdminID());
    }

    @Test
    public void getStatusTest() throws Exception {
        assertEquals(status, session.getStatus());
    }

    @Test
    public void toJsonTest() throws Exception {
        JsonObject jsonObject = session.toJson();

        assertEquals(sessionID, jsonObject.get("sessionID").getAsString());
        assertEquals(adminID, jsonObject.get("adminID").getAsString());
        assertEquals(status, jsonObject.get("status").getAsInt());
        assertEquals(created.toString(), jsonObject.get("created").getAsString());
    }

}