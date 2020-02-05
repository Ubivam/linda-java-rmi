package rs.ac.bg.etf.js150411d.linda.server;

import rs.ac.bg.etf.js150411d.linda.Linda;
import rs.ac.bg.etf.js150411d.linda.util.Callback;
import rs.ac.bg.etf.js150411d.linda.util.SynchronousCallback;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteStub;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.locks.ReentrantLock;

public class LindaClient implements Linda {

    private LindaRMI linda;

    final ReentrantLock lock = new ReentrantLock(true);
    private int port;

    private String host;

    public LindaClient(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            Registry r = LocateRegistry.getRegistry(host, port);
            linda = (LindaRMI) r.lookup("/LindaServer");
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void out(String[] tuple) {
        lock.lock();
        {
            try {
                linda.out(tuple);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        lock.unlock();
    }

    @Override
    public void in(String[] tuple) {
        lock.lock();
        {
            try {
                var data = linda.in(tuple);
                fill(tuple, data);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        lock.unlock();
    }

    @Override
    public boolean inp(String[] tuple) {
        boolean ret = false;
        lock.lock();
        {
            try {
                ret = linda.inp(tuple);
                //   fill(tuple, remoteObj.getData());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        lock.unlock();
        return ret;
    }

    @Override
    public void rd(String[] tuple) {
        lock.lock();
        {
            try {
                var data = linda.rd(tuple);
                fill(tuple, data);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        lock.unlock();
    }

    @Override
    public boolean rdp(String[] tuple) {
        boolean ret = false;
        lock.lock();
        {
            try {
                ret = linda.rdp(tuple);
                //fill(tuple, ret);
                return ret;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        lock.unlock();
        return ret;
    }

    @Override
    public void eval(String name, Runnable thread) {
        // new SynchronousCallback().call(name,thread);
        try {
            linda.eval(name, thread);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eval(String className, Object[] construct, String methodName, Object[] arguments) {
        // new SynchronousCallback().call(className, construct, methodName, arguments);
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
}
