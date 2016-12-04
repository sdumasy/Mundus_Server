import database.DatabaseConnect;

import static spark.Spark.get;
import static spark.Spark.webSocket;
/**
 * Created by macbookpro on 29/11/2016.
 */
public class HelloWorld {
    public static void main(String[] args) {
        webSocket("/echo", EchoWebSocket.class);

        get("/hello", (req, res) -> "hoi");

        DatabaseConnect.connectToDb();
    }
}
