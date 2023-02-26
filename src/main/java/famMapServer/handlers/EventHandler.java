package famMapServer.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import famMapServer.dataAccess.DataAccessException;
import famMapServer.services.EventService;
import results.AllEventsResult;
import results.EventResult;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.util.Locale;

/**
 * Handler for the Event API
 */
public class EventHandler extends Handler implements HttpHandler {

    /**
     * Calls EventService function to interact with Database
     * @param exchange HttpExchange object to access request and response bodies
     * @throws IOException
     */
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        try {
            if(exchange.getRequestMethod().toLowerCase(Locale.ROOT).equals("get")) {
                Gson gson = new Gson();
                String urlPath = exchange.getRequestURI().toString();
                String[] arrOfStr = urlPath.split("/", 0);
                Headers reqHeaders = exchange.getRequestHeaders();
                AllEventsResult aResult = new AllEventsResult(false, "");
                EventResult eResult = new EventResult(false,null,null,null,0,
                        0,null,null,null,0);

                if (reqHeaders.containsKey("Authorization")) {
                    String authToken = reqHeaders.getFirst("Authorization");
                    EventService service = new EventService();
                    if(arrOfStr.length != 3) {
                        aResult = service.getUserFamilyEvents(authToken);
                        if(!aResult.success) {
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                            Writer rspBody = new OutputStreamWriter(exchange.getResponseBody());
                            gson.toJson(aResult, rspBody);
                            rspBody.close();
                        }
                    } else {
                        String eventId = arrOfStr[2];
                        eResult = service.getEvent(eventId,authToken);
                        if(!eResult.success) {
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                            Writer rspBody = new OutputStreamWriter(exchange.getResponseBody());
                            gson.toJson(eResult, rspBody);
                            rspBody.close();
                        }
                    }
                }

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                Writer rspBody = new OutputStreamWriter(exchange.getResponseBody());
                if(arrOfStr.length != 3) {
                    gson.toJson(aResult, rspBody);
                } else {
                    gson.toJson(eResult, rspBody);
                }
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
