package com.example;

//Interfaz grafica
    import javax.swing.JOptionPane;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JFrame;

import java.io.IOException;
//Excepciones
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import org.apache.commons.codec.binary.Base32;
//Aux
    import org.json.JSONObject;
    import java.util.Base64;

public class LgNeg_Register{
    private final String name;
    private final String pass;
    private String hash;
    private String publicKeyRSA;
    private String privateKeyRSA;

    private String salt;
    
    public LgNeg_Register(String name, String pass){
        this.name = name;
        this.pass = pass;
        this.salt = null;
    }

    public void main(JFrame frame){  //take the id as register
        //1º check teh data is ok
            CheckData chek = new CheckData(this.pass, this.name);
            Boolean ok = chek.checkData();
            if(ok){
                //2º obtein teh data to send
                    this.hash = hash();
                //3º Create the json we send
                    JSONObject user = createJsonUser();
                //4º send the json and take the response
                    JSONObject jsonRes = send(user);                
                //5º check the server response        
                    checkResponse(jsonRes, frame, hash);
            }
            else
                showDialog(3, "");
    }
    //security
        private String hash(){
            Hash hash = new Hash();
            
            hash.genSalt(null);
            hash.doHash(this.pass);

            this.salt = Base64.getEncoder().encodeToString(hash.getSalt());
            return hash.getHash();
        }
        private void createKeys(){
            //obtener el hahs
                byte[] keyAES_For_RSA = getHalfHash(this.hash);

                EncriptadorRSA encriptadorRSA = null;
                
                try{
                    encriptadorRSA = new EncriptadorRSA(null, false);
                }catch(NoSuchAlgorithmException | InvalidKeySpecException e){
                    e.printStackTrace();
                }
            //Cifrar la clave RSA con AES no aleatorio
                byte[] aux3 = encriptAES(encriptadorRSA.getPrivateKey().getEncoded(), keyAES_For_RSA);
                this.privateKeyRSA = Base64.getEncoder().encodeToString(aux3);
            //crear los ficheros json para mandarlos
                this.publicKeyRSA = Base64.getEncoder().encodeToString(encriptadorRSA.getPublicKey().getEncoded());
        }
        private byte[] encriptAES(byte[] data, byte[] keyAES){
            byte[] encriptado = null;
            try {
                EncriptadorAES encriptadorAES = new EncriptadorAES();
                encriptado = encriptadorAES.encript(data, keyAES);
            } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException ex) {
                ex.printStackTrace();
                showDialog(-1, ex.toString());
            }
    
            return encriptado;
        }
        private String genKeyAuth(){
            String res = "";
            SecureRandom sr = new SecureRandom();
            byte[] bytes = new byte[20];
            sr.nextBytes(bytes);

            byte[] keyAES_For_RSA = getHalfHash(this.hash);

            bytes = encriptAES(bytes, keyAES_For_RSA);

            Base32 base32 = new Base32();
            res = base32.encodeToString(bytes);
            return res;
        }
    //conexion
        private JSONObject createJsonUser(){
            JSONObject res = new JSONObject();

            createKeys();
            String keyAuth = genKeyAuth();

            res.put("name", this.name);
            res.put("password", hash);
            res.put("salt", this.salt);
            res.put("publicKeyRSA",  this.publicKeyRSA);
            res.put("privateKeyRSA", this.privateKeyRSA);
            res.put("keyAuth", keyAuth);
            
            return res;
        }
        private JSONObject send(JSONObject json){
            Conexion conect = new Conexion("http://localhost:3000/user/register");
            return conect.sendPost(json);
        }
    //aux
        private byte[] getHalfHash(String hash){
            byte[] hashByte = Base64.getDecoder().decode(hash);

            byte[] halfByte = new byte[hashByte.length/2];
            for(int i = 0; i < hashByte.length/2; i++)
                halfByte[i] = hashByte[i];
            return halfByte;
        }
        private void checkResponse(JSONObject res, JFrame frame, String hash){
            String result = res.get("result").toString();
            if(result.equals("KO")){
                String err = res.get("err").toString();
                if(err.equals("Invalid userName"))
                    showDialog(2, "");
                else
                    showDialog(-1, err);
            }                    
            else if(result.equals("OK")){
                createKeys();
                String userId = res.get("id").toString();
                new Interfaz_Autenticator(frame, userId, name, hash, false);
            }
            else{
                String msg = "Error inesperado";
                showDialog(-1, msg);
            }
        }
        private void showDialog(int option, String error){
        String msg = "Ha ocurrido el siguiente error en el proceso: " + error;

        switch(option){
            case 1:
                msg = "El proceso de registro se ha completado correctamente.";
                break;
            case 2:
                msg = "No puede registrarse debido a que el nombre introducido ya se encuentra en uso";
                break;
            case 3:
                msg = "<html><p align=center>El nombre ha de tener 4 o mas caracteres y solo puede contener letras del alfabeto ingles en minuscula y mayuscula y numeros.<br>";
                msg += "La contraseña ha de tener 8 o mas caracteres y no podra contener caracteres fuera del alfabeto ingles, numeros y caracteres especiales comunes como '-' y '_'.<br>";
                msg += "La contraseña debera de contener al menos una mayuscula, una minuscula, un numero y un caracter especial para una mayor seguridad<br>";
                msg += "Ni el nombre ni la contraseña podrán contener espacios en blanco";
                break;
        }
        JOptionPane.showMessageDialog(null, msg);
    }
}
