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
 * Created by Thomas on 23-12-2016.
 */
public class SessionTest {
    private Session session;
    private String sessionID = "sessionID";
    private String adminPlayerID = "adminID" ;
    private int status = 1;
    private LocalDateTime created = LocalDateTime.now();

    @BeforeClass
    public static void clean() {
        DatabaseTest.cleanDatabase();
    }

    @Before
    public void setup() throws Exception {
        session = new Session(sessionID,adminPlayerID,status,created);
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
        assertEquals(adminPlayerID, session.getAdminID());
    }

    @Test
    public void getStatusTest() throws Exception {
        assertEquals(status, session.getStatus());
    }

    @Test
    public void toJsonTest() throws Exception {
        JsonObject jsonObject = session.toJson();

        assertEquals(sessionID, jsonObject.get("sessionID").getAsString());
        assertEquals(adminPlayerID, jsonObject.get("admin").getAsString());
        assertEquals(status, jsonObject.get("status").getAsInt());
        assertEquals(created.toString(), jsonObject.get("created").getAsString());
    }

}