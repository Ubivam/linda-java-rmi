package rs.ac.bg.etf.js150411d.linda.sharedspace;


import rs.ac.bg.etf.js150411d.linda.Linda;
import rs.ac.bg.etf.js150411d.linda.util.Callback;
import rs.ac.bg.etf.js150411d.linda.util.SynchronousCallback;
import rs.ac.bg.etf.js150411d.linda.util.Tuple;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementacija linde preko deljene memorije
 */
public class CentralizedLinda implements Linda {

    private List<Tuple> tupleSpace = new CopyOnWriteArrayList<>();

    final ReentrantLock lock = new ReentrantLock(true);

    private List<Tuple> readConditions = new CopyOnWriteArrayList<>();
    private List<Tuple> takeConditions = new CopyOnWriteArrayList<>();

    public CentralizedLinda() {
        super();
    }

    /**
     * Ubacuje torku u prostor torki. Nije dozvoljeno da bilo slati bilo koji
     * string koji je null
     */
    @Override
    public void out(String[] tuple) { //write
        Tuple t = Tuple.valueOf(Tuple.arrayOfStringToString(tuple));

        lock.lock();
        {
            tupleSpace.add(0, t);

            /*
             * Ovde idemo kroz torke i proveravamo uslove za citanje ako se kljuc
             * poklapa signaliziramo da se sledeca nit odblokira
             */
            for (Tuple entry : readConditions) {
                Tuple key = (Tuple) entry.get(0);
                Condition value = (Condition) entry.get(1);
                if (t.matches(key)) {
                    entry.add(2, t.deep_clone());
                    value.signal();
                }
            }
            //Ekvivaletno gornjem bloku samo za take
            //Samo ovde nalazimo jedan match
            boolean match = false;
            Iterator it = takeConditions.iterator();
            while (it.hasNext() && !match) {
                Tuple entry = (Tuple) it.next();
                Tuple key = (Tuple) entry.get(0);
                Condition value = (Condition) entry.get(1);
                if (t.matches(key)) {
                    entry.add(2, t);
                    value.signal();
                    match = true;
                }
            } // !it.hasNext() || match
        }
        lock.unlock();
    }


