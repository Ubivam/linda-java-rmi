package rs.ac.bg.etf.kdp;

import java.lang.reflect.*;
import java.util.*;

public class LocalLinda implements Linda {
	ArrayList<String[]> tupleSpace;

	public LocalLinda() {
		if (tupleSpace == null) {
			tupleSpace = new ArrayList<String[]>();
		}
	}

	public void eval(String name, Runnable thread) {
		Thread t = new Thread(thread, name);
		t.start();
	}

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

	public synchronized void in(String[] tuple) {
		boolean found = false;
		while (!found) {
			for (String[] data : tupleSpace) {
				if (equals(tuple, data)) {
					fill(tuple, data);
					tupleSpace.remove(data);
					return;
				}
			}
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized boolean inp(String[] tuple) {
		for (String[] data : tupleSpace) {
			if (equals(tuple, data)) {
				fill(tuple, data);
				return true;
			}
		}
		return false;
	}

	public synchronized void out(String[] tuple) {
		tupleSpace.add(tuple);
		notifyAll();
	}

	public synchronized void rd(String[] tuple) {
		boolean found = false;
		while (!found) {
			for (String[] data : tupleSpace) {
				if (equals(tuple, data)) {
					fill(tuple, data);
					return;
				}
			}
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public synchronized boolean rdp(String[] tuple) {
		for (String[] data : tupleSpace) {
			if (equals(tuple, data)) {
				fill(tuple, data);
				return true;
			}
		}
		return false;
	}

	private boolean equals(String[] a, String[] b) {
		if ((a == null) || (b == null) || (a.length != b.length)) {
			return false;
		}
		boolean match = true;
		for (int i = 0; i < a.length; i++) {
			if (a[i] != null) {
				match = match && a[i].equals(b[i]);
			}
		}
		return match;
	}

	private void fill(String a[], String b[]) {
		for (int i = 0; i < a.length; i++) {
			if (a[i] == null) {
				a[i] = new String(b[i]);
			}
		}
	}

}
