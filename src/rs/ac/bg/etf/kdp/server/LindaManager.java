package rs.ac.bg.etf.kdp.server;

import rs.ac.bg.etf.kdp.Linda;
import rs.ac.bg.etf.kdp.gui.ControlPanel;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

public class LindaManager implements Linda,ClientCallbackInterface {

    private static ControlPanel cp;

    private LindaRMI linda;

    private int port;

    private String host;

    public LindaManager(ControlPanel cp , String host, int port) {
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
            ClientCallbackInterface client = this;
            UnicastRemoteObject.exportObject(client,0);
            Registry r = LocateRegistry.getRegistry(host, port);
            linda = (LindaRMI) r.lookup("/LindaServer");
            linda.registerManager(client, UUID.fromString("067e6162-3b6f-4ae2-a171-2470b63dff00"));
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
        try {
            linda.eval(name, thread);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eval(String className, Object[] construct, String methodName, Object[] arguments) {
        try {
            linda.eval(className, construct, methodName, arguments);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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

    }

    @Override
    public void notifyChanges(String prefix) throws RemoteException {
        if(cp != null){
            cp.guiLog(prefix);
        }else {
            System.out.println(prefix);
        }
    }

    @Override
    public void executeCommand(String pathToFile, String javaCommand) throws RemoteException {
        linda.invokeServerCommandOnWorker(pathToFile,javaCommand);
    }
}