    /**
     * Dohvatanje torke iz prostora torki. Ovo je blokirajuca operacija. Ukoliko
     * je neko od polja unutar ovog niza postavljeno na vrednost null onda to
     * polje takodje treba popuniti. Ukoliko ima vise torki uzima se bilo koja.
     * Nakon ove operacije torka se vise ne nalazi u prostoru torki.
     */
    @Override
    public void in(String[] tuple) { //take
        Tuple template = Tuple.valueOf(Tuple.arrayOfStringToString(tuple));
        Tuple t = null;
        boolean found = false;
        lock.lock();
        {
            Iterator<Tuple> it = tupleSpace.iterator();
            while (!found && it.hasNext()) {
                t = it.next();
                if (t.matches(template)) {
                    tupleSpace.remove(t);
                    found = true;
                }
            } // found || !it.hasNext()
            if(!found) {
                Condition cond = lock.newCondition();
                Tuple condEntry = new Tuple();
                condEntry.add(0, template);
                condEntry.add(1, (Serializable) cond);
                takeConditions.add(condEntry);
                while (!found) {
                    try {
                        cond.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (condEntry.get(2) != null) {
                        found = true;
                        t = (Tuple) condEntry.get(2);
                        tupleSpace.remove(t);
                        takeConditions.remove(condEntry);
                    }
                }
            }
        }
        fill(tuple, t.toStringArray());

        lock.unlock();
    }

    /**
     * Dohvatanje torke iz prostora torki. Ovo je neblokirajuca operacija.
     * Ukoliko je neko od polja unutar ovog niza postavljeno na vrednost null
     * onda to polje takodje treba popuniti. Ukoliko torka postoji onda se kao
     * rezultat vraca vrednost true, u suprotnom se vraca vrednost false.
     * Ukoliko ima vise torki uzima se bilo koja. Nakon ove operacije torka se
     * vise ne nalazi u prostoru torki.
     */
    @Override
    public boolean inp(String[] tuple) { //tryTake
        Tuple template = Tuple.valueOf(Tuple.arrayOfStringToString(tuple));
        Iterator<Tuple> it = tupleSpace.iterator();
        Tuple t = null;
        boolean found = false;
        while (!found && it.hasNext()) {
            t = it.next();
            if (t.matches(template)) {
                tupleSpace.remove(t);
                found = true;
                fill(tuple, t.toStringArray());
            }
        } // found || !it.hasNext()
        return found;
    }

    /**
     * Cita torku iz prostora torki. Ovo je blokirajuca operacija. Ukoliko je
     * neko od polja unutar ovog niza postavljeno na vrednost null onda to polje
     * takodje treba popuniti. Ukoliko ima vise torki uzima se bilo koja. Nakon
     * ove metode torka se i dalje nalazi u prostoru torki.
     */
    @Override
    public void rd(String[] tuple) { // read
        Tuple template = Tuple.valueOf(Tuple.arrayOfStringToString(tuple));
        Tuple t = null;
        boolean found = false;
        lock.lock();
        {
            Iterator<Tuple> it = tupleSpace.iterator();
            while (!found && it.hasNext()) {
                t = it.next();
                if (t.matches(template)) {
                    found = true;
                }
            } // found || !it.hasNext()
            if(!found) {
                Condition cond = lock.newCondition();
                Tuple condEntry = new Tuple();
                condEntry.add(0, template);
                condEntry.add(1, (Serializable) cond);
                readConditions.add(condEntry);
                while (!found) {
                    try {
                        cond.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (condEntry.get(2) != null) {
                        found = true;
                        t = (Tuple) condEntry.get(2);
                        readConditions.remove(condEntry);
                    }
                }
            }
        }
        var temp = t.deep_clone();
        fill(tuple, temp.toStringArray());
        lock.unlock();
    }

    /**
     * Cita torku iz prostora torki. Ovo je neblokirajuca operacija. Ukoliko je
     * neko od polja unutar ovog niza postavljeno na vrednost null onda to polje
     * takodje treba popuniti. Ukoliko torka postoji onda se kao rezultat vraca
     * vrednost true, u suprotnom se vraca vrednost false. Ukoliko ima vise
     * torki uzima se bilo koja. Nakon ove operacije torka se i dalje nalazi u
     * prostoru torki.
     */

    @Override
    public boolean rdp(String[] tuple) { //tryRead
        Tuple template = Tuple.valueOf(Tuple.arrayOfStringToString(tuple));
        Iterator<Tuple> it = tupleSpace.iterator();
        Tuple t = null;
        boolean found = false;
        while (!found && it.hasNext()) {
            t = it.next();
            if (t.matches(template)) {
                found = true;
                fill(tuple, t.toStringArray());
            }
        } //found || !it.hasNext()

        return found;
    }

    /**
     * Pokretanje nove niti na datom racunaru.
     */
    @Override
    public void eval(String name, Runnable thread) {
        Thread t = new Thread(thread, name);
        t.start();
    }

    /**
     * Pokretanje nove niti na datom racunaru. Pokrece se izvrsavanje metode
     * zadate parametrom methodName koja prima argimente arguments. Izvrsavanje
     * se pokrece na instanci klase zadate nazivom klase className i argumentima
     * konstruktora initargs.
     */
    @Override
    public void eval(final String className, final Object[] initargs,
                     final String methodName, final Object[] arguments) {
        Thread t = new Thread(() -> {

            try {
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

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
    }


    private void fill(String a[], String b[]) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == null) {
                a[i] = new String(b[i]);
            }
        }
    }

    public void debug(String prefix) {
        System.out.println("###################");

        System.out.println("# " + prefix);
        System.out.println("## TUPLES SPACE (" + tupleSpace.size() + ")");
        for (Tuple t : tupleSpace) {
            System.out.println("# " + t);
        }

        System.out.println("###################");
    }
}
