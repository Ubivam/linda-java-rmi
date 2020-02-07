package rs.ac.bg.etf.kdp.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface LindaRMI extends Remote {

    public void out(String [] tuple) throws RemoteException;

    public String[] in(String [] tuple)throws RemoteException;

    public boolean inp(String [] tuple)throws RemoteException;


    public String[] rd(String [] tuple)throws RemoteException;

    public boolean rdp(String [] tuple)throws RemoteException;

    public void eval(String name, Runnable thread) throws RemoteException;

    public void eval(String className, Object[] construct, String methodName,
                     Object[] arguments) throws RemoteException;

    public void registerManager(ClientCallbackInterface cbi, UUID id) throws RemoteException;

    public void registerWorker(ClientCallbackInterface cbi, UUID id) throws RemoteException;

    public void pingAnswer(UUID id) throws RemoteException;

    public void invokeServerAnswer() throws RemoteException;

    public void invokeServerCommandOnWorker(String filePath,String javaCommand) throws  RemoteException;

    public void returnResponseToManager(UUID id, String response) throws RemoteException;

    public ClientCallbackInterface getAvailableWorkStation() throws RemoteException;

    public void debug (String prefix) throws RemoteException;
}
