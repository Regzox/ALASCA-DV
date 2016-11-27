package fr.upmc.datacenter.software.dispatcher.time.interfaces;

public interface ChronometerI {

	void start();
	DurationI top();
	DurationI stop();
	
}
