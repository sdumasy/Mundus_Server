package http;

import application.App;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import database.Database;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.Spark;
import validation.Validation;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static database.Database.executeUpdateQuery;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Thomas on 15-12-2016.
 */
public class RoutesTest {
    private String deviceID = "deviceID_42";

    @BeforeClass
    public static void beforeClass() {
        App.main(null);
        //SessionQueriesTest.tearDown()
    }

    @AfterClass
    public static void afterClass() {
        Spark.stop();
    }

    /**
     * Test the route that generates a token for a new device.
     * @throws IOException Throws an exception if the request execution fails.
     */
    @Test
    public void aNewTokenShouldBeCreated() throws IOException {
        HttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost("http://localhost:4567/token");
        String json = "{\"deviceID\" : \"" + deviceID + "\"}";

        HttpEntity entity = new ByteArrayEntity(json.getBytes("UTF-8"));
        httpPost.setEntity(entity);
        HttpResponse response = httpClient.execute(httpPost);
        String result = EntityUtils.toString(response.getEntity());

        assertEquals("HTTP/1.1 200 OK", response.getStatusLine().toString());
        tearDown();
    }

    @Test
    public void aSessionShouldBeCreated() throws IOException {
        HttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost("http://localhost:4567/session/create");
        String token = Validation.createToken(deviceID);
        String json = "{\"deviceID\" : \"" + deviceID + "\", \"token\" : \""+ token + "\"}";

        HttpEntity entity = new ByteArrayEntity(json.getBytes("UTF-8"));
        httpPost.setEntity(entity);
        HttpResponse response = httpClient.execute(httpPost);
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(EntityUtils.toString(response.getEntity()));

        assertEquals("HTTP/1.1 200 OK", response.getStatusLine().toString());
        tearDownSession(jsonObject);
    }


    /**
     * Delete the device and token from the database.
     */
    public void tearDown() {
        executeUpdateQuery("DELETE FROM device WHERE device_id='" + deviceID + "';");
    }

    /**
     * Tear down a session after it has been created via a route
     * @param jsonObject A JsonObject containing all session information
     */
    public void tearDownSession(JsonObject jsonObject) {
        String sessionID = jsonObject.get("sessionID").getAsString();
        String playerID = jsonObject.get("playerID").getAsString();
        String userToken = jsonObject.get("userToken").getAsString();
        String modToken = jsonObject.get("modToken").getAsString();
        Database.executeUpdateQuery("DELETE FROM session_player WHERE player_id='" + playerID + "';");
        Database.executeUpdateQuery("DELETE FROM session_token WHERE join_token='" + userToken + "';");
        Database.executeUpdateQuery("DELETE FROM session_token WHERE join_token='" + modToken + "';");
        Database.executeUpdateQuery("DELETE FROM session WHERE session_id='" + sessionID + "';");
        Database.executeUpdateQuery("DELETE FROM device WHERE device_id='" + deviceID + "';");
    }

    /**
     * Test whether constructor is private and does not raise any exceptions.
     * @throws NoSuchMethodException The method must be there.
     * @throws IllegalAccessException The method must be accessible.
     * @throws InvocationTargetException The method must be invocable
     * @throws InstantiationException The method must be instantiationable.
     */
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        Constructor<Routes> constructor = Routes.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }


}