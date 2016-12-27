package fr.upmc.external.software.applications.webserver;

import java.lang.reflect.Method;

import fr.upmc.datacenter.software.interfaces.RequestNotificationI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;
import fr.upmc.datacenter.software.ports.RequestNotificationInboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.external.software.applications.AbstractApplication;
import fr.upmc.external.software.applications.webserver.interfaces.HttpRequestI;
import fr.upmc.external.software.applications.webserver.interfaces.WebServerI;

/**
 * Application simulant un serveur web très simple.
 * 
 * 
 * 
 * @author Daniel RADEAU
 *
 */

public class WebServer 
	extends 
		AbstractApplication
	implements
		WebServerI
		
{
	public RequestSubmissionOutboundPort rsop;
	public RequestNotificationInboundPort rnip;
	
	public WebServer(String uri, String rsopURI, String rnipURI) throws Exception {
		super(1, 1);
		
		this.addRequiredInterface(RequestSubmissionI.class);
		rsop = new RequestSubmissionOutboundPort("web-server-rsop", this);
		addPort(rsop);
		rsop.publishPort();
		
		this.addOfferedInterface(RequestNotificationI.class);
		rnip = new RequestNotificationInboundPort("web-server-rnip", this);
		addPort(rnip);
		rnip.publishPort();
		
		for (Class<?> inter : this.getClass().getInterfaces()) {
			evaluateMethodsInstructions(inter);
		}
	}
	
	public void evaluateMethodsInstructions(Class<?> inter) {
		for (Method method : inter.getMethods())
			this.methodsInstructions.put(
					method, 
					(long) (Math.abs(maxInstructions * Math.random()) % maxInstructions)
					);
	}
	
	@Override
	public String getWebPage(HttpRequestI request) throws Exception {
		rsop.submitRequestAndNotify(request);
		return request.getUrl();
	}

	@Override
	public Boolean checkConnection(HttpRequestI request) throws Exception {
		rsop.submitRequestAndNotify(request);
		return true;
	}
	
}
