package http;

import models.Device;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertTrue;

/**
 * Created by Thomas on 3-1-2017.
 */
public class RoutesTest {

    /**
     * Method that makes requests and executes them.
     * @param uri The uri with the route that is supposed to be triggered.
     * @param device The json device making the request.
     * @return An http response object.
     * @throws IOException Throws an exception if the request execution fails.
     */
    public static HttpResponse processAuthorizedPostRoute(String uri, Device device) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:4567" + uri);
        httpPost.addHeader("Authorization", device.getDeviceID() + ":" + device.getToken());
        return httpClient.execute(httpPost);
    }

    /**
     * Method that makes requests and executes them.
     * @param uri The uri with the route that is supposed to be triggered.
     * @param device The json device making the request.
     * @return An http response object.
     * @throws IOException Throws an exception if the request execution fails.
     */
    public static HttpResponse processAuthorizedPutRoute(String uri, Device device) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPut httpPut = new HttpPut("http://localhost:4567" + uri);
        httpPut.addHeader("Authorization", device.getDeviceID() + ":" + device.getToken());
        return httpClient.execute(httpPut);
    }

    /**
     * Method that makes requests and executes them.
     * @param uri The uri with the route that is supposed to be triggered.
     * @param device The json device making the request.
     * @return An http response object.
     * @throws IOException Throws an exception if the request execution fails.
     */
    public static HttpResponse processAuthorizedGetRoute(String uri, Device device) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:4567" + uri);
        httpGet.addHeader("Authorization", device.getDeviceID() + ":" + device.getToken());
        return httpClient.execute(httpGet);
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
