package security;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {

    public static byte[] hash(byte[] src) {
        return hash(new ByteArrayInputStream(src));
    }

    public static byte[] hash(InputStream in){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new byte[0];
        }
        byte[] data = new byte[2048];
        int read = 0;
        try {
            while ((read = in.read(data)) != -1)
            {
                md.update(data,0,read);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }

        return md.digest();
    }
}