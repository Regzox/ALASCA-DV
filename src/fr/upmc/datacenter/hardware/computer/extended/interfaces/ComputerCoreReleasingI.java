package fr.upmc.datacenter.hardware.computer.extended.interfaces;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

public interface ComputerCoreReleasingI {
	
	/**
	 * Libère le coeur alloué passé en paramètre
	 * 
	 * @param allocatedCore
	 */
	
	void releaseCore(AllocatedCore allocatedCore) throws Exception;
	
	/**
	 * Libère l'ensemble des coeurs alloués passés en paramètres
	 * 
	 * @param allocatedCores
	 */
	
	void releaseCores(AllocatedCore[] allocatedCores) throws Exception;

}
