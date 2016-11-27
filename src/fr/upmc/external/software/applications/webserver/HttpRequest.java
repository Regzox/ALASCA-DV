package fr.upmc.external.software.applications.webserver;

import fr.upmc.external.software.applications.webserver.interfaces.HttpRequestI;

/**
 * Requête http pouvant être faites au {@link WebServer}.
 * 
 * @author Daniel RADEAU
 *
 */

public class HttpRequest 
	implements HttpRequestI 
{

	protected String url;

	@Override
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String getUrl() {
		return url;
	}
	
}
