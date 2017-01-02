package fr.upmc.datacenter.software.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.interfaces.ApplicationVMReleasingNotificationI;

public class ApplicationVMReleasingNotificationConnector
	extends
		AbstractConnector
	implements
		ApplicationVMReleasingNotificationI
{

	@Override
	public void notifyApplicationVMReleasing(String dispatcherURI, String rsopURI, String rnipURI) throws Exception {
		((ApplicationVMReleasingNotificationI) this.offering).notifyApplicationVMReleasing(dispatcherURI, rsopURI, rnipURI);		
	}

}
