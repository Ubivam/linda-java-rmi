package rs.ac.bg.etf.js150411d.linda.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteObject extends Remote {

    public String[] getData() throws RemoteException;

    public void setData(String[] t) throws RemoteException;

}
