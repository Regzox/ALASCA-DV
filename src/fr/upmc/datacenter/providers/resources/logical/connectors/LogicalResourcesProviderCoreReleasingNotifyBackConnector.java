package fr.upmc.datacenter.providers.resources.logical.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderCoreReleasingNotifyBackI;

public class LogicalResourcesProviderCoreReleasingNotifyBackConnector 
	extends
		AbstractConnector
	implements
		LogicalResourcesProviderCoreReleasingNotifyBackI
{

	@Override
	public void notifyBackCoreReleasing(String requesterUri, String answererUri, AllocatedApplicationVM aavm, AllocatedCore ac) throws Exception {
		((LogicalResourcesProviderCoreReleasingNotifyBackI) this.offering).notifyBackCoreReleasing(requesterUri, answererUri, aavm, ac);
	}
	
}
