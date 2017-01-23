package fr.upmc.datacenter.data.interfaces;

import fr.upmc.datacenter.providers.resources.physical.PhysicalResourcesProvider;

/**
 * Interface du {@link PhysicalResourcesProviderPortsData}
 * 
 * @author Daniel RADEAU
 *
 */

public interface PhysicalResourcesProviderPortsDataI extends PortsDataI {

	/**
	 * Retourne l'uri du port d'entr�e de gestion du composant {@link PhysicalResourcesProvider}
	 * @return
	 */
	
	String getPhysicalResourcesProviderManagementInboundPort();
	
	/**
	 * Retourne l'uri du port d'entr�e de requ�tage du composant {@link PhysicalResourcesProvider}
	 * @return
	 */
	
	String getPhysicalResourcesProviderRequestingInboundPort();
	
	/**
	 * Retourne l'uri du port d'entr�e de services du composant {@link PhysicalResourcesProvider}
	 * @return
	 */
	
	String getPhysicalResourcesProviderServicesInboundPort();
	
}
