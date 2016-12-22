package models;

import com.google.gson.JsonObject;
import database.DatabaseTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static database.Database.executeManipulationQuery;
import static junit.framework.TestCase.assertNotNull;
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
    public void toJsonTest() throws Exception {
        JsonObject jsonObject = device.toJson();

        assertEquals(deviceID, jsonObject.get("deviceID").getAsString());
        assertEquals(token, jsonObject.get("token").getAsString());
    }

}