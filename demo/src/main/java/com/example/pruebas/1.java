// even if you are working with just swings.
import javax.swing.*;
import java.awt.*;
class gui {
    public static void main(String args[]) {

        //Creating the Frame
        JFrame frame = new JFrame("");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 100);

        //Creating the panel at bottom and adding components
        JPanel panel = new JPanel(); // the panel is not visible in output
        JLabel label = new JLabel("Enter the file id you want to have back:");
        JTextField tf = new JTextField(20); // accepts upto 10 characters
        JButton request = new JButton("Request");
        panel.add(label); // Components Added using Flow Layout
        panel.add(tf);
        panel.add(request);

        // Text Area at the Center
        JTextArea ta = new JTextArea();

        //Adding Components to the frame.
        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.setVisible(true);
    }
}