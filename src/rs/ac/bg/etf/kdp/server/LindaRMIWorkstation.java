package rs.ac.bg.etf.kdp.server;

import rs.ac.bg.etf.kdp.beans.NonBlockReturn;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface LindaRMIWorkstation extends Remote {

    public void out(String [] tuple) throws RemoteException;

    public String[] in(String [] tuple)throws RemoteException;

    public NonBlockReturn inp(String [] tuple)throws RemoteException;


    public String[] rd(String [] tuple)throws RemoteException;

    public NonBlockReturn rdp(String [] tuple)throws RemoteException;

    public void eval(String name, Runnable thread) throws RemoteException;

    public void eval(String className, Object[] construct, String methodName,
                     Object[] arguments) throws RemoteException;

    public void registerProcess(RemoteCallback cb) throws RemoteException;

    public void notifyJobDone() throws RemoteException;

    public void debug (String prefix) throws RemoteException;
}
