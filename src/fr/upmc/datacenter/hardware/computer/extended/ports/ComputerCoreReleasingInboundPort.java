package fr.upmc.datacenter.hardware.computer.extended.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.hardware.computer.extended.Computer;
import fr.upmc.datacenter.hardware.computer.extended.interfaces.ComputerCoreReleasingI;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

public class ComputerCoreReleasingInboundPort
	extends
		AbstractInboundPort
	implements
		ComputerCoreReleasingI
{
	private static final long serialVersionUID = -7327303835874268268L;

	public ComputerCoreReleasingInboundPort(Class<?> implementedInterface, ComponentI owner) throws Exception {
		super(implementedInterface, owner);
	}

	public ComputerCoreReleasingInboundPort(String uri, Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public void releaseCore(AllocatedCore allocatedCore) throws Exception {
		final Computer cpt = (Computer) this.owner;

		cpt.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				cpt.releaseCore(allocatedCore);
				return null;
			}

		});
	}

	@Override
	public void releaseCores(AllocatedCore[] allocatedCores) throws Exception {
		final Computer cpt = (Computer) this.owner;

		cpt.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				cpt.releaseCores(allocatedCores);
				return null;
			}

		});
	}
	
	
}
