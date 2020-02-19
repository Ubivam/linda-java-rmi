package rs.ac.bg.etf.kdp.server;

import rs.ac.bg.etf.kdp.Linda;
import rs.ac.bg.etf.kdp.ToupleSpace;
import rs.ac.bg.etf.kdp.gui.WorkstationPanel;
import rs.ac.bg.etf.kdp.util.SynchronousCallback;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class LindaProcess implements Linda, RemoteCallback {

    private static WorkstationPanel wp = null;

    private LindaRMIWorkstation linda;

    public LindaProcess() {
        try {
            Registry r = LocateRegistry.getRegistry("localhost", 4001);
            linda = (LindaRMIWorkstation) r.lookup("/LindaWorkstation");
            linda.registerProcess(this);

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void out(String[] tuple) {
        try {
            linda.out(tuple);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void in(String[] tuple) {

        try {
            var data = linda.in(tuple);
            fill(tuple, data);
        } catch (RemoteException e) {
            e.printStackTrace();
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
        try {
            var data = linda.rd(tuple);
            fill(tuple, data);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean rdp(String[] tuple) {
        boolean ret = false;
        try {
            var nonBlockReturn = linda.rdp(tuple);
            fill(tuple, nonBlockReturn.getTuple());
            ret = nonBlockReturn.isPassed();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public void eval(String name, Runnable thread) {
        new SynchronousCallback().call(name, thread);
    }

    @Override
    public void eval(String className, Object[] construct, String methodName, Object[] arguments) {
        try {
           // new SynchronousCallback().call(className, construct, methodName, arguments);
            linda.eval(className,construct,methodName,arguments);
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

    @Override
    public void call(String className, Object[] construct, String methodName, Object[] arguments) throws RemoteException {
        new SynchronousCallback().call(className, construct, methodName, arguments);
    }

    @Override
    public void call(String name, Runnable thread) throws RemoteException {
        new SynchronousCallback().call(name, thread);
    }
}
