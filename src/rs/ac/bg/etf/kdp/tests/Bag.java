package rs.ac.bg.etf.js150411d.linda.tests;

import rs.ac.bg.etf.js150411d.linda.Linda;
import rs.ac.bg.etf.js150411d.linda.ToupleSpace;

public class Bag implements Runnable {
	Linda linda;
	double xmin;
	double xmax;
	int n;

	public Bag() {
		this.linda = ToupleSpace.getLinda();
		String[] parameters = { "bagParameters", null, null, null };
		linda.in(parameters);
		this.xmin = Double.parseDouble(parameters[1]);
		this.xmax = Double.parseDouble(parameters[2]);
		this.n = Integer.parseInt(parameters[3]);
	}

	public Bag(int i) {
	}

	public void run() {
		double dx, x;
		int i;
		x = xmin;
		dx = calcDX(xmin, xmax, n);

		for (i = 0; i < n; i++) {
			String[] getTask = { "getTask", null };
			linda.in(getTask);

			String[] request = { "request", "" + getTask[1], "" + x,
					"" + (x + dx) };
			linda.out(request);
			x = x + dx;
		}
		String[] numNode = { "numNode", null };
		linda.in(numNode);
		int num = Integer.parseInt(numNode[1]);
		for (i = 0; i < num; i++) {
			String[] getTask = { "getTask", null };
			linda.in(getTask);

			String[] request = { "request", "" + getTask[1], "0", "-1" };
			linda.out(request);
		}
	}

	private double calcDX(double min, double max, int N) {
		return (max - min) / N;
	}
}
