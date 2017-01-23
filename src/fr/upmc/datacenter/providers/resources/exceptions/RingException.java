package fr.upmc.datacenter.providers.resources.exceptions;

/**
 * Exception levée lors l'accomplissement d'un tour d'anneau pour diverses raisons
 * 
 * @author Daniel RADEAU
 *
 */

public class RingException extends Exception {
	
	private static final long serialVersionUID = 1938734663743595731L;

	public RingException(String message) {
		super(message);
	}

}
