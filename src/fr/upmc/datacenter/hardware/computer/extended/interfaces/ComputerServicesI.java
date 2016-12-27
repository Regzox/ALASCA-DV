package fr.upmc.datacenter.hardware.computer.extended.interfaces;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

public interface ComputerServicesI 
	extends fr.upmc.datacenter.hardware.computers.interfaces.ComputerServicesI
{
	/**
	 * Lib�re le coeur du associ� � l'allocation pass�e en param�tre
	 * @param allocatedCore
	 */
	
	void releaseCore(AllocatedCore allocatedCore) throws Exception;
	
	/**
	 * Lib�re les coeurs associ�s aux allocations pass�es en param�tre
	 * @param allocatedCores
	 */
	
	void releaseCores(AllocatedCore[] allocatedCores) throws Exception;
}
