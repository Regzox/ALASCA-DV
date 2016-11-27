package fr.upmc.external.software.applications.webserver;

import java.lang.reflect.Method;

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
	
	public void evaluateMethodsInstructions(Class<?> inter) {
		for (Method method : inter.getMethods())
			this.methodsInstructions.put(
					method, 
					(long) (Math.abs(maxInstructions * Math.random()) % maxInstructions)
					);
	}
	
	@Override
	public String getWebPage(HttpRequestI request) {
		return request.getUrl();
	}

	@Override
	public Boolean checkConnection(HttpRequestI request) {
		return true;
	}
	
}
