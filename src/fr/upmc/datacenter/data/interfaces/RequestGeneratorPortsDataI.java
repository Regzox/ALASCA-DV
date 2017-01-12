package fr.upmc.datacenter.data.interfaces;

public interface RequestGeneratorPortsDataI extends PortsDataI {
	
	String getRequestGeneratorManagementInboundPort();
	String getRequestNotificationInboundPort();

}
