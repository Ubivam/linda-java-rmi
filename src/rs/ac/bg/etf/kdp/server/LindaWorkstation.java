package rs.ac.bg.etf.js150411d.linda.server;

import rs.ac.bg.etf.js150411d.linda.Linda;
import rs.ac.bg.etf.js150411d.linda.gui.WorkstationPanel;
import rs.ac.bg.etf.js150411d.linda.util.Callback;
import rs.ac.bg.etf.js150411d.linda.util.SynchronousCallback;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteStub;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public class LindaWorkstation implements Linda, ClientCallbackInterface {

    private static WorkstationPanel wp = null;

    private LindaRMI linda;

    private int port;

    private String host;

    public LindaWorkstation(WorkstationPanel wp, String host, int port) {
        this.wp = wp;
        new LindaWorkstation(host,port);
    }

    public LindaWorkstation(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            ClientCallbackInterface client = this;
            UnicastRemoteObject.exportObject(client,0);
            Registry r = LocateRegistry.getRegistry(host, port);
            linda = (LindaRMI) r.lookup("/LindaServer");
            linda.registerWorker(client,UUID.randomUUID());
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

    }

    @Override
    public void notifyChanges(String prefix) throws RemoteException {

        if(wp != null){
            wp.guiLog(prefix);
        }else {
            System.out.println(prefix);
        }
    }

}
