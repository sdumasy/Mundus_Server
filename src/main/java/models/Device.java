package models;

import com.google.gson.JsonObject;
import database.CreateUniqueIDs;

import static database.SessionQueries.selectAuthorizationToken;

/**
 * Model of a users device.
 */
public class Device {
    private String deviceID, token;

    public Device(String deviceID, String token) {
        this.deviceID = deviceID;
        this.token = token;
    }

    public static Device newDevice(String deviceID) {
        if (selectAuthorizationToken(deviceID) == null) {
            return new Device(deviceID, CreateUniqueIDs.createToken(deviceID));
        } else {
            return null;
        }
    }

    public static Device getDevice(String deviceID) {
        String token = selectAuthorizationToken(deviceID);
        if (token != null) {
            return new Device(deviceID, token);
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

    public boolean authenticate(){
        String authToken = selectAuthorizationToken(deviceID);
        return authToken != null && authToken.equals(token);
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceID", deviceID);
        jsonObject.addProperty("token", token);
        return jsonObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Device device = (Device) o;

        return deviceID.equals(device.deviceID) && token.equals(device.token);
    }
}
