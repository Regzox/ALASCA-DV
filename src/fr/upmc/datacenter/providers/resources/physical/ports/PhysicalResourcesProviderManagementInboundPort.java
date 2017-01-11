package fr.upmc.datacenter.providers.resources.physical.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.data.interfaces.ComputerPortsDataI;
import fr.upmc.datacenter.data.interfaces.PhysicalResourcesProviderPortsDataI;
import fr.upmc.datacenter.providers.resources.physical.PhysicalResourcesProvider;
import fr.upmc.datacenter.providers.resources.physical.interfaces.PhysicalResourcesProviderManagementI;

public class PhysicalResourcesProviderManagementInboundPort
extends		AbstractInboundPort
implements	PhysicalResourcesProviderManagementI
{
	private static final long serialVersionUID = 6118515367819036083L;

	public PhysicalResourcesProviderManagementInboundPort(Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(implementedInterface, owner);
	}

	public PhysicalResourcesProviderManagementInboundPort(String uri, Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public void connectComputer(ComputerPortsDataI cpd) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		prp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				prp.connectComputer(cpd);
				return null;
			}

		});
	}

	@Override
	public void disconnectComputer(ComputerPortsDataI cpd) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		prp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				prp.disconnectComputer(cpd);
				return null;
			}

		});
	}

	@Override
	public void connectPhysicalResourcesProvider(PhysicalResourcesProviderPortsDataI prppd) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		prp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				prp.connectPhysicalResourcesProvider(prppd);
				return null;
			}

		});
	}

	@Override
	public void disconnectPhysicalResourcesProvider(PhysicalResourcesProviderPortsDataI prppd) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		prp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				prp.disconnectPhysicalResourcesProvider(prppd);
				return null;
			}

		});
	}

}
