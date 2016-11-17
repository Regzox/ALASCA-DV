package fr.upmc.datacenter.software.dispatcher.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.software.dispatcher.Dispatcher;
import fr.upmc.datacenter.software.dispatcher.interfaces.DispatcherManagementI;

public	class DispatcherManagementInboundPort 
		extends 
			AbstractInboundPort
		implements 
			DispatcherManagementI 
{

	private static final long serialVersionUID = 745691983373025792L;
	
	public DispatcherManagementInboundPort(Class<?> implementedInterface, ComponentI owner) throws Exception {
		super(implementedInterface, owner);
	}
	
	public DispatcherManagementInboundPort(String uri, Class<?> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedInterface, owner);
	}
	
	@Override
	public String connectToRequestGenerator(final String rnipURI) throws Exception {
		final Dispatcher dispatcher = (Dispatcher) this.owner;
		
		return dispatcher.handleRequestSync(new ComponentService<String>() {

			@Override
			public String call() throws Exception {
				// TODO Auto-generated method stub
				return dispatcher.connectToRequestGenerator(rnipURI);
			}
			
		});
	}

	@Override
	public void disconnectFromRequestGenerator() throws Exception {
		final Dispatcher dispatcher = (Dispatcher) this.owner;
		
		dispatcher.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				dispatcher.disconnectFromRequestGenerator();
				return null;
			}
			
		});
	}

	@Override
	public String connectToApplicationVM(final String rsipURI) throws Exception {
		final Dispatcher dispatcher = (Dispatcher) this.owner;
		
		return dispatcher.handleRequestSync(new ComponentService<String>() {

			@Override
			public String call() throws Exception {
				// TODO Auto-generated method stub
				return dispatcher.connectToApplicationVM(rsipURI);
			}
			
		});
	}

	@Override
	public void disconnectFromApplicationVM() throws Exception {
		final Dispatcher dispatcher = (Dispatcher) this.owner;
		
		dispatcher.handleRequestSync(new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				dispatcher.disconnectFromApplicationVM();
				return null;
			}
			
		});
	}
	
	
	
}
