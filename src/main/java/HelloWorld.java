import static spark.Spark.*;
/**
 * Created by macbookpro on 29/11/2016.
 */
public class HelloWorld {
    public static void main(String[] args) {
        get("/hello", (req, res) -> "Hello World");
    }
}
