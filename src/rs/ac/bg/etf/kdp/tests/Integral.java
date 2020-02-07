package rs.ac.bg.etf.kdp.tests;

import rs.ac.bg.etf.kdp.Linda;
import rs.ac.bg.etf.kdp.ToupleSpace;

public class Integral{
	public static void main(String args[]) {
		try {

			// String className = args[0];
			// Linda linda = (Linda) Class.forName(className).newInstance();
			// ToupleSpace.setLinda(linda);

			Linda linda = ToupleSpace.getLinda();
			int num = 10;
			int n = 100;
			double xmin = 0;
			double xmax = 50;

			String[] bagParameters = { "bagParameters", "" + xmin, "" + xmax,
					"" + n };
			linda.out(bagParameters);
			String[] numNode = { "numNode", "" + num };
			linda.out(numNode);
			Object[] construct = {};
			Object[] arguments = {};
			linda.eval("rs.ac.bg.etf.kdp.tests.Bag", construct, "run", arguments);

			String[] collectorParameters = { "collectorParameters", "" + n };
			linda.out(collectorParameters);
			linda.eval("rs.ac.bg.etf.kdp.tests.Collector", construct, "run",
					arguments);

			String[] workerParameters = { "workerParameters", "0" };
			linda.out(workerParameters);
			for (int i = 0; i < num; i++) {
				linda.eval("rs.ac.bg.etf.kdp.tests.Worker", construct, "run",
						arguments);
			}
			String[] result = { "result", null };
			linda.in(result);
			double integral = Double.parseDouble(result[1]);
			System.out.println("[" + xmin + ", " + xmax + ", " + n + "] = "
					+ integral);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
