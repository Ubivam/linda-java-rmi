package rs.ac.bg.etf.js150411d.linda.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientCallbackInterface extends Remote {

    void ping() throws RemoteException;

    void notifyChanges(String prefix) throws RemoteException;

}