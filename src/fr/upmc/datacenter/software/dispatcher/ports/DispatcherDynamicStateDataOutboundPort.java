package fr.upmc.datacenter.software.dispatcher.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataRequiredI.DataI;
import fr.upmc.datacenter.ports.AbstractControlledDataOutboundPort;
import fr.upmc.datacenter.software.dispatcher.interfaces.DispatcherDynamicStateDataConsumerI;
import fr.upmc.datacenter.software.dispatcher.interfaces.DispatcherDynamicStateI;

public class DispatcherDynamicStateDataOutboundPort
	extends
		AbstractControlledDataOutboundPort
{
	private static final long serialVersionUID = 1058126101658571701L;

	public DispatcherDynamicStateDataOutboundPort(ComponentI owner) throws Exception {
		super(owner);
	}

	public DispatcherDynamicStateDataOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, owner);
	}

	@Override
	public void receive(DataI d) throws Exception {
		((DispatcherDynamicStateDataConsumerI)this.owner).acceptDispatcherDynamicStateData((DispatcherDynamicStateI) d) ;
	}

}
