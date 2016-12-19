import http.Routes;
import spark.Request;

import static database.SessionQueries.retrieveSessionToken;

/**
 * Created by macbookpro on 29/11/2016.
 */
public class App {
    public static void main(String[] args) {
        Routes.setupRoutes();
    }
}
