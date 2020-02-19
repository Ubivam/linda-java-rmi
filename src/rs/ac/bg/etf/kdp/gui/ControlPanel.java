package rs.ac.bg.etf.kdp.gui;

import rs.ac.bg.etf.kdp.ToupleSpace;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.rmi.RemoteException;


public class ControlPanel extends JPanel {
    protected static final int WIDTH = 900;
    protected static final int HEIGHT = 700;

    private static final String START_JOB_LABEL = "START A JOB";
    private static final String CONNECT_TO_A_SERVER = "CONNECT TO A SERVER";
    private static final String ENTRY_FUNCTION = "Pocetna funkcija";
    private static final String MAIN_CLASS_NAME = "Ime klase u kojoj je pocetna funkcija";
    private static final String HOST_IP = "Ip adressa servera";
    private static final String PORT = "Port servera";
    private static final String CONSOLE = "Console";

    private JButton startJobButton;
    private JButton connectToAServer;
    private JLabel commandLabel;
    private JTextField commandTextField;
    private JLabel pathLabel;
    private JTextField pathTextField;
    private JLabel hostIpLabel;
    private JTextField hostIpTextField;
    private JLabel portLabel;
    private JTextField portTestField;
    private JLabel consoleLabel;
    private JTextArea console;
    private JFrame appFrame;

    public ControlPanel() {
        appFrame = new JFrame("Control Panel");
        appFrame.add("Center", this);
        appFrame.setSize(WIDTH,HEIGHT);

        setLayout(new GridLayout(2,1));
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        this.add(panel1);
        this.add(panel2);
        panel1.setLayout(new GridLayout(11, 1));
        panel2.setLayout(new BorderLayout());

        commandLabel = new JLabel(ENTRY_FUNCTION);

        commandTextField = new JTextField();

        pathLabel = new JLabel(MAIN_CLASS_NAME);

        pathTextField = new JTextField();

        hostIpLabel = new JLabel(HOST_IP);

        hostIpTextField = new JTextField();

        portLabel = new JLabel(PORT);

        portTestField = new JTextField();

        startJobButton = new JButton(START_JOB_LABEL);

        startJobButton.addActionListener(
                e -> {
                    try {
                        Object[] c = {};
                        Object[] m = {};
                        ToupleSpace.getLindaManager().executeCommand(pathTextField.getText(),c, commandTextField.getText(),m);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                }
        );

        connectToAServer = new JButton(CONNECT_TO_A_SERVER);

        connectToAServer.addActionListener(
                e -> {
                    new Thread(() -> {
                        guiLog("Connecting to a server...");
                        ToupleSpace.host = hostIpTextField.getText();
                        if(portTestField.getText() != null) {
                            ToupleSpace.port = Integer.parseInt(portTestField.getText());
                        }
                        ToupleSpace.createLindaManager(this);
                    }).start();
                }
        );

        consoleLabel = new JLabel(CONSOLE);

        console = new JTextArea();
        JScrollPane scroll = new JScrollPane (console,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        panel1.add(commandLabel);
        panel1.add(commandTextField);
        panel1.add(pathLabel);
        panel1.add(pathTextField);
        panel1.add(hostIpLabel);
        panel1.add(hostIpTextField);
        panel1.add(portLabel);
        panel1.add(portTestField);
        panel1.add(connectToAServer);
        panel1.add(startJobButton);
        panel1.add(consoleLabel);
        panel2.add(scroll);
        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        appFrame.setVisible(true);
    }

    public static void main (String args[]) {
        new ControlPanel();
    }
    public void guiLog (String s) {
        StringWriter text = new StringWriter();
        PrintWriter out = new PrintWriter(text);
        out.println(console.getText());
        out.println(s);
        console.setText(text.toString());
    }

}
