package http;

import application.App;
import models.Device;
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
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import spark.Spark;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by Thomas on 15-12-2016.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Device.class)
@PowerMockIgnore("javax.net.ssl.*")
public class RoutesTest {

    @BeforeClass
    public static void beforeClass() {
        App.main(null);
    }

    @AfterClass
    public static void afterClass() {
        Spark.stop();
    }

    /**
     * Method that makes requests and executes them.
     * @param uri The uri with the route that is supposed to be triggered.
     * @param json The json body attributes
     * @return An http response object
     * @throws IOException Throws an exception if the request execution fails.
     */
    public HttpResponse processRoute(String uri, String json) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:4567/" + uri);
        HttpEntity entity = new ByteArrayEntity(json.getBytes("UTF-8"));
        httpPost.setEntity(entity);
        return httpClient.execute(httpPost);

    }

    /**
     * Test the route that generates a token for a new device.
     *
     * @throws IOException Throws an exception if the request execution fails.
     */
    @Test
    public void createNewTokenTest() throws IOException {
        PowerMockito.mockStatic(Device.class);
        when(Device.newDevice(anyString())).thenReturn(new Device("some_id", "some_token"));

        String uri = "token";
        String json = "{\"deviceID\" : \"" + "some_id" + "\"}";
        HttpResponse response = processRoute(uri, json);

        String result = EntityUtils.toString(response.getEntity());
        assertEquals("HTTP/1.1 200 OK", response.getStatusLine().toString());
    }

    /**
     * Test the route that generates a token for a new device, when a token already exists.
     *
     * @throws IOException Throws an exception if the request execution fails.
     */
    @Test
    public void createDuplicateTokenTest() throws IOException {
        PowerMockito.mockStatic(Device.class);
        when(Device.newDevice(anyString())).thenReturn(null);

        String uri = "token";
        String json = "{\"deviceID\" : \"" + "some_id" + "\"}";
        HttpResponse response = processRoute(uri, json);

        String result = EntityUtils.toString(response.getEntity());
        assertEquals("HTTP/1.1 401 Unauthorized", response.getStatusLine().toString());
    }

//    //TODO: Fix create
//    @Test
//    public void createSessionTest() throws IOException {
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
//    }

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