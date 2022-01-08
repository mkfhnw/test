package common;

import java.security.MessageDigest;
import java.util.Random;

public class Token {

    // Fields
    private String tokenString;
    private String user;
    private final int timeToLive = 3600000;
    private final long unixTimeOfCreation = System.currentTimeMillis() / 1000L;
    private final long unixTimeOfDeath = this.unixTimeOfCreation + this.timeToLive;
    public static int globalCounter = 0;

    // Constructor
    // A fresh token must always be unique - otherwise one could either forge or calculate a token and hijack a session
    // To generate a unique token, we will hash a random byte array and append a counter to the end of the hexdigest of it
    // This way, we'll generate a 'continuously random' string and somewhat avoid the birthday problem.
    // Tokens have a 15min time to live, measured in UnixTime.
    public Token() {

        // Count up
        globalCounter++;

        // Generate random byte array
        byte[] byteArray = new byte[10];
        new Random().nextBytes(byteArray);

        // Hash the random byte array with SHA-256
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] encodedPassword = sha256.digest(byteArray);
            StringBuilder stringBuilder = new StringBuilder();

            // Build hash string from hex values
            for(int i = 0; i < encodedPassword.length; i++) {
                String hexString = Integer.toHexString(0xff & encodedPassword[i]);
                if(hexString.length() == 1) { stringBuilder.append('0'); }
                stringBuilder.append(hexString);
            }

            // Generate string and append counter int to it
            String randomString = stringBuilder.toString();
            randomString = randomString + globalCounter;

            // Set as field
            this.tokenString = randomString;


        } catch (Exception e) {
            System.out.println("[TOKEN-EXCEPTION] " + e.getMessage());


        }

    }

    // Getter
    public String getTokenString() {
        return this.tokenString;
    }
    public String getUser() { return this.user; }
    public long getUnixTimeOfDeath() { return this.unixTimeOfDeath; }

    // Setter
    public void setUser(String username) { this.user = username; }

}
