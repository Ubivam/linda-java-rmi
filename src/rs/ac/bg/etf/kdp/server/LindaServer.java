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

    private static ArrayList<JobContainer> jobQueue;

    private static ArrayList<JobContainer> jobHistory;

    private static JobContainer activeJob;

    private static int pointerToWorker = 0;

    private static ArrayList<UUID> registeredManagers;

    private static HashMap<UUID, ClientCallback> managersCallback;

    private static ClientCallback managerCallback;

    private static ServerPanel sp = null;

    public static int port = 4000;

    private Linda linda;

    public LindaServer(ServerPanel sp) throws RemoteException {
        this.sp = sp;
        linda = new CentralizedLinda();
        jobHistory = new ArrayList<>();
    }

    public LindaServer() throws RemoteException {
        linda = new CentralizedLinda();
        jobHistory = new ArrayList<>();
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
    }

    @Override
    public void debug(String prefix) throws RemoteException {
        ((CentralizedLinda) this.linda).debug(prefix);
    }

    @Override
    public void registerManager(ClientCallback cbi, UUID id) throws RemoteException {
        if(registeredManagers == null) {
            registeredManagers = new ArrayList<>();
        }
        if(managerCallback == null){
            managersCallback = new HashMap<>();
        }
            registeredManagers.add(id);
            managerCallback = cbi;
            managersCallback.put(id,cbi);
            cbi.notifyChanges("Connected!");
            log("Manager registered with id: " + id);
    }

    @Override
    public void registerWorker(ClientCallback cbi, UUID id) throws RemoteException {
        if (registeredWorkersID == null) {
            registeredWorkersID = new ArrayList<>();
            onlineWorkers = new ArrayList<>();
        }
        if (workersCallback == null) {
            workersCallback = new HashMap<>();
        }
        registeredWorkersID.add(id);
        onlineWorkers.add(id);
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
                        if(activeJob != null) {
                            managerCallback.notifyChanges("Worker with ID: " + id + " stopped working");
                        }
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
    public void notifyJobDone() throws RemoteException {
        if (sp != null) {
            sp.guiLog("Job Done");
        } else {
            System.out.println("Job Done");
        }
        if(jobQueue == null){
            jobQueue = new ArrayList<>();
        }
        activeJob.setJobState(JobContainer.State.DONE);
        activeJob = null;
        if(jobQueue.size() > 0) {
            var job = jobQueue.remove(0);
            if(sp != null) {
                sp.updateGraphicsToEmpty(jobQueue.size() + 1);
                if(activeJob == null){
                    sp.updateGraphicsToEmpty(0);
                }
            }
            invokeServerCommandOnWorker(job.getClassName(), job.getConstruct(),job.getMethodName(),job.getArguments());
        }
        if(activeJob == null){
            sp.updateGraphicsToEmpty(0);
        }
    }


    @Override
    public void invokeServerCommandOnWorker(String className, Object[] construct, String methodName,
                                            Object[] arguments) throws RemoteException {
        if(activeJob == null) {
            activeJob = new JobContainer(className,construct,methodName,arguments);
            activeJob.setJobState(JobContainer.State.RUNNING);
            jobHistory.add(activeJob);
            if(sp != null){
                sp.updateGraphicsToExecuting(0);
            }
            workersCallback.get(onlineWorkers.get(pointerToWorker)).executeCommand(className, construct, methodName, arguments);
            pointerToWorker = (pointerToWorker + 1) % onlineWorkers.size();
        }else {
            if(jobQueue == null) {
                jobQueue = new ArrayList<>();
            }
            var job = new JobContainer(className,construct,methodName,arguments);
            jobQueue.add(job);
            jobHistory.add(job);
            if(sp != null){
                sp.updateGraphicToReady(jobQueue.size());
            }
        }
    }

    @Override
    public void returnResponseToManager(UUID id, String response) throws RemoteException {
        if(managerCallback != null) {
            managerCallback.notifyChanges("Worker with ID: " + id + "\nReturned result: " + response);
        }
    }

    @Override
    public ClientCallback getAvailableWorkStation() throws RemoteException {
        return workersCallback.get(onlineWorkers.get(0));
    }

    @Override
    public void cancelCurrentJob(UUID id) throws RemoteException {

    }

    public void log(String prefix) throws RemoteException {
        if (sp != null) {
            sp.guiLog(prefix);
        } else {
            System.out.println(prefix);
        }
    }
}
