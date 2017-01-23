package fr.upmc.datacenter.providers.resources.exceptions;

import fr.upmc.datacenter.hardware.processors.Core;

/**
 * Exception lev�e lorsqu'il n'y plus possibilit� d'allouer/d'obtenir un {@link Core}
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
