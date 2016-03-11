package core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import processing.core.*;

public class ParticleFilterCore extends PApplet {
	
	// Create vars
	List<Landmark> landmarks = new ArrayList<>();
	final double WORLD_SIZE = 100.0;
	
	List <Double> sensorReadings;
	List <Robot> particlesList;
	
	Robot robot;
	
	final int maxParticles = 1000;
	
	int Yellow 	= color(255, 255, 0);
	int Red 	= color(255, 0, 0);
	int Green 	= color(0, 255, 0);
	int Blue 	= color(0, 0, 255);
	int White 	= color(255);
	int Black 	= color(0);
	
	public void settings() 
	{
		size(100, 100);
		noSmooth();
	}
	
	public void setup() 
	{
		background(0);
		
		// setup vars
		landmarks.add(new Landmark(20.0, 20.0));
		landmarks.add(new Landmark(80.0, 80.0));
		landmarks.add(new Landmark(20.0, 80.0));
		landmarks.add(new Landmark(80.0, 20.0));
		
		// create robot - give map of world
		robot = new Robot(this, WORLD_SIZE, landmarks);
		
		particlesList = randomParticleDistribution(maxParticles, WORLD_SIZE, landmarks);
		
		/*
		robot.setNoise(5, 0.1, 5);
		
		// set the robots initial position of (30, 50) heading of 90 deg
		robot.setPose(30, 50, Math.PI/2);
		PApplet.println(robot);
		
		// robot turn 90 degrees  clockwise, and moves 15 meters
		robot = robot.move(d2r(-90), 15);
		PApplet.println(robot);
		
		// senses
		sensorReadings = robot.sense();
		PApplet.println(readingsToString(sensorReadings));
		
		
		// turn clockwise 90 degrees and move 10 meters
		robot = robot.move(d2r(-90), 10);
		PApplet.println(robot);
		
		// senses
		sensorReadings = robot.sense();
		PApplet.println(readingsToString(sensorReadings));
		*/
	}
	
	public void draw() 
	{
		strokeWeight(3);
		
		// set up the axis to begin at bottom left
		translate(0, height);
		scale(1, -1);
		
		
		// landmark points
		int i = 0;
		for (Landmark currLandmark : landmarks)
		{
			switch (i)
			{
				case 0:
					stroke(Green);
					break;
				case 1:
					stroke(Red);			
					break;
				case 2:
					stroke(Yellow);
					break;
				case 3:
					stroke(Blue);
					break;
			}
			
			point((float)currLandmark._xPos, (float)currLandmark._yPos);
			i++;
		}
		
		// draw robot
		stroke(255);
		point((float)robot._xPos, (float)robot._yPos);
	}
	
	public String readingsToString(List <Double> readings) 
	{
		
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append('[');
		
		for (Iterator<Double> readingItr = readings.iterator(); readingItr.hasNext();) 
		{
			
			double val = readingItr.next().doubleValue();
			stringBuilder.append(String.format("%.3f", val));
			
			if (readingItr.hasNext())
			{
				stringBuilder.append(", ");
			}
			else 
			{
				stringBuilder.append(']');
			}
		}
		
		return stringBuilder.toString();
		
	}
	
	public List <Robot> randomParticleDistribution (final int numParticles, double worldSize, List<Landmark> landmarks)
	{
		List<Robot> genParticles = new ArrayList<>(numParticles);
		
		for (int i = 0; i < numParticles; i++)
		{
			genParticles.add(new Robot(this, worldSize, landmarks));
		}
		
		return genParticles;
	}
	
	public double d2r (double degrees)
	{
		return Math.toRadians(degrees);
	}

}
 