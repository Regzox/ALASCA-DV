package fr.upmc.datacenter.providers.resources.exceptions;

import fr.upmc.datacenter.software.applicationvm.extended.ApplicationVM;

/**
 * Exception levée lorsqu'il n'y a plus possibilité d'allouer/d'obtenir une nouvelle {@link ApplicationVM}
 * 
 * @author Daniel RADEAU
 *
 */

public class NoApplicationVMException extends Exception {

	private static final long serialVersionUID = -4796416598643466842L;

	public NoApplicationVMException(String message) {
		super(message);
	}
	
}
