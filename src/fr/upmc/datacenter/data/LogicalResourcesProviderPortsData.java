package fr.upmc.datacenter.data;

import java.util.List;

import fr.upmc.datacenter.data.interfaces.LogicalResourcesProviderPortsDataI;
import fr.upmc.datacenter.providers.resources.logical.LogicalResourceProvider;

/**
 * Données de ports d'entrée du {@link LogicalResourceProvider}
 * 
 * @author Daniel RADEAU
 *
 */

public class LogicalResourcesProviderPortsData 
extends AbstractData
implements LogicalResourcesProviderPortsDataI
{
	
	
	public LogicalResourcesProviderPortsData(
			String uri,
			String lrpmipURI,
			String lrpripURI,
			String lrpsipURI,
			String lrpcrnbipURI)
	{
		super(uri);
		addInboundPort(lrpmipURI);
		addInboundPort(lrpripURI);
		addInboundPort(lrpsipURI);
		addInboundPort(lrpcrnbipURI);
	}
	
	public LogicalResourcesProviderPortsData(String uri, List<String> inboundPortsUri, List<String> outboundPortsUri) {
		super(uri, inboundPortsUri, outboundPortsUri);
	}

	public LogicalResourcesProviderPortsData(String uri) {
		super(uri);
	}

	@Override
	public String getLogicalResourcesProviderManagementInboundPort() {
		return inboundPortsUri.get(0);
	}

	@Override
	public String getLogicalResourcesProviderRequestingInboundPort() {
		return inboundPortsUri.get(1);
	}

	@Override
	public String getLogicalResourcesProviderServicesInboundPort() {
		return inboundPortsUri.get(2);
	}

	@Override
	public String getLogicalResourcesProviderCoreReleasingNotifyBackInboundPort() {
		return inboundPortsUri.get(3);
	}
	
}
