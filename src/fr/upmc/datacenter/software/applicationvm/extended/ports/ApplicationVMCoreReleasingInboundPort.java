package fr.upmc.datacenter.software.applicationvm.extended.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.software.applicationvm.extended.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.extended.interfaces.ApplicationVMCoreReleasingI;

public class ApplicationVMCoreReleasingInboundPort 
	extends
		AbstractInboundPort
	implements 
		ApplicationVMCoreReleasingI 
{
	
	private static final long serialVersionUID = -8110360172314379818L;

	public ApplicationVMCoreReleasingInboundPort(Class<?> implementedInterface, ComponentI owner) throws Exception {
		super(implementedInterface, owner);
	}

	public ApplicationVMCoreReleasingInboundPort(String uri, Class<?> implementedInterface, ComponentI owner) throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public void releaseCore() throws Exception {
		final ApplicationVM avm = (ApplicationVM) this.owner;

		avm.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				avm.releaseCore();
				return null;
			}


		});
	}

	@Override
	public void releaseCores(int cores) throws Exception {
		final ApplicationVM avm = (ApplicationVM) this.owner;

		avm.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				avm.releaseCores(cores);
				return null;
			}


		});
	}

	@Override
	public void releaseMaximumCores() throws Exception {
		final ApplicationVM avm = (ApplicationVM) this.owner;

		avm.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				avm.releaseMaximumCores();
				return null;
			}


		});
	}

}
