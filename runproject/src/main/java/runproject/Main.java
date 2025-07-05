package runproject;

//interfaz
    import java.awt.BorderLayout;
    import java.awt.Color;
    import java.awt.Desktop;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    
    import javax.swing.JButton;
    import javax.swing.JFrame;
    import javax.swing.JLabel;
    import javax.swing.JPanel;
    import javax.swing.JOptionPane;
    import javax.swing.SwingWorker;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class Main {

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

        JLabel label = new JLabel("Click button to run project");
        label.setForeground(Color.white);
        panel.add(label);

        JButton start = new JButton("Start Project");
        ActionListener startAction = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (!hasDockerCompose()) {
                    askInstall(frame);
                }
                else{
                    startCompose(frame);
                }
            }
        };
        panel1.add(start);
        start.addActionListener(startAction);

        panel.add(panel1);

        panel1.setBackground(new java.awt.Color(33, 33, 33));
        panel.setBackground(new java.awt.Color(33, 33, 33));

        frame.getContentPane().add(BorderLayout.CENTER, panel);
        
        frame.setVisible(true); 
    }
// ---------- helpers ----------

    public static int runCommand(String... cmd) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.inheritIO();
        Process p = pb.start();
        return p.waitFor();
    }

    public static boolean hasDockerCompose() {
        String os = System.getProperty("os.name").toLowerCase();
        String[] checkCmd = os.contains("win")
                ? new String[]{"cmd.exe", "/c", "docker compose version"}
                : new String[]{"bash", "-c", "docker compose version"};
        try {
            return runCommand(checkCmd) == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    public static boolean askInstall(JFrame parent) {
        int ans = JOptionPane.showConfirmDialog(
                parent,
                "Docker Compose is not installed.\n" +
                "Would you like to open the installation page?",
                "Install Docker Compose?",
                JOptionPane.YES_NO_OPTION);
        if (ans == JOptionPane.YES_OPTION) {
            try {
                Desktop.getDesktop().browse(
                        new URI("https://docs.docker.com/compose/install/"));
            } catch (Exception ignored) {}
        }
        return ans == JOptionPane.YES_OPTION;
    }

    public static void startCompose(JFrame parent) {
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                // System.out.println("Starting Docker Compose...");

                String os = System.getProperty("os.name").toLowerCase();
                String[] runCmd = os.contains("win")
                        ? new String[]{"cmd.exe", "/c", "docker compose up --build -d"}
                        : new String[]{"bash", "-c", "docker compose up --build -d"};
                int exit = runCommand(runCmd);
                JOptionPane.showMessageDialog(parent,
                        exit == 0 ? "Docker Compose finished." :
                                    "Docker Compose exited with code " + exit);
                return null;
            }
            @Override
            protected void done() {
                // System.out.println("Starting the client app...");

                try {
                    // Auto-detect path to the current JAR file
                    File currentJar = new File(Main.class.getProtectionDomain()
                        .getCodeSource().getLocation().toURI());

                        
                    File clientJar = new File(currentJar.getParent() + "/demo/cs-app.jar");
                        
                    // System.out.println("Current JAR path: " + currentJar.getAbsolutePath() + 
                    //  " | Client jar: " + clientJar.getAbsolutePath());

                    // Step 1: open your JAR app
                    Desktop.getDesktop().open(clientJar);

                    // Step 2: close the current window
                    frame.dispose(); // closes the runproject window

                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(parent,
                            "Failed to start the client app: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
};