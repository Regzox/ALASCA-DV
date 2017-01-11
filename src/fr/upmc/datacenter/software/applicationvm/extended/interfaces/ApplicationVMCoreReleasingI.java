package fr.upmc.datacenter.software.applicationvm.extended.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

public interface ApplicationVMCoreReleasingI
extends RequiredI, OfferedI
{
	
	/**
	 * Permet de libérer un coeur. La libération d'un coeur doit être faite de manière atomique.
	 */
	
	void releaseCore() throws Exception;
	
	/**
	 * Permet de libérer plusieurs coeurs.
	 * 
	 * @param cores
	 */
	void releaseCores(int cores) throws Exception;
	
	/**
	 * Permet de libérer le maximum de coeur en fonction du composant qui implante l'interface.
	 * 
	 * @throws Exception
	 */
	
	void releaseMaximumCores() throws Exception;
	
}
