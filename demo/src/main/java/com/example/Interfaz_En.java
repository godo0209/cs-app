package com.example;

//Archivos
    import java.io.File;
//Interfaz  grafica
    import javax.swing.JFileChooser;
    import javax.swing.JFrame;
    import javax.swing.JOptionPane;
    import javax.swing.UIManager;
    import javax.swing.filechooser.FileNameExtensionFilter;


public class Interfaz_En {
    private File fichero;
    private String path;

    public Interfaz_En(String userId, String hash, JFrame frame){
        main(userId, hash, frame);
    }
    
    public void main(String userId, String hash, JFrame frame) {
        showDialog(1, "");
        chooseFile(frame, userId, hash);    //recogemos el fichero
        if(fichero != null){
            showDialog(2, "");
            new LgNeg_En(fichero, path, userId, hash, frame);
        }
    }
    private void chooseFile(JFrame frame, String id, String hash){
        JFileChooser fc = new JFileChooser();   //creamso objeto fc

        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);    //solo archivos

        //filtros
        FileNameExtensionFilter imgFilter = new FileNameExtensionFilter("Images, videos and text", "jpg", "gif", "png", "txt", "mp4", "avi"); 
        fc.setFileFilter(imgFilter);

        fc.setApproveButtonText("Seleccionar"); //añadimos boton seleccionar

        int result = fc.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            fichero = fc.getSelectedFile();
            path = fc.getCurrentDirectory().toString();
            UIManager.put("Filechooser.text", "File.name");
        }
        else{
            showDialog(4, "");
            //new Interfaz_Choose(frame, id, hash);
        }
    }
    private void showDialog(int option, String error){
        String msg = "Ha ocurrido el siguiente error en el proceso: " + error;

        switch(option){
            case 1:
                msg = "Por favor seleccione el fichero que desea guardar";
                break;
            case 2:
                msg = "El fichero ha sido seleccionado correctamente";
                break;
            case 3:
                msg = "Por favor seleccione la carpeta donde quiere guardar la clave del cifrado";
                break;
            case 4:
                msg = "El fichero no ha sido seleccionado correctamente";
                break;
        }
        JOptionPane.showMessageDialog(null, msg);
    }
}