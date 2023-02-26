package famMapServer.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.*;
import famMapServer.dataAccess.DataAccessException;
import famMapServer.services.RegisterService;
import requests.RegisterRequest;
import results.RegisterResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.util.Locale;

/**
 * Handler for the Register API
 */
public class RegisterHandler extends Handler implements HttpHandler {

    /**
     * Calls RegisterService function to interact with Database
     * @param exchange HttpExchange object to access request and response bodies
     * @throws IOException
     */
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        try {
            if(exchange.getRequestMethod().toLowerCase(Locale.ROOT).equals("post")) {
                InputStream reqBody = exchange.getRequestBody();
                Gson gson = new Gson();
                RegisterRequest request = gson.fromJson(readString(reqBody), RegisterRequest.class);
                reqBody.close();

                RegisterService service = new RegisterService();
                RegisterResult result = service.register(request);
                if(!result.success) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                    Writer rspBody = new OutputStreamWriter(exchange.getResponseBody());
                    gson.toJson(result, rspBody);
                    rspBody.close();
                }

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
