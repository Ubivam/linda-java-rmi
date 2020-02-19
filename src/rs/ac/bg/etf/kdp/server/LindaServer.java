package rs.ac.bg.etf.kdp.server;

import rs.ac.bg.etf.kdp.Linda;
import rs.ac.bg.etf.kdp.beans.JobContainer;
import rs.ac.bg.etf.kdp.beans.NonBlockReturn;
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


public class LindaServer extends UnicastRemoteObject implements LindaRMIServer {

    private static final long serialVersionUID = 1L;

    private static ArrayList<UUID> registeredWorkersID;

    private static HashMap<UUID, ClientCallback> workersCallback;

    private static ArrayList<UUID> onlineWorkers;

    private static ArrayList<UUID> freeWorkers;

    private static ArrayList<JobContainer> jobQueue;

    private static int pointerToWorker = 0;

    private static UUID registeredManagerID = UUID.fromString("067e6162-3b6f-4ae2-a171-2470b63dff00");

    private static ClientCallback managerCallback;

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
    public NonBlockReturn inp(String[] tuple) throws RemoteException {
        var passed = this.linda.inp(tuple);
        return new NonBlockReturn(passed, tuple);
    }

    @Override
    public String[] rd(String[] tuple) throws RemoteException {
        this.linda.rd(tuple);
        return tuple;
    }

    @Override
    public NonBlockReturn rdp(String[] tuple) throws RemoteException {
        var passed = this.linda.rdp(tuple);
        return new NonBlockReturn(passed,tuple);
    }

    @Override
    public void eval(UUID id, String name, Runnable thread) throws RemoteException {
        this.linda.eval(name, thread);
    }

    @Override
    public void eval(UUID id ,String className, Object[] construct, String methodName, Object[] arguments) throws RemoteException {
        workersCallback.get(onlineWorkers.get(pointerToWorker)).executeCommand(className, construct, methodName, arguments);
        pointerToWorker = (pointerToWorker + 1) % onlineWorkers.size();
//        if(freeWorkers.size() > 0){
//            workersCallback.get(freeWorkers.remove(0)).executeCommand(className,construct, methodName,arguments);
//        } else {
//            workersCallback.get(id).callbackExecute(className, construct, methodName, arguments);
//        }
    }

    @Override
    public void debug(String prefix) throws RemoteException {
        ((CentralizedLinda) this.linda).debug(prefix);
    }

    @Override
    public void registerManager(ClientCallback cbi, UUID id) throws RemoteException {

        if (!registeredManagerID.equals(id)) {
            cbi.notifyChanges("Not Connected, wrong UUID");
        } else {
            managerCallback = cbi;
            cbi.notifyChanges("Connected!");
            log("Manager registered with id: " + id);
        }
    }

    @Override
    public void registerWorker(ClientCallback cbi, UUID id) throws RemoteException {
        if (registeredWorkersID == null) {
            registeredWorkersID = new ArrayList<>();
            onlineWorkers = new ArrayList<>();
            freeWorkers = new ArrayList<>();
        }
        if (workersCallback == null) {
            workersCallback = new HashMap<>();
        }
        registeredWorkersID.add(id);
        onlineWorkers.add(id);
        freeWorkers.add(id);
        workersCallback.put(id, cbi);
        cbi.notifyChanges("Connected");
        log("Worker registered with id: " + id);
    }

    @Override
    public void pingAnswer(UUID id) throws RemoteException {
        onlineWorkers.add(id);
    }

    @Override
    public void invokeServerAnswer() throws RemoteException {
        if (registeredWorkersID != null && workersCallback != null) {
            try {
                Iterator<UUID> it = registeredWorkersID.iterator();
                while (it.hasNext()) {
                    var id = it.next();
                    if (!onlineWorkers.contains(id)) {
                        managerCallback.notifyChanges("Worker with ID: " + id + " stopped working");
                        registeredWorkersID.remove(id);
                    }
                    onlineWorkers.remove(id);
                    ClientCallback cbi = workersCallback.get(id);
                    cbi.ping();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void serverRefresh() throws RemoteException {
        if(jobQueue != null){
            Iterator<JobContainer> it = jobQueue.iterator();
            while (it.hasNext()) {
                var job = it.next();
                if(freeWorkers.size() > 0){
                    jobQueue.remove(job);
                    workersCallback.get(freeWorkers.remove(0)).executeCommand(job.getClassName(),job.getConstruct()
                            , job.getMethodName(),job.getArguments());
                }
            }
        }
    }

    @Override
    public void invokeServerCommandOnWorker(String className, Object[] construct, String methodName,
                                            Object[] arguments) throws RemoteException {
        if(freeWorkers.size() > 0) {
            workersCallback.get(freeWorkers.remove(0)).executeCommand(className, construct, methodName, arguments);
        } else {
            if(jobQueue == null) {
                jobQueue = new ArrayList<>();
            }
            jobQueue.add(new JobContainer(className,construct,methodName,arguments));
        }
    }

    @Override
    public void returnResponseToManager(UUID id, String response) throws RemoteException {
        if(managerCallback != null) {
            managerCallback.notifyChanges("Worker with ID: " + id + "\nReturned result: " + response);
            freeWorkers.add(id);
        }
    }

    @Override
    public ClientCallback getAvailableWorkStation() throws RemoteException {
        return workersCallback.get(onlineWorkers.get(0));
    }

    public void log(String prefix) throws RemoteException {
        if (sp != null) {
            sp.guiLog(prefix);
        } else {
            System.out.println(prefix);
        }
    }
}
