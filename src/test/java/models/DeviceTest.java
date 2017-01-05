package models;

import com.google.gson.JsonObject;
import database.DatabaseTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static database.Database.executeManipulationQuery;
import static junit.framework.TestCase.assertNotNull;
import static models.Device.getDevice;
import static models.Device.newDevice;
import static org.junit.Assert.*;

/**
 * Tests device.
 */
public class DeviceTest {
    private Device device;
    private String deviceID = "deviceID";
    private String token = "token";

    /**
     * Make sure the database is clean before we do anything else.
     */
    @BeforeClass
    public static void clean() {
        DatabaseTest.cleanDatabase();
    }

    /**
     * Setup dat default device for testing purposes.
     */
    @Before
    public void setup() {
        device = new Device(deviceID, token);
    }

    /**
     * Try creating a new device successfully.
     */
    @Test
    public void newDeviceTest() {
        Device newDevice = newDevice(deviceID);

        assertEquals(deviceID, newDevice.getDeviceID());
        assertNotNull(newDevice.getToken());

        executeManipulationQuery("DELETE FROM device WHERE device_id = ?", deviceID);
    }

    /**
     * Try creating a device that already exists.
     */
    @Test
    public void newDeviceDuplicateTest() {
        DatabaseTest.setupDevice();
        Device newDevice = newDevice(DatabaseTest.DEVICE_ID);

        assertNull(newDevice);
        DatabaseTest.cleanDatabase();
    }

    /**
     * Try getting a device from the database.
     */
    @Test
    public void getDeviceTest() {
        DatabaseTest.setupDevice();
        Device device = new Device(DatabaseTest.DEVICE_ID, DatabaseTest.TOKEN);

        assertEquals(device, getDevice(DatabaseTest.DEVICE_ID));
        DatabaseTest.cleanDatabase();
    }

    /**
     * Try getting a non existing device.
     */
    @Test
    public void getDeviceNullTest() {
        assertNull(getDevice("Non existing device"));
    }

    /**
     * Test the deviceID get method.
     */
    @Test
    public void getDeviceIDTest() {
        assertEquals(deviceID, device.getDeviceID());
    }

    /**
     * Test the token get method.
     */
    @Test
    public void getTokenTest() {
        assertEquals(token, device.getToken());
    }

    /**
     * Try authenticating a correct device.
     */
    @Test
    public void authenticateTestSucces() {
        DatabaseTest.setupDevice();
        Device device = new Device(DatabaseTest.DEVICE_ID, DatabaseTest.TOKEN);
        assertTrue(device.authenticate());
        DatabaseTest.cleanDatabase();
    }

    /**
     * Try authenticating a device that does not exist in the database.
     */
    @Test
    public void authenticateTestFailure() {
        assertFalse(device.authenticate());
    }

    /**
     * Try authenticating a device with an invalid token.
     */
    @Test
    public void authenticateTestFailure2() {
        DatabaseTest.setupDevice();
        Device device = new Device(DatabaseTest.DEVICE_ID, "other_token");
        assertFalse(device.authenticate());
        DatabaseTest.cleanDatabase();
    }

    /**
     * Try converting the device fields to JSON.
     */
    @Test
    public void toJsonTest() {
        JsonObject jsonObject = device.toJson();

        assertEquals(deviceID, jsonObject.get("deviceID").getAsString());
        assertEquals(token, jsonObject.get("token").getAsString());
    }

    /**
     * Verify that a device equal to itself.
     */
    @Test
    public void equalsSelfTest() {
        assertEquals(device, device);
    }

    /**
     * Verify that a device is the same if all fields are the same.
     */
    @Test
    public void equalsSameTest() {
        Device device2 = new Device(deviceID, token);
        assertEquals(device, device2);
    }

    /**
     * Verify that a device is not the same if the token is different.
     */
    @Test
    public void equalsOtherTest() {
        Device device2 = new Device(deviceID, "other");
        assertNotEquals(device, device2);
    }

    /**
     * Verify that a device is not the same if the deviceID is different.
     */
    @Test
    public void equalsOtherTest2() {
        Device device2 = new Device("other", token);
        assertNotEquals(device, device2);
    }

    /**
     * Verify that a device does not equal null.
     */
    @Test
    public void equalsOtherTest3() {
        assertFalse(device.equals(null));
    }

    /**
     * Verify that a device does not equal a string.
     */
    @Test
    public void equalsOtherTest4() {
        assertFalse(device.equals(""));
    }

    /**
     * Verify that the hashcode for two identical devices is the same.
     */
    @Test
    public void hashCodeTest() {
        Device device2 = new Device(deviceID, token);
        assertEquals(device.hashCode(), device2.hashCode());
    }

    /**
     * Verify that the hashcode for two different devices is NOT the same.
     */
    @Test
    public void hashCodeTest2() {
        Device device2 = new Device("other", token);
        assertNotEquals(device.hashCode(), device2.hashCode());
    }
}