package fr.upmc.datacenter.software.interfaces;

/**
 * Interface permettant de notifier d'une libération de coeur.
 * 
 * @author Daniel RADEAU
 *
 */

public interface CoreReleasingNotificationI {

	/**
	 * Notifie d'une libération de coeur
	 * 
	 * @param avmURI l'URI de l'AVM qui libère un coeur pour pouvoir authentifier la source de la libération
	 */
	void notifyCoreReleasing(String avmURI) throws Exception;
	
}
