package com.example;

public class Fichero {
    private String id;
    private String nombre;
    private String fecha;

    public Fichero(String id, String nombre, String fecha){
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
    }
    public String getId() {
        return id;
    }
    public String getNombre() {
        return nombre;
    }

    public String getFecha(){
        return fecha;
    }
}
