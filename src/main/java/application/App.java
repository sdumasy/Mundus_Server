package application;

import http.Routes;

/**
 * Created by macbookpro on 29/11/2016.
 */
public final class App {
    /**
     * Private constructor.
     */
    private App() { }

    /**
     * Main method.
     * @param args Runtime args.
     */
    public static void main(String[] args) {
        Routes.setupRoutes();
    }
}
