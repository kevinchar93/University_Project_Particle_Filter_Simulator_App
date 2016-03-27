package core;
import processing.core.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import processing.core.PApplet;

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

	// uncertainty information
	protected double _forwardNoise = 0.0;
	protected double _turnNoise = 0.0;
	protected double _senseNoise = 0.0;

	protected Random _rand = new Random();

	public Robot(PApplet parent, double worldSizeWidth, double worldSizeHeight, List<Landmark> landmarks) {

		_parent = parent;
		_worldSizeWidth = worldSizeWidth;
		_worldSizeHeight = worldSizeHeight;
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

		if (forward < 0) {
			return null;
		}

		double newHeading = _heading + turn + (_rand.nextGaussian() * _turnNoise + 0.0);
		newHeading = Util.mod(newHeading, 2 * Math.PI);

		double dist = forward + (_rand.nextGaussian() * _forwardNoise + 0.0);
		double newX = _xPos + (Math.cos(newHeading) * dist);
		double newY = _yPos + (Math.sin(newHeading) * dist);

		// the world is cyclic , wrap the robots position
		newX = Util.mod(newX, _worldSizeWidth);
		newY = Util.mod(newY, _worldSizeHeight);

		Robot newRobot = new Robot(_parent, _worldSizeWidth, _worldSizeHeight, _landmarks);
		newRobot.setPose(newX, newY, newHeading);
		newRobot.setNoise(_forwardNoise, _turnNoise, _senseNoise);
		return newRobot;
	}
	
	
	void draw() {
		
		int Yellow 	= _parent.color(255, 255, 0);
		int Red 	= _parent.color(255, 80, 80);
		int Green 	= _parent.color(0, 255, 0);
		int Blue 	= _parent.color(30, 80, 200);
		int White 	= _parent.color(255);
		int Black 	= _parent.color(0);
		
		_parent.fill(Red, 255);
		_parent.ellipseMode(PApplet.CENTER);
		_parent.ellipse((float)_xPos * 10, (float)_yPos * 10, 20, 20);
		
		// draw heading pointer
		final float lineLen = 1.5f;
		_parent.strokeWeight(2);
		_parent.stroke(Black, 150);
		double endX = _xPos + (Math.cos(_heading) * lineLen);
		double endY = _yPos + (Math.sin(_heading) * lineLen);
		_parent.line((float)_xPos * 10, (float)_yPos * 10, (float)endX * 10, (float)endY * 10);
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
