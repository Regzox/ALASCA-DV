package fr.upmc.datacenter.software.applicationvm.extended.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.datacenter.software.applicationvm.extended.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.extended.interfaces.ApplicationVMManagementI;

public class ApplicationVMManagementInboundPort 
	extends fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementInboundPort
	implements ApplicationVMManagementI
{
	private static final long serialVersionUID = -8110360172314379818L;

	public ApplicationVMManagementInboundPort(ComponentI owner) throws Exception {
		super(owner);
	}

	public ApplicationVMManagementInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, owner);
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
