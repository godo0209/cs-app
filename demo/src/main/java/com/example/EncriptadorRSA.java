package com.example;


//excepciones
import java.security.InvalidKeyException;
import java.security.KeyFactory;

import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;

//seguridad
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyPairGenerator;
import javax.crypto.Cipher;


public class EncriptadorRSA {
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public EncriptadorRSA(byte[] keyRSA, Boolean aux) throws NoSuchAlgorithmException, InvalidKeySpecException{
        if(keyRSA != null){
            KeyFactory kf = KeyFactory.getInstance("RSA");
            if(aux){
                PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(keyRSA);
                PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);
                this.privateKey = privKey;
            }
            else{
                X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(keyRSA);
                PublicKey publicKey = kf.generatePublic(keySpecX509);
                this.publicKey = publicKey;
            }
        }
        else{
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            KeyPair keyPair = keyGen.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
        }
    }

    public byte[] encript(byte[] data_enc_AES, PublicKey publicKeyRSA) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        byte[] data_enc_RSA = null;
        Cipher rsaCipher = Cipher.getInstance("RSA");

        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKeyRSA);
        data_enc_RSA = rsaCipher.doFinal(data_enc_AES);
        return data_enc_RSA;
    }
    public byte[] decript(byte[] data_enc_RSA, PrivateKey privateKeyRSA) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        byte[] data_enc_AES = null;
        Cipher rsaCipher = Cipher.getInstance("RSA");
        
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKeyRSA);
        data_enc_AES = rsaCipher.doFinal(data_enc_RSA);

        return data_enc_AES;
    }
    public PublicKey getPublicKey(){
        return this.publicKey;
    }
    public PrivateKey getPrivateKey(){
        return this.privateKey;
    }
}
