package com.custom.widget;

import org.apache.logging.log4j.LogManager;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import javax.crypto.Cipher;
import org.apache.logging.log4j.Logger;

public class Decryption
{
    private static final String AES = "AES";
    private static final Logger logger;
    
    public static String decrypt(final String strToDecrypt, final String secret) {
        try {
            final Key key = generateKey(secret);
            final Cipher cipher = Cipher.getInstance("AES");
            cipher.init(2, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        }
        catch (Exception e) {
            Decryption.logger.error("Error while decrypting: " + e.toString());
            return null;
        }
    }
    
    private static Key generateKey(final String secret) throws Exception {
        final byte[] decoded = Base64.getDecoder().decode(secret.getBytes());
        final Key key = new SecretKeySpec(decoded, "AES");
        return key;
    }
    
    public static String encodeKey(final String str) {
        final byte[] encoded = Base64.getEncoder().encode(str.getBytes());
        return new String(encoded);
    }
    
    public static String getDecryptedData(final String inputvalue) {
        final String encodedBase64Key = encodeKey("A6$%0_2n@!*$3gc@");
        final String decrStr = decrypt(inputvalue, encodedBase64Key);
        return decrStr;
    }
    
    static {
        logger = LogManager.getLogger("Widget");
    }
}
