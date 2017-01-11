package fr.upmc.datacenter.providers.resources.logical.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;
import fr.upmc.datacenter.providers.resources.logical.LogicalResourceProvider;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderRequestingI;

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
	public void increaseApplicationVMFrequency(String requesterUri, AllocatedApplicationVM avm) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		lrp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				lrp.increaseApplicationVMFrequency(requesterUri, avm);
				return null;
			}

		});
	}

	@Override
	public void decreaseApplicationVMFrequency(String requesterUri, AllocatedApplicationVM avm) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		lrp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				lrp.decreaseApplicationVMFrequency(requesterUri, avm);
				return null;
			}

		});
	}

	@Override
	public void increaseApplicationVMCores(String requesterUri, AllocatedApplicationVM avm,
			Integer coreCount) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		lrp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				lrp.increaseApplicationVMCores(requesterUri, avm, coreCount);
				return null;
			}

		});
	}

	@Override
	public void decreaseApplicationVMCores(String requesterUri, AllocatedApplicationVM avm,
			Integer coreCount) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		lrp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				lrp.decreaseApplicationVMCores(requesterUri, avm, coreCount);
				return null;
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
	public void releaseApplicationVMs(String requesterUri, AllocatedApplicationVM[] avms) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		lrp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				lrp.releaseApplicationVMs(requesterUri, avms);
				return null;
			}

		});
	}
	
}
