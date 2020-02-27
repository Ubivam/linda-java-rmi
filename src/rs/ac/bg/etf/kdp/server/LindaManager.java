package rs.ac.bg.etf.kdp.server;

import rs.ac.bg.etf.kdp.Linda;
import rs.ac.bg.etf.kdp.gui.ControlPanel;
import rs.ac.bg.etf.kdp.util.SynchronousCallback;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

public class LindaManager implements Linda, ClientCallback {

    private static ControlPanel cp;

    private LindaRMIServer linda;

    private int port;

    private String host;

    public LindaManager(ControlPanel cp, String host, int port) {
        this.cp = cp;
        this.host = host;
        this.port = port;
        bindToServer();
    }

    public LindaManager(String host, int port) {
        this.host = host;
        this.port = port;
        bindToServer();
    }

    public void bindToServer() {
        try {
            ClientCallback client = this;
            UnicastRemoteObject.exportObject(client, 0);
            Registry r = LocateRegistry.getRegistry(host, port);
            linda = (LindaRMIServer) r.lookup("/LindaServer");
            linda.registerManager(client, ControlPanel.getUUID());
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
                var nonBlockReturn = linda.inp(tuple);
                fill(tuple, nonBlockReturn.getTuple());
                ret = nonBlockReturn.isPassed();
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
                var nonBlockReturn = linda.rdp(tuple);
                fill(tuple, nonBlockReturn.getTuple());
                ret = nonBlockReturn.isPassed();
                return ret;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    @Override
    public void eval(String name, Runnable thread) {
        new SynchronousCallback().call(name, thread);
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

    public LindaRMIServer getLinda() {
        return linda;
    }

    @Override
    public void ping() throws RemoteException {

    }

    @Override
    public void notifyChanges(String prefix) throws RemoteException {
        if (cp != null) {
            cp.guiLog(prefix);
        } else {
            System.out.println(prefix);
        }
    }

    @Override
    public void executeCommand(String className, Object[] construct, String methodName,
                               Object[] arguments) throws RemoteException {
        linda.invokeServerCommandOnWorker(ControlPanel.getUUID(), className, construct, methodName, arguments);
    }

    @Override
    public void callbackExecute(String className, Object[] construct, String methodName, Object[] arguments) throws RemoteException {
        new SynchronousCallback().call(className, construct, methodName, arguments);
    }

    @Override
    public void restartWorkstation() throws RemoteException {
        linda.restartWorkstations();
    }

    @Override
    public void removeProcesses() throws RemoteException {

    }

    @Override
    public void downloadFile(byte[] fileData, String fileName) throws RemoteException {

    }

    public void sendAFileToServer(byte[] fileData, String fileName) throws RemoteException{
        linda.downloadFile(fileData, fileName);
    }
    public void cancelCurrentJob(UUID id) throws RemoteException {
        linda.cancelCurrentJob(id);
    }
}
