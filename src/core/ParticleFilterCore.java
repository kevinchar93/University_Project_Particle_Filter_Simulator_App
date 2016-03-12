package core;

import java.util.*;

import processing.core.*;

public class ParticleFilterCore extends PApplet {

	// Create default variables
	List<Landmark> landmarks = new ArrayList<>();
	final double WORLD_SIZE = 100.0;
	int maxParticles = 1000;
	Robot robot;
	Random rand = new Random();

	double FORWARD_NOISE = 0.05;
	double TURN_NOISE = 0.05;
	double SENSOR_NOISE = 5.0;

	// Collections to hold the particles and the readings generated by the robot
	List<Double> sensorReadings;
	List<Robot> particlesList;

	// user interface variables
	int Yellow = color(255, 255, 0);
	int Red = color(255, 0, 0);
	int Green = color(0, 255, 0);
	int Blue = color(0, 0, 255);
	int White = color(255);
	int Black = color(0);

	public void settings() {
		size(100, 100);
		noSmooth();
	}

	public void setup() {
		background(0);

		// setup vars
		landmarks.add(new Landmark(20.0, 20.0));
		landmarks.add(new Landmark(80.0, 80.0));
		landmarks.add(new Landmark(20.0, 80.0));
		landmarks.add(new Landmark(80.0, 20.0));

		// create robot - give map of world
		robot = new Robot(this, WORLD_SIZE, landmarks);

		particlesList = genParticles(maxParticles, WORLD_SIZE, landmarks, FORWARD_NOISE, TURN_NOISE, SENSOR_NOISE);
		// PApplet.println(particlesList.get(400));

		particlesList = moveParticles(particlesList, 0.1, 5);
		// PApplet.println(particlesList.get(400));

		sensorReadings = robot.sense();
		particlesList = weighParticles(particlesList, sensorReadings);
		// PApplet.println(particlesList.get(400).getWeight());

		double A = 18.0 / 35.0;
		double B = 9.0 / 35.0;
		double C = 23.0 / 175.0;
		double D = 12.0 / 175.0;
		double E = 1.0 / 35.0;

		double A_itv_begin = 0.0 + A;
		double B_itv_begin = A_itv_begin + B;
		double C_itv_begin = B_itv_begin + C;
		double D_itv_begin = C_itv_begin + D;
		double E_itv_begin = D_itv_begin + E;

		// PApplet.println(A_itv_begin);

		String[] strList = new String[5];
		strList[0] = "A range";
		strList[1] = "B range";
		strList[2] = "C range";
		strList[3] = "D range";
		strList[4] = "E range";

		double[] probs = new double[5];
		probs[0] = 18.0 / 35.0;
		probs[1] = 9.0 / 35.0;
		probs[2] = 23.0 / 175.0;
		probs[3] = 12.0 / 175.0;
		probs[4] = 1.0 / 35.0;

		NavigableMap<Double, String> demoMap = new TreeMap();
		double rangeSum = 0.0;
		for (int i = 0; i < probs.length; i++) {

			double currentProb = probs[i];
			rangeSum += currentProb;
			PApplet.println(rangeSum);
			demoMap.put(rangeSum, strList[i]);
		}

		

		for (int i = 0; i < 100; i++) {
			double randVal = rand.nextDouble();
			PApplet.println(demoMap.get(demoMap.ceilingKey(randVal)), " ", randVal);
		}

		/*
		 * PApplet.println(demoMap.get(demoMap.ceilingKey(0.22222)));
		 * PApplet.println(demoMap.get(demoMap.ceilingKey(0.312413413)));
		 * PApplet.println(demoMap.get(demoMap.ceilingKey(0.5231232)));
		 * PApplet.println(demoMap.get(demoMap.ceilingKey(0.70280325)));
		 * PApplet.println(demoMap.get(demoMap.ceilingKey(0.7783974)));
		 * PApplet.println(demoMap.get(demoMap.ceilingKey(0.8293842)));
		 * PApplet.println(demoMap.get(demoMap.ceilingKey(0.912103)));
		 * PApplet.println(demoMap.get(demoMap.ceilingKey(0.955402)));
		 * PApplet.println(demoMap.get(demoMap.ceilingKey(0.981331)));
		 * PApplet.println(demoMap.get(demoMap.ceilingKey(0.99345)));
		 * PApplet.println(demoMap.get(demoMap.ceilingKey(01.0)));
		 * PApplet.println(demoMap.get(demoMap.ceilingKey(18.0 / 35.0)));
		 * 
		 * 
		 * /* A 0.5142857142857142 B 0.7714285714285714 C 0.9028571428571428 D
		 * 0.9714285714285714 E 1.0
		 */

	}

