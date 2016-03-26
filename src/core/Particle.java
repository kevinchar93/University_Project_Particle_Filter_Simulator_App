package core;

import java.util.List;

import processing.core.PApplet;

public class Particle extends Robot {
	
	// information related to particles
	private double _weight = -1.0; 		// negative indicates weight not yet set
	private double _weightNormalised = -1.0;

	public Particle(PApplet parent, double worldSize, List<Landmark> landmarks) {
		super(parent, worldSize, landmarks);
	}
	
	public Particle(Robot robot) {
		super(robot);
	}
	
	public double measurementProb(List<Double> measurementVec) {
		
		double prob = 1.0;
		double dist = 0.0;
		double xPosSq = 0.0;
		double yPosSq = 0.0;

		// go through each land mark a determine probability of measurements
		// matching distance to landmarks
		for (int i = 0; i < _landmarks.size(); i++) {

			// get the current land mark from the landmark collection
			Landmark currLandmark = _landmarks.get(i);

			// calculate the distance to the landmark - using trig
			xPosSq = Math.pow(_xPos - currLandmark._xPos, 2);
			yPosSq = Math.pow(_yPos - currLandmark._yPos, 2);
			dist = Math.sqrt(xPosSq + yPosSq);

			// multiplying to get the joint probability of all the measurements
			prob *= Util.gaussianFormula(measurementVec.get(i), dist, _senseNoise);
		}

		return prob;
	}
	
	
	public double getWeight() {
		return _weight;
	}
	
	
	public void setWeight(double weight) {
		_weight = weight;
	}

	
	public double getNormalisedWeight() {
		return _weightNormalised;
	}

	
	public void setNormalisedWeight(double _weightNormalised) {
		this._weightNormalised = _weightNormalised;
	}

}
