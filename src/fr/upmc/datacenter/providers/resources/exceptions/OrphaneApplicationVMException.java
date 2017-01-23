package fr.upmc.datacenter.providers.resources.exceptions;

import fr.upmc.datacenter.providers.resources.logical.LogicalResourceProvider;
import fr.upmc.datacenter.software.applicationvm.extended.ApplicationVM;

/**
 * Exception lev�e lorsqu'un tour d'anneau de {@link LogicalResourceProvider} est accomplit
 * sans avoir trouver de d�tenteur d'une {@link ApplicationVM}.
 * Cette lev�e d'exception peut signifier de la m�me mani�re que {@link OrphaneAllocatedCoreException}
 * un soucis lors de la connexion/d�connexion des {@link LogicalResourceProvider} mais �galement une
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
