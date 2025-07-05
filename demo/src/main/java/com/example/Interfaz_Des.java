package com.example;
//Interfaz gráfica
    import javax.swing.JFrame;
    import javax.swing.JPanel;
    import javax.swing.JScrollPane;
    import javax.swing.border.EmptyBorder;
    import javax.swing.BoxLayout;
    import javax.swing.ImageIcon;
    import java.awt.Color;
    import javax.swing.JButton;
    import javax.swing.JLabel;
    import javax.swing.JOptionPane;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.awt.BorderLayout;
    import java.awt.Component;
//Aux
    import org.json.JSONArray;
    import org.json.JSONObject;

public class Interfaz_Des {
    private String fileId;
    private String userId;
    private String hash;

    private Fichero[] ficheros;
    private Button[] botones;
    private Button[] trashButtons;
    private JFrame frame;

    public Interfaz_Des(JFrame frame2, String userIdAux, String hashAux) {
        this.frame = frame2;
        this.userId = userIdAux;
        this.hash = hashAux;
        main();
    }
    
    public void main(){
        JSONArray info =  requestFiles();
        createFiles(info);
        
        resetFrame();

        JPanel panel = new JPanel(), panelAux = new JPanel();
        //label
            JLabel label = new JLabel("Choose one of the files below");
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            label.setBorder(new EmptyBorder(10, 0, 10, 0));
            label.setForeground(Color.white);

        //ficheros
            JScrollPane scrollPanel = new JScrollPane(createPanel());
            scrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPanel.setBackground(new java.awt.Color(33, 33, 33));
        //boton back
            JButton back = new JButton();
            back.setText("Back");
            back.setIcon(new ImageIcon(getClass().getResource("/icons/back.png")));
            ActionListener back_action = new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    new Interfaz_Choose(frame, userId, hash);
                }
            };
            back.addActionListener(back_action);
            back.setBorder(new EmptyBorder(5, 5, 5, 5));

        //añadimos los componentes al panel principaal
            panelAux.add(back);
            panelAux.setBorder(new EmptyBorder(5, 0, 5, 0));
            panelAux.setBackground(new java.awt.Color(33, 33, 33));

            panel.add(label);
            panel.add(scrollPanel);
            panel.add(panelAux);
            panel.setBackground(new java.awt.Color(33, 33, 33));
        //lanzamos el frame
            frame.getContentPane().add(BorderLayout.CENTER, panel);
            frame.setVisible(true);
    }

//Preparing the files you can choose
    private JSONArray requestFiles() {  //request data for the view
        Conexion conect = new Conexion("http://localhost:3000/files/ids/" + this.userId);
        return conect.sendGet2();
    }
    public void createFiles(JSONArray info){  //create files with the data from requestFiles()
        ficheros = new Fichero[info.length()];
        
        for(int i = 0; i< info.length(); i++){
            JSONObject json = info.getJSONObject(i);
            ficheros[i] = new Fichero(json.getString("_id"), json.getString("name"), json.getString("date"));
        }
    }
//Creating the view
    private JPanel createPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new java.awt.Color(33, 33, 33));

        this.botones = new Button[ficheros.length];
        this.trashButtons = new Button[ficheros.length];

        for(int i = 0; i < ficheros.length; i++){
            JPanel panelAux = new JPanel();
            String txt = ficheros[i].getFecha()+"   " + ficheros[i].getNombre();
            Button bot = new Button(i, txt);
            Button trashBot = new Button(i, new ImageIcon(getClass().getResource("/icons/trash.png")));

            int spaceTop = 2, spaceBot = 2;
            if(i == 0)
                spaceTop = 10;
            if(i == ficheros.length-1)
                spaceBot = 10;
            panelAux.setBorder(new EmptyBorder(spaceTop, 0, spaceBot, 0));
            panelAux.setBackground(new java.awt.Color(33, 33, 33));

            panelAux.add(addButon(bot), BorderLayout.CENTER);
            panelAux.add(addTrashButon(trashBot), BorderLayout.CENTER);

            panel.add(panelAux, BorderLayout.CENTER);

            this.botones[i] = bot;
            this.trashButtons[i] = trashBot;
        }
        return panel;
    }
    private JButton addButon(Button bot){
        JButton boton = bot.getButton();

        ActionListener boton_action = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                for(int i = 0; i < botones.length; i++){
                    if(botones[i].getButton() == event.getSource()){
                        Integer aux = botones[i].getId();
                        fileId = ficheros[aux].getId();
                        new LgNeg_Des(fileId,userId, hash, frame);
                    }
                }
            }
        };
        boton.addActionListener(boton_action);
        return boton;
    }
    private JButton addTrashButon(Button bot){
        JButton trash = bot.getButton();

        ActionListener trash_action = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                for(int i = 0; i < trashButtons.length; i++){
                    if(trashButtons[i].getButton() == event.getSource()){
                        Integer aux = botones[i].getId();
                        fileId = ficheros[aux].getId();
                        deleteFile(fileId);
                    }
                }
            }
        };
        trash.addActionListener(trash_action);
        return trash;
    }
    private void deleteFile(String fileId){
        int res = JOptionPane.showConfirmDialog(null, "¿Esta seguro que quiere eliminar el archivo?", "mensaje", JOptionPane.YES_NO_OPTION);
        if(res == 0){
            Conexion conect = new Conexion("http://localhost:3000/files/" + fileId);
            JSONObject res2 = conect.sendDelete();
            if(res2.getString("result").equals("OK")){
                Conexion conect2 = new Conexion("http://localhost:3000/notifications/file/" + fileId);
                JSONObject res3 = conect2.sendDelete();
                if(res3.getString("result").equals("OK")){
                    showDialog(1, "");
                }
            }
            new Interfaz_Des(this.frame, this.userId, this.hash);
        }
    }
//aux
    private void resetFrame(){
        this.frame.getContentPane().removeAll();
        this.frame.invalidate();
        this.frame.validate();
        this.frame.repaint();
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setLocation(800, 450);
        this.frame.setResizable(false);
        if(this.ficheros.length > 0)
            this.frame.setSize(400, 230);
        else{
            this.frame.setSize(400, 130);
            showDialog(2, "");
        }
    }
    private void showDialog(Integer option, String error){
        String msg = "Ha ocurrido el siguiente error en el proceso: " + error;

        switch(option){
            case 1:
                msg = "Se ha eliminado el archivo correctamente";
                break;
            case 2:
                msg = "Actualmente no ha subido ningun fichero.Por favor suba ficheros para luego poder descargarlo";
                break;
        }
        JOptionPane.showMessageDialog(null, msg);
    }
}