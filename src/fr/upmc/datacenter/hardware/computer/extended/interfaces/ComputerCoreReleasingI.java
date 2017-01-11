package fr.upmc.datacenter.hardware.computer.extended.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

public interface ComputerCoreReleasingI extends OfferedI, RequiredI {
	
	/**
	 * Lib�re le coeur allou� pass� en param�tre
	 * 
	 * @param allocatedCore
	 */
	
	void releaseCore(AllocatedCore allocatedCore) throws Exception;
	
	/**
	 * Lib�re l'ensemble des coeurs allou�s pass�s en param�tres
	 * 
	 * @param allocatedCores
	 */
	
	void releaseCores(AllocatedCore[] allocatedCores) throws Exception;

}
