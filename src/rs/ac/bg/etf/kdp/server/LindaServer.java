package rs.ac.bg.etf.kdp.server;

import rs.ac.bg.etf.kdp.Linda;
import rs.ac.bg.etf.kdp.gui.ServerPanel;
import rs.ac.bg.etf.kdp.sharedspace.CentralizedLinda;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;


public class LindaServer extends UnicastRemoteObject implements LindaRMI {

    private static final long serialVersionUID = 1L;

    private static ArrayList<UUID> registeredWorkersID;

    private static HashMap<UUID, ClientCallbackInterface> workersCallback;

    private static ArrayList<UUID> activeWorkers;

    private static UUID registeredManagerID = UUID.fromString("067e6162-3b6f-4ae2-a171-2470b63dff00");

    private static ClientCallbackInterface managerCallback;

    private static ServerPanel sp = null;

    public static int port = 4000;

    private Linda linda;

    public LindaServer(ServerPanel sp) throws RemoteException {
        registeredManagerID = UUID.fromString("067e6162-3b6f-4ae2-a171-2470b63dff00");
        this.sp = sp;
        linda = new CentralizedLinda();
    }

    public LindaServer() throws RemoteException {
        linda = new CentralizedLinda();
    }

    public static void main(String[] args) {
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
    public void out(String[] tuple) throws RemoteException {
        this.linda.out(tuple);
    }

    @Override
    public String[] in(String[] tuple) throws RemoteException {
        //var temp = tuple.getData();
        this.linda.in(tuple);
        return tuple;
    }

    @Override
    public boolean inp(String[] tuple) throws RemoteException {
        return this.linda.inp(tuple);
    }

    @Override
    public String[] rd(String[] tuple) throws RemoteException {
        this.linda.rd(tuple);
        return tuple;
    }

    @Override
    public boolean rdp(String[] tuple) throws RemoteException {
        return this.linda.rdp(tuple);
    }

    @Override
    public void eval(String name, Runnable thread) throws RemoteException {
        this.linda.eval(name, thread);
    }

    @Override
    public void eval(String className, Object[] construct, String methodName, Object[] arguments) throws RemoteException {
        this.linda.eval(className, construct, methodName, arguments);
    }

    @Override
    public void debug(String prefix) throws RemoteException {
        ((CentralizedLinda) this.linda).debug(prefix);
    }

    @Override
    public void registerManager(ClientCallbackInterface cbi, UUID id) throws RemoteException {

        if (!registeredManagerID.equals(id)) {
            cbi.notifyChanges("Not Connected, wrong UUID");
        } else {
            managerCallback = cbi;
            cbi.notifyChanges("Connected!");
            log("Manager registered with id: " + id);
        }
    }

    @Override
    public void registerWorker(ClientCallbackInterface cbi, UUID id) throws RemoteException {
        if(registeredWorkersID == null) {
            registeredWorkersID = new ArrayList<>();
            activeWorkers = new ArrayList<>();
        }
        if(workersCallback == null) {
            workersCallback = new HashMap<>();
        }
        registeredWorkersID.add(id);
        activeWorkers.add(id);
        workersCallback.put(id, cbi);
        cbi.notifyChanges("Connected");
        log("Worker registered with id: " + id);
    }

    @Override
    public void pingAnswer(UUID id) throws RemoteException {
        activeWorkers.add(id);
    }

    @Override
    public void invokeServerAnswer() throws RemoteException {
        if (registeredWorkersID != null && workersCallback != null) {
            try {
                Iterator<UUID> it = registeredWorkersID.iterator();
                while (it.hasNext()){
                    var id = it.next();
                    if(!activeWorkers.contains(id)) {
                        managerCallback.notifyChanges("Worker with ID: " + id +" stopped working");
                        registeredWorkersID.remove(id);

                    }
                    activeWorkers.remove(id);
                    ClientCallbackInterface cbi = workersCallback.get(id);
                    cbi.ping();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void invokeServerCommandOnWorker(String filePath, String javaCommand) throws RemoteException {
        workersCallback.get(activeWorkers.get(0)).executeCommand(filePath, javaCommand);
    }

    @Override
    public void returnResponseToManager(UUID id, String response) throws RemoteException {
        managerCallback.notifyChanges("Worker with ID: " + id +"\nReturned result: " + response);
    }

    @Override
    public ClientCallbackInterface getAvailableWorkStation() throws RemoteException {
       return workersCallback.get(activeWorkers.get(0));
    }

    public void log(String prefix) throws RemoteException {
        if (sp != null) {
            sp.guiLog(prefix);
        } else {
            System.out.println(prefix);
        }
    }
}
