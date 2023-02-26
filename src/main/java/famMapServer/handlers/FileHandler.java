package famMapServer.handlers;

import com.sun.net.httpserver.*;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.file.Files;

/**
 * Handler for the default API
 */
public class FileHandler extends Handler implements HttpHandler {

    /**
     * Default handler to return files
     * @param exchange HttpExchange object to access request and response bodies
     * @throws IOException
     */
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        try {
            System.out.println("File being handled");
            String urlPath = exchange.getRequestURI().toString();
            if(urlPath == null | urlPath.equals("/")) {
                urlPath = "/index.html";
            }
            String filePath = "web" + urlPath;
            File file = new File(filePath);
            if(!file.exists()) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
                filePath = "web/HTML/404.html";
                file = new File(filePath);
            } else {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                success = true;
                System.out.println("File handled");
            }
            OutputStream rspBody = exchange.getResponseBody();
            Files.copy(file.toPath(), rspBody);
            exchange.getResponseBody().close();
            if (!success) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        } catch (IOException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
            e.printStackTrace();
        }
    }
}
