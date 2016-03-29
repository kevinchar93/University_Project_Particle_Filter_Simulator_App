package robot;
import processing.core.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import map.Landmark;
import processing.core.PApplet;
import util.UtilMath;

public class Robot {

	PApplet _parent;

	// world information
	final double _worldSizeWidth;
	final double _worldSizeHeight;
	List<Landmark> _landmarks = new ArrayList<>();

	// pose information
	protected double _xPos = 0.0;
	protected double _yPos = 0.0;
	protected double _heading = 0.0;
	private double _sensorRange = 0.0;

	// uncertainty information
	protected double _forwardNoise = 0.0;
	protected double _turnNoise = 0.0;
	protected double _senseNoise = 0.0;

	protected Random _rand = new Random();

	public Robot(PApplet parent, double worldSizeWidth, double worldSizeHeight, List<Landmark> landmarks, double sensorRange) {

		_parent = parent;
		_worldSizeWidth = worldSizeWidth;
		_worldSizeHeight = worldSizeHeight;
		_sensorRange = sensorRange;
		_landmarks.addAll(landmarks);

		// randomly initialize the pose of the robot
		_xPos = worldSizeWidth * _rand.nextDouble();
		_yPos = worldSizeHeight * _rand.nextDouble();
		_heading = (2 * Math.PI) * _rand.nextDouble();
	}
	
	public Robot(Robot robot) {
		
		_parent = robot._parent;
		_worldSizeWidth = robot._worldSizeWidth;
		_worldSizeHeight = robot._worldSizeHeight;
		_landmarks = robot._landmarks;
		_sensorRange = robot._sensorRange;
		_xPos = robot._xPos;
		_yPos = robot._yPos;
		_heading = robot._heading;
		_forwardNoise = robot._forwardNoise;
		_turnNoise = robot._turnNoise;
		_senseNoise = robot._senseNoise;
		_rand = new Random();
	}


	public boolean setPose(double in_xPos, double in_yPos, double in_heading) {

		if (in_xPos < 0 || in_xPos >= _worldSizeWidth) {

			PApplet.println("X pos outside of world");
			return false;
		} else if (in_yPos < 0 || in_yPos >= _worldSizeHeight) {
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

	public NavigableMap<Integer, Double> sense() {
		
		// map to store the landmark and the reading from that landmark
		NavigableMap<Integer, Double> readingsMap = new TreeMap<>();
		double distance = 0.0;
		double x_pos_sq = 0.0;
		double y_pos_sq = 0.0;

		for (Landmark currLandmark : _landmarks) {
			
			// calculate the distance to the landmark - using trig
			x_pos_sq = Math.pow(_xPos - currLandmark._xPos, 2);
			y_pos_sq = Math.pow(_yPos - currLandmark._yPos, 2);

			distance = Math.sqrt(x_pos_sq + y_pos_sq);
			distance += _rand.nextGaussian() * _senseNoise + 0.0;
			
			// if the sensed landmark was within range add it to the measurement vector
			if (distance <= _sensorRange) {
				readingsMap.put(currLandmark._id, distance);
			}
		}

		return readingsMap;
	}

	public Robot move(double turn, double forward) {

		if (forward < 0) {
			return null;
		}

		double newHeading = _heading + turn + (_rand.nextGaussian() * _turnNoise + 0.0);
		newHeading = UtilMath.mod(newHeading, 2 * Math.PI);

		double dist = forward + (_rand.nextGaussian() * _forwardNoise + 0.0);
		double newX = _xPos + (Math.cos(newHeading) * dist);
		double newY = _yPos + (Math.sin(newHeading) * dist);

		// the world is cyclic , wrap the robots position
		newX = UtilMath.mod(newX, _worldSizeWidth);
		newY = UtilMath.mod(newY, _worldSizeHeight);

		Robot newRobot = new Robot(_parent, _worldSizeWidth, _worldSizeHeight, _landmarks, _sensorRange);
		newRobot.setPose(newX, newY, newHeading);
		newRobot.setNoise(_forwardNoise, _turnNoise, _senseNoise);
		return newRobot;
	}
	
	
	public void draw() {
		
		int Yellow 	= _parent.color(255, 255, 0);
		int Red 	= _parent.color(255, 80, 80);
		int Green 	= _parent.color(0, 255, 0);
		int Blue 	= _parent.color(30, 80, 200);
		int White 	= _parent.color(255);
		int Black 	= _parent.color(0);
		final int MUL = 10;
		
		_parent.fill(Red, 255);
		_parent.ellipseMode(PApplet.CENTER);
		_parent.ellipse((float)_xPos * MUL, (float)_yPos * MUL, 20, 20);
		
		// draw heading pointer
		final float lineLen = 1.5f;
		_parent.strokeWeight(2);
		_parent.stroke(Black, 150);
		double endX = _xPos + (Math.cos(_heading) * lineLen);
		double endY = _yPos + (Math.sin(_heading) * lineLen);
		_parent.line((float)_xPos * MUL, (float)_yPos * MUL, (float)endX * MUL, (float)endY * MUL);
		
		// draw the sensor range ring
		_parent.strokeWeight(0);
		_parent.stroke(Red, 180);
		_parent.fill(Red, 30);
		_parent.ellipseMode(PApplet.CENTER);
		_parent.ellipse((float)_xPos * MUL, (float)_yPos * MUL, (float)_sensorRange * MUL, (float)_sensorRange * MUL);
		
		_parent.stroke(Black, 255);
		
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
