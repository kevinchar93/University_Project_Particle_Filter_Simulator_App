package util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.Map.Entry;

import map.Landmark;
import processing.core.PApplet;
import robot.Particle;

public class UtilParticle {
	
	private static Random _rand = new Random();
	
	/**
	 * Create string representation of the distances to the landmarks
	 * 
	 * @param readings
	 *            the list of readings to turn into a string
	 * @return
	 */
	public static String readingsToString(List<Double> readings) {

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
	public static List<Particle> genParticles(PApplet parent, final int numParticles, double worldSizeWidth, double worldSizeHeight, List<Landmark> landmarks, double sensorRange,
			double forwardNoise, double turnNoise, double sensorNoise) {

		List<Particle> genParticles = new ArrayList<>(numParticles);

		for (int i = 0; i < numParticles; i++) {
			Particle tempParticle = new Particle(parent, worldSizeWidth, worldSizeHeight, landmarks, sensorRange);
			tempParticle.setNoise(forwardNoise, turnNoise, sensorNoise);
			genParticles.add(tempParticle);
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
	public static List<Particle> moveParticles(List<Particle> originParticles, double turn, double forward) {

		List<Particle> newParticles = new ArrayList<>(originParticles.size());

		for (Particle currParticle : originParticles) {
			newParticles.add(new Particle(currParticle.move(turn, forward)));
		}

		return newParticles;
	}

	
	/**
	 * Give a list of particles each a weight depending on how probable their positions is
	 * compared to the measurement vector
	 * 
	 * @param originParticles the list of particles to each give a weight
	 * @param measurementVec the measurement vector containing the distance to each landmark
	 * @return
	 */
	public static List<Particle> weighParticles(List<Particle> originParticles, NavigableMap<Integer, Double> measurementVec) {

		List<Particle> weightedParticles = new ArrayList<>(originParticles.size());
		double weightSum = 0.0;

		// if we actually took some measurements then create a weight for the particles
		if (measurementVec.size() > 0) {
			// loop through each particle and calculate how plausible position is
			// given measurementVec
			for (Particle currParticle : originParticles) {
	
				double particleWeight = currParticle.measurementProb(measurementVec);
				weightSum += particleWeight;
	
				currParticle.setWeight(particleWeight);
				weightedParticles.add(currParticle);
			}
	
			// use the sum of the particle weights to give each particle a
			// normalised weight
			for (Particle currParticle : weightedParticles) {
	
				double normWeight = currParticle.getWeight() / weightSum;
				currParticle.setNormalisedWeight(normWeight);
			}
		}
		else {
			// if no measurements were taken then particles should be given uniform weight
			for (Particle currParticle : originParticles) {
				double particleWeight = 1.0/originParticles.size();
				currParticle.setNormalisedWeight(particleWeight);
				weightedParticles.add(currParticle);
			}
		}

		return weightedParticles;
	}
	

	/**
	 * Create a new sample of particles according to the weight of the particles
	 * in the given list, more probable particles are more likely to survive.
	 * 
	 * Uses BigDecimal to represent to probability of each particle
	 * 
	 * @param originParticles the list of particles to create the new sample from
	 * @return the newly resampled list of particles
	 */
	public static List<Particle> resampleParticlesBigDecimal(List<Particle> originParticles, boolean addRandomParticles) {

		final float PERCENTAGE_RANDOM = 0.1f;
		final float PERCENTAGE_NORMAL = 0.9f;
		
		final int ORIGIN_SIZE = originParticles.size();
		final int NORMAL_SAMPLE_SIZE;
		final int RANDOM_SAMPLE_SIZE;
		
		if (ORIGIN_SIZE >= 100 && addRandomParticles == true) {
			// get calculate how much 10% is of the number of particles (ensures a whole number)
			int div = ORIGIN_SIZE / 100;
			int nearestHundred = div * 100; 
			int remainder = ORIGIN_SIZE - nearestHundred;
			NORMAL_SAMPLE_SIZE = (int)((PERCENTAGE_NORMAL * nearestHundred) + remainder);
			RANDOM_SAMPLE_SIZE = (int)(PERCENTAGE_RANDOM * nearestHundred);
		}
		else {
			// not enough particles to do a random sample
			NORMAL_SAMPLE_SIZE = ORIGIN_SIZE;
			RANDOM_SAMPLE_SIZE = 0;
		}
		
		// map the particles to a finite range less than 1 that defines
		// how likely they are to be correct, re-sample based on this map
		NavigableMap<BigDecimal, Particle> probMap = new TreeMap<>();
		

		// accumulate the probability of each particle into probabilitySum and
		// use it to give each particle a slice of the values from 0 - 1
		BigDecimal probSum = new BigDecimal(0.0);
		for (Particle currParticle : originParticles) {

			// BigDecimal used for arbitrary precision arithmetic
			BigDecimal currentProb = new BigDecimal(currParticle.getNormalisedWeight());
			probSum = probSum.add(currentProb);
			probMap.put(probSum, currParticle);
		}

		// create a new empty list of particles to store the new samples
		List<Particle> newParticles = new ArrayList<>(ORIGIN_SIZE);

		// Loop through the probMap and randomly generate N keys to pick out
		// N new particles for the re-sample, the probability of a particle
		// being picked depends on the plausibility of the particles measurement
		// vector
		for (int i = 0; i < NORMAL_SAMPLE_SIZE; i++) {

			// use random num between [0 : 1) to choose particle
			BigDecimal randVal = new BigDecimal(_rand.nextDouble());
			Particle pickedParticle = probMap.get(probMap.ceilingKey(randVal));

			// NOTE: we use the copy constructor here as each particle must be a
			// new distinct particle with its own allocated memory & attributes
			newParticles.add(new Particle(pickedParticle));
		}
		
		// put a portion of particles in the distribution with random poses
		for (int i = 0; i < RANDOM_SAMPLE_SIZE; i++) {
			
			Particle tempPar = originParticles.get(0);
			
			// create a random pose 
			Particle randomParticle = new Particle(tempPar);
			randomParticle.randomisePose();
			newParticles.add(randomParticle);
		}

		return newParticles;
	}
	
	
	/**
	 * Create a new sample of particles according to the weight of the particles
	 * in the given list, more probable particles are more likely to survive.
	 * 
	 * Uses Double to represent to probability of each particle
	 * 
	 * @param originParticles the list of particles to create the new sample from
	 * @return the newly resampled list of particles
	 */
	public static List<Particle> resampleParticlesDouble(List<Particle> originParticles) {

		// map the particles to a finite range less than 1 that defines
		// how likely they are to be correct, re-sample based on this map
		NavigableMap<Double, Particle> probMap = new TreeMap<>();
		final int ORIGIN_SIZE = originParticles.size();

		// accumulate the probability of each particle into probabilitySum and
		// use it to give each particle a slice of the values from 0 - 1
		Double probSum = 0.0;
		for (Particle currParticle : originParticles) {

			// BigDecimal used for arbitrary precision arithmetic
			Double currentProb = currParticle.getNormalisedWeight();
			probSum += currentProb;
			probMap.put(probSum, currParticle);
		}

		// create a new empty list of particles to store the new samples
		List<Particle> newParticles = new ArrayList<>(ORIGIN_SIZE);

		// Loop through the probMap and randomly generate N keys to pick out
		// N new particles for the re-sample, the probability of a particle
		// being picked depends on the plausibility of the particles measurement
		// vector
		for (int i = 0; i < ORIGIN_SIZE; i++) {

			// use random num between [0 : 1) to choose particle
			Double randVal = _rand.nextDouble();
			Particle pickedParticle = probMap.get(probMap.ceilingKey(randVal));

			// NOTE: we use the copy constructor here as each particle must be a
			// new distinct particle with its own allocated memory & attributes
			newParticles.add(new Particle(pickedParticle));
		}

		return newParticles;
	}
	
	
	/**
	 * Count the occurrences of each particle in the given list and display this 
	 * information, particles with the same values for their fields are counted
	 * as the same particle
	 * 
	 * @param particles the list of particles to print the distribution for
	 */
	public static void printParticleDistributionCount (List<Particle> particles) {
		
		// create a map that counts each particles occurrence in the re-sample
		NavigableMap<String, Integer> map = new TreeMap<>();
		for (Particle par : particles) {
			
			String posStr = String.format("X: %.3f Y: %.3f", par.getX(), par.getY());
			int val;
			if (map.containsKey(posStr)) {
				val = map.get(posStr);
				val++;
				map.put(posStr, val);
			}
			else {
				map.put(posStr, 1);
			}
		}
		
		int total = 0;
		for (Entry<String, Integer> entry : map.entrySet()) {
			String key = entry.getKey();
		    Integer value = entry.getValue();
		    total += value;
		    System.out.println("Pos: " + key + "   Count:" + value);
		}
		
		System.out.println("Total count:" + total);
	}

}
