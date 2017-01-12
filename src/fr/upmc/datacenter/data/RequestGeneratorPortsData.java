package fr.upmc.datacenter.data;

import fr.upmc.datacenter.data.interfaces.RequestGeneratorPortsDataI;

public class RequestGeneratorPortsData extends AbstractData implements RequestGeneratorPortsDataI {

	public RequestGeneratorPortsData(
			String uri,
			String requestGeneratorManagementInboundPort,
			String requestNotificationInboundPortURI) {
		super(uri);
		inboundPortsUri.add(requestGeneratorManagementInboundPort);
		inboundPortsUri.add(requestNotificationInboundPortURI);
	}
		
	@Override
	public String getRequestGeneratorManagementInboundPort() {
		return inboundPortsUri.get(0);
	}

	@Override
	public String getRequestNotificationInboundPort() {
		return inboundPortsUri.get(1);
	}

}
