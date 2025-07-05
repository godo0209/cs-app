package com.example;

//Interfaz gráfica
    import javax.swing.JFrame;
    import javax.swing.JPanel;
    import javax.swing.JScrollPane;
    import javax.swing.JTextField;
    import javax.swing.border.EmptyBorder;
    import javax.swing.BoxLayout;
    import javax.swing.ImageIcon;
    import javax.swing.JButton;
    import javax.swing.JLabel;
    import javax.swing.JOptionPane;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.awt.BorderLayout;
    import java.awt.Color;
    import java.awt.Component;
//Aux
    import org.json.JSONArray;
    import org.json.JSONObject;

public class Interfaz_Send extends JFrame{
    private String userReciberName;
    private String userSenderId;
    private String hash;

    private String[] friends;
    private Button[] botones;
    private JFrame frame;

    public Interfaz_Send(JFrame frame2, String userIdAux, String hashAux){
        this.frame = frame2;
        this.userSenderId = userIdAux;
        this.hash = hashAux;
        main();
    }
    public void main(){

        requestFriends();

        resetFrame();

        JPanel panel = new JPanel(), panel1 = createPanel1(), panelAux = new JPanel();

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(panel1);
        //friends
            if(this.friends != null){
                JLabel label2 = new JLabel("Or send it to one of your friends");
                label2.setAlignmentX(Component.CENTER_ALIGNMENT);
                label2.setBackground(new java.awt.Color(33, 33, 33));
                label2.setForeground(Color.white);
                label2.setBorder(new EmptyBorder(10, 0, 10, 0));

                JScrollPane scrollPanel = new JScrollPane(createPanel2());
                scrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                scrollPanel.setBackground(new java.awt.Color(33, 33, 33));

                panel.add(label2);
                panel.add(scrollPanel);
            }
        //boton back
            JButton back = new JButton();
            back.setText("Back");
            back.setIcon(new ImageIcon(getClass().getResource("/icons/back.png")));
            ActionListener back_action = new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    new Interfaz_Choose(frame, userSenderId, hash);
                }
            };
            back.addActionListener(back_action);
            back.setBorder(new EmptyBorder(5, 5, 5, 5));
            panelAux.add(back);
            panelAux.setBorder(new EmptyBorder(5, 0, 5, 0));
            panelAux.setBackground(new java.awt.Color(33, 33, 33));
            panel.add(panelAux);

        panel.setBackground(new java.awt.Color(33, 33, 33));
        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.setVisible(true);
    }

//Preparing the files you can choose
    private void requestFriends() {  //request data for the view
        Conexion conect = new Conexion("http://localhost:3000/user/friends/" + this.userSenderId);
        JSONObject res2 = conect.sendGet();
        if(res2.getString("result").equals("OK")){
            JSONArray res = res2.getJSONArray("friends");
            createFriends(res);
        }
    }
    public void createFriends(JSONArray info){  //create freinds with the data from requestFriends()
        friends = new String[info.length()];
        
        for(int i = 0; i< info.length(); i++){
            friends[i] = info.getString(i);
        }
    }
//Creating the view
    private JPanel createPanel1() {
        JPanel panel = new JPanel(), panelAux = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Enter the name of the user you want to send the file:");
        label.setBorder(new EmptyBorder(10, 0, 10, 0));
        label.setBackground(new java.awt.Color(33, 33, 33));
        label.setForeground(Color.white);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);

        final JTextField textF = new JTextField(20);
        textF.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(textF);

        JButton send = new JButton("Send");
        ActionListener send_action = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                userReciberName = textF.getText();
                if(!userReciberName.equals(""))
                    new Interfaz_PickFile(frame, userSenderId, userReciberName, hash);
                else
                    showDialog(1, "");
            }
        };
        send.addActionListener(send_action);
        send.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelAux.add(send);
        panelAux.setBorder(new EmptyBorder(10, 0, 0, 0));
        panelAux.setBackground(new java.awt.Color(33, 33, 33));
        panel.add(panelAux);
        panel.setBackground(new java.awt.Color(33, 33, 33));
        
        return panel;
    }
    private JPanel createPanel2(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new java.awt.Color(33, 33, 33));

        botones = new Button[friends.length];
        for(int i = 0; i < friends.length; i++){
            JPanel panelAux = new JPanel();
            panelAux.setLayout(new BoxLayout(panelAux, BoxLayout.Y_AXIS));
            panelAux.setBackground(new java.awt.Color(33, 33, 33));

            Button bot = new Button(i, friends[i]);
            JButton boton = bot.getButton();

            ActionListener boton_action = new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    for(int i = 0; i < botones.length; i++){
                        if(botones[i].getButton() == event.getSource()){
                            Integer aux = botones[i].getId();
                            userReciberName = friends[aux];
                            new Interfaz_PickFile(frame, userSenderId, userReciberName, hash);
                        }
                    }
                }
            };
            boton.addActionListener(boton_action);
            boton.setAlignmentX(Component.CENTER_ALIGNMENT);

            int spaceTop = 2, spaceBot = 2;
            if(i == 0)
                spaceTop = 10;
            if(i == friends.length-1)
                spaceBot = 10;
            panelAux.setBorder(new EmptyBorder(spaceTop, 0, spaceBot, 0));
            panelAux.add(boton, BorderLayout.CENTER);

            panel.add(panelAux, BorderLayout.CENTER);

            botones[i] = bot;
        }
        return panel;
    }
//aux
    private void resetFrame(){
        this.frame.getContentPane().removeAll();
        this.frame.invalidate();
        this.frame.validate();
        this.frame.repaint();
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.frame.setResizable(true);
        if(this.friends != null)
            this.frame.setSize(400, 275);
        else
            this.frame.setSize(400, 180);
        this.frame.setLocation(800, 450);
        this.frame.setResizable(false);
    }
    private void showDialog(Integer option, String error){
        String msg = "Ha ocurrido el siguiente error en el proceso: " + error;

        switch(option){
            case 1:
                msg = "No puede dejar el campo user vacio";
                break;
        }
        JOptionPane.showMessageDialog(null, msg);
    }
}