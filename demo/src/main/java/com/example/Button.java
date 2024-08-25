package com.example;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Button {
    private Integer id;
    private JButton boton;

    public Button(int id, String name) {
        this.id = id;
        this.boton = new JButton(name);
    }
    public Button(int id, ImageIcon icon){
        this.id = id;
        this.boton = new JButton(icon);
    }
    public JButton getButton(){
        return boton;
    }
    public int getId(){
        return id;
    }
}
