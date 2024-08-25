package com.example;

//interfaz
    import java.awt.BorderLayout;
    import java.awt.Color;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import javax.swing.JButton;
    import javax.swing.JFrame;
    import javax.swing.JLabel;
    import javax.swing.JPanel;
    import javax.swing.JTextField;

public class Interfaz_Login {
    private static JFrame frame;

    public static void main( String[] args ){
        //Creating the Frame
        frame = new JFrame("");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 170);
        frame.setLocation(800, 450);
        frame.setResizable(false);

        JPanel panel = new JPanel();
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();

        JLabel label = new JLabel("Please Login or register");
        label.setForeground(Color.white);
        panel.add(label);

        JLabel label_login = new JLabel("Name");
        final JTextField text_log = new JTextField(20);
        label_login.setForeground(Color.white);
        panel1.add(label_login);
        panel1.add(text_log);

        JLabel label_register = new JLabel("Password");
        final JTextField text_reg = new JTextField(20);
        label_register.setForeground(Color.white);
        panel2.add(label_register);
        panel2.add(text_reg);

        JButton login = new JButton("Login");
        ActionListener log_action = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                new LgNeg_Login(text_log.getText(), text_reg.getText(), frame);
            }
        };
        login.addActionListener(log_action);
        panel3.add(login);
        JButton register = new JButton("Register");
        ActionListener reg_action = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                LgNeg_Register app = new LgNeg_Register(text_log.getText(), text_reg.getText());
                app.main(frame);
            }
        };
        register.addActionListener(reg_action);

        panel3.add(register);

        panel.add(panel1);
        panel.add(panel2);
        panel.add(panel3);

        panel1.setBackground(new java.awt.Color(33, 33, 33));
        panel2.setBackground(new java.awt.Color(33, 33, 33));
        panel3.setBackground(new java.awt.Color(33, 33, 33));
        panel.setBackground(new java.awt.Color(33, 33, 33));

        frame.getContentPane().add(BorderLayout.CENTER, panel);
        
        frame.setVisible(true); 
    }
}