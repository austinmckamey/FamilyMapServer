package famMapServer.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import famMapServer.dataAccess.DataAccessException;
import famMapServer.services.FillService;
import results.FillResult;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.util.Locale;

/**
 * Handler for the Fill API
 */
public class FillHandler extends Handler implements HttpHandler {

    /**
     * Calls FillService function to interact with Database
     * @param exchange HttpExchange object to access request and response bodies
     * @throws IOException
     */
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        try {
            if (exchange.getRequestMethod().toLowerCase(Locale.ROOT).equals("post")) {
                Gson gson = new Gson();
                String urlPath = exchange.getRequestURI().toString();
                String[] arrOfStr = urlPath.split("/", 0);
                String username = arrOfStr[2];
                int generations = 4;
                if(arrOfStr.length == 4) {
                    generations = Integer.parseInt(arrOfStr[3]);
                }

                FillService service = new FillService();
                FillResult result = service.fill(username,generations);

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
