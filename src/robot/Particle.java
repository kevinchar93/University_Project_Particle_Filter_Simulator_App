package robot;

import java.util.List;
import java.util.NavigableMap;
import java.util.Map.Entry;

import map.Landmark;
import processing.core.PApplet;
import util.UtilMath;

public class Particle extends Robot {
	
	// information related to particles
	private double _weight = -1.0; 		// negative indicates weight not yet set
	private double _weightNormalised = -1.0;

	public Particle(PApplet parent, double worldSizeWidth, double worldSizeHeight, List<Landmark> landmarks, double sensorRange) {
		super(parent, worldSizeWidth, worldSizeHeight, landmarks, sensorRange);
	}
	
	public Particle(Robot robot) {
		super(robot);
	}
	
	public double measurementProb(NavigableMap<Integer, Double> measurementVec) {
		
		double prob = 1.0;
		double dist = 0.0;
		double xPosSq = 0.0;
		double yPosSq = 0.0;

		// go through each land mark a determine probability of measurements
		// matching distance to landmarks
		for (Entry<Integer, Double> entry : measurementVec.entrySet()) {
			
			// get the current land mark from the measurementVec
			Landmark currLandmark = _landmarks.get(entry.getKey());
			
			// calculate the distance to the landmark - using trig
			xPosSq = Math.pow(_xPos - currLandmark._xPos, 2);
			yPosSq = Math.pow(_yPos - currLandmark._yPos, 2);
			dist = Math.sqrt(xPosSq + yPosSq);
			
			// multiplying to get the joint probability of all the measurements
			prob *= UtilMath.gaussianFormula(entry.getValue(), dist, _senseNoise);
		}

		return prob;
	}
	
	@Override
	public void draw() {
		int Yellow 	= _parent.color(255, 255, 0);
		int Red 	= _parent.color(255, 0, 0);
		int Green 	= _parent.color(0, 255, 0);
		int Blue 	= _parent.color(0, 0, 255);
		int White 	= _parent.color(255);
		int Black 	= _parent.color(0);
		final int MUL = 10;
		
		// draw center ellipse
		_parent.strokeWeight(0);
		_parent.stroke(Black, 50);
		_parent.fill(Black, 20);
		_parent.ellipseMode(PApplet.CENTER);
		_parent.ellipse((float)_xPos * MUL, (float)_yPos * MUL, 10, 10);
		
		// draw heading pointer
		final float pointerLen = 1.0f;
		_parent.strokeWeight(0);
		_parent.stroke(Black, 50);
		double endX = _xPos + (Math.cos(_heading) * pointerLen);
		double endY = _yPos + (Math.sin(_heading) * pointerLen);
		_parent.line((float)_xPos * MUL, (float)_yPos * MUL, (float)endX * MUL, (float)endY * MUL);
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
