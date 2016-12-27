package fr.upmc.external.software.applications.webserver.interfaces;

import fr.upmc.datacenter.software.interfaces.RequestI;

public interface HttpRequestI extends RequestI {

	void setUrl(String url);
	String getUrl();
	
}
