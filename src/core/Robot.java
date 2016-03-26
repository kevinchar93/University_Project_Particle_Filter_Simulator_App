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
	protected double _xPos = 0.0;
	protected double _yPos = 0.0;
	protected double _heading = 0.0;

	// uncertainty information
	protected double _forwardNoise = 0.0;
	protected double _turnNoise = 0.0;
	protected double _senseNoise = 0.0;

	protected Random _rand = new Random();

	public Robot(PApplet parent, double worldSize, List<Landmark> landmarks) {

		_parent = parent;
		_worldSize = worldSize;
		_landmarks.addAll(landmarks);

		// randomly initialize the pose of the robot
		_xPos = worldSize * _rand.nextDouble();
		_yPos = worldSize * _rand.nextDouble();
		_heading = (2 * Math.PI) * _rand.nextDouble();
	}
	
	public Robot(Robot robot) {
		
		_parent = robot._parent;
		_worldSize = robot._worldSize;
		_landmarks = robot._landmarks;
		_xPos = robot._xPos;
		_yPos = robot._yPos;
		_heading = robot._heading;
		_forwardNoise = robot._forwardNoise;
		_turnNoise = robot._turnNoise;
		_senseNoise = robot._senseNoise;
		_rand = new Random();
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

		_xPos = in_xPos;
		_yPos = in_yPos;
		_heading = in_heading;

		return true;
	}

	public void setNoise(double forward_noise, double turn_noise, double sense_noise) {
		_forwardNoise = forward_noise;
		_turnNoise = turn_noise;
		_senseNoise = sense_noise;
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
			distance += _rand.nextGaussian() * _senseNoise + 0.0;
			readings.add(distance);
		}

		return readings;
	}

	public Robot move(double turn, double forward) {
		
		//String turnDir = (turn >= 0) ? "counter clockwise" : "clockwise";

		if (forward < 0) {
			return null;
		}

		double newHeading = _heading + turn + (_rand.nextGaussian() * _turnNoise + 0.0);
		newHeading = Util.mod(newHeading, 2 * Math.PI);

		double dist = forward + (_rand.nextGaussian() * _forwardNoise + 0.0);
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
	
	
}
