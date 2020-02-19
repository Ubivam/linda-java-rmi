package rs.ac.bg.etf.kdp.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientCallback extends Remote {

    void ping() throws RemoteException;

    void notifyChanges(String prefix) throws RemoteException;

    void executeCommand(String className, Object[] construct, String methodName,
                        Object[] arguments) throws RemoteException;

    void callbackExecute(String className, Object[] construct, String methodName,
                         Object[] arguments) throws RemoteException;
}