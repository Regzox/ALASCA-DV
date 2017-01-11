package fr.upmc.datacenter.providers.resources.physical.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.providers.resources.physical.PhysicalResourcesProvider;
import fr.upmc.datacenter.providers.resources.physical.interfaces.PhysicalResourcesProviderRequestingI;

public class PhysicalResourcesProviderRequestingInboundPort
extends 	AbstractInboundPort
implements	PhysicalResourcesProviderRequestingI
{
	private static final long serialVersionUID = -3897898450150359684L;

	public PhysicalResourcesProviderRequestingInboundPort(Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(implementedInterface, owner);
	}

	public PhysicalResourcesProviderRequestingInboundPort(String uri, Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public boolean isLocal(Object o) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		return prp.handleRequestSync(new ComponentService<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				return prp.isLocal(o);
			}

		});
	}

	@Override
	public void increaseCoreFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		prp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				prp.increaseCoreFrenquency(requesterUri, ac);
				return null;
			}

		});
	}

	@Override
	public void decreaseCoreFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		prp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				prp.decreaseCoreFrenquency(requesterUri, ac);
				return null;
			}

		});
	}

	@Override
	public void increaseProcessorFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		prp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				prp.increaseProcessorFrenquency(requesterUri, ac);
				return null;
			}

		});
	}

	@Override
	public void decreaseProcessorFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		prp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				prp.decreaseProcessorFrenquency(requesterUri, ac);
				return null;
			}

		});
	}

	@Override
	public void increaseComputerFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		prp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				prp.increaseComputerFrenquency(requesterUri, ac);
				return null;
			}

		});
	}

	@Override
	public void decreaseComputerFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		prp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				prp.decreaseComputerFrenquency(requesterUri, ac);
				return null;
			}

		});
	}

	@Override
	public AllocatedCore[] allocateCores(String requesterUri, AllocatedCore[] acs, Integer cores) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		return prp.handleRequestSync(new ComponentService<AllocatedCore[]>() {

			@Override
			public AllocatedCore[] call() throws Exception {
				return prp.allocateCores(requesterUri, acs, cores);
			}

		});
	}

	@Override
	public void releaseCores(String requesterUri, AllocatedCore[] allocatedCores) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		prp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				prp.releaseCores(requesterUri, allocatedCores);
				return null;
			}

		});
	}

}
