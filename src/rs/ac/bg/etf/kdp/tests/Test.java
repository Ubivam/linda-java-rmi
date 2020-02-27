package rs.ac.bg.etf.kdp.tests;

import rs.ac.bg.etf.kdp.gui.WorkstationPanel;
import rs.ac.bg.etf.kdp.util.Tuple;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

public class Test {
    public static void main(String[] args) {
        Tuple t1 = new Tuple(Integer.class,"popo",true,78 , Boolean.class);

        Tuple t2 = Tuple.valueOf("[ ?Integer \"popo\" true 78 ?Boolean ]");

        String [] arr = {"?Integer", "\"popo\"", "true", "78", "?Boolean"};
        Tuple t3 = Tuple.valueOf(Tuple.arrayOfStringToString(arr));
        t1 = Tuple.valueOf(t1.toString());

        boolean isSame = t1.matches(t3);

        System.out.println(isSame);

        System.out.println(t1.toString());

        System.out.println(t2.toString());

        System.out.println(t3.toString());

    }

}
