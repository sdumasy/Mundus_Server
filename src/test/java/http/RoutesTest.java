package http;

import application.App;
import com.google.gson.JsonObject;
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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static database.Database.executeManipulationQuery;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
//import static validation.Validation.createToken;

/**
 * Created by Thomas on 15-12-2016.
 */
public class RoutesTest {
    private String deviceID = "deviceID_42";
    private String deviceID2 = "deviceID_43";

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
     *
     * @throws IOException Throws an exception if the request execution fails.
     */
    @Test
    public void createNewTokenTest() throws IOException {
        try {
            HttpClient httpClient = HttpClients.createDefault();

            HttpPost httpPost = new HttpPost("https://expeditionmundus.herokuapp.com/token");
            String json = "{\"deviceID\" : \"" + deviceID + "\"}";

            HttpEntity entity = new ByteArrayEntity(json.getBytes("UTF-8"));
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity());

            assertEquals("HTTP/1.1 200 OK", response.getStatusLine().toString());
        } finally {
            tearDown();
        }
    }

    //TODO: Fix test
    @Test
    public void createSessionTest() throws IOException {
//        JsonObject jsonObject = null;
//        try {
//            HttpClient httpClient = HttpClients.createDefault();
//
//            HttpPost httpPost = new HttpPost("https://expeditionmundus.herokuapp.com/session/create");
//            String token = createToken(deviceID);
//            String json = "{\"deviceID\" : \"" + deviceID + "\", \"token\" : \"" + token + "\"}";
//
//            HttpEntity entity = new ByteArrayEntity(json.getBytes("UTF-8"));
//            httpPost.setEntity(entity);
//            HttpResponse response = httpClient.execute(httpPost);
//            JsonParser jsonParser = new JsonParser();
//            jsonObject = (JsonObject) jsonParser.parse(EntityUtils.toString(response.getEntity()));
//            assertEquals("HTTP/1.1 200 OK", response.getStatusLine().toString());
//        } finally {
//            if(jsonObject != null) {
//                tearDownSession(jsonObject);
//            }
//            tearDown();
//        }
    }

//    @Test
//    public void joinSessionTest() throws IOException {
//        JsonObject jsonObject1 = null;
//        JsonObject jsonObject2 = null;
//        try {
//            createToken(deviceID);
//            String token2 = createToken(deviceID2);
//            jsonObject1 = createSession(deviceID);
//            String joinToken = jsonObject1.get("userToken").getAsString();
//
//            HttpClient httpClient = HttpClients.createDefault();
//            HttpPost httpPost = new HttpPost("https://expeditionmundus.herokuapp.com/session/join");
////            String json = "{\"deviceID\" : \"" + deviceID2 + "\", \"token\" : \"" + token2 + "\", \"joinToken\" : \""
////                    + joinToken + "\"}";
//
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("deviceID", deviceID2);
//            jsonObject.addProperty("token", token2);
//            jsonObject.addProperty("joinToken", joinToken);
//
//            HttpEntity entity = new ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
//            httpPost.setEntity(entity);
//            HttpResponse response = httpClient.execute(httpPost);
//            JsonParser jsonParser = new JsonParser();
//
//            jsonObject2 = (JsonObject) jsonParser.parse(EntityUtils.toString(response.getEntity()));
//
//            assertEquals("HTTP/1.1 200 OK", response.getStatusLine().toString());
//        } finally {
//            if (jsonObject2 != null) {
//                tearDownExtraPlayer(jsonObject2);
//            }
//            if (jsonObject1 != null) {
//                tearDownSession(jsonObject1);
//            }
//            tearDown();
//        }
//
//    }

    /**
     * Delete the extra player that was added to the session.
     *
     * @param jsonObject A jsonObject that contains the required playerID.
     */
    protected void tearDownExtraPlayer(JsonObject jsonObject) {
        String playerID = jsonObject.get("playerID").getAsString();
        Database.executeManipulationQuery("DELETE FROM session_player WHERE player_id='" + playerID + "';");
    }

    /**
     * Delete the device and token from the database.
     */
    public void tearDown() {
        executeManipulationQuery("DELETE FROM device WHERE device_id='" + deviceID + "';");
        executeManipulationQuery("DELETE FROM device WHERE device_id='" + deviceID2 + "';");
    }

    /**
     * Tear down a session after it has been created via a route
     *
     * @param jsonObject A JsonObject containing all session information
     */
    public void tearDownSession(JsonObject jsonObject) {
        String sessionID = jsonObject.get("sessionID").getAsString();
        String playerID = jsonObject.get("playerID").getAsString();
        String userToken = jsonObject.get("userToken").getAsString();
        String modToken = jsonObject.get("modToken").getAsString();
        Database.executeManipulationQuery("DELETE FROM session_player WHERE player_id='" + playerID + "';");
        Database.executeManipulationQuery("DELETE FROM session_token WHERE join_token='" + userToken + "';");
        Database.executeManipulationQuery("DELETE FROM session_token WHERE join_token='" + modToken + "';");
        Database.executeManipulationQuery("DELETE FROM session WHERE session_id='" + sessionID + "';");
        Database.executeManipulationQuery("DELETE FROM device WHERE device_id='" + deviceID + "';");
    }

    /**
     * Test whether constructor is private and does not raise any exceptions.
     *
     * @throws NoSuchMethodException     The method must be there.
     * @throws IllegalAccessException    The method must be accessible.
     * @throws InvocationTargetException The method must be invocable
     * @throws InstantiationException    The method must be instantiationable.
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