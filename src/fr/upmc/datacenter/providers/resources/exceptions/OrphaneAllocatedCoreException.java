package fr.upmc.datacenter.providers.resources.exceptions;

import fr.upmc.datacenter.hardware.processors.Core;
import fr.upmc.datacenter.providers.resources.physical.PhysicalResourcesProvider;

/**
 * Exception levée lorsqu'un tour d'anneau des {@link PhysicalResourcesProvider} est accomplit
 * sans avoir pu déterminé la provenance d'un ou plusieurs {@link Core}.
 * Cette levée exception peut signifier une rupture de l'anneau ou bien une mauvaise manipulation
 * lors de déconnexion/reconnexion des ports de requêtage des {@link PhysicalResourcesProvider}
 * 
 * @author Daniel RADEAU
 *
 */

public class OrphaneAllocatedCoreException extends Exception {
	
	private static final long serialVersionUID = 5331610054171264909L;

	public OrphaneAllocatedCoreException(String message) {
		super(message);
	}

}
