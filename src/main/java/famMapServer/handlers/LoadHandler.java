package famMapServer.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import famMapServer.dataAccess.DataAccessException;
import famMapServer.services.LoadService;
import requests.LoadRequest;
import results.LoadResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.util.Locale;

/**
 * Handler for the Load API
 */
public class LoadHandler extends Handler implements HttpHandler {

    /**
     * Calls LoadService function to interact with Database
     * @param exchange HttpExchange object to access request and response bodies
     * @throws IOException
     */
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        try {
            if (exchange.getRequestMethod().toLowerCase(Locale.ROOT).equals("post")) {
                InputStream reqBody = exchange.getRequestBody();
                Gson gson = new Gson();
                LoadRequest request = gson.fromJson(readString(reqBody), LoadRequest.class);
                reqBody.close();

                LoadService service = new LoadService();
                LoadResult result = service.load(request);

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
