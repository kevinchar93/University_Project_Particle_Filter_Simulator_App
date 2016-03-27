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
	
	void draw() {
		int Yellow 	= _parent.color(255, 255, 0);
		int Red 	= _parent.color(255, 0, 0);
		int Green 	= _parent.color(0, 255, 0);
		int Blue 	= _parent.color(0, 0, 255);
		int White 	= _parent.color(255);
		int Black 	= _parent.color(0);
		
		// draw center ellipse
		_parent.strokeWeight(0);
		_parent.stroke(Black, 150);
		_parent.fill(Black, 20);
		_parent.ellipseMode(PApplet.CENTER);
		_parent.ellipse((float)_xPos * 10, (float)_yPos * 10, 10, 10);
		
		// draw heading pointer
		final float lineLen = 1.0f;
		_parent.strokeWeight(0);
		_parent.stroke(Black, 150);
		double endX = _xPos + (Math.cos(_heading) * lineLen);
		double endY = _yPos + (Math.sin(_heading) * lineLen);
		_parent.line((float)_xPos * 10, (float)_yPos * 10, (float)endX * 10, (float)endY * 10);
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