	public void draw() {
		strokeWeight(3);

		// set up the axis to begin at bottom left
		translate(0, height);
		scale(1, -1);

		// landmark points
		int i = 0;
		for (Landmark currLandmark : landmarks) {
			switch (i) {
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

			point((float) currLandmark._xPos, (float) currLandmark._yPos);
			i++;
		}

		// draw robot
		stroke(255);
		point((float) robot.getX(), (float) robot.getY());
	}

	/**
	 * Create string representation of the distances to the landmarks
	 * 
	 * @param readings
	 *            the list of readings to turn into a string
	 * @return
	 */
	public String readingsToString(List<Double> readings) {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append('[');

		for (Iterator<Double> readingItr = readings.iterator(); readingItr.hasNext();) {

			double val = readingItr.next().doubleValue();
			stringBuilder.append(String.format("%.3f", val));

			if (readingItr.hasNext()) {
				stringBuilder.append(", ");
			} else {
				stringBuilder.append(']');
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * Generate a random distribution of particles
	 * 
	 * @param numParticles
	 *            number of particles the distribution will have
	 * @param worldSize
	 *            the size of the world the particles will be placed in
	 * @param landmarks
	 *            landmarks that are in the world
	 * @return
	 */
	public List<Robot> genParticles(final int numParticles, double worldSize, List<Landmark> landmarks,
			double forwardNoise, double turnNoise, double sensorNoise) {

		List<Robot> genParticles = new ArrayList<>(numParticles);

		for (int i = 0; i < numParticles; i++) {
			Robot tempRobot = new Robot(this, worldSize, landmarks);
			tempRobot.setNoise(forwardNoise, turnNoise, sensorNoise);
			genParticles.add(tempRobot);
		}
		return genParticles;
	}

	/**
	 * Move a given distribution of particles by the provided parameters
	 * 
	 * @param originParticles
	 *            the set of particles to move
	 * @param turn
	 *            how much heading of each particle will change by
	 * @param forward
	 *            how much forward motion will each particle make
	 * @return a new distributions of particles that have been move using the
	 *         parameters
	 */
	public List<Robot> moveParticles(List<Robot> originParticles, double turn, double forward) {

		List<Robot> newParticles = new ArrayList<>(originParticles.size());

		for (Robot currParticle : originParticles) {
			newParticles.add(currParticle.move(turn, forward));
		}

		return newParticles;
	}

	public List<Robot> weighParticles(List<Robot> originParticles, List<Double> measurementVec) {

		List<Robot> weightedParticles = new ArrayList<>(originParticles.size());
		double weightSum = 0.0;

		// loop through each particle and calculate how plausible position is
		// given measurementVec
		for (Robot currParticle : originParticles) {

			double particleWeight = currParticle.measurementProb(measurementVec);
			weightSum += particleWeight;

			currParticle.setWeight(particleWeight);
			weightedParticles.add(currParticle);
		}

		// use the sum of the particle weights to give each particle a
		// normalised weight
		for (Robot currParticle : weightedParticles) {

			double normWeight = currParticle.getWeight() / weightSum;
			currParticle.setNormalisedWeight(normWeight);
		}

		return weightedParticles;
	}

	public List<Robot> resampleParticles(List<Robot> originParticles) {

		// map the particles to a finite range less than 1 that defines
		// how likely they are to be correct, re-sample based on this map
		NavigableMap<Double, Robot> probMap = new TreeMap<>();
		final int ORIGIN_SIZE = originParticles.size();

		// accumulate the probability of each particle into probabilitySum and
		// use it to give each particle a slice of the values from 0 - 1
		double probSum = 0.0;
		for (Robot currParticle : originParticles) {

			double currentProb = currParticle.getNormalisedWeight();
			probSum += currentProb;
			probMap.put(probSum, currParticle);
		}
		
		// create a new empty list of particles to store the new samples
		List<Robot> newParticles  = new ArrayList<>(ORIGIN_SIZE); 
		
		// Loop through the probMap and randomly generate N keys to pick out
		// N new particles for the re-sample, the probability of a particle being 
		// picked depends on the plausibility of the particles measurement vector
		for (int i = 0; i < ORIGIN_SIZE; i ++) {
			
			double randVal = rand.nextDouble();
			Robot pickedParticle = probMap.get(probMap.ceilingKey(randVal));
			
			// NOTE: we use the copy constructor here as each particle must be a new one
			// with its own independent memory & attributes - to move on its own
			newParticles.add(new Robot(pickedParticle));
		}

		return newParticles;
	}

	/**
	 * Convert degrees to radians, (short hand wrapper method)
	 * 
	 * @param degrees
	 *            degrees value to convert
	 * @return converted value in radians
	 */
	public double d2r(double degrees) {
		return Math.toRadians(degrees);
	}

}