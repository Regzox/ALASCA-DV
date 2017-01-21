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
	public Integer[] increaseApplicationVMFrequency(AllocatedApplicationVM avm) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		return lrp.handleRequestSync(new ComponentService<Integer[]>() {

			@Override
			public Integer[] call() throws Exception {
				return lrp.increaseApplicationVMFrequency(avm);
			}

		});
	}

	@Override
	public Integer[] decreaseApplicationVMFrequency(AllocatedApplicationVM avm) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		return lrp.handleRequestSync(new ComponentService<Integer[]>() {

			@Override
			public Integer[] call() throws Exception {
				return lrp.decreaseApplicationVMFrequency(avm);
			}

		});
	}

	@Override
	public Integer increaseApplicationVMCores(AllocatedApplicationVM avm, Integer coreCount) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		return lrp.handleRequestSync(new ComponentService<Integer>() {

			@Override
			public Integer call() throws Exception {
				return lrp.increaseApplicationVMCores(avm, coreCount);
			}

		});
	}

	@Override
	public Integer decreaseApplicationVMCores(AllocatedApplicationVM avm, Integer coreCount) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		return lrp.handleRequestSync(new ComponentService<Integer>() {

			@Override
			public Integer call() throws Exception {
				return lrp.decreaseApplicationVMCores(avm, coreCount);
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
	public AllocatedApplicationVM[] releaseApplicationVMs(AllocatedApplicationVM[] avms) throws Exception {
		final LogicalResourceProvider lrp = (LogicalResourceProvider) this.owner;

		return lrp.handleRequestSync(new ComponentService<AllocatedApplicationVM[]>() {

			@Override
			public AllocatedApplicationVM[] call() throws Exception {
				return lrp.releaseApplicationVMs(avms);
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
