package rs.ac.bg.etf.kdp;

import rs.ac.bg.etf.kdp.gui.ControlPanel;
import rs.ac.bg.etf.kdp.gui.WorkstationPanel;
import rs.ac.bg.etf.kdp.server.LindaManager;
import rs.ac.bg.etf.kdp.server.LindaWorkstation;

public class ToupleSpace {

    static Linda linda;

    static LindaManager lindaMan;

    public static String host = "localhost";

    public static int port = 4000;

    public static Linda getLinda() {

        if(linda == null) {
            linda = new LindaWorkstation(host,port);
        }
        return linda;
    }

    public static void setLinda(Linda l) {
        linda = l;
    }

    public static void createLindaWorkstation() {
        linda = new LindaWorkstation(host, port);
    }

    public static void createLindaWorkstation(WorkstationPanel wp) {
         linda = new LindaWorkstation(wp,host, port);
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
}
