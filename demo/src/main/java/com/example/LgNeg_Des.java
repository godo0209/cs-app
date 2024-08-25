package com.example;

//Excepciones
    import java.io.IOException;
    import javax.crypto.BadPaddingException;
    import javax.crypto.IllegalBlockSizeException;
    import javax.crypto.NoSuchPaddingException;
    import java.security.InvalidKeyException;
    import java.security.NoSuchAlgorithmException;
    import java.security.spec.InvalidKeySpecException;
//Interfaz gráfica
    import javax.swing.JFileChooser;
    import javax.swing.JFrame;
    import javax.swing.JOptionPane;
//seguridad
    import java.security.PrivateKey;
//ficheros
    import java.io.ByteArrayInputStream;
    import java.awt.image.BufferedImage;
    import java.io.File;
    import java.io.FileOutputStream;
    import java.io.InputStream;
    import javax.imageio.ImageIO;
//Aux
    import org.apache.commons.io.FileUtils;
    import org.json.JSONObject;
    import java.util.Base64;

public class LgNeg_Des {

    private String fileId;
    private String userId;

    private EncriptadorRSA encripter;

    public LgNeg_Des(String fileId, String userId2, String hash, JFrame frame){
        this.fileId = fileId;
        this.userId = userId2;
        main(hash, frame);
    }

    public void main(String hash, JFrame frame){
        //1º Obtener los datos a desencriptar
            JSONObject resUser = requestUser();
            JSONObject resData = requestData();
        //2º Obtener el hash
            byte[] keyAES_For_RSA = getHalfHash(hash);
        //3º Desencriptamos la clave privada
            String auxKeyRSA = resUser.get("privateKeyRSA").toString();
            PrivateKey privateKeyRSA = decriptKeyRSA(auxKeyRSA, keyAES_For_RSA);
        //4º Desencriptamos con la clave privada la clave aes
            byte[] auxKeyAES = Base64.getDecoder().decode(resData.get("keyAES").toString());
            byte[] keyAES = decriptKeyAES(auxKeyAES, privateKeyRSA);
        //5º Desencriptamos con la clave aes el archivo
            String dataAES = resData.get("data_en").toString();
            byte[] data = decriptAES(dataAES, keyAES);
        //6º Guardamos el archivo desencriptado
            showDialog(1,"");
            String path = chooseFolder();
            saveFile(path, data, resData, frame, this.userId, hash);
    }
//Conexion con el servidor
    private JSONObject requestUser(){    //request the data encripted
        Conexion conect = new Conexion("http://localhost:3000/user/" + this.userId);
        return conect.sendGet();
    }
    private JSONObject requestData(){    //request the data encripted
        Conexion conect = new Conexion("http://localhost:3000/files/" + this.fileId);
        return conect.sendGet();
    }
//Seguridad
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
    private byte[] decriptAES(String dataAES, byte[] keyAES){
        byte[] res = null;

        try{
            EncriptadorAES encripter = new EncriptadorAES();
            res = encripter.decript(dataAES, keyAES, true);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException ex) {
            ex.printStackTrace();
            showDialog(-1, ex.toString());
        }
        
        return res;
    }

//Aux
    private byte[] getHalfHash(String hash){
        byte[] hashByte = Base64.getDecoder().decode(hash);

        byte[] halfByte = new byte[hashByte.length/2];
        for(int i = 0; i < hashByte.length/2; i++)
            halfByte[i] = hashByte[i];
        return halfByte;
    }
    private String chooseFolder(){
        String path = "";

        JFileChooser fc = new JFileChooser();   //creamso objeto fc

        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);    //solo archivos

        fc.setApproveButtonText("Seleccionar"); //añadimos boton seleccionar

        int result = fc.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            path = file.getPath();
        }
        return path;
    }
    private void saveFile(String path, byte[] data, JSONObject resData, JFrame frame, String id, String pass){
        //formateamos el path
        path = path + "/" + resData.get("name");
        String extension = resData.get("extension").toString();

        try{
            if(extension.equals("png") || extension.equals("jpg") || extension.equals("gif")){   
                ByteArrayInputStream bis = new ByteArrayInputStream(data);
                BufferedImage bi = ImageIO.read(bis);                
                ImageIO.write(bi, extension, new File(path));
            }
            else if(extension.equals("txt"))
                FileUtils.writeByteArrayToFile(new File(path), data);
            else if(extension.equals("mp4")){
                File file = new File(path);
                InputStream inputStream = new ByteArrayInputStream(data);
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    int read;
                    byte[] bytes = new byte[1024];

                    while ((read = inputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, read);
                    }
                }
            }
            showDialog(2,"");
        }catch(IOException e){
            showDialog(-1, e.toString());
        }
        new Interfaz_Choose(frame, id, pass);
    }
    private void showDialog(Integer option, String error){
        String msg = "Ha ocurrido el siguiente error en el proceso: " + error;

        switch(option){
            case 1:
                msg = "El archivo ha sido recuperado exitosamente. Por favor seleccione la carpeta donde desea guardar el fichero";
                break;
            case 2:
                msg = "Se ha completado el proceso de desencriptación. Muchas gracias por confiar en nuestro programa.";
                break;
        }
        JOptionPane.showMessageDialog(null, msg);
    }
}
