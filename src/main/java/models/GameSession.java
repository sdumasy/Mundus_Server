package models;

/**
 * Created by macbookpro on 12/12/2016.
 */
public class GameSession {
    private String id;
    private String adminUserId;
    private String adminJoinToken;
    private String userJoinToken;


    public String getUserJoinToken() {
        return userJoinToken;
    }

    public GameSession(String adminUserId, String adminJoinToken, String userJoinToken) {
        this.adminJoinToken =  adminJoinToken;
        this.adminUserId = adminUserId;
        this.userJoinToken = userJoinToken;
    }

    public String getId() {
        return id;
    }

    public String getAdminUserId() {
        return adminUserId;
    }

    public String getAdminJoinToken() {
        return adminJoinToken;
    }
}
