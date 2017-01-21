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
	public Integer increaseCoreFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		return prp.handleRequestSync(new ComponentService<Integer>() {

			@Override
			public Integer call() throws Exception {
				return prp.increaseCoreFrenquency(requesterUri, ac);
			}

		});
	}

	@Override
	public Integer decreaseCoreFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		return prp.handleRequestSync(new ComponentService<Integer>() {

			@Override
			public Integer call() throws Exception {
				return prp.decreaseCoreFrenquency(requesterUri, ac);
			}

		});
	}

	@Override
	public Integer[] increaseProcessorFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		return prp.handleRequestSync(new ComponentService<Integer[]>() {

			@Override
			public Integer[] call() throws Exception {
				return prp.increaseProcessorFrenquency(requesterUri, ac);
			}

		});
	}

	@Override
	public Integer[] decreaseProcessorFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		return prp.handleRequestSync(new ComponentService<Integer[]>() {

			@Override
			public Integer[] call() throws Exception {
				return prp.decreaseProcessorFrenquency(requesterUri, ac);
			}

		});
	}

	@Override
	public Integer[][] increaseComputerFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		return prp.handleRequestSync(new ComponentService<Integer[][]>() {

			@Override
			public Integer[][] call() throws Exception {
				return prp.increaseComputerFrenquency(requesterUri, ac);
			}

		});
	}

	@Override
	public Integer[][] decreaseComputerFrenquency(String requesterUri, AllocatedCore ac) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		return prp.handleRequestSync(new ComponentService<Integer[][]>() {

			@Override
			public Integer[][] call() throws Exception {
				return prp.decreaseComputerFrenquency(requesterUri, ac);
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
	public AllocatedCore[] releaseCores(String requesterUri, AllocatedCore[] allocatedCores) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		return prp.handleRequestSync(new ComponentService<AllocatedCore[]>() {

			@Override
			public AllocatedCore[] call() throws Exception {
				return prp.releaseCores(requesterUri, allocatedCores);
			}

		});
	}

}
