package fr.upmc.datacenter.software.dispatcher.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.interfaces.DataOfferedI.DataI;
import fr.upmc.datacenter.ports.AbstractControlledDataInboundPort;
import fr.upmc.datacenter.software.dispatcher.Dispatcher;

public class DispatcherDynamicStateDataInboundPort
	extends
		AbstractControlledDataInboundPort
{
	private static final long serialVersionUID = 1875149060478375570L;

	public DispatcherDynamicStateDataInboundPort(ComponentI owner) throws Exception {
		super(owner);
	}

	public DispatcherDynamicStateDataInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, owner);
	}

	@Override
	public DataI get() throws Exception {
		final Dispatcher dsp = (Dispatcher) this.owner ;
		return dsp.handleRequestSync(
					new ComponentI.ComponentService<DataOfferedI.DataI>() {
						@Override
						public DataOfferedI.DataI call() throws Exception {
							return dsp.getDynamicState() ;
						}
					});
	}

}
