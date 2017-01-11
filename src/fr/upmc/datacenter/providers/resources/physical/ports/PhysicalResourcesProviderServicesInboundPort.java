package fr.upmc.datacenter.providers.resources.physical.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.providers.resources.physical.PhysicalResourcesProvider;
import fr.upmc.datacenter.providers.resources.physical.interfaces.PhysicalResourcesProviderServicesI;

public class PhysicalResourcesProviderServicesInboundPort
extends		AbstractInboundPort
implements	PhysicalResourcesProviderServicesI
{
	private static final long serialVersionUID = 8655498056124585757L;

	public PhysicalResourcesProviderServicesInboundPort(Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(implementedInterface, owner);
	}

	public PhysicalResourcesProviderServicesInboundPort(String uri, Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public void increaseCoreFrenquency(AllocatedCore ac) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		prp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				prp.increaseCoreFrenquency(ac);
				return null;
			}

		});
	}

	@Override
	public void decreaseCoreFrenquency(AllocatedCore ac) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		prp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				prp.decreaseCoreFrenquency(ac);
				return null;
			}

		});
	}

	@Override
	public void increaseProcessorFrenquency(AllocatedCore ac) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		prp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				prp.increaseProcessorFrenquency(ac);
				return null;
			}

		});
	}

	@Override
	public void decreaseProcessorFrenquency(AllocatedCore ac) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		prp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				prp.decreaseProcessorFrenquency(ac);
				return null;
			}

		});
	}

	@Override
	public void increaseComputerFrenquency(AllocatedCore ac) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		prp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				prp.increaseComputerFrenquency(ac);
				return null;
			}

		});
	}

	@Override
	public void decreaseComputerFrenquency(AllocatedCore ac) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		prp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				prp.decreaseComputerFrenquency(ac);
				return null;
			}

		});
	}

	@Override
	public AllocatedCore[] allocateCores(Integer cores) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		return prp.handleRequestSync(new ComponentService<AllocatedCore[]>() {

			@Override
			public AllocatedCore[] call() throws Exception {
				return prp.allocateCores(cores);
			}

		});
	}

	@Override
	public AllocatedCore[] allocateCores(AllocatedCore[] acs, Integer cores) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		return prp.handleRequestSync(new ComponentService<AllocatedCore[]>() {

			@Override
			public AllocatedCore[] call() throws Exception {
				return prp.allocateCores(acs, cores);
			}

		});
	}

	@Override
	public void releaseCores(AllocatedCore[] allocatedCores) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		prp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				prp.releaseCores(allocatedCores);
				return null;
			}

		});
	}
	
}
