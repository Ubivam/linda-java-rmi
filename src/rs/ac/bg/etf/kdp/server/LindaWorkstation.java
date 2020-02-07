package rs.ac.bg.etf.kdp.server;

import rs.ac.bg.etf.kdp.Linda;
import rs.ac.bg.etf.kdp.ToupleSpace;
import rs.ac.bg.etf.kdp.gui.WorkstationPanel;
import rs.ac.bg.etf.kdp.util.SynchronousCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

public class LindaWorkstation implements Linda, ClientCallbackInterface {

    private static WorkstationPanel wp = null;

    private final UUID ID = UUID.randomUUID();

    private LindaRMI linda;

    private int port;

    private String host;

    public LindaWorkstation(WorkstationPanel wp, String host, int port) {
        this.wp = wp;
        this.host = host;
        this.port = port;
        bindToServer();
    }

    public LindaWorkstation(String host, int port) {
        this.host = host;
        this.port = port;
        bindToServer();
    }

    private void bindToServer() {
        try {
            ClientCallbackInterface client = this;
            UnicastRemoteObject.exportObject(client,0);
            Registry r = LocateRegistry.getRegistry(host, port);
            linda = (LindaRMI) r.lookup("/LindaServer");
            linda.registerWorker(client,ID);
            ToupleSpace.setLinda(this);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void out(String[] tuple) {
        {
            try {
                linda.out(tuple);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void in(String[] tuple) {
        {
            try {
                var data = linda.in(tuple);
                fill(tuple, data);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean inp(String[] tuple) {
        boolean ret = false;
        {
            try {
                ret = linda.inp(tuple);
                //   fill(tuple, remoteObj.getData());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    @Override
    public void rd(String[] tuple) {
        {
            try {
                var data = linda.rd(tuple);
                fill(tuple, data);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean rdp(String[] tuple) {
        boolean ret = false;
        {
            try {
                ret = linda.rdp(tuple);
                //fill(tuple, ret);
                return ret;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    @Override
    public void eval(String name, Runnable thread) {
         new SynchronousCallback().call(name,thread);
    }

    @Override
    public void eval(String className, Object[] construct, String methodName, Object[] arguments) {
         new SynchronousCallback().call(className, construct, methodName, arguments);
    }

    private void fill(String a[], String b[]) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == null) {
                a[i] = new String(b[i]);
            }
        }
    }

    public LindaRMI getLinda() {
        return linda;
    }

    @Override
    public void ping() throws RemoteException {
        linda.pingAnswer(ID);
        if(wp != null){
            wp.guiLog("Ping from the server");
        }else {
            System.out.println("Ping from the server");
        }
    }

    @Override
    public void notifyChanges(String prefix) throws RemoteException {

        if(wp != null){
            wp.guiLog(prefix);
        }else {
            System.out.println(prefix);
        }
    }

    @Override
    public void executeCommand(String pathToFile, String javaCommand) throws RemoteException {
        Process proc = null;
        try {

              proc = Runtime.getRuntime().exec(javaCommand+"\n",null,new File(pathToFile));
        //    proc = Runtime.getRuntime().exec("java -cp TestLinda.jar;CentralizedLinda.jar;Linda.jar rs.ac.bg.etf.kdp.Integral\n",null,new File("C:/Users/joker/Documents/ETF_Projects/KDP Linda Projekat/Test"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // Then retreive the process output
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));

        // Read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        String s = null;
        while (true) {
            try {
                if (!((s = stdInput.readLine()) != null)) break;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
           linda.returnResponseToManager(ID,s);
        }

        // Read any errors from the attempted command
        System.out.println("Here is the standard error of the command (if any):\n");
        while (true) {
            try {
                if (!((s = stdError.readLine()) != null)) break;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            linda.returnResponseToManager(ID,s);
        }
    }

}
