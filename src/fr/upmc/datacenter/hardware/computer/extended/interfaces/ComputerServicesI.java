package fr.upmc.datacenter.hardware.computer.extended.interfaces;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

public interface ComputerServicesI 
	extends fr.upmc.datacenter.hardware.computers.interfaces.ComputerServicesI
{
	/**
	 * Libère le coeur du associé à l'allocation passée en paramètre
	 * @param allocatedCore
	 */
	
	void releaseCore(AllocatedCore allocatedCore) throws Exception;
	
	/**
	 * Libère les coeurs associés aux allocations passées en paramètre
	 * @param allocatedCores
	 */
	
	void releaseCores(AllocatedCore[] allocatedCores) throws Exception;
}
