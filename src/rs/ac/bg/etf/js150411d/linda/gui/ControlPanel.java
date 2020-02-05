package rs.ac.bg.etf.js150411d.linda.gui;

import rs.ac.bg.etf.js150411d.linda.ToupleSpace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ControlPanel extends JPanel {
    protected static final int WIDTH = 400;
    protected static final int HEIGHT = 400;

    private static final String START_JOB_LABEL = "START A JOB";
    private static final String PATH_TO_JOB = "Putanja do posla";
    private static final String HOST_IP = "Ip adressa servera";
    private static final String PORT = "Port servera";
    private static final String CONSOLE = "Console";

    private JButton startJobButton;
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
        panel1.setLayout(new GridLayout(8, 1));
        panel2.setLayout(new BorderLayout());
        pathLabel = new JLabel(PATH_TO_JOB);

        pathTextField = new JTextField();

        hostIpLabel = new JLabel(HOST_IP);

        hostIpTextField = new JTextField();

        portLabel = new JLabel(PORT);

        portTestField = new JTextField();

        startJobButton = new JButton(START_JOB_LABEL);

        startJobButton.addActionListener(
                e -> {
                    ToupleSpace.host = hostIpTextField.getText();
                    ToupleSpace.port = Integer.parseInt(hostIpTextField.getText());
                    ToupleSpace.createLindaClient();
                }
        );

        consoleLabel = new JLabel(CONSOLE);

        console = new JTextArea();

        panel1.add(pathLabel);
        panel1.add(pathTextField);
        panel1.add(hostIpLabel);
        panel1.add(hostIpTextField);
        panel1.add(portLabel);
        panel1.add(portTestField);
        panel1.add(startJobButton);
        panel1.add(consoleLabel);
        panel2.add(console);
        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        appFrame.setVisible(true);
    }

    public static void main (String args[]) {
        new ControlPanel();
    }

}
