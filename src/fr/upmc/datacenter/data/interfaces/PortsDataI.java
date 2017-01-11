package fr.upmc.datacenter.data.interfaces;

import java.util.List;

public interface PortsDataI {

	String getUri();
	List<String> getInboundPorts();
	List<String> getOutboundPorts();
}
