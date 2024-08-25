package com.example;
//Archivos
    import java.io.File;
    import java.io.FileInputStream;
    import java.nio.file.Files;
    import java.nio.file.Paths;
    import java.io.ByteArrayOutputStream;
    import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
    import java.io.BufferedInputStream;
//conexion
//Excepciones
    import java.security.InvalidKeyException;
    import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
    import javax.crypto.IllegalBlockSizeException;
    import javax.crypto.NoSuchPaddingException;
    import java.io.IOException;

import javax.swing.JFrame;
//Interfaz grafica
    import javax.swing.JOptionPane;

import org.json.JSONException;
//Aux
    import org.json.JSONObject;
    import java.util.Base64;

public class LgNeg_En {
    public LgNeg_En(File file_Ori, String path, String userId, String hash, JFrame frame){
        main(file_Ori, path, userId, hash, frame);
    }
    public void main(File file_Ori, String path, String userId, String hash, JFrame frame){   
        String passToMain = hash;         
        //2º obtener los datos a encriptar
            String extension = getExtension(file_Ori.getName()), name = file_Ori.getName();
            path = path + "/" + name;
            byte[] data = null;
            try {
                data = getData(path, extension);
            } catch (IOException e) {
                showDialog(-1, e.toString());
            }
        //2º cifrar el archivo con AES aleatorio
            byte[] secretAES = genSecret();
            byte[] aux = encriptAES(data, secretAES);
            String dataAES = Base64.getEncoder().encodeToString(aux);
        //3º cifrar el secreto AES con RSA
            EncriptadorRSA encriptadorRSA = null;
            byte[] aux2 = null;
            String secretAES_RSA = null;
            try {
                byte[] publicKeyRSA = getPublicKeyRSA(userId);
                encriptadorRSA = new EncriptadorRSA(publicKeyRSA, false);                   
                aux2 = encriptadorRSA.encript(secretAES, encriptadorRSA.getPublicKey());
                secretAES_RSA = Base64.getEncoder().encodeToString(aux2);
            } catch (NoSuchAlgorithmException | BadPaddingException |NoSuchPaddingException | InvalidKeyException |IllegalBlockSizeException | InvalidKeySpecException e) {
                showDialog(-1, e.toString());
                e.printStackTrace();
            }
        //5º crear los ficheros json para mandarlos
            JSONObject file = createJsonFile(name, extension, dataAES, secretAES_RSA, userId);
        //6º guardar los json
            JSONObject resFile = sendFile(file);
        //7º comprobamos la respuesta y volvemos a la pantalla principal
            checkResponse(resFile, frame, userId, passToMain);
    }
//Recogida de datos
    private byte[] getPublicKeyRSA(String userId){
        byte[] res = null;

        Conexion conect = new Conexion("http://localhost:3000/user/publicKey/" + userId);
        JSONObject aux = conect.sendGet();

        res = Base64.getDecoder().decode(aux.getString("publicKeyRSA"));

        return res;
    }
//Seguridad de los datos
    private byte[] genSecret(){
        SecureRandom aux = new SecureRandom();
        byte[] res = new byte[16];
        aux.nextBytes(res);
        return res;
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
//Preparacion de los datos a enviar
    private JSONObject createJsonFile(String name, String extension, String dataAES, String secretAES_RSA, String userId) {
        JSONObject res = new JSONObject();

        res.put("name", name);
        res.put("extension", extension);
        res.put("data_en", dataAES);
        res.put("keyAES", secretAES_RSA);
        res.put("userId", userId);        

        return res;
    }
//envio de datos
    private JSONObject sendFile(JSONObject file){
        Conexion conect = new Conexion("http://localhost:3000/files");
        return conect.sendPost(file);
    }
//checkRespuesta
    private void checkResponse(JSONObject resFile, JFrame frame, String userId, String passToMain){
        try {
            if(resFile.get("result").equals("OK"))
                showDialog(1, "");
            else
                showDialog(-1, "Error inesperado");
        } catch (JSONException e) {
            showDialog(-1, e.toString());
        }
    }
//Aux
    private byte[] getData(String path, String extension) throws IOException{
        byte[] data = null;
        if((extension.equals("png")) || (extension.equals("gif")) ||(extension.equals("jpg"))){
            BufferedImage bi2 = ImageIO.read(new File(path)); 
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bi2, extension, bos);

            data = bos.toByteArray();
        }
        else if(extension.equals("txt"))
            data = Files.readAllBytes(Paths.get(path));
        else if(extension.equals("mp4")){
            File file = new File(path);
            int size = (int) file.length();
            data = new byte[size];

            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(data, 0, data.length);
            buf.close();
        }
        return data;
    }
    private void showDialog(int option, String error){
        String msg = "Ha ocurrido el siguiente error en el proceso: " + error;
        if(option == 1)
            msg = "Se ha completadop el proceso de guardado. Muchas gracias por confiar en nuestro programa";
        JOptionPane.showMessageDialog(null, msg);
    }
    private String getExtension(String filename){
        String extension = "";  //Nos quedamos con la extension

        int index = filename.lastIndexOf('.');
        if (index > 0)
            extension = filename.substring(index+1);

        return extension;
    }
}
