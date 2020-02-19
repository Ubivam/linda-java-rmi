package rs.ac.bg.etf.kdp.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteCallback  extends Remote {

    void call (String className, Object[] construct, String methodName,
               Object[] arguments) throws RemoteException;

    void call (String name, Runnable thread) throws RemoteException;
}
