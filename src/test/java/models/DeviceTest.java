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
 * Created by Thomas on 22-12-2016.
 */
public class DeviceTest {
    private Device device;
    private String deviceID = "deviceID";
    private String token = "token";

    @BeforeClass
    public static void clean() {
        DatabaseTest.cleanDatabase();
    }

    @Before
    public void setup() throws Exception {
        device = new Device(deviceID, token);
    }

    @Test
    public void newDeviceTest() {
        Device newDevice = newDevice(deviceID);

        assertEquals(deviceID, newDevice.getDeviceID());
        assertNotNull(newDevice.getToken());

        executeManipulationQuery("DELETE FROM device WHERE device_id='" + deviceID + "';");
    }

    @Test
    public void newDeviceNullTest() {
        DatabaseTest.setupDevice();
        Device newDevice = newDevice(DatabaseTest.DEVICE_ID);

        assertNull(newDevice);
        DatabaseTest.cleanDatabase();
    }

    @Test
    public void getDeviceTest() {
        DatabaseTest.setupDevice();
        Device device = new Device(DatabaseTest.DEVICE_ID, DatabaseTest.TOKEN);

        assertEquals(device, getDevice(DatabaseTest.DEVICE_ID));
        DatabaseTest.cleanDatabase();
    }

    @Test
    public void getDeviceNullTest() {
        assertNull(getDevice("Non existing device"));
    }

    @Test
    public void getDeviceIDTest() {
        assertEquals(deviceID, device.getDeviceID());
    }

    @Test
    public void getTokenTest() {
        assertEquals(token, device.getToken());
    }


    @Test
    public void authenticateTestSucces() throws Exception {
        DatabaseTest.setupDevice();
        Device device = new Device(DatabaseTest.DEVICE_ID, DatabaseTest.TOKEN);
        assertTrue(device.authenticate());
        DatabaseTest.cleanDatabase();
    }

    @Test
    public void authenticateTestFailure() throws Exception {
        assertFalse(device.authenticate());
    }

    @Test
    public void authenticateTestNull() throws Exception {
        Device device = new Device("device", null);
        assertFalse(device.authenticate());
    }

    @Test
    public void toJsonTest() throws Exception {
        JsonObject jsonObject = device.toJson();

        assertEquals(deviceID, jsonObject.get("deviceID").getAsString());
        assertEquals(token, jsonObject.get("token").getAsString());
    }

    @Test
    public void equalsSelfTest() throws Exception {
        assertEquals(device, device);
    }

    @Test
    public void equalsSameTest() throws Exception {
        Device device2 = new Device(deviceID, token);
        assertEquals(device, device2);
    }

    @Test
    public void equalsOtherTest() throws Exception {
        Device device2 = new Device(deviceID, "other");
        assertNotEquals(device, device2);
    }

    @Test
    public void equalsOtherTest2() throws Exception {
        Device device2 = new Device("other", token);
        assertNotEquals(device, device2);
    }

    @Test
    public void equalsOtherTest3() throws Exception {
        assertFalse(device.equals(null));
    }

    @Test
    public void equalsOtherTest4() throws Exception {
        assertFalse(device.equals(""));
    }

    @Test
    public void hashCodeTest() throws Exception {
        Device device2 = new Device(deviceID, token);
        assertEquals(device.hashCode(), device2.hashCode());
    }

    @Test
    public void hashCodeTest2() throws Exception {
        Device device2 = new Device("other", token);
        assertNotEquals(device.hashCode(), device2.hashCode());
    }
}