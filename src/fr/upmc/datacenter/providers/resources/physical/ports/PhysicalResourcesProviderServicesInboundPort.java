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
	public Integer increaseCoreFrenquency(AllocatedCore ac) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		return prp.handleRequestSync(new ComponentService<Integer>() {

			@Override
			public Integer call() throws Exception {
				return prp.increaseCoreFrenquency(ac);
			}

		});
	}

	@Override
	public Integer decreaseCoreFrenquency(AllocatedCore ac) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		return prp.handleRequestSync(new ComponentService<Integer>() {

			@Override
			public Integer call() throws Exception {
				return prp.decreaseCoreFrenquency(ac);
			}

		});
	}

	@Override
	public Integer[] increaseProcessorFrenquency(AllocatedCore ac) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		return prp.handleRequestSync(new ComponentService<Integer[]>() {

			@Override
			public Integer[] call() throws Exception {
				return prp.increaseProcessorFrenquency(ac);
			}

		});
	}

	@Override
	public Integer[] decreaseProcessorFrenquency(AllocatedCore ac) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		return prp.handleRequestSync(new ComponentService<Integer[]>() {

			@Override
			public Integer[] call() throws Exception {
				return prp.decreaseProcessorFrenquency(ac);
			}

		});
	}

	@Override
	public Integer[][] increaseComputerFrenquency(AllocatedCore ac) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		return prp.handleRequestSync(new ComponentService<Integer[][]>() {

			@Override
			public Integer[][] call() throws Exception {
				return prp.increaseComputerFrenquency(ac);
			}

		});
	}

	@Override
	public Integer[][] decreaseComputerFrenquency(AllocatedCore ac) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		return prp.handleRequestSync(new ComponentService<Integer[][]>() {

			@Override
			public Integer[][] call() throws Exception {
				return prp.decreaseComputerFrenquency(ac);
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
	public AllocatedCore[] releaseCores(AllocatedCore[] allocatedCores) throws Exception {
		final PhysicalResourcesProvider prp = (PhysicalResourcesProvider) this.owner;

		return prp.handleRequestSync(new ComponentService<AllocatedCore[]>() {

			@Override
			public AllocatedCore[] call() throws Exception {
				return prp.releaseCores(allocatedCores);
			}

		});
	}
	
}
