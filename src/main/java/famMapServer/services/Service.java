package famMapServer.services;

import java.util.Random;

/**
 * Generic service class other services can implement
 */
public class Service {

    /**
     * Creates randomized token for authTokens and IDs
     * @return random token
     */
    public String createToken() {
        String CHARS = "abcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder token = new StringBuilder();
        Random rnd = new Random();
        while (token.length() < 8) {
            int index = (int) (rnd.nextFloat() * CHARS.length());
            token.append(CHARS.charAt(index));
        }
        return token.toString();
    }
}
