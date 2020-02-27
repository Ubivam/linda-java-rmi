package rs.ac.bg.etf.kdp.server;

import rs.ac.bg.etf.kdp.beans.NonBlockReturn;
import rs.ac.bg.etf.kdp.gui.WorkstationPanel;
import rs.ac.bg.etf.kdp.util.SynchronousCallback;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class LindaWorkstation implements LindaRMIWorkstation, ClientCallback {

    private static WorkstationPanel wp = null;

    private final UUID ID = UUID.randomUUID();

    ReentrantLock lock;
    private CopyOnWriteArrayList<Process> activeProcesses;

    private LindaRMIServer linda;

    private RemoteCallback cb;

    private Registry r;

    private int localPort = 4028;

    private int port;

    private String host;

    public LindaWorkstation(WorkstationPanel wp, String host, int port) throws RemoteException {
        this.wp = wp;
        this.host = host;
        this.port = port;
        bindToServer();
        createLocalServer();
        activeProcesses = new CopyOnWriteArrayList<>();
        lock = new ReentrantLock();
    }

    public LindaWorkstation(String host, int port) throws RemoteException {
        this.host = host;
        this.port = port;
        bindToServer();
        createLocalServer();
        activeProcesses = new CopyOnWriteArrayList<>();
        lock = new ReentrantLock();
    }

    private void bindToServer() {
        try {
            ClientCallback client = this;
            UnicastRemoteObject.exportObject(client, 0);
            Registry reg = LocateRegistry.getRegistry(host, port);
            linda = (LindaRMIServer) reg.lookup("/LindaServer");
            linda.registerWorker(client, ID);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    private void rebindToServer() {
        try{
            Registry reg = LocateRegistry.getRegistry(host, port);
            linda = (LindaRMIServer) reg.lookup("/LindaServer");
        }catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    private void createLocalServer() throws RemoteException {
        r = LocateRegistry.createRegistry(localPort);
        r.rebind("/LindaWorkstation", this);
    }

    @Override
    public void out(String[] tuple) throws RemoteException {
        if(linda != null) {
            try {
                linda.out(tuple);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String[] in(String[] tuple) throws RemoteException {
        if (linda == null) return null;
        String[] ret = null;
        try {
            ret = linda.in(tuple);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public NonBlockReturn inp(String[] tuple) throws RemoteException {
        if(linda == null) return null;
        try {
            var nonBlockReturn = linda.inp(tuple);
            return nonBlockReturn;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String[] rd(String[] tuple) throws RemoteException {
        if(linda == null) return null;
        String[] ret = null;
        try {
            ret = linda.rd(tuple);
            //fill(tuple, data);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return ret;
    }

    @Override
    public NonBlockReturn rdp(String[] tuple) throws RemoteException {
        if(linda == null) return null;
        try {
            var nonBlockReturn = linda.rdp(tuple);
            return nonBlockReturn;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void eval(String name, Runnable thread) {
        new SynchronousCallback().call(name, thread);
    }

    @Override
    public void eval(String className, Object[] construct, String methodName, Object[] arguments) throws RemoteException {
        if(linda != null) {
            linda.eval(ID, className, construct, methodName, arguments);
        }
    }

    @Override
    public void registerProcess(RemoteCallback cb) throws RemoteException {
        this.cb = cb;
    }

    @Override
    public void notifyJobDone() throws RemoteException {
        if(linda!=null) {
            linda.notifyJobDone();
        }
    }

    @Override
    public void downloadFile(byte[] fileData, String fileName) throws RemoteException {
        File file = new File("C:/Users/joker/Desktop/"+fileName);
        try {
            OutputStream os = new FileOutputStream(file,false);
            os.write(fileData);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void restartWorkstation() throws RemoteException {
        linda = null;
        UnicastRemoteObject.unexportObject(r,false);
        Iterator<Process> it = activeProcesses.iterator();
        while (it.hasNext()){
            Process proc = it.next();
            proc.destroyForcibly();
            activeProcesses.remove(proc);
        }
        StringBuilder cmd = new StringBuilder();
        cmd.append(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java ");
        for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            cmd.append(jvmArg + " ");
        }
        cmd.append("-cp ").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append(" ");
        cmd.append(WorkstationPanel.class.getName()).append(" ");
        try {
            Runtime.getRuntime().exec(cmd.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread( () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }).start();
    }

    @Override
    public void removeProcesses() throws RemoteException {
        lock.lock();
        linda = null;
        UnicastRemoteObject.unexportObject(r,false);
        Iterator<Process> it = activeProcesses.iterator();
        while (it.hasNext()){
            Process proc = it.next();
            proc.destroyForcibly();
            activeProcesses.remove(proc);
        }
        rebindToServer();
        createLocalServer();
        lock.unlock();
    }

    @Override
    public void debug(String prefix) throws RemoteException {

    }

    private void fill(String a[], String b[]) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == null) {
                a[i] = new String(b[i]);
            }
        }
    }

    public LindaRMIServer getLinda() {
        return linda;
    }

    @Override
    public void ping() throws RemoteException {
        linda.pingAnswer(ID);
        if (wp != null) {
            wp.guiLog("Ping from the server");
        } else {
            System.out.println("Ping from the server");
        }
    }

    @Override
    public void notifyChanges(String prefix) throws RemoteException {

        if (wp != null) {
            wp.guiLog(prefix);
        } else {
            System.out.println(prefix);
        }
    }

    @Override
    public void executeCommand(String className, Object[] construct, String methodName,
                               Object[] arguments) {
        Thread t = new Thread(() -> {
            if (wp != null) {
                wp.guiLog("Current job: "+className + " " + methodName);
            } else {
                System.out.println("Current job: "+className + " " + methodName);
            }
            Process proc = null;
            try {

//                proc = Runtime.getRuntime().exec("java -cp TestLinda.jar;CentralizedLinda.jar;Linda.jar rs.ac.bg.etf.kdp.ToupleSpace "
//                                + className + " " + methodName + "\n",
//                        null,
//                        new File("C:/Users/joker/Documents/ETF_Projects/KDP Linda Projekat/Test"));
                proc = Runtime.getRuntime().exec("java -cp MainJob.jar;CentralizedLinda.jar rs.ac.bg.etf.kdp.ToupleSpace "
                                + className + " " + methodName + "\n",
                        null,
                        new File("C:/Users/joker/Desktop"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            lock.lock();
            activeProcesses.add(proc);
            lock.unlock();
            // Then retreive the process output
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));

            // Read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            String s = null;
            while (true) {
                try {
                    if (!((s = stdInput.readLine()) != null)) break;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try {
                    linda.returnResponseToManager(ID, s);
                    Thread.sleep (20);
                } catch (RemoteException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while (true) {
                try {
                    if (!((s = stdError.readLine()) != null)) break;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try {
                    linda.returnResponseToManager(ID, s);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            lock.lock();
            activeProcesses.remove(proc);
            lock.unlock();
        });
        t.start();
    }

    @Override
    public void callbackExecute(String className, Object[] construct, String methodName, Object[] arguments) throws RemoteException {
        cb.call(className, construct, methodName, arguments);
    }

}
