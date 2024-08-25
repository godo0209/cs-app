package com.example;

import java.util.Arrays;

public class CheckData {
    private String pass;
    private String name;

    private final String leters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String numbers = "0123456789";
    private final String characters = ".%$!?¿+*-_:~";

    public CheckData(String pass, String name){
        this.pass = pass;
        this.name = name;
    }

    public boolean checkData(){
        boolean res = true;

        if(this.pass.equals("") && this.name.equals(""))  //esta vacio
            res = false;
        else if(this.name.indexOf(' ') != -1 && this.pass.indexOf(' ') != -1)   //hay espacios            
            res = false;
        else if(this.pass.length() < 8 || this.name.length() < 4)
            res = false;
        else if(!checkName() || !checkPass())
            res = false;
        return res;
    }
    private boolean checkName(){
        boolean res = true;

        for(int i = 0; i < this.name.length() && res; i++){
            boolean ok = false;
            char caracter = this.name.charAt(i);
            
            for(int j = 0; j < this.leters.length() && !ok; j++){   //check letras
                if(caracter == this.leters.charAt(j))    //check mayusculas
                    ok = true;
                if(caracter == this.leters.toLowerCase().charAt(j))    //check minusculas
                    ok = true;
            }
            for(int j = 0; j < this.numbers.length() && !ok; j++)   //check numeros
                if(caracter == this.numbers.charAt(j))
                    ok = true;
            if(!ok)
                res = false;
        }
        return res;
    }
    private boolean checkPass(){
        /*  [0] = lo que devolvemos (si al final esta bien o no)
         *  [1] = si hay mayuscula
         *  [2] = si hay minuscula
         *  [3] = si hay numero
         *  [4] = si hay caracter
         */
        boolean[] res = new boolean[5];
        Arrays.fill(res, Boolean.FALSE);
        res[0] = true;

        for(int i = 0; i < this.pass.length() && res[0]; i++){
            boolean ok = false;
            char caracter = this.pass.charAt(i);
            
            for(int j = 0; j < this.leters.length() && !ok; j++){   //check letras
                if(caracter == this.leters.charAt(j)){    //check mayusculas
                    ok = true;
                    res[1] = true;
                }
                if(caracter == this.leters.toLowerCase().charAt(j)){    //check minusculas
                    ok = true;
                    res[2] = true;
                }
            }
            for(int j = 0; j < this.numbers.length() && !ok; j++){   //check numeros
                if(caracter == this.numbers.charAt(j)){
                    ok = true;
                    res[3] = true;
                }
            }
            for(int j = 0; j < this.characters.length() && !ok; j++){   //check caracteres
                if(caracter == this.characters.charAt(j)){
                    ok = true;
                    res[4] = true;
                }
            }
            if(!ok) //un caracter no pasa y se cancela la validacion
                res[0] = false;
        }
        for(int i = 0; i < res.length; i++)
            if(!res[i])
                res[0] = false;
        return res[0];
    }
}
