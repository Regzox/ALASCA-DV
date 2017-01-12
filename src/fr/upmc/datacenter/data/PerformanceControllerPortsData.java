package fr.upmc.datacenter.data;

import java.util.List;

import fr.upmc.datacenter.data.interfaces.PerformanceControllerPortsDataI;

public class PerformanceControllerPortsData
extends AbstractData
implements PerformanceControllerPortsDataI {

	public PerformanceControllerPortsData(String uri, List<String> inboundPortsUri, List<String> outboundPortsUri) {
		super(uri, inboundPortsUri, outboundPortsUri);
		// TODO Auto-generated constructor stub
	}

	public PerformanceControllerPortsData(String uri) {
		super(uri);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getUri() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getInboundPorts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getOutboundPorts() {
		// TODO Auto-generated method stub
		return null;
	}

}
