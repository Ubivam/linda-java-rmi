package rs.ac.bg.etf.kdp;

import rs.ac.bg.etf.kdp.gui.ControlPanel;
import rs.ac.bg.etf.kdp.gui.WorkstationPanel;
import rs.ac.bg.etf.kdp.server.LindaManager;
import rs.ac.bg.etf.kdp.server.LindaProcess;
import rs.ac.bg.etf.kdp.server.LindaRMIWorkstation;
import rs.ac.bg.etf.kdp.server.LindaWorkstation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;

public class ToupleSpace {

    static Linda linda;

    static LindaManager lindaMan;

    static LindaWorkstation lindaWork;

    public static String host = "localhost";

    public static int port = 4001;

    public static Linda getLinda() {

        if(linda == null) {
            linda = new LindaProcess();
        }
        return linda;
    }

    public static void setLinda(Linda l) {
        linda = l;
    }

    public static void createLindaWorkstation() throws RemoteException {
        lindaWork = new LindaWorkstation(host, port);
    }

    public static void createLindaWorkstation(WorkstationPanel wp) throws RemoteException {
         lindaWork = new LindaWorkstation(wp,host, port);
    }

    public static void createLindaManager() {
        var l = new LindaManager(host, port);
        lindaMan = l;
        linda = l;
    }
    public static void createLindaManager(ControlPanel cp) {
        var l = new LindaManager(cp,host, port);
        lindaMan = l;
        linda = l;
    }
    public static LindaManager getLindaManager() {
        return lindaMan;
    }

    public static void main (String argv[]) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, RemoteException {
        String[] construct = {};
        if(argv.length > 2) {
            construct = new String[argv.length - 2];
            for (int i = 2; i < argv.length; i++) {
                construct[i - 2] = argv[i];
            }
        }
        String className = argv[0];
        String methodName = argv[1];
        Object [] initargs = {};
        Object [] arguments = {};
 //       System.out.println(methodName);
        if(methodName.equals("main")){
            arguments =new Object[]{construct};
        }
        Class threadClass = Class.forName(className);
        Class[] parameterTypes = new Class[initargs.length];
        for (int i = 0; i < initargs.length; i++) {
            parameterTypes[i] = initargs[i].getClass();
        }
        Constructor[] constructors = threadClass.getConstructors();
        Constructor constructor = threadClass
                .getConstructor(parameterTypes);
        Object runningThread = constructor.newInstance(initargs);
        parameterTypes = new Class[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            parameterTypes[i] = arguments[i].getClass();
        }
        Method method = threadClass.getMethod(methodName,
                parameterTypes);
        method.invoke(runningThread, arguments);

        if(methodName.equals("main")){
            ((LindaProcess)getLinda()).notiffyJobDone();
        }

    }
}
