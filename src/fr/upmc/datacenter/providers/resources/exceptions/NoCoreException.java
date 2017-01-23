package fr.upmc.datacenter.providers.resources.exceptions;

import fr.upmc.datacenter.hardware.processors.Core;

/**
 * Exception levée lorsqu'il n'y plus possibilité d'allouer/d'obtenir un {@link Core}
 * 
 * @author Daniel RADEAU
 *
 */

public class NoCoreException extends Exception {

	private static final long serialVersionUID = -4796416598643466688L;

	public NoCoreException(String message) {
		super(message);
	}

	
}
