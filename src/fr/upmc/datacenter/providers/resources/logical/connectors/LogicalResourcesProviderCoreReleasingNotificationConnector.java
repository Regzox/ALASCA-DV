package fr.upmc.datacenter.providers.resources.logical.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderCoreReleasingNotificationI;

public class LogicalResourcesProviderCoreReleasingNotificationConnector
	extends
		AbstractConnector
	implements
		LogicalResourcesProviderCoreReleasingNotificationI
{

	@Override
	public void notifyCoreReleasing(AllocatedApplicationVM aavm) throws Exception {
		( (LogicalResourcesProviderCoreReleasingNotificationI) this.offering ).notifyCoreReleasing(aavm);		
	}

}
