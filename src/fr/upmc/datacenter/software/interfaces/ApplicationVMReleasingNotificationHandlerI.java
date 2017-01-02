package fr.upmc.datacenter.software.interfaces;

/**
 * Inteface de gestion des notifications de lib�ration d'AVM
 * 
 * @author Daniel RADEAU
 *
 */

public interface ApplicationVMReleasingNotificationHandlerI {

	/**
	 * Re�oit l'�v�nement de lib�ration d'une AVM de la part d'un dispatcher
	 * 
	 * @param dispatcherURI
	 * @param rsopURI
	 * @param rnipURI
	 */
	
	void acceptApplicationVMReleasing(String dispatcherURI, String rsopURI, String rnipURI) throws Exception;
	
}
