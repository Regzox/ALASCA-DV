package fr.upmc.datacenter.data.interfaces;

import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;

/**
 * Interface du {@link RequestGeneratorPortsData}
 * 
 * @author Daniel RADEAU
 *
 */

public interface RequestGeneratorPortsDataI extends PortsDataI {
	
	/**
	 * Retourne l'uri du port d'entrée de gestion du composant {@link RequestGenerator}
	 * @return
	 */
	
	String getRequestGeneratorManagementInboundPort();
	
	/**
	 * Retourne l'uri du port d'entrée de notification de requête du composant {@link RequestGenerator}
	 * @return
	 */
	
	String getRequestNotificationInboundPort();

}
