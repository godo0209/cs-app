package com.example;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base32;

public class EncriptadorAES {
    private SecretKeySpec genKey(byte[] claveEncriptacion) throws UnsupportedEncodingException, NoSuchAlgorithmException {                    
        
        SecretKeySpec secretKey = new SecretKeySpec(claveEncriptacion, "AES");
        return secretKey;
    }
    public byte[] decript(String datosEncriptados, byte[] claveSecreta, Boolean aux) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec secretKey = this.genKey(claveSecreta);

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] bytesEncriptados = null;
        if(aux)
            bytesEncriptados = Base64.getDecoder().decode(datosEncriptados);
        else{
            Base32 base32 = new Base32();
            bytesEncriptados = base32.decode(datosEncriptados);
        }
        byte[] datosDesencriptados = cipher.doFinal(bytesEncriptados);
        
        return datosDesencriptados;
    }
    public byte[] encript(byte[] datos, byte[] claveSecreta) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        
        SecretKeySpec secretKey = this.genKey(claveSecreta);

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");        
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] bytesEncriptados = cipher.doFinal(datos);
        //String encriptado = Base64.getEncoder().encodeToString(bytesEncriptados);

        return bytesEncriptados;
    }
}