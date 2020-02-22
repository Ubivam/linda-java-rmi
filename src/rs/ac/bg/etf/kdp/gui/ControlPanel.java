package rs.ac.bg.etf.kdp.gui;

import rs.ac.bg.etf.kdp.ToupleSpace;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.UUID;


public class ControlPanel extends JPanel {
    protected static final int WIDTH = 900;
    protected static final int HEIGHT = 700;

    private static final String START_JOB_LABEL = "START A JOB";
    private static final String CANCEL_JOB_LABEL = "CANCEL A CURRENTLY ACTIVE JOB";
    private static final String CONNECT_TO_A_SERVER = "CONNECT TO A SERVER";
    private static final String ENTRY_FUNCTION = "Pocetna funkcija";
    private static final String MAIN_CLASS_NAME = "Ime klase u kojoj je pocetna funkcija: ";
    private static final String HOST_IP = "Ip adressa servera: ";
    private static final String PATH_TO_JAR_JOB = "Putanja do JAR fajla posla: ";
    private static final String PATH_TO_JAR_LIB = "Putanja do JAR fajla biblioteke: ";
    private static final String PORT = "Port servera: ";
    private static final String CONSOLE = "Console";

    private JButton startJobButton;
    private JButton cancelAJobButton;
    private JButton connectToAServer;
    private JLabel pathJobLabel;
    private JTextField pathJobField;
    private JLabel pathLibLabel;
    private JTextField pathLibField;
    private JLabel functionLabel;
    private JTextField functionField;
    private JLabel mainClassLabel;
    private JTextField mainClassField;
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
        panel1.setLayout(new GridLayout(16, 1));
        panel2.setLayout(new BorderLayout());

        pathJobLabel = new JLabel(PATH_TO_JAR_JOB);

        pathJobField = new JTextField();

        pathLibLabel = new JLabel(PATH_TO_JAR_LIB);

        pathLibField = new JTextField();

        functionLabel = new JLabel(ENTRY_FUNCTION);

        functionField = new JTextField();

        mainClassLabel = new JLabel(MAIN_CLASS_NAME);

        mainClassField = new JTextField();

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
                        ToupleSpace.getLindaManager().executeCommand(mainClassField.getText(),c, functionField.getText(),m);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                }
        );

        cancelAJobButton = new JButton(CANCEL_JOB_LABEL);

        cancelAJobButton.addActionListener(
                e -> {
                    UUID id = getUUID();
                    try {
                        ToupleSpace.getLindaManager().cancelCurrentJob(id);
                    } catch (Exception ex) {
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
                        startJobButton.setEnabled(true);
                        cancelAJobButton.setEnabled(true);
                        connectToAServer.setEnabled(false);
                    }).start();
                }
        );

        consoleLabel = new JLabel(CONSOLE);

        console = new JTextArea();
        JScrollPane scroll = new JScrollPane (console,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        panel1.add(pathJobLabel);
        panel1.add(pathJobField);
        panel1.add(pathLibLabel);
        panel1.add(pathLibField);
        panel1.add(functionLabel);
        panel1.add(functionField);
        panel1.add(mainClassLabel);
        panel1.add(mainClassField);
        panel1.add(hostIpLabel);
        panel1.add(hostIpTextField);
        panel1.add(portLabel);
        panel1.add(portTestField);
        panel1.add(connectToAServer);
        panel1.add(startJobButton);
        panel1.add(cancelAJobButton);
        panel1.add(consoleLabel);
        panel2.add(scroll);
        startJobButton.setEnabled(false);
        cancelAJobButton.setEnabled(false);
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

    public static UUID getUUID() {
        ArrayList<String> output = null;
        try {
            var proc = Runtime.getRuntime().exec("wmic csproduct get UUID");
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));
            String s = null;
            output = new ArrayList<>();
            while (true) {
                try {
                    if (!((s = stdInput.readLine()) != null)) break;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                output.add(s);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        String uid = output.get(2).substring(0,36);
        UUID id = UUID.fromString(uid);
        return id;
    }

}
