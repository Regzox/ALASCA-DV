package fr.upmc.datacenter.software.applicationvm.extended.interfaces;

public interface ApplicationVMManagementI 
	extends fr.upmc.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI 
{
	/**
	 * Permet de lib�rer un coeur. La lib�ration d'un coeur doit �tre faite de mani�re atomique.
	 */
	
	void releaseCore() throws Exception;
	
	/**
	 * Permet de lib�rer plusieurs coeurs.
	 * 
	 * @param cores
	 */
	void releaseCores(int cores) throws Exception;
	
	/**
	 * Permet de lib�rer tous les coeurs sauf 1
	 * @throws Exception
	 */
	
	void releaseMaximumCores() throws Exception;
}
