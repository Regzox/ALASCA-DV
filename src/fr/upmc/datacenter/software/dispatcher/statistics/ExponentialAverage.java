package fr.upmc.datacenter.software.dispatcher.statistics;

import fr.upmc.datacenter.software.dispatcher.statistics.interfaces.ExponentialAverageI;
import fr.upmc.datacenter.software.dispatcher.time.Duration;
import fr.upmc.datacenter.software.dispatcher.time.interfaces.DurationI;

/**
 * 
 * Moyenneur exponentiel mobile donnant plus d'importance aux résultats récents
 * 
 * @author Daniel RADEAU
 *
 */

public class ExponentialAverage implements ExponentialAverageI {

	protected double weight;
	protected Duration average;
	
	public ExponentialAverage(double weight) {
		assert weight < 1 && weight > 0;
		
		this.weight = weight;
		this.average = new Duration(0, 0);
	}
	
	public double getWeight() {
		return weight;
	}
	
	public void setWeight(double weight) {
		assert weight < 1 && weight > 0;
		
		this.weight = weight;
	}
	
	public Duration getValue() {
		assert average != null;
		
		return average;
	}
	
	/**
	 * Pousse une nouvelle durée à prendre en compte dans le calcul de la moyenne
	 * 
	 * @param duration
	 */
	
	public void push(DurationI duration) {
		if (average == null)
			average = new Duration((Duration)duration);
		else 
			average.setNanoseconds((long)(weight * duration.getNanoseconds() + (1 - weight) * average.getNanoseconds()));
	}
}
