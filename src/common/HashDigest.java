package common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashDigest {

    // Fields
    private String digest;
    private MessageDigest sha256;

    // Constructor
    /* The HasDigest constructor
     * The constructor takes a string as an input. While constructing the instance, the string gets hashed and assigned
     * to the digest-field.
     */
    public HashDigest(String hashString) {
        try {
            this.sha256 = MessageDigest.getInstance("SHA-256");
            StringBuilder stringBuilder = new StringBuilder();
            byte[] byteString = hashString.getBytes(StandardCharsets.UTF_8);
            byte[] hashedBytes = sha256.digest(byteString);

            for(int i = 0; i < hashedBytes.length; i++) {
                String hexString = Integer.toHexString(0xff & hashedBytes[i]);
                if(hexString.length() == 1) { stringBuilder.append('0'); }
                stringBuilder.append(hexString);
            }

            String hashedString = stringBuilder.toString();
            this.digest = hashedString;

        } catch (Exception e) {
            System.out.println("[HASH-EXCEPTION] " + e.getMessage());
        }
    }

    // Getter
    public String getDigest() {
        return this.digest;
    }


}
