package fr.upmc.datacenter.software.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.interfaces.CoreReleasingNotificationI;

public class CoreReleasingNotifiactionConnector
	extends
		AbstractConnector
	implements
		CoreReleasingNotificationI
{

	@Override
	public void notifyCoreReleasing(String avmURI) throws Exception 
	{
		((CoreReleasingNotificationI)this.offering).notifyCoreReleasing(avmURI) ;		
	}

}
