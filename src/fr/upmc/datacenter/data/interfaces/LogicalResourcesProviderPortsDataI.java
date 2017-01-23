package fr.upmc.datacenter.data.interfaces;

import fr.upmc.datacenter.providers.resources.logical.LogicalResourceProvider;

/**
 * Intefrace du {@link LogicalResourcesProviderPortsData}
 * 
 * @author Daniel RADEAU
 *
 */

public interface LogicalResourcesProviderPortsDataI extends PortsDataI {

	/**
	 * Retourne nom du port d'entr�e de gestion du composant {@link LogicalResourceProvider}
	 * @return
	 */
	
	public String getLogicalResourcesProviderManagementInboundPort();
	
	/**
	 * Retourne nom du port d'entr�e de requ�tage du composant {@link LogicalResourceProvider}
	 * @return
	 */
	
	public String getLogicalResourcesProviderRequestingInboundPort();
	
	/**
	 * Retourne nom du port d'entr�e de services du composant {@link LogicalResourceProvider}
	 * @return
	 */
	
	public String getLogicalResourcesProviderServicesInboundPort();
	
	/**
	 * Retourne nom du port d'entr�e de notification de lib�artion de coeur du composant {@link LogicalResourceProvider}
	 * @return
	 */
	
	public String getLogicalResourcesProviderCoreReleasingNotifyBackInboundPort();
	
}
