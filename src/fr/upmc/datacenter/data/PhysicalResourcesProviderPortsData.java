package fr.upmc.datacenter.data;

import java.util.List;

import fr.upmc.datacenter.data.interfaces.PhysicalResourcesProviderPortsDataI;

public class PhysicalResourcesProviderPortsData
extends		AbstractData
implements	PhysicalResourcesProviderPortsDataI
{
	public PhysicalResourcesProviderPortsData(
			String uri,
			String physicalResourceProviderManagementInboundPortURI,
			String physicalResourceProviderRequestingInboundPortURI,
			String physicalResourceProviderServicesInboundPortURI) {
		super(uri);
		addInboundPort(physicalResourceProviderManagementInboundPortURI);
		addInboundPort(physicalResourceProviderRequestingInboundPortURI);
		addInboundPort(physicalResourceProviderServicesInboundPortURI);
	}

	public PhysicalResourcesProviderPortsData(String uri, List<String> inboundPortsUri, List<String> outboundPortsUri) {
		super(uri, inboundPortsUri, outboundPortsUri);
	}

	public PhysicalResourcesProviderPortsData(String uri) {
		super(uri);
	}

	@Override
	public String getPhysicalResourcesProviderManagementInboundPort() {
		return getInboundPorts().get(0);
	}

	@Override
	public String getPhysicalResourcesProviderRequestingInboundPort() {
		return getInboundPorts().get(1);
	}

	@Override
	public String getPhysicalResourcesProviderServicesInboundPort() {
		return getInboundPorts().get(2);
	}

	
}
