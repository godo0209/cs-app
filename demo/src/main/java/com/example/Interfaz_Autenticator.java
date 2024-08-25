package com.example;

//totp
    import java.io.File;
    import java.io.FileOutputStream;
    import org.apache.commons.codec.binary.Base32;
    import org.apache.commons.codec.binary.Hex;
    import com.google.zxing.BarcodeFormat;
    import com.google.zxing.MultiFormatWriter;
    import com.google.zxing.common.BitMatrix;
    import com.google.zxing.client.j2se.MatrixToImageWriter;
    import de.taimos.totp.TOTP;
//excepciones
    import java.security.InvalidKeyException;
    import java.security.NoSuchAlgorithmException;
    import javax.crypto.BadPaddingException;
    import javax.crypto.IllegalBlockSizeException;
    import javax.crypto.NoSuchPaddingException;
    import java.io.UnsupportedEncodingException;

//interfaz
    import javax.swing.BoxLayout;
    import javax.swing.ImageIcon;
    import javax.swing.JButton;
    import javax.swing.JFrame;
    import java.awt.BorderLayout;
    import java.awt.EventQueue;
    import javax.swing.JLabel;
    import javax.swing.JOptionPane;
    import javax.swing.JPanel;
    import javax.swing.JTextField;
    import javax.swing.border.EmptyBorder;
    import java.awt.Color;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.awt.Component;
//aux
    import java.util.Base64;
    import org.json.JSONObject;  

public class Interfaz_Autenticator {
    private JFrame frame;
    private String userId;
    private String hash;
    private String code;
    private String name;
    private String keyAu;
    private Boolean parar = false;

    public Interfaz_Autenticator(JFrame frame, String userId, String name, String hash, Boolean login){
        this.frame = frame;
        this.userId = userId;
        this.hash = hash;
        this.name = name;
        main(login);
    }
    public void main(Boolean login){
        //1º Creamos el frame
            createFrame();
        //2º generate secretKey and show to user
            keyAu = getKeyAutenticator();
            
            if(!login){
                showDialog(1, "");
                String conexion = getGoogleBarCode();
                createQR(conexion);
                showQr();
            }
        //check code
            checkCode();
    }
//segudidad
    private String getKeyAutenticator(){
        String res = null;
        Conexion conect = new Conexion("http://localhost:3000/user/" + this.userId);
        JSONObject res2 = conect.sendGet();
        
        byte[] key = decriptKey(res2.getString("keyAuth"));

        Base32 base32 = new Base32();
        res = base32.encodeToString(key);
        
        return res;
    }
    private byte[] decriptKey(String data){
        byte[] res = null;

        try{
            res = new EncriptadorAES().decript(data, getHalfHash(), false);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException ex) {
            ex.printStackTrace();
            showDialog(-1, ex.toString());
        }
        
        return res;
    }
    private String getTOTPCode(String key){
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(key);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey); 
    }
    private String getGoogleBarCode(){
        String format = "otpauth://totp/%s?secret=%s";
        return String.format(format, name, keyAu);
    }
    private void createQR(String barCode){
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(barCode, BarcodeFormat.QR_CODE, 200, 200);
            FileOutputStream out = new FileOutputStream("/home/godo/Desktop/Dropbox/cs-p3/cs-app/icons/QR.png");
            MatrixToImageWriter.writeToStream(matrix, "png", out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void checkCode(){
        Runnable runnable = new Runnable() {
			@Override
			public void run() {
				while (!parar) {
					try {
                        code = getTOTPCode(keyAu);
						Thread.sleep(1000);
					} catch (InterruptedException e) {e.printStackTrace();}
				}
			}
		};
        Thread hilo = new Thread(runnable);
		hilo.start();
    }
//interfaz
    private void createFrame(){
        resetFrame();

        JPanel panel = new JPanel(), panelAux = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new java.awt.Color(33, 33, 33));

        panelAux.setBackground(new java.awt.Color(33, 33, 33));

        JLabel label = new JLabel("Introduzca epl código de 6 dígitos");
        label.setBorder(new EmptyBorder(10, 0, 10, 0));
        label.setBackground(new java.awt.Color(33, 33, 33));
        label.setForeground(Color.white);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
        
        final JTextField cod = new JTextField(6);
        cod.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(cod);

        JButton check = new JButton("Check code");
        ActionListener check_action = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if(code.equals(cod.getText())){
                    parar = true;
                    File f = new File("/home/godo/Desktop/Dropbox/cs-p3/cs-app/icons/QR.png");
                    f.delete();
                    new Interfaz_Choose(frame, userId, hash);
                }
                else
                    showDialog(2, "keyAu");
            }
        };
        check.addActionListener(check_action);
        check.setBorder(new EmptyBorder(5, 5, 5, 5));
        check.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelAux.add(check);
        panel.add(panelAux);

        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.setVisible(true);
    }
    private void resetFrame(){
        this.frame.getContentPane().removeAll();
        this.frame.invalidate();
        this.frame.validate();
        this.frame.repaint();
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.frame.setResizable(true);
        this.frame.setSize(400, 125);
        this.frame.setLocation(800, 450);
        this.frame.setResizable(false);
    }
    private void showDialog(int option, String key){
        String msg = "";
        switch(option){
            case 1:
                msg = "<html><p align=center>Le vamos a mostrar un codígo qr que deberá de escanear con google autenticator. <br>";
                msg += "Asegurese de que nadie pueda verla.";
                break;
            case 2:
                msg = "Lo sentimos el código no es correcto.";
        }
        JOptionPane.showMessageDialog(null, msg);
    }
//aux
    private byte[] getHalfHash(){
        byte[] hashByte = Base64.getDecoder().decode(hash);

        byte[] halfByte = new byte[hashByte.length/2];
        for(int i = 0; i < hashByte.length/2; i++)
            halfByte[i] = hashByte[i];
        return halfByte;
    }
    private void showQr(){
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ImageIcon icon = new ImageIcon("/home/godo/Desktop/Dropbox/cs-p3/cs-app/icons/QR.png");
                JOptionPane.showMessageDialog(
                        null,
                        new JLabel("", icon, JLabel.LEFT),
                        "QR", JOptionPane.INFORMATION_MESSAGE);

            }
        });
    }
}
