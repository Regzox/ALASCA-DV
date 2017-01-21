package fr.upmc.datacenter.providers.resources.logical.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.data.interfaces.LogicalResourcesProviderPortsDataI;
import fr.upmc.datacenter.data.interfaces.PerformanceControllerPortsDataI;
import fr.upmc.datacenter.data.interfaces.PhysicalResourcesProviderPortsDataI;
import fr.upmc.datacenter.providers.resources.logical.LogicalResourceProvider;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderManagementI;

public class LogicalResourcesProviderManagementInboundPort 
extends AbstractInboundPort
implements LogicalResourcesProviderManagementI
{
	private static final long serialVersionUID = 6256921545850006086L;

	public LogicalResourcesProviderManagementInboundPort(Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(implementedInterface, owner);
	}

	public LogicalResourcesProviderManagementInboundPort(String uri, Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public void connectPhysicalResourcesProvider(PhysicalResourcesProviderPortsDataI prppdi) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		lrp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				lrp.connectPhysicalResourcesProvider(prppdi);
				return null;
			}

		});
	}

	@Override
	public void disconnectPhysicalResourcesProvider(PhysicalResourcesProviderPortsDataI prppdi) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		lrp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				lrp.disconnectPhysicalResourcesProvider(prppdi);
				return null;
			}

		});
	}

	@Override
	public void connectLogicalResourcesProvider(LogicalResourcesProviderPortsDataI lrppdi) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		lrp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				lrp.connectLogicalResourcesProvider(lrppdi);
				return null;
			}

		});
	}

	@Override
	public void disconnectLogicalResourcesProvider(LogicalResourcesProviderPortsDataI lrppdi) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		lrp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				lrp.disconnectLogicalResourcesProvider(lrppdi);
				return null;
			}

		});
	}

	@Override
	public void connectPerformanceController(PerformanceControllerPortsDataI pcpdi) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		lrp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				lrp.connectPerformanceController(pcpdi);
				return null;
			}

		});
	}

	@Override
	public void disconnectPerformanceController(PerformanceControllerPortsDataI pcpdi) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		lrp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				lrp.disconnectPerformanceController(pcpdi);
				return null;
			}

		});
	}

	@Override
	public void connectLogicalResourcesProviderNotifyBack(LogicalResourcesProviderPortsDataI lrppdi) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		lrp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				lrp.connectLogicalResourcesProviderNotifyBack(lrppdi);
				return null;
			}

		});
	}

	@Override
	public void disconnectLogicalResourcesProviderNotifyBack(LogicalResourcesProviderPortsDataI lrppdi)
			throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		lrp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				lrp.disconnectLogicalResourcesProviderNotifyBack(lrppdi);
				return null;
			}

		});
	}
	
	
	
}
