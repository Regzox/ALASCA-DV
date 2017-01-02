package fr.upmc.datacenter.software.interfaces;

/**
 * Inteface de gestion des notifications de libération d'AVM
 * 
 * @author Daniel RADEAU
 *
 */

public interface ApplicationVMReleasingNotificationHandlerI {

	/**
	 * Reçoit l'événement de libération d'une AVM de la part d'un dispatcher
	 * 
	 * @param dispatcherURI
	 * @param rsopURI
	 * @param rnipURI
	 */
	
	void acceptApplicationVMReleasing(String dispatcherURI, String rsopURI, String rnipURI) throws Exception;
	
}
