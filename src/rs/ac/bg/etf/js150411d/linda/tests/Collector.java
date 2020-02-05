package rs.ac.bg.etf.js150411d.linda.tests;

import rs.ac.bg.etf.js150411d.linda.Linda;
import rs.ac.bg.etf.js150411d.linda.ToupleSpace;

public class Collector implements Runnable {
	Linda linda;
	int n;

	public Collector() {
		this.linda = ToupleSpace.getLinda();
		String[] parameters = { "collectorParameters", null };
		linda.in(parameters);
		this.n = Integer.parseInt(parameters[1]);
	}

	public void run() {
		double integral, data;
		integral = 0;
		for (int i = 0; i < n; i++) {
			String[] responce = { "responce", null };
			linda.in(responce);
			data = Double.parseDouble(responce[1]);
			integral = integral + data;
		}
		String[] result = { "result", "" + integral };
		linda.out(result);

	}

}
