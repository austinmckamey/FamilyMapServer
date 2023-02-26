package famMapServer.handlers;

import com.sun.net.httpserver.*;

import famMapServer.dataAccess.DataAccessException;
import famMapServer.services.LoginService;
import requests.LoginRequest;
import results.LoginResult;

import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.Locale;

/**
 * Handler for the Login API
 */
public class LoginHandler extends Handler implements HttpHandler {

    /**
     * Calls LoginService function to interact with Database
     * @param exchange HttpExchange object to access request and response bodies
     * @throws IOException
     */
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        try {
            if(exchange.getRequestMethod().toLowerCase(Locale.ROOT).equals("post")) {
                InputStream reqBody = exchange.getRequestBody();
                Gson gson = new Gson();
                LoginRequest request = gson.fromJson(readString(reqBody), LoginRequest.class);
                reqBody.close();

                LoginService service = new LoginService();
                LoginResult result = service.login(request);
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
