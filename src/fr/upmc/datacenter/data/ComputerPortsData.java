package fr.upmc.datacenter.data;

import java.util.List;

import fr.upmc.datacenter.data.interfaces.ComputerPortsDataI;
import fr.upmc.datacenter.hardware.computer.extended.Computer;

/**
 * Données de ports d'entrée du {@link Computer}
 * 
 * @author Daniel RADEAU
 *
 */

public class ComputerPortsData 
extends 	AbstractData
implements	ComputerPortsDataI
{

	public ComputerPortsData(
			String uri, 
			String computerServicesInboundPortURI,
			String computerStaticStateInboundPortURI,
			String computerDynamicStateInboundPortURI,
			String computerCoreReleasingInboundPortURI) {
		super(uri);
		addInboundPort(computerServicesInboundPortURI);
		addInboundPort(computerStaticStateInboundPortURI);
		addInboundPort(computerDynamicStateInboundPortURI);
		addInboundPort(computerCoreReleasingInboundPortURI);
	}
	
	public ComputerPortsData(String uri, List<String> inboundPortsUri, List<String> outboundPortsUri) {
		super(uri, inboundPortsUri, outboundPortsUri);
	}

	public ComputerPortsData(String uri) {
		super(uri);
	}

	@Override
	public String getComputerServicesInboundPort() {
		return getInboundPorts().get(0);
	}

	@Override
	public String getComputerStaticStateDataInboundPort() {
		return getInboundPorts().get(1);
	}

	@Override
	public String getComputerDynamicStateDataInboundPort() {
		return getInboundPorts().get(2);
	}

	@Override
	public String getComputerCoreReleasingInboundPort() {
		return getInboundPorts().get(3);
	}

	
}
