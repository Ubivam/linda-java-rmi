package rs.ac.bg.etf.js150411d.linda;

import rs.ac.bg.etf.js150411d.linda.gui.ControlPanel;
import rs.ac.bg.etf.js150411d.linda.gui.WorkstationPanel;
import rs.ac.bg.etf.js150411d.linda.server.LindaManager;
import rs.ac.bg.etf.js150411d.linda.server.LindaWorkstation;
import rs.ac.bg.etf.js150411d.linda.sharedspace.CentralizedLinda;

public class ToupleSpace {

    static Linda linda;

    public static String host = "localhost";

    public static int port = 4000;

    public static Linda getLinda() {
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
        linda = new LindaManager(host, port);
    }
    public static void createLindaManager(ControlPanel cp) {
        linda = new LindaManager(cp,host, port);
    }

    static {
        //Local Linda
        linda = new CentralizedLinda();
        //   linda = new LocalLinda();
    }
}
