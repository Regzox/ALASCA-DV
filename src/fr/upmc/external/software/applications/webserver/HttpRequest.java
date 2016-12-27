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
	private static final long serialVersionUID = -6898718684673612789L;
	
	protected String requestURI;
	protected long predictedNumberOfInstructions;
	protected String url;

	public HttpRequest(
			String requestURI,
			long predictedNumberOfInstructions,
			String url) 
	{
		this.requestURI = requestURI;
		this.predictedNumberOfInstructions = predictedNumberOfInstructions;
		this.url = url;
	}
	
	@Override
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public String getRequestURI() {
		return requestURI;
	}

	@Override
	public long getPredictedNumberOfInstructions() {
		return predictedNumberOfInstructions;
	}
	
}
