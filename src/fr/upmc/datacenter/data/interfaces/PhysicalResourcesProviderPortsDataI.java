package fr.upmc.datacenter.data.interfaces;

public interface PhysicalResourcesProviderPortsDataI extends PortsDataI {

	String getPhysicalResourcesProviderManagementInboundPort();
	String getPhysicalResourcesProviderRequestingInboundPort();
	String getPhysicalResourcesProviderServicesInboundPort();
	
}
