package core;

public class Util {

	public static double mod(double numA, double numB) {
		return (numA % numB + numB) % numB;
	}

	/**
	 * Find the probability of a measurement x from a landmark sensor, given a gaussian 
	 * that defines the uncertainty of the sensor
	 * 
	 * @param _x
	 *            the measurement to find the probability for
	 * @param _mu
	 *            the mean (the actual measurement)
	 * @param _sigma
	 *            the standard deviation (the variance from the actual measurement)
	 * @return
	 */
	public static double gaussianFormula(double _x, double _mu, double _sigma) {

		double _1_div_sig_sqRoot_2pi = 1 / (_sigma * Math.sqrt(2 * Math.PI));
		double _x_minus_mu_sq = Math.pow((_x - _mu), 2);
		double _2_sigma_sq = 2 * Math.pow(_sigma, 2);
		double _neg_xMinusMuSq_div_2SigmaSq = -(_x_minus_mu_sq / _2_sigma_sq);

		double res = _1_div_sig_sqRoot_2pi * Math.exp(_neg_xMinusMuSq_div_2SigmaSq);

		return res;
	}

}
