package rs.ac.bg.etf.kdp.tests;

import rs.ac.bg.etf.kdp.Linda;
import rs.ac.bg.etf.kdp.ToupleSpace;

public class Worker implements Runnable {
	private static final double PRECISION = 0.000001;
	Linda linda;
	int id;

	public Worker() {
		this.linda = ToupleSpace.getLinda();
		String[] workerParameters = { "workerParameters", null };
		linda.in(workerParameters);
		this.id = Integer.parseInt(workerParameters[1]);
		String[] workerOutParameters = { "workerParameters", "" + (id + 1) };
		linda.out(workerOutParameters);
	}

	public void run() {
		boolean end = false;
		while (!end) {

			String[] getTask = { "getTask", "" + id };
			linda.out(getTask);

			String[] request = { "request", "" + id, null, null };
			linda.in(request);

			double left = Double.parseDouble(request[2]);
			double right = Double.parseDouble(request[3]);
			if (left > right) {
				end = true;
				break;
			}
			double data = calcIntegral(left, right);

			String[] responce = { "responce", "" + data };
			linda.out(responce);
		}
	}

	private double calcIntegral(double left, double right) {
		double data = 0;
		if (left < right) {
			if ((right - left) < PRECISION) {
				data = (function(left) + function(right)) / 2;
				data = data * (right - left);
			} else {
				double midle = (left + right) / 2;
				data = calcIntegral(left, midle) + calcIntegral(midle, right);
			}
		}
		return data;
	}

	private double function(double x) {
		if (x > PRECISION) {
			return Math.exp(x) / x;
		} else
			return 1;
	}
}
