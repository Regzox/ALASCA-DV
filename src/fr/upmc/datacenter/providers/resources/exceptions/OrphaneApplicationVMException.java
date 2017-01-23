package fr.upmc.datacenter.providers.resources.exceptions;

import fr.upmc.datacenter.providers.resources.logical.LogicalResourceProvider;
import fr.upmc.datacenter.software.applicationvm.extended.ApplicationVM;

/**
 * Exception levée lorsqu'un tour d'anneau de {@link LogicalResourceProvider} est accomplit
 * sans avoir trouver de détenteur d'une {@link ApplicationVM}.
 * Cette levée d'exception peut signifier de la même manière que {@link OrphaneAllocatedCoreException}
 * un soucis lors de la connexion/déconnexion des {@link LogicalResourceProvider} mais également une
 * chute d'un des composant de l'anneau.
 * 
 * @author Daniel RADEAU
 *
 */

public class OrphaneApplicationVMException extends Exception {
	
	private static final long serialVersionUID = 680630983996570061L;

	public OrphaneApplicationVMException(String message) {
		super(message);
	}

}
