package fr.upmc.datacenter.providers.resources.logical.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;
import fr.upmc.datacenter.providers.resources.logical.LogicalResourceProvider;
import fr.upmc.datacenter.providers.resources.logical.interfaces.LogicalResourcesProviderServicesI;
import fr.upmc.datacenter.software.controllers.performance.AllocatedDispatcher;

public class LogicalResourcesProviderServicesInboundPort 
extends AbstractInboundPort
implements LogicalResourcesProviderServicesI
{
	private static final long serialVersionUID = 6084330090111770879L;

	public LogicalResourcesProviderServicesInboundPort(Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(implementedInterface, owner);
	}

	public LogicalResourcesProviderServicesInboundPort(String uri, Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedInterface, owner);
	}

	@Override
	public void increaseApplicationVMFrequency(AllocatedApplicationVM avm) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		lrp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				lrp.increaseApplicationVMFrequency(avm);
				return null;
			}

		});
	}

	@Override
	public void decreaseApplicationVMFrequency(AllocatedApplicationVM avm) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		lrp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				lrp.decreaseApplicationVMFrequency(avm);
				return null;
			}

		});
	}

	@Override
	public void increaseApplicationVMCores(AllocatedApplicationVM avm, Integer coreCount) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		lrp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				lrp.increaseApplicationVMCores(avm, coreCount);
				return null;
			}

		});
	}

	@Override
	public void decreaseApplicationVMCores(AllocatedApplicationVM avm, Integer coreCount) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		lrp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				lrp.decreaseApplicationVMCores(avm, coreCount);
				return null;
			}

		});
	}

	@Override
	public AllocatedApplicationVM[] allocateApplicationVMs(Integer avmCount) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		return lrp.handleRequestSync(new ComponentService<AllocatedApplicationVM[]>() {

			@Override
			public AllocatedApplicationVM[] call() throws Exception {
				return lrp.allocateApplicationVMs(avmCount);
			}

		});
	}

	@Override
	public void releaseApplicationVMs(AllocatedApplicationVM[] avms) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		lrp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				lrp.releaseApplicationVMs(avms);
				return null;
			}

		});
	}
	
	@Override
	public void connectApplicationVM(AllocatedApplicationVM aavm, AllocatedDispatcher adsp) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		lrp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				lrp.connectApplicationVM(aavm, adsp);
				return null;
			}

		});
	}
	
	@Override
	public void disconnectApplicationVM(AllocatedApplicationVM aavm, AllocatedDispatcher adsp) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		lrp.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				lrp.disconnectApplicationVM(aavm, adsp);
				return null;
			}

		});
	}
}
