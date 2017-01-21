package fr.upmc.datacenter.software.dispatcher.interfaces;

/**
 * Interface de réception des états dynamiques reçus depuis les dispatchers
 * 
 * @author Daniel RADEAU
 *
 */

public interface DispatcherDynamicStateDataConsumerI {

	/**
	 * Accepte la donnée émise par un dispatcher (L'identité du sipatcher est comprise dans les data)
	 * 
	 * @param data
	 * @throws Exception 
	 */
	
	void acceptDispatcherDynamicStateData(DispatcherDynamicStateI data) throws Exception;
	
}
