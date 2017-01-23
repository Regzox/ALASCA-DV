package fr.upmc.datacenter.data;

import fr.upmc.datacenter.data.interfaces.RequestGeneratorPortsDataI;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;

/**
 * Données de ports d'entrée du {@link RequestGenerator}
 *  
 * @author Daniel RADEAU
 *
 */

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
