package fr.upmc.datacenter.software.applicationvm.extended.interfaces;

public interface ApplicationVMCoreReleasingI {
	
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
	 * Permet de lib�rer le maximum de coeur en fonction du composant qui implante l'interface.
	 * 
	 * @throws Exception
	 */
	
	void releaseMaximumCores() throws Exception;
	
}
