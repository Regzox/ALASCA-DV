package fr.upmc.datacenter.data.interfaces;

public interface PerformanceControllerPortsDataI extends PortsDataI {

	String getPerformanceControllerManagementInboundPortURI();
	String getPerformanceControllerServicesInboundPortURI();
	String getPerformanceControllerCoreReleasingNotificationInboundPortURI();
	
}
