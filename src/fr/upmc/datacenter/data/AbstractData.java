package fr.upmc.datacenter.data;

import java.util.ArrayList;
import java.util.List;

import fr.upmc.datacenter.data.interfaces.PortsDataI;

public abstract class AbstractData
	implements PortsDataI
{
	String uri;
	List<String> inboundPortsUri;
	List<String> outboundPortsUri;
	
	public AbstractData(String uri) {
		this.uri = uri;
		inboundPortsUri = new ArrayList<>();
		outboundPortsUri = new ArrayList<>();
	}
	
	public AbstractData(String uri, List<String> inboundPortsUri, List<String> outboundPortsUri) {
		this.uri = uri;
		this.inboundPortsUri = inboundPortsUri;
		this.outboundPortsUri = outboundPortsUri;
	}
	
	public void addInboundPort(String inboundPortUri) {
		inboundPortsUri.add(inboundPortUri);
	}
	
	public void addOutboundPort(String outboundPortUri) {
		outboundPortsUri.add(outboundPortUri);
	}
	
	@Override
	public String getUri() {
		return uri;
	}

	@Override
	public List<String> getInboundPorts() {
		return inboundPortsUri;
	}

	@Override
	public List<String> getOutboundPorts() {
		return outboundPortsUri;
	}

}
