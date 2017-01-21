package fr.upmc.datacenter.data;

import java.util.List;

import fr.upmc.datacenter.data.interfaces.PerformanceControllerPortsDataI;

public class PerformanceControllerPortsData
extends AbstractData
implements PerformanceControllerPortsDataI {

	public PerformanceControllerPortsData(
			String uri,
			String pcmipURI,
			String pcsipURI,
			String pccrnipURI)
	{
		super(uri);
		addInboundPort(pcmipURI);
		addInboundPort(pcsipURI);
		addInboundPort(pccrnipURI);
	}

	public PerformanceControllerPortsData(String uri, List<String> inboundPortsUri, List<String> outboundPortsUri) {
		super(uri, inboundPortsUri, outboundPortsUri);
	}

	public PerformanceControllerPortsData(String uri) {
		super(uri);
	}

	@Override
	public String getPerformanceControllerManagementInboundPortURI() {
		return getInboundPorts().get(0);
	}

	@Override
	public String getPerformanceControllerServicesInboundPortURI() {
		return getInboundPorts().get(1);
	}

	@Override
	public String getPerformanceControllerCoreReleasingNotificationInboundPortURI() {
		return getInboundPorts().get(2);
	}

}
