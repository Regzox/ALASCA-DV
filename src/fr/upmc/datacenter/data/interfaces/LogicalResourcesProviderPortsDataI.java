package fr.upmc.datacenter.data.interfaces;

public interface LogicalResourcesProviderPortsDataI extends PortsDataI {

	public String getLogicalResourcesProviderManagementInboundPort();
	public String getLogicalResourcesProviderRequestingInboundPort();
	public String getLogicalResourcesProviderServicesInboundPort();
	public String getLogicalResourcesProviderCoreReleasingNotifyBackInboundPort();
	
}
