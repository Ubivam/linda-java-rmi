package rs.ac.bg.etf.js150411d.linda;

import rs.ac.bg.etf.js150411d.linda.server.LindaClient;
import rs.ac.bg.etf.js150411d.linda.sharedspace.CentralizedLinda;

public class ToupleSpace {

    static Linda linda;

    public static String host = "91.187.150.65";

    public static int port = 4000;

    public static Linda getLinda() {
        return linda;
    }

    public static void setLinda(Linda l) {
        linda = l;
    }

    public static void createLindaClient() {
        linda = new LindaClient(host, port);
    }

    static {
        //Local Linda
        linda = new CentralizedLinda();
     //   linda = new LocalLinda();
    }
}
