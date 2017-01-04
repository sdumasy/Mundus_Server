package database;

import models.Device;
import models.Player;
import models.Role;
import models.Session;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Thomas on 4-1-2017.
 */
final class MockHelper {
    private static String s;
    private static Object o;
    private static Map<String, Object> m;
    private static List<Map<String, Object>> result;
    private static List<Map<String, Object>> empty;
    private static Device device = new Device("", "");
    private static Session session = new Session("", "", 1, LocalDateTime.now());
    private static Player player = new Player("", session, device, Role.Admin, 0, "");

    /**
     * Private constructor.
     */
    private MockHelper() {
        //empty on purpose
    }

    /**
     * Helper method that sets up return maps for search querries in case the calls are mocked.
     */
    static void beforeMock() {
        s = "";
        o = "";
        m = new HashMap<>();
        m.put(s, o);
        result = new ArrayList<>();
        result.add(m);
        empty = new ArrayList<>();
    }

    /**
     * Get the map.
     * @return m
     */
    public static Map<String, Object> getM() {
        return m;
    }

    /**
     * Get the result List.
     * @return result
     */
    public static List<Map<String, Object>> getResult() {
        return result;
    }

    /**
     * Get the empty list.
     * @return empty
     */
    public static List<Map<String, Object>> getEmpty() {
        return empty;
    }

    /**
     * Get the test player.
     * @return player
     */
    public static Player getPlayer() {
        return player;
    }

    /**
     * Get the test device.
     * @return device
     */
    public static Device getDevice() {
        return device;
    }
}
