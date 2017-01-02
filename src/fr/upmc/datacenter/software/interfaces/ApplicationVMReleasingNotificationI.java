package fr.upmc.datacenter.software.interfaces;

/**
 * Interface de notification des lib�rations d'AVM
 * 
 * @author Daniel RADEAU
 *
 */

public interface ApplicationVMReleasingNotificationI {

	/**
	 * Permet de notifier de la liba�ration d'un AVM
	 * 
	 * @param dispatcherURI
	 * @param rsopURI
	 * @param rnipURI
	 */
	
	void notifyApplicationVMReleasing(String dispatcherURI, String rsopURI, String rnipURI) throws Exception;
	
}
