package fr.upmc.datacenter.software.controllers.admission.interfaces;

/**
 * Interface des m�thodes communes du contr�leur d'admission
 * 
 * @author Daniel RADEAU
 *
 */

public interface AdmissionControllerI {

	/**
	 * Permet la creation d'un contr�leur de performances
	 * 
	 * @throws Exception
	 */
	
	void createPerformanceController() throws Exception;
	
	/**
	 * Permet de savoir si le cloud peut accepter de nouvelles applications 
	 * 
	 * @return
	 * @throws Exception
	 */
	
	boolean isHostable() throws Exception;
	
	/**
	 * Permet � un acteur ext�rieur de soumettre une application (simulation)
	 * 
	 * @return
	 * @throws Exception
	 */
	
	boolean submitApplication() throws Exception;
	
	/**
	 * Generation d'uri pour le contr�leur d'admission en se basant sur la quantit� de contr�leurs de performances cr��s 
	 * 
	 * @param ownerUri l'uri du d�tenteur du composant pour lequel l'uri est g�n�r�e
	 * @param tag identificateur pour le composant
	 * @return retourne une uri combin�e des param�tres et de l'�tat de du contr�leur d'admission
	 * @throws Exception
	 */
	
	String generatePerformanceControllerUri(String ownerUri, Object tag) throws Exception;
	
	/**
	 * Generation d'uri pour le contr�leur d'admission en se basant sur la quantit� de fournisseurs de ressources
	 * logiques connect�s 
	 * 
	 * @param ownerUri l'uri du d�tenteur du composant pour lequel l'uri est g�n�r�e
	 * @param tag identificateur pour le composant
	 * @return retourne une uri combin�e des param�tres et de l'�tat de du contr�leur d'admission
	 * @throws Exception
	 */
	
	String generateLogicalResourcesProviderUri(String ownerUri, Object tag) throws Exception;
	
}
