package rs.ac.bg.etf.kdp.server;

import rs.ac.bg.etf.kdp.beans.NonBlockReturn;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface LindaRMIServer extends Remote {

    public void out(String [] tuple) throws RemoteException;

    public String[] in(String [] tuple)throws RemoteException;

    public NonBlockReturn inp(String [] tuple)throws RemoteException;


    public String[] rd(String [] tuple)throws RemoteException;

    public NonBlockReturn rdp(String [] tuple)throws RemoteException;

    public void eval(UUID id, String name, Runnable thread) throws RemoteException;

    public void eval(UUID id, String className, Object[] construct, String methodName,
                     Object[] arguments) throws RemoteException;

    public void registerManager(ClientCallback cbi, UUID id) throws RemoteException;

    public void registerWorker(ClientCallback cbi, UUID id) throws RemoteException;

    public void pingAnswer(UUID id) throws RemoteException;

    public void invokeServerAnswer() throws RemoteException;

    public void serverRefresh() throws RemoteException;

    public void invokeServerCommandOnWorker(String className, Object[] construct, String methodName,
                                            Object[] arguments) throws  RemoteException;

    public void returnResponseToManager(UUID id, String response) throws RemoteException;

    public ClientCallback getAvailableWorkStation() throws RemoteException;

    public void debug (String prefix) throws RemoteException;
}
