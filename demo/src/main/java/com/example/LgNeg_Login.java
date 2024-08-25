package com.example;

//Interfaz grafica
    import javax.swing.JOptionPane;
    import javax.swing.JFrame;
//Excepciones
import java.util.Base64;

//Aux
    import org.json.JSONObject;

public class LgNeg_Login {
    private final String name;
    private String pass;
    
    public LgNeg_Login(String name, String pass, JFrame frame){
        this.name = name;
        this.pass = pass;
        main(frame);
    }

    public void main(JFrame frame){  //take the id as login or register
        //1º check teh data is ok
        CheckData chek = new CheckData(this.pass, this.name);
        Boolean ok = chek.checkData();
        if(ok){
            //2º obtain the salt
                byte[] salt = requestSalt();
                if(salt != null){
                //3º obtain the hash from that salt
                    String hash = hash(salt);       
                //4º Create the json we send
                    JSONObject userData = new JSONObject();
                    userData.put("name", this.name);
                    userData.put("password", hash);
                //5º send the json and take the response
                    JSONObject jsonRes = login(userData);                
                //6º check the server response        
                    checkResponse(jsonRes, frame, hash);
                }
        }
        else
            showDialog(4, "");
    }
    private byte[] requestSalt(){
        byte[] salt = null;

        Conexion conect = new Conexion("http://localhost:3000/user/salt/" + this.name);
        JSONObject res = conect.sendGet();
        if(res.getString("result").equals("OK"))
            salt = Base64.getDecoder().decode(res.getString("salt"));
        else
            showDialog(2, "");
        return salt;
    }
    private String hash(byte[] salt){
        Hash hash = new Hash();

        hash.genSalt(salt);
        hash.doHash(this.pass);

        return this.pass = hash.getHash();
    }
    private JSONObject login(JSONObject userdata){
        Conexion conect = new Conexion("http://localhost:3000/user/login");
        return conect.sendPost(userdata);
    }
    private void checkResponse(JSONObject res, JFrame frame, String hash){
        String result = res.get("result").toString();
        if(result.equals("KO")){
            String err = res.get("err").toString();
            if(err.equals("User not found"))
                showDialog(2, "");
            else if(err.equals("Bad key"))
                showDialog(3, "");
            else
                showDialog(-1, "Error inesperado");
        }                    
        else if(result.equals("OK")){
            String userId = res.get("id").toString();
            showDialog(1, "");
            new Interfaz_Autenticator(frame, userId, name, hash, true);
        }
        else
            showDialog(-1, "Error inesperado");
    }
    private void showDialog(int option, String error){
        String msg = "Ha ocurrido el siguiente error en el proceso: " + error;

        switch(option){
            case 1:
                msg = "El proceso de autenticacion se ha completado correctamente.";
                break;
            case 2:
                msg = "Lo sentimos pero no se ha podido encontrar su nombre de usuario";
                break;
            case 3:
                msg = "Lo sentimos pero su contraseña no coincide";
                break;
            case 4:
                msg = "<html><p align=center>El nombre ha de tener 4 o mas caracteres y solo puede contener letras del alfabeto ingles en minuscula y mayuscula y numeros.<br>";
                msg += "La contraseña ha de tener 8 o mas caracteres y no podra contener caracteres fuera del alfabeto ingles, numeros y caracteres especiales comunes como '-' y '_'.<br>";
                msg += "La contraseña debera de contener al menos una mayuscula, una minuscula, un numero y un caracter especial para una mayor seguridad<br>";
                msg += "Ni el nombre ni la contraseña podrán contener espacios en blanco";
                break;
        }
        JOptionPane.showMessageDialog(null, msg);
    }
}
