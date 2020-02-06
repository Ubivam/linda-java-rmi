package rs.ac.bg.etf.js150411d.linda.gui;

import rs.ac.bg.etf.js150411d.linda.ToupleSpace;
import rs.ac.bg.etf.js150411d.linda.server.LindaServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.stream.Stream;


public class ServerPanel extends JPanel {
    protected static final int WIDTH = 400;
    protected static final int HEIGHT = 400;

    private static final String START_SERVER = "START A SERVER";
    private static final String CONSOLE = "Console";

    private JButton startJobButton;
    private JLabel consoleLabel;
    private JTextArea console;
    private JFrame appFrame;
    private boolean serverActive = false;

    private LindaServer lindaServer = null;
    private Registry r = null;

    public ServerPanel() {
        appFrame = new JFrame("Control Panel");
        appFrame.add("Center", this);
        appFrame.setSize(WIDTH,HEIGHT);

        setLayout(new GridLayout(2,1));
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        this.add(panel1);
        this.add(panel2);
        panel1.setLayout(new GridLayout(2, 1));
        panel2.setLayout(new BorderLayout());

        startJobButton = new JButton(START_SERVER);

        startJobButton.addActionListener(
                e -> {
                    try {
                        if(lindaServer == null) {
                            lindaServer = new LindaServer(this);
                        }
                        if (!serverActive) {
                            r = LocateRegistry.createRegistry(LindaServer.port);
                            r.rebind("/LindaServer", lindaServer);
                            LindaServer.setLog(System.out);
                            guiLog("Linda server started...");
                            serverActive = true;
                        }
                        else {
                        //    r.unbind("/LindaServer");
                            UnicastRemoteObject.unexportObject(r,false);
                            lindaServer = null;
                            r = null;
                            guiLog("Linda server stopped...");
                            serverActive = false;
                        }

                    } catch (RemoteException  exception) {
                        exception.printStackTrace();
                    }
                }
        );

        consoleLabel = new JLabel(CONSOLE);

        console = new JTextArea();

        panel1.add(startJobButton);
        panel1.add(consoleLabel);
        panel2.add(console);
        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        appFrame.setVisible(true);
    }

    public static void main (String args[]) {
        new ServerPanel();
    }

    public void guiLog (String s) {
        StringWriter text = new StringWriter();
        PrintWriter out = new PrintWriter(text);
        out.println(console.getText());
        out.println(s);
        console.setText(text.toString());
    }

}