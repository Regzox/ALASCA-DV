package fr.upmc.datacenter.data.interfaces;

import java.util.List;

import fr.upmc.datacenter.data.AbstractData;

/**
 * Interface générale de {@link AbstractData}
 * 
 * @author Daniel RADEAU
 *
 */

public interface PortsDataI {

	/**
	 * Retourne l'uri du composant
	 * @return
	 */
	String getUri();
	
	/**
	 * Retourne l'uri des ports d'entrée du composant
	 * @return
	 */
	List<String> getInboundPorts();
	
	/**
	 * Retourne l'uri des port de sortie du composant
	 * @return
	 */
	
	List<String> getOutboundPorts();
}
