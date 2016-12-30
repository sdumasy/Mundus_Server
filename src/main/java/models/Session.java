package models;

import com.google.gson.JsonObject;
import database.SessionQueries;

import java.time.LocalDateTime;

/**
 * Model of a session.
 */
public class Session {
    private String sessionID, adminID;
    private int status;
    private LocalDateTime created;

    /**
     * Constructor for a session.
     *
     * @param sessionID The sessions id.
     * @param adminID   The id of the admin.
     * @param status    The status of the session.
     * @param created   The time of creation.
     */
    public Session(String sessionID, String adminID, int status, LocalDateTime created) {
        this.sessionID = sessionID;
        this.adminID = adminID;
        this.status = status;
        this.created = created;
    }

    /**
     * Gets the session from the database.
     *
     * @param sessionID The sessions id.
     * @return The session.
     */
    public static Session getSession(String sessionID) {
        return SessionQueries.getSession(sessionID);
    }

    /**
     * Getter for the adminID.
     *
     * @return Returns the adminID.
     */
    public String getAdminID() {
        return adminID;
    }

    /**
     * Getter for the sessionID.
     *
     * @return Returns the sessionID.
     */
    public String getSessionID() {
        return sessionID;
    }

    /**
     * Getter for the sessions status.
     *
     * @return The status.
     */
    public int getStatus() {
        return status;
    }

    /**
     * Converts the model to Json.
     *
     * @return The Json object.
     */
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("sessionID", sessionID);
        jsonObject.addProperty("adminID", adminID);
        jsonObject.addProperty("status", status);
        jsonObject.addProperty("created", created.toString());
        return jsonObject;
    }

    /**
     * Checks whether object is equal to given object.
     *
     * @param o The object to compare with.
     * @return Whether they are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Session session = (Session) o;

        if (status != session.status) {
            return false;
        }
        if (!sessionID.equals(session.sessionID)) {
            return false;
        }
        if (!adminID.equals(session.adminID)) {
            return false;
        }
        return created.equals(session.created);
    }

    /**
     * Creates a hashcode of the session.
     *
     * @return The hashcode.
     */
    @SuppressWarnings("checkstyle:magicnumber") //31 is defined by intellij.
    @Override
    public int hashCode() {
        int result = sessionID.hashCode();
        result = 31 * result + adminID.hashCode();
        result = 31 * result + status;
        result = 31 * result + created.hashCode();
        return result;
    }
}
