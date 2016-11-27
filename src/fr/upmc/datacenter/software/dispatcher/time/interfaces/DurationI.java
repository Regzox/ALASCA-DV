package fr.upmc.datacenter.software.dispatcher.time.interfaces;

public interface DurationI {

	double getSeconds();
	double getMilliseconds();
	double getMicroseconds();
	double getNanoseconds();
	
	void setNanoseconds(long nanoseconds);
}
