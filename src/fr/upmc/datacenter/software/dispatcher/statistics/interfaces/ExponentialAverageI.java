package fr.upmc.datacenter.software.dispatcher.statistics.interfaces;

import fr.upmc.datacenter.software.dispatcher.time.interfaces.DurationI;

public interface ExponentialAverageI {

	void push(DurationI duration);
	
}
