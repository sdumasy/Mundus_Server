package models;

import com.google.gson.JsonObject;
import database.CreateUniqueIDs;
import org.eclipse.jetty.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static database.SessionQueries.selectAuthorizationToken;
import static spark.Spark.halt;

/**
 * Model of a users device.
 */
public class Device {
    private String deviceID, token;
    private Map<String, Player> players;

    public Device(String deviceID, String token) {
        this.deviceID = deviceID;
        this.token = token;
        this.players = new HashMap<>();
    }

    public static Device newDevice(String deviceID) {
        if (selectAuthorizationToken(deviceID) == null) {
            return new Device(deviceID, CreateUniqueIDs.createToken(deviceID));
        } else {
            return null;
        }
    }

    public String getDeviceID() {
        return deviceID;
    }

    public String getToken() {
        return token;
    }

    public void addPlayer(Player player) {
        if (player.getDeviceID()==deviceID) {
            players.put(player.getPlayerID(),player);
        } else {
            halt(HttpStatus.UNAUTHORIZED_401, "DeviceID does not correspond with players deviceID.");
        }
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public boolean authenticate(){
        String authToken = selectAuthorizationToken(deviceID);
        return authToken != null && authToken.equals(token);
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceID",deviceID);
        jsonObject.addProperty("token", token);
        return jsonObject;
    }
}
