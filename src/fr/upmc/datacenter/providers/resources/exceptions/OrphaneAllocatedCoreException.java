package fr.upmc.datacenter.providers.resources.exceptions;

public class OrphaneAllocatedCoreException extends Exception {
	
	private static final long serialVersionUID = 5331610054171264909L;

	public OrphaneAllocatedCoreException(String message) {
		super(message);
	}

}
