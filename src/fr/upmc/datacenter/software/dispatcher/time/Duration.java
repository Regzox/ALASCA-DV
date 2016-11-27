package fr.upmc.datacenter.software.dispatcher.time;

import java.util.Date;

import fr.upmc.datacenter.software.dispatcher.time.interfaces.DurationI;

/**
 * 
 * Exprime une durée basée sur des {@link Date} soit d'une précision de l'ordre des millisecondes 
 * 
 * @author Daniel RADEAU
 *
 */

public class Duration implements DurationI {
	
	protected long nanoseconds = 0;
	
	public Duration (long nanobegin, long nanoend) {
		assert nanoend >= nanobegin;
		
		nanoseconds = (nanoend - nanobegin);
	}
	
	public Duration (Duration duration) {
		nanoseconds = duration.nanoseconds;
	}

	@Override
	public double getSeconds() {
		return getMilliseconds() / 1000;
	}

	@Override
	public double getMilliseconds() {
		return getMicroseconds() / 1000 ;
	}

	@Override
	public double getMicroseconds() {
		return getNanoseconds() / 1000;
	}
	
	@Override
	public double getNanoseconds() {
		double nanoseconds = (double) this.nanoseconds;
		
		assert nanoseconds >= 0;
		
		return nanoseconds;
	}

	@Override
	public void setNanoseconds(long nanoseconds) {
		this.nanoseconds = nanoseconds;		
	}
	
}
