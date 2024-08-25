package com.example;

//interfaz
    import java.awt.BorderLayout;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.awt.Color;
    import javax.swing.ImageIcon;
    import javax.swing.JButton;
    import javax.swing.JFrame;
    import javax.swing.JLabel;
    import javax.swing.JOptionPane;
    import javax.swing.JPanel;
//aux
    import org.json.JSONArray;
    import org.json.JSONObject;

public class Interfaz_Choose {
    private JFrame frame;
    private JPanel panel;
    private String userId;
    private String hash;

    public Interfaz_Choose(JFrame frame2, String id, String pass){
        this.userId = id;
        this.frame = frame2;
        this.hash = pass;
        main();
    }

    public void main(){
        resetFrame();
        panel = new JPanel();
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();

        JLabel label = new JLabel("Select what you want to do");
        label.setForeground(Color.white);

        JButton request = new JButton("Request a file");
        ActionListener req_action = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                new Interfaz_Des(frame, userId, hash);
            }
        };
        JButton save = new JButton("Save a File");
        ActionListener save_action = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                new Interfaz_En(userId, hash, frame);
            }
        };
        JButton send = new JButton("Send a File");
        ActionListener send_action = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                new Interfaz_Send(frame, userId, hash);
            }
        };
        final int notis = checkNotifications();
        JButton noti = new JButton();
        noti.setText(String.valueOf(notis));
        noti.setIcon(new ImageIcon("/home/godo/Desktop/Dropbox/cs-p3/cs-app/icons//msg.png"));
        ActionListener noti_action = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if(notis == 0)
                    showDialog(1, "");
                else{
                    JSONArray notis = requestNotifications();
                    for(int i = 0; i < notis.length(); i++)
                        askFileDownload(notis.getJSONObject(i));           
                }
            }
        };
        noti.addActionListener(noti_action);

        save.addActionListener(save_action);
        request.addActionListener(req_action);
        send.addActionListener(send_action);

        panel1.add(label);
        panel2.add(save);
        panel2.add(request);
        panel2.add(send);

        panel.setBackground(new java.awt.Color(33, 33, 33));
        panel1.setBackground(new java.awt.Color(33, 33, 33));
        panel2.setBackground(new java.awt.Color(33, 33, 33));

        panel.add(panel1);
        panel.add(panel2);
        panel.add(noti);
        
        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.setVisible(true);
    }
//connection
    private int checkNotifications(){
            Conexion conect = new Conexion("http://localhost:3000/notifications/number/" + this.userId);
            JSONObject res = conect.sendGet();
            return res.getInt("notis");
    }
    private JSONArray requestNotifications(){
        JSONArray res = new JSONArray();

        Conexion conect = new Conexion("http://localhost:3000/notifications/" + this.userId);
        JSONObject res2 = conect.sendGet();
        if(res2.getString("result").equals("OK"))
            res = res2.getJSONArray("notis");

        return res;
    }
    private String requestUserName(String senderId){
        Conexion conect = new Conexion("http://localhost:3000/user/" + senderId);
        JSONObject res = conect.sendGet();
        return res.getString("name");
    }
    private String requestFileName(String fileId){
        Conexion conect = new Conexion("http://localhost:3000/files/" + fileId);
        JSONObject res = conect.sendGet();
        return res.getString("name");
    }
    private void addUserIdToFile(String userReciberId, String fileId){
        JSONObject send = new JSONObject();
        send.put("userId", userReciberId);

        Conexion conect = new Conexion("http://localhost:3000/files/" + fileId);
        JSONObject res = conect.sendPut(send);
        if(res.getString("result").equals("OK"))
            showDialog(2, "");
    }
    private void deleteNoti(String notiId){
        Conexion conect = new Conexion("http://localhost:3000/notifications/" + notiId);
        conect.sendDelete();
    }
    private void deleteFile(String fileId){
        Conexion conect = new Conexion("http://localhost:3000/files/" + fileId);
        conect.sendDelete();    
    }
//others
    private void askFileDownload(JSONObject noti){
        String userSenderName = requestUserName(noti.getString("userSender"));
        String fileName = requestFileName(noti.getString("file"));
        int res = JOptionPane.showConfirmDialog(null, userSenderName + " le ha enviado un archivo" + fileName + ". ¿Quiere añadirlo a su lista de archivos?", "mensaje", JOptionPane.YES_NO_OPTION);
        if(res == 0){
            addUserIdToFile(this.userId, noti.getString("file"));
            int res2 = JOptionPane.showConfirmDialog(null, "Le gustaríá descargar el archivo", "mensaje", JOptionPane.YES_NO_OPTION);
            if(res2 == 0){
                new LgNeg_Des(noti.getString("file"), this.userId, this.hash, this.frame);
                deleteNoti(noti.getString("_id"));
            }
            else{
                deleteNoti(noti.getString("_id"));
                deleteFile(noti.getString("file"));
            }
        }
        else{
            deleteNoti(noti.getString("_id"));
            deleteFile(noti.getString("file"));
        }
        main();
    }    
//aux
    private void resetFrame(){
        this.frame.getContentPane().removeAll();
        this.frame.invalidate();
        this.frame.validate();
        this.frame.repaint();
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.frame.setResizable(true);
        this.frame.setSize(400, 150);
        this.frame.setLocation(800, 450);
        this.frame.setResizable(false);
    }
    private void showDialog(Integer option, String error){
        String msg = "Ha ocurrido el siguiente error en el proceso: " + error;

        switch(option){
            case 1:
                msg = "No se ha encontrado ninguna notificacion para usted";
                break;
            case 2:
                msg = "Se ha añadido el archivo a su lista personal de archivos";
                break;
        }
        JOptionPane.showMessageDialog(null, msg);
    }
}