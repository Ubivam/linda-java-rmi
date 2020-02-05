package rs.ac.bg.etf.js150411d.linda.server;

import rs.ac.bg.etf.js150411d.linda.Linda;
import rs.ac.bg.etf.js150411d.linda.ToupleSpace;
import rs.ac.bg.etf.js150411d.linda.sharedspace.CentralizedLinda;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class LindaServer extends UnicastRemoteObject implements LindaRMI {

    private static final long serialVersionUID = 1L;

    public static int port = 4000;

    private Linda linda;

    protected LindaServer() throws RemoteException {
        super();
        linda = new CentralizedLinda();
        ToupleSpace.setLinda(linda);
    }

    public static void main (String[] args) {
        try {
            LindaServer lindaServer = new LindaServer();
            Registry r = LocateRegistry.createRegistry(port);
            r.rebind("/LindaServer", lindaServer);

            LindaServer.setLog(System.out);
            System.out.println("Linda server started...");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void out(String [] tuple) throws RemoteException {
        this.linda.out(tuple);
    }

    @Override
    public String[] in(String [] tuple) throws RemoteException {
        //var temp = tuple.getData();
        this.linda.in(tuple);
        return tuple;
    }

    @Override
    public boolean inp(String [] tuple) throws RemoteException {
        return this.linda.inp(tuple);
    }

    @Override
    public String[] rd(String [] tuple) throws RemoteException {
        this.linda.rd(tuple);
        return tuple;
    }

    @Override
    public boolean rdp(String [] tuple) throws RemoteException {
        return this.linda.rdp(tuple);
    }

    @Override
    public void eval(String name, Runnable thread) throws RemoteException {
        this.linda.eval(name,thread);
    }

    @Override
    public void eval(String className, Object[] construct, String methodName, Object[] arguments) throws RemoteException{
        this.linda.eval(className,construct,methodName,arguments);
    }

    @Override
    public void debug(String prefix) throws RemoteException {
        ((CentralizedLinda) this.linda).debug(prefix);
    }
}
