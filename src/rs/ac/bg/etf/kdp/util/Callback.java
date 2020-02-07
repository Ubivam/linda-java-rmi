package rs.ac.bg.etf.kdp.util;

public interface Callback {
    /**
     * Poziv kada se torka pojavi, u eventu detaljnije objasnjeno
     * @param arguments
     * @param className
     * @param construct
     * @param methodName
     */

    void call (String className, Object[] construct, String methodName,
               Object[] arguments);

    void call (String name, Runnable thread);
}
