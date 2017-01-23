package fr.upmc.datacenter.data.interfaces;

import fr.upmc.datacenter.data.PerformanceControllerPortsData;

/**
 * Interface du {@link PerformanceControllerPortsData}
 * 
 * @author Daniel RADEAU
 *
 */

public interface PerformanceControllerPortsDataI extends PortsDataI {

	/**
	 * Retourne l'uri du port d'entr�e de gestion du composant {@link PerformanceController}
	 * @return
	 */
	
	String getPerformanceControllerManagementInboundPortURI();
	
	/**
	 * Retourne l'uri du port d'entr�e de services du composant {@link PerformanceController}
	 * @return
	 */
	
	String getPerformanceControllerServicesInboundPortURI();
	
	/**
	 * Retourne l'uri du port d'entr�e de notification de lib�ration de coeur du composant {@link PerformanceController}
	 * @return
	 */
	
	String getPerformanceControllerCoreReleasingNotificationInboundPortURI();
	
}
