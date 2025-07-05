package com.example;

//Interfaz gráfica
    import javax.swing.JFrame;
    import javax.swing.JPanel;
    import javax.swing.JScrollPane;
    import javax.swing.border.EmptyBorder;
    import javax.swing.BoxLayout;
    import javax.swing.ImageIcon;
    import javax.swing.JButton;
    import java.awt.Color;
    import javax.swing.JLabel;
    import javax.swing.JOptionPane;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.awt.BorderLayout;
    import java.awt.Component;
//Aux
    import org.json.JSONArray;
    import org.json.JSONObject;

public class Interfaz_PickFile extends JFrame{
    private String fileId;
    private String userReciberName;
    private String userSenderId;
    private String hash;

    private Fichero[] ficheros;
    private Button[] botones;
    private JFrame frame;

    public Interfaz_PickFile(JFrame frame2, String userSenderIdAux, String userReciberNameAux, String hashAux){
        this.frame = frame2;
        this.userSenderId = userSenderIdAux;
        this.userReciberName = userReciberNameAux;
        this.hash = hashAux;
        main();
    }
    
    public void main()  {
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
                    new Interfaz_Send(frame, userSenderId, hash);
                }
            };
            back.addActionListener(back_action);
            back.setBorder(new EmptyBorder(5, 5, 5, 5));
        //boton home
            JButton home = new JButton();
            home.setText("Home");
            home.setIcon(new ImageIcon(getClass().getResource("/icons/home.png")));
            ActionListener home_action = new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    new Interfaz_Choose(frame, userSenderId, hash);
                }
            };
            home.addActionListener(home_action);
            home.setBorder(new EmptyBorder(5, 5, 5, 5));

        //añadimos los componentes al panel principaal
            panelAux.add(back);
            panelAux.add(home);
            panelAux.setBorder(new EmptyBorder(5, 5, 5, 5));
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

        Conexion conect = new Conexion("http://localhost:3000/files/ids/" + this.userSenderId);
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
        panel.setBackground(new java.awt.Color(33, 33, 33));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        botones = new Button[ficheros.length];

        for(int i = 0; i < ficheros.length; i++){
            JPanel panelAux = new JPanel();
            String txt = ficheros[i].getFecha()+"   " + ficheros[i].getNombre();
            Button bot = new Button(i, txt);
            JButton boton = bot.getButton();

            ActionListener boton_action = new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    for(int i = 0; i < botones.length; i++){
                        if(botones[i].getButton() == event.getSource()){
                            Integer aux = botones[i].getId();
                            fileId = ficheros[aux].getId();
                            LgNeg_Send app = new LgNeg_Send(fileId,userReciberName, userSenderId, hash, frame);
                            app.main();
                        }
                    }
                }
            };
            boton.addActionListener(boton_action);
            boton.setAlignmentX(Component.CENTER_ALIGNMENT);
            int spaceTop = 2, spaceBot = 2;
            if(i == 0)
                spaceTop = 10;
            if(i == ficheros.length-1)
                spaceBot = 10;
            panelAux.setBorder(new EmptyBorder(spaceTop, 0, spaceBot, 0));
            panelAux.add(boton, BorderLayout.CENTER);
            panelAux.setBackground(new java.awt.Color(33, 33, 33));
            panel.add(panelAux, BorderLayout.CENTER);

            botones[i] = bot;
        }
        return panel;
    }
//aux
    private void resetFrame(){
        frame.getContentPane().removeAll();
        frame.invalidate();
        frame.validate();
        frame.repaint();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(800, 450);
        frame.setResizable(false);
        if(this.ficheros.length > 0)
            this.frame.setSize(400, 250);
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
                msg = "Actualmente no ha subido ningun fichero.Por favor suba ficheros para luego poder enviarlos";
                break;
        }
        JOptionPane.showMessageDialog(null, msg);
    }
}