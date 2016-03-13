package core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import processing.core.PApplet;

public class Robot {

	PApplet _parent;

	// world information
	double _worldSize;
	List<Landmark> _landmarks = new ArrayList<>();

	// pose information
	private double _xPos = 0.0;
	private double _yPos = 0.0;
	private double _heading = 0.0;

	// uncertainty information
	private double _forwardNoise = 0.0;
	private double _turnNoise = 0.0;
	private double _senseNoise = 0.0;

	// information related to particles
	private double _weight = -1.0; 		// negative indicates weight not yet set
	private double _weightNormalised = -1.0;
	
	private Random rand = new Random();

	public Robot(PApplet parent, double worldSize, List<Landmark> landmarks) {

		this._parent = parent;
		this._worldSize = worldSize;
		this._landmarks.addAll(landmarks);

		// randomly initialize the pose of the robot
		this._xPos = worldSize * rand.nextDouble();
		this._yPos = worldSize * rand.nextDouble();
		this._heading = (2 * Math.PI) * rand.nextDouble();
	}
	
	public Robot(Robot robot) {
		
		super();
		this._parent = robot._parent;
		this._worldSize = robot._worldSize;
		this._landmarks = robot._landmarks;
		this._xPos = robot._xPos;
		this._yPos = robot._yPos;
		this._heading = robot._heading;
		this._forwardNoise = robot._forwardNoise;
		this._turnNoise = robot._turnNoise;
		this._senseNoise = robot._senseNoise;
		this._weight = robot._weight;
		this._weightNormalised = robot._weightNormalised;
		this.rand = new Random();
	}



	public boolean setPose(double in_xPos, double in_yPos, double in_heading) {

		if (in_xPos < 0 || in_xPos >= _worldSize) {

			PApplet.println("X pos outside of world");
			return false;
		} else if (in_yPos < 0 || in_yPos >= _worldSize) {
			PApplet.println("Y pos outside of world");
			return false;
		} else if (in_heading < 0 || in_heading >= 2 * Math.PI) {
			PApplet.println("Orientation pos outside of possible bounds (0 ... 2pi)");
			return false;
		}

		this._xPos = in_xPos;
		this._yPos = in_yPos;
		this._heading = in_heading;

		return true;
	}

	public void setNoise(double forward_noise, double turn_noise, double sense_noise) {
		this._forwardNoise = forward_noise;
		this._turnNoise = turn_noise;
		this._senseNoise = sense_noise;
	}

	public List<Double> sense() {
		
		List<Double> readings = new ArrayList<>();
		double distance = 0.0;
		double x_pos_sq = 0.0;
		double y_pos_sq = 0.0;

		for (Landmark currLandmark : _landmarks) {
			
			// calculate the distance to the landmark - using trig
			x_pos_sq = Math.pow(_xPos - currLandmark._xPos, 2);
			y_pos_sq = Math.pow(_yPos - currLandmark._yPos, 2);

			distance = Math.sqrt(x_pos_sq + y_pos_sq);
			distance += rand.nextGaussian() * _senseNoise + 0.0;
			readings.add(distance);
		}

		return readings;
	}

	public Robot move(double turn, double forward) {
		
		//String turnDir = (turn >= 0) ? "counter clockwise" : "clockwise";

		if (forward < 0) {
			return null;
		}

		double newHeading = _heading + turn + (rand.nextGaussian() * _turnNoise + 0.0);
		newHeading = Util.mod(newHeading, 2 * Math.PI);

		double dist = forward + (rand.nextGaussian() * _forwardNoise + 0.0);
		double newX = _xPos + (Math.cos(newHeading) * dist);
		double newY = _yPos + (Math.sin(newHeading) * dist);

		// the world is cyclic , wrap the robots position
		newX = Util.mod(newX, _worldSize);
		newY = Util.mod(newY, _worldSize);

		Robot newRobot = new Robot(_parent, _worldSize, _landmarks);
		newRobot.setPose(newX, newY, newHeading);
		newRobot.setNoise(_forwardNoise, _turnNoise, _senseNoise);
		return newRobot;
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

	@Override
	public String toString() {
		return String.format("X: %.3f Y: %.3f Heading: %.3f", _xPos, _yPos, Math.toDegrees(_heading));
	}
	
	public double getX() {
		return _xPos;
	}
	
	public double getY() {
		return _yPos;
	}
	
	public double getHeading() {
		return _heading;
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
