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
	public double _xPos = 0.0;
	public double _yPos = 0.0;
	public double _heading = 0.0;
	
	// uncertainty information
	double _forwardNoise = 0.0;
	double _turnNoise = 0.0;
	double _senseNoise = 0.0;
	
	Random rand = new Random();
	
	public Robot(PApplet parent, double worldSize, List<Landmark> landmarks) 
	{
		this._parent = parent;
		this._worldSize = worldSize;
		this._landmarks.addAll(landmarks);
		
		// randomly initialize the pose of the robot
		this._xPos = worldSize * rand.nextDouble();
		this._yPos = worldSize * rand.nextDouble();
		this._heading = (2 * Math.PI) * rand.nextDouble();
	}
	
	
	public boolean setPose (double in_xPos, double in_yPos, double in_heading ) 
	{
		
		if (in_xPos < 0 || in_xPos >= _worldSize) 
		{
			
			PApplet.println("X pos outside of world");
			return false;
		}
		else if (in_yPos < 0 || in_yPos >= _worldSize) 
		{
			PApplet.println("Y pos outside of world");
			return false;
		}
		else if (in_heading < 0 || in_heading >= 2 * Math.PI) 
		{
			PApplet.println("Orientation pos outside of possible bounds (0 ... 2pi)");
			return false;
		}
		
		this._xPos = in_xPos;
		this._yPos = in_yPos;
		this._heading = in_heading;
		
		return true;
	}
	
	
	public void setNoise(double forward_noise, double turn_noise, double sense_noise) 
	{
		this._forwardNoise = forward_noise;
		this._turnNoise = turn_noise;
		this._senseNoise = sense_noise;
	}
	
	
	public List <Double> sense ()
	{
		List<Double> readings = new ArrayList<>();
		double distance = 0.0;
		double x_pos_sq = 0.0;
		double y_pos_sq = 0.0;
				
		
		for (Landmark currLandmark: _landmarks) 
		{
			// use pythagoras to calculate distance between robot and land mark
			x_pos_sq =  Math.pow(_xPos - currLandmark._xPos , 2);
			y_pos_sq =  Math.pow(_yPos - currLandmark._yPos , 2);
			
			distance = Math.sqrt(x_pos_sq + y_pos_sq);
			distance += rand.nextGaussian() * _senseNoise + 0.0;
			readings.add(distance);
		}
		
		return readings;
	}
	
	
	public Robot move(double turn, double forward)
	{
		String turnDir;
		if (turn >= 0)
		{
			turnDir = "counter clockwise";
		}
		else
		{
			turnDir = "clockwise";
		}
		
		PApplet.println(String.format("Turn: %.3f %s, Move: %.3f" ,Math.toDegrees(Math.abs(turn)),turnDir, forward));
		
		if (forward < 0)
		{
			return null;
		}
		
		double newHeading = _heading + turn + (rand.nextGaussian() * _turnNoise + 0.0);
		final double _2pi = 2 * Math.PI;
		newHeading = (newHeading % _2pi + _2pi) % _2pi;
		
		double dist = forward + (rand.nextGaussian() * _forwardNoise + 0.0);
		double newX = _xPos + (Math.cos(newHeading) * dist);
		double newY = _yPos + (Math.sin(newHeading) * dist);
		
		// the world is cyclic
		newX %= _worldSize;
		newY %= _worldSize;
		
		Robot newRobot = new Robot(_parent, _worldSize, _landmarks);
		newRobot.setPose(newX, newY, newHeading);
		newRobot.setNoise(_forwardNoise, _turnNoise, _senseNoise);
		return newRobot;
	}
	
	public double gaussian(double mu, double sigma, double x)
	{
		// negative mu minus x all squared
		double mu_minus_X_sq =  Math.pow(mu - x, 2);
		
		// sigma squared divided by two
		double sigma_sq = Math.pow(sigma, 2);
		
		// the square root of, 2 times Pi times sigma squared
		double sqrt_2_pi_sigma_sq = Math.sqrt(2.0f * Math.PI * sigma_sq);
		
		double expRes = Math.exp( -mu_minus_X_sq / sigma_sq / 2.0f );
		
		return expRes / sqrt_2_pi_sigma_sq;
	}
	
	
	public double measurement_prob(List <Double> measurement)
	{
		double prob = 1.0;
		
		double dist = 0.0;
		double xPosSq = 0.0;
		double yPosSq = 0.0;
		
		for (int i = 0; i < _landmarks.size(); i++) 
		{
			Landmark currLandmark = _landmarks.get(i);
			
			xPosSq =  Math.pow(_xPos - currLandmark._xPos , 2);
			yPosSq =  Math.pow(_yPos - currLandmark._yPos , 2);
			dist = 	  Math.sqrt(xPosSq + yPosSq);
			
			prob *= this.gaussian(dist, _senseNoise, measurement.get(i));
		}
		
		return prob;
	}
	
	@Override
	public String toString()
	{
		return String.format("X: %.3f Y: %.3f Heading: %.3f", _xPos, _yPos, Math.toDegrees(_heading));
	}
}
 