package fr.upmc.datacenter.providers.resources.logical.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;
import fr.upmc.datacenter.providers.resources.logical.LogicalResourceProvider;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderRequestingI;
import fr.upmc.datacenter.software.controllers.performance.AllocatedDispatcher;

public class LogicalResourcesProviderRequestingInboundPort 
extends AbstractInboundPort
implements LogicalResourcesProviderRequestingI
{
	private static final long serialVersionUID = -4128228432557490802L;

	public LogicalResourcesProviderRequestingInboundPort(Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(implementedInterface, owner);
	}

	public LogicalResourcesProviderRequestingInboundPort(String uri, Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public boolean isLocal(Object o) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		return lrp.handleRequestSync(new ComponentService<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				return lrp.isLocal(o);
			}

		});
	}

	@Override
	public Integer[] increaseApplicationVMFrequency(String requesterUri, AllocatedApplicationVM avm) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		return lrp.handleRequestSync(new ComponentService<Integer[]>() {

			@Override
			public Integer[] call() throws Exception {
				return lrp.increaseApplicationVMFrequency(requesterUri, avm);
			}

		});
	}

	@Override
	public Integer[] decreaseApplicationVMFrequency(String requesterUri, AllocatedApplicationVM avm) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		return lrp.handleRequestSync(new ComponentService<Integer[]>() {

			@Override
			public Integer[] call() throws Exception {
				return lrp.decreaseApplicationVMFrequency(requesterUri, avm);
			}

		});
	}

	@Override
	public Integer increaseApplicationVMCores(String requesterUri, AllocatedApplicationVM avm,
			Integer coreCount) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		return lrp.handleRequestSync(new ComponentService<Integer>() {

			@Override
			public Integer call() throws Exception {
				return lrp.increaseApplicationVMCores(requesterUri, avm, coreCount);
			}

		});
	}

	@Override
	public Integer decreaseApplicationVMCores(String requesterUri, AllocatedApplicationVM avm,
			Integer coreCount) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		return lrp.handleRequestSync(new ComponentService<Integer>() {

			@Override
			public Integer call() throws Exception {
				return lrp.decreaseApplicationVMCores(requesterUri, avm, coreCount);
			}

		});
	}

	@Override
	public AllocatedApplicationVM[] allocateApplicationVMs(String requesterUri, Integer avmCount) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		return lrp.handleRequestSync(new ComponentService<AllocatedApplicationVM[]>() {

			@Override
			public AllocatedApplicationVM[] call() throws Exception {
				return lrp.allocateApplicationVMs(requesterUri, avmCount);
			}

		});
	}

	@Override
	public AllocatedApplicationVM[] releaseApplicationVMs(String requesterUri, AllocatedApplicationVM[] avms) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		return lrp.handleRequestSync(new ComponentService<AllocatedApplicationVM[]>() {

			@Override
			public AllocatedApplicationVM[] call() throws Exception {
				return lrp.releaseApplicationVMs(requesterUri, avms);
			}

		});
	}
	
	@Override
	public void connectApplicationVM(String requesterUri, AllocatedApplicationVM aavm, AllocatedDispatcher adsp)
			throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		lrp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				lrp.connectApplicationVM(requesterUri, aavm, adsp);
				return null;
			}

		});
	}
	
	@Override
	public void disconnectApplicationVM(String requesterUri, AllocatedApplicationVM aavm, AllocatedDispatcher adsp)
			throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		lrp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				lrp.disconnectApplicationVM(requesterUri, aavm, adsp);
				return null;
			}

		});
	}
	
}
