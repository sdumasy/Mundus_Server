package models;

/**
 * Created by macbookpro on 12/12/2016.
 */
public class JoinToken {
    private String tokenId;
    private String sessionId;
    private String roleId;

    public JoinToken(String sessionId, String roleId) {
        this.sessionId = sessionId;
        this.roleId = roleId;
    }
}
