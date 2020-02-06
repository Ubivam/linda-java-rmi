package rs.ac.bg.etf.js150411d.linda.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class SynchronousCallback implements Callback {

    private Callback cb;

    public SynchronousCallback() {};
    public SynchronousCallback(Callback cb){
        this.cb = cb;
    }

    @Override
    public void call(String className, Object[] initargs, String methodName, Object[] arguments) {
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

    @Override
    public void call(String name, Runnable thread) {
        Thread t = new Thread(thread, name);
        t.start();
    }
}
