package fr.upmc.datacenter.software.applicationvm.extended.interfaces;

public interface ApplicationVMManagementI 
	extends fr.upmc.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI 
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
	 * Permet de libérer tous les coeurs sauf 1
	 * @throws Exception
	 */
	
	void releaseMaximumCores() throws Exception;
}
