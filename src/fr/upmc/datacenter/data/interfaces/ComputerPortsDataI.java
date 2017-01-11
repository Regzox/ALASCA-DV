package fr.upmc.datacenter.data.interfaces;

public interface ComputerPortsDataI extends PortsDataI {
	
	String getComputerServicesInboundPort();
	String getComputerStaticStateDataInboundPort();
	String getComputerDynamicStateDataInboundPort();
	String getComputerCoreReleasingInboundPort();
	
}
