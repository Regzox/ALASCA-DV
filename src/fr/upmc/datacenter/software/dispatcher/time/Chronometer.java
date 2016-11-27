package fr.upmc.datacenter.software.dispatcher.time;

import fr.upmc.datacenter.software.dispatcher.time.interfaces.ChronometerI;
import fr.upmc.datacenter.software.dispatcher.time.interfaces.DurationI;

/**
 * 
 * Chronomètre produisant des {@link Duration} à l'appel d'un top ou stop après l'avoir lancé
 * 
 * @author Daniel RADEAU
 *
 */

public class Chronometer implements ChronometerI {

	public enum State {
		STOPPED,
		STARTED
	}
	
	protected Long start;
	protected State state;
	
	public Chronometer() {
		start = null;
		state = State.STOPPED;
	}
	
	public void start() {
		assert start == null;
		assert state == State.STOPPED;
		
		start = System.nanoTime();
		state = State.STARTED;
		
		assert start != null;
		assert state == State.STARTED;
	}
	
	public DurationI top() {
		assert start != null;
		assert state == State.STARTED;
		
		return new Duration(start, System.nanoTime());
	}
	
	public DurationI stop() {
		assert start != null;
		assert state == State.STARTED;
		
		DurationI duration = new Duration(start, System.nanoTime());
		start = null;
		state = State.STOPPED;
		
		assert start == null;
		assert state == State.STOPPED;
		
		return duration;
	}
}
