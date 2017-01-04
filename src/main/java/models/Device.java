package models;

import com.google.gson.JsonObject;
import database.CreateUniqueIDs;

import static database.AuthenticationTokenQueries.selectAuthorizationToken;

/**
 * Model of a users device.
 */
public class Device {
    private String deviceID, token;

    /**
     * Constructor of device.
     *
     * @param deviceID The device is of the device.
     * @param token    The authentication token of the device.
     */
    public Device(String deviceID, String token) {
        this.deviceID = deviceID;
        this.token = token;
    }

    /**
     * Creates a new device based on the deviceID.
     *
     * @param deviceID The deviceID.
     * @return A new device.
     */
    public static Device newDevice(String deviceID) {
        if (selectAuthorizationToken(deviceID) != null) {
            return null;
        }
        return new Device(deviceID, CreateUniqueIDs.createToken(deviceID));
    }

    /**
     * Creates a device based on the data given and from the database.
     *
     * @param deviceID The deviceID for the device.
     * @return The device.
     */
    public static Device getDevice(String deviceID) {
        String token = selectAuthorizationToken(deviceID);
        if (token == null) {
            return null;
        }
        return new Device(deviceID, token);
    }

    /**
     * Getter for the deviceID.
     *
     * @return The deviceID.
     */
    public String getDeviceID() {
        return deviceID;
    }

    /**
     * Getter for authentication token.
     *
     * @return The authenticcation token.
     */
    public String getToken() {
        return token;
    }

    /**
     * Authenticates whether the data in the Device model matches the data in the database.
     *
     * @return Whether it is correct.
     */
    public boolean authenticate() {
        String authToken = selectAuthorizationToken(deviceID);
        return authToken != null && authToken.equals(token);
    }

    /**
     * Converts the model to a JsonObject.
     *
     * @return The Json object.
     */
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceID", deviceID);
        jsonObject.addProperty("token", token);
        return jsonObject;
    }

    /**
     * Equals method to see if the objects are equal.
     *
     * @param o The object to compare it with.
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

        Device device = (Device) o;

        return deviceID.equals(device.deviceID) && token.equals(device.token);
    }

    /**
     * Hashcode method.
     *
     * @return returns a hashcode for the object.
     */
    @SuppressWarnings("checkstyle:magicnumber") //31 is defined by intellij.
    @Override
    public int hashCode() {
        int result = deviceID.hashCode();
        result = 31 * result + token.hashCode();
        return result;
    }
}
