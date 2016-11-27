package fr.upmc.datacenter.software.dispatcher.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.dispatcher.interfaces.DispatcherManagementI;

/**
 * Effectue la connexion entre les interfaces requises et offertes de composants 
 * implémentant @link {@link DispatcherManagementI}
 * 
 * @author Daniel RADEAU
 *
 */

public class DispatcherManagementConnector 
		extends 
			AbstractConnector
		implements
			DispatcherManagementI
{

	@Override
	public String connectToRequestGenerator(String rnipURI) throws Exception {
		return ((DispatcherManagementI) this.offering).connectToRequestGenerator(rnipURI);
	}

	@Override
	public void disconnectFromRequestGenerator() throws Exception {
		((DispatcherManagementI) this.offering).disconnectFromRequestGenerator();
	}

	@Override
	public String connectToApplicationVM(String rsipURI) throws Exception {
		return ((DispatcherManagementI) this.offering).connectToApplicationVM(rsipURI);
	}

	@Override
	public void disconnectFromApplicationVM() throws Exception {
		 ((DispatcherManagementI) this.offering).disconnectFromApplicationVM();
	}

}
