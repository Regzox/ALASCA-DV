package fr.upmc.datacenter.software.controllers.admission.interfaces;

/**
 * Interface des méthodes communes du contrôleur d'admission
 * 
 * @author Daniel RADEAU
 *
 */

public interface AdmissionControllerI {

	/**
	 * Permet la creation d'un contrôleur de performances
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
	 * Permet à un acteur extérieur de soumettre une application (simulation)
	 * 
	 * @return
	 * @throws Exception
	 */
	
	boolean submitApplication() throws Exception;
	
	/**
	 * Generation d'uri pour le contrôleur d'admission en se basant sur la quantité de contrôleurs de performances créés 
	 * 
	 * @param ownerUri l'uri du détenteur du composant pour lequel l'uri est générée
	 * @param tag identificateur pour le composant
	 * @return retourne une uri combinée des paramètres et de l'état de du contrôleur d'admission
	 * @throws Exception
	 */
	
	String generatePerformanceControllerUri(String ownerUri, Object tag) throws Exception;
	
	/**
	 * Generation d'uri pour le contrôleur d'admission en se basant sur la quantité de fournisseurs de ressources
	 * logiques connectés 
	 * 
	 * @param ownerUri l'uri du détenteur du composant pour lequel l'uri est générée
	 * @param tag identificateur pour le composant
	 * @return retourne une uri combinée des paramètres et de l'état de du contrôleur d'admission
	 * @throws Exception
	 */
	
	String generateLogicalResourcesProviderUri(String ownerUri, Object tag) throws Exception;
	
}
