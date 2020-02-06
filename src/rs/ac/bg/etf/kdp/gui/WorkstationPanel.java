package rs.ac.bg.etf.js150411d.linda.gui;

import rs.ac.bg.etf.js150411d.linda.ToupleSpace;
import rs.ac.bg.etf.js150411d.linda.server.LindaRMI;
import rs.ac.bg.etf.js150411d.linda.server.LindaWorkstation;
import rs.ac.bg.etf.js150411d.linda.util.Tuple;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.RemoteException;


public class WorkstationPanel extends JPanel {
    protected static final int WIDTH = 400;
    protected static final int HEIGHT = 400;

    private static WorkstationPanel wp;
    private static final String CONNECT_TO_A_SERVER = "CONNECT TO A SERVER";
    private static final String HOST_IP = "Ip adressa servera";
    private static final String PORT = "Port servera";
    private static final String CONSOLE = "Console";

    private JButton connectToAServer;
    private JLabel hostIpLabel;
    private JTextField hostIpTextField;
    private JLabel portLabel;
    private JTextField portTestField;
    private JLabel consoleLabel;
    private JTextArea console;
    private JFrame appFrame;

    public WorkstationPanel() {
        appFrame = new JFrame("Control Panel");
        appFrame.add("Center", this);
        appFrame.setSize(WIDTH, HEIGHT);

        wp = this;
        setLayout(new GridLayout(2, 1));
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        this.add(panel1);
        this.add(panel2);
        panel1.setLayout(new GridLayout(9, 1));
        panel2.setLayout(new BorderLayout());

        hostIpLabel = new JLabel(HOST_IP);

        hostIpTextField = new JTextField();

        portLabel = new JLabel(PORT);

        portTestField = new JTextField();

        connectToAServer = new JButton(CONNECT_TO_A_SERVER);

        connectToAServer.addActionListener(
                e -> {
                    guiLog("Connecting to a server...");
                    ToupleSpace.host = hostIpTextField.getText();
                    if (portTestField.getText() != null) {
                        ToupleSpace.port = Integer.parseInt(portTestField.getText());
                    }
                    ToupleSpace.createLindaWorkstation(wp);
                }
        );

        consoleLabel = new JLabel(CONSOLE);

        console = new JTextArea();

        panel1.add(hostIpLabel);
        panel1.add(hostIpTextField);
        panel1.add(portLabel);
        panel1.add(portTestField);
        panel1.add(connectToAServer);
        panel1.add(consoleLabel);
        panel2.add(console);
        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        appFrame.setVisible(true);
    }

    public static void main(String args[]) {
        new WorkstationPanel();
    }

    public void guiLog(String s) {
        StringWriter text = new StringWriter();
        PrintWriter out = new PrintWriter(text);
        out.println(console.getText());
        out.println(s);
        console.setText(text.toString());
    }

}