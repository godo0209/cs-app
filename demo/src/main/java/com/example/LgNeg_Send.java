package com.example;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LgNeg_Send {
    private String oriFileId;
    private String newFileId;

    private String userReciberId;
    private String userReciberName;
    private String userSenderId;

    private JSONObject userSender;
    private JSONObject userReciber;
    private JSONObject oriFile;

    private String hash;

    private EncriptadorRSA encripter;

    private JFrame frame;

    public LgNeg_Send(String oriFileId, String userReciberName, String userSenderId, String hash, JFrame frame){
        this.oriFileId = oriFileId;
        this.userReciberName = userReciberName;
        this.userSenderId = userSenderId;
        this.hash = hash;
        this.frame = frame;
    }

    public void main(){
        //1º Recoger info del usuario sender, user reciber y file
            this.userSender = requestUser(true);
            this.userReciberId = requestUserId();
            if(this.userReciberId != null){
                this.userReciber = requestUser(false);
                this.oriFile = requestFile();
                //2º Desencrptar el file
                    byte[] keyAES_DECRIPT = decript(oriFile.getString("keyAES"), userSender.getString("privateKeyRSA"));
                //3º encriptar el file
                    String keyAES_ENCRIPT =  encript(keyAES_DECRIPT, userReciber.getString("publicKeyRSA"));
                //4º cambiar y enviar el file
                    JSONObject newFile = createJsonFile(keyAES_ENCRIPT);
                    JSONObject res = sendFile(newFile);
                    this.newFileId = res.getString("id");
                //5º guardar la notificacion
                    sendNotification();
                //6º Lanzar opcion de amigo
                    checkFriend();
                    new Interfaz_Choose(this.frame, this.userSenderId, this.hash);
            }
    }
//request
    private String requestUserId(){
        String id = null;

        Conexion conect = new Conexion("http://localhost:3000/user/id/" + this.userReciberName);
        JSONObject res = conect.sendGet();
        
        if(res.getString("result").equals("KO")){
            showDialog(4, "");
            new Interfaz_Send(this.frame, this.userSenderId, this.hash);
        }
        else if(res.getString("result").equals("OK")){
            if(!res.getString("id").equals(userSenderId))
                id = res.getString("id");
            else{
                showDialog(4, "");
            new Interfaz_Send(this.frame, this.userSenderId, this.hash);
            }  
        }

        return id;
    }
    private JSONObject requestUser(Boolean aux){
        String userId = "";
        if(aux)
            userId = this.userSenderId;
        else
            userId = this.userReciberId;
        Conexion conect = new Conexion("http://localhost:3000/user/"+userId);
        return conect.sendGet();
    }
    private JSONObject requestFile(){
        Conexion conect = new Conexion("http://localhost:3000/files/" + this.oriFileId);
        return conect.sendGet();
    }
//security
    private byte[] decript(String keyAES, String privateKeyRSA_AUX){
        byte[] res = null;
        //1º Obtenemos la mitad del hash para desencriptar la clave privada
            byte[] keyAES_For_RSA = getHalfHash(hash);
        //2º Desencriptamos la clave privada
            PrivateKey privateKeyRSA = decriptKeyRSA(privateKeyRSA_AUX, keyAES_For_RSA);
        //4º Desencriptamos con la clave privada la clave aes
            byte[] auxKeyAES = Base64.getDecoder().decode(keyAES);
            res = decriptKeyAES(auxKeyAES, privateKeyRSA);

        return res;
    }
    private PrivateKey decriptKeyRSA(String aux, byte[] keyAES_For_RSA){
        byte[] privateKeyRSA = null;

        try{
            EncriptadorAES encripter = new EncriptadorAES();
            privateKeyRSA = encripter.decript(aux, keyAES_For_RSA, true);
            this.encripter = new EncriptadorRSA(privateKeyRSA, true);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException ex) {
            ex.printStackTrace();
            showDialog(-1, ex.toString());
        }
        
        return this.encripter.getPrivateKey();
    }
    private byte[] decriptKeyAES(byte[] keyAES, PrivateKey privateKeyRSA){
        byte[] res = null;

        try{
            res = this.encripter.decript(keyAES, privateKeyRSA);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException ex) {
            ex.printStackTrace();
            showDialog(-1, ex.toString());
        }
        
        return res;
    }
    private String encript(byte[] keyAES, String publicKeyRSA){
        String res = null;
        try{
            EncriptadorRSA encriptadorRSA = null;
            encriptadorRSA = new EncriptadorRSA(Base64.getDecoder().decode(publicKeyRSA), false);
            res = Base64.getEncoder().encodeToString(encriptadorRSA.encript(keyAES, encriptadorRSA.getPublicKey()));
        } catch (NoSuchAlgorithmException | BadPaddingException |NoSuchPaddingException | InvalidKeyException |IllegalBlockSizeException | InvalidKeySpecException e) {
            showDialog(-1, e.toString());
            e.printStackTrace();
        }

        return res;
    }
//send
    private JSONObject createJsonFile(String keyAES){
        JSONObject newFile = new JSONObject();

        newFile.put("name", this.oriFile.getString("name"));
        newFile.put("extension", this.oriFile.getString("extension"));
        newFile.put("data_en", this.oriFile.getString("data_en"));
        newFile.put("keyAES", keyAES);
        newFile.put("userId", ""); 

        return newFile;
    }
    private JSONObject sendFile(JSONObject newFile){
        Conexion conect = new Conexion("http://localhost:3000/files");
        return conect.sendPost(newFile);
    }
    private void sendNotification(){
        JSONObject res = null;
        JSONObject notification = new JSONObject();

        notification.put("userSender", this.userSenderId);
        notification.put("userReciber", this.userReciberId);
        notification.put("file", this.newFileId);

        Conexion conect = new Conexion("http://localhost:3000/notifications");
        res = conect.sendPost(notification);

        if(res.getString("result").equals("KO") && res.getString("err").equals("Too manny notifications"))
            showDialog(2, "");
        else
            showDialog(1, "");
        
    }
    private JSONObject createJsonFriend(){
        JSONObject newFriend = new JSONObject();

        newFriend.put("userId", this.userSenderId);
        newFriend.put("newFriend", this.userReciberName); 

        return newFriend;
    }
    private void addFriend(){
        JSONObject res = null;

        Conexion conect = new Conexion("http://localhost:3000/user/"+userSenderId+"/friends");
        res = conect.sendPut(createJsonFriend());
        if(res.getString("result").equals("OK")){
            showDialog(3, "");
        }
    }
//aux
    private byte[] getHalfHash(String hash){
        byte[] hashByte = Base64.getDecoder().decode(hash);

        byte[] halfByte = new byte[hashByte.length/2];
        for(int i = 0; i < hashByte.length/2; i++)
            halfByte[i] = hashByte[i];
        return halfByte;
    }
    private void showDialog(Integer option, String error){
        String msg = "Ha ocurrido el siguiente error en el proceso: " + error;

        switch(option){
            case 1:
                msg = "El archivo ha sido enviado correctamente";
                break;
            case 2:
                msg = "<html><p aling=center>El número máximo de archivos que puede enviar a un mismo usuario es de 5.<br>";
                msg += "Si desea enviar más archivos al mismo usuario espere a que este abra los que ya le envió.";
                break;
            case 3:
                msg = "El usuario se añadió a su lista de amigos correctamente";
                break;
            case 4:
                msg = "El usuario introducido no existe o es usted mismo";
                break;
        }
        JOptionPane.showMessageDialog(null, msg);
    }
    private void checkFriend(){
        Boolean isIn = false;
        try{
            JSONArray friends = this.userSender.getJSONArray("friends");
            for(int i = 0; i < friends.length() && !isIn; i++)
                if(friends.getString(i).equals(this.userReciberName))    
                    isIn = true;
            
        }
        catch(JSONException e){
            
        }
        if(!isIn)
            friendRequest();
    }
    private void friendRequest(){
        int res = JOptionPane.showConfirmDialog(null, "¿Quiere añadir a " + this.userReciberName + " a su lista de amigos?", "mensaje", JOptionPane.YES_NO_OPTION);
        if(res == 0)
            addFriend();
    }
}
