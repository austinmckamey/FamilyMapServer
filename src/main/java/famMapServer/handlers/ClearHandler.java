package famMapServer.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.*;
import famMapServer.dataAccess.DataAccessException;
import famMapServer.services.ClearService;
import results.ClearResult;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.util.Locale;

/**
 * Handler for the Clear API
 */
public class ClearHandler extends Handler implements HttpHandler {

    /**
     * Calls ClearService function to interact with Database
     * @param exchange HttpExchange object to access request and response bodies
     * @throws IOException
     */
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        try {
            if(exchange.getRequestMethod().toLowerCase(Locale.ROOT).equals("post")) {
                Gson gson = new Gson();

                ClearService clear = new ClearService();
                ClearResult result = clear.clear();

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                Writer rspBody = new OutputStreamWriter(exchange.getResponseBody());
                gson.toJson(result, rspBody);
                rspBody.close();
                success = true;
            }
            if(!success) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        } catch (IOException | DataAccessException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            exchange.getResponseBody().close();
            e.printStackTrace();
        }
    }
}
