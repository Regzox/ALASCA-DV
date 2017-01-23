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
	 * Retourne l'uri du port d'entr�e de gestion du composant {@link RequestGenerator}
	 * @return
	 */
	
	String getRequestGeneratorManagementInboundPort();
	
	/**
	 * Retourne l'uri du port d'entr�e de notification de requ�te du composant {@link RequestGenerator}
	 * @return
	 */
	
	String getRequestNotificationInboundPort();

}
