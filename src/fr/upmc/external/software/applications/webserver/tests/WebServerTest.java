package fr.upmc.external.software.applications.webserver.tests;

import java.lang.reflect.Method;

import fr.upmc.external.software.applications.webserver.WebServer;
import fr.upmc.external.software.applications.webserver.interfaces.WebServerI;

public class WebServerTest {

	public static void main(String[] args) {
		
		WebServer ws;
		try {
			ws = new WebServer("web-server", "web-rsop", "web-rnip");
			ws.evaluateMethodsInstructions(WebServerI.class);
			for (Method key : ws.methodsInstructions.keySet())
				System.out.println(key.getName() + " : " + ws.methodsInstructions.get(key));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
