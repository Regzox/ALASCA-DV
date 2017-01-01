package fr.upmc.datacenter.software.interfaces;

/**
 * Interface permettant de notifier d'une lib�ration de coeur.
 * 
 * @author Daniel RADEAU
 *
 */

public interface CoreReleasingNotificationI {

	/**
	 * Notifie d'une lib�ration de coeur
	 * 
	 * @param avmURI l'URI de l'AVM qui lib�re un coeur pour pouvoir authentifier la source de la lib�ration
	 */
	void notifyCoreReleasing(String avmURI) throws Exception;
	
}
