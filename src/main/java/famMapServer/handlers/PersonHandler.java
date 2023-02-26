package famMapServer.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import famMapServer.dataAccess.DataAccessException;
import famMapServer.services.PersonService;
import results.FamilyResult;
import results.PersonResult;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.util.Locale;

/**
 * Handler for the Person API
 */
public class PersonHandler extends Handler implements HttpHandler {

    /**
     * Calls PersonService function to interact with Database
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
                FamilyResult fResult = new FamilyResult(false, "");
                PersonResult pResult = new PersonResult(false,null,null,null,null,
                        null,null,null,null);

                if (reqHeaders.containsKey("Authorization")) {
                    String authToken = reqHeaders.getFirst("Authorization");
                    PersonService service = new PersonService();
                    if(arrOfStr.length != 3) {
                        fResult = service.getFamilyMembers(authToken);
                        if(!fResult.success) {
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                            Writer rspBody = new OutputStreamWriter(exchange.getResponseBody());
                            gson.toJson(fResult, rspBody);
                            rspBody.close();
                        }
                    } else {
                        String personId = arrOfStr[2];
                        pResult = service.getPerson(personId,authToken);
                        if(!pResult.success) {
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                            Writer rspBody = new OutputStreamWriter(exchange.getResponseBody());
                            gson.toJson(pResult, rspBody);
                            rspBody.close();
                        }
                    }
                }

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                Writer rspBody = new OutputStreamWriter(exchange.getResponseBody());
                if(arrOfStr.length != 3) {
                    gson.toJson(fResult, rspBody);
                } else {
                    gson.toJson(pResult, rspBody);
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
