package fr.upmc.datacenter.software.dispatcher.interfaces;

/**
 * Interface de r�ception des �tats dynamiques re�us depuis les dispatchers
 * 
 * @author Daniel RADEAU
 *
 */

public interface DispatcherDynamicStateDataConsumerI {

	/**
	 * Accepte la donn�e �mise par un dispatcher (L'identit� du sipatcher est comprise dans les data)
	 * 
	 * @param data
	 * @throws Exception 
	 */
	
	void acceptDispatcherDynamicStateData(DispatcherDynamicStateI data) throws Exception;
	
}
