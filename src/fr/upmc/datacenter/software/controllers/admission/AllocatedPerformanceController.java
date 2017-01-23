package fr.upmc.datacenter.software.controllers.admission;

import java.io.Serializable;

/**
 * Contrôleur de performance alloué.
 * 
 * Monnaie de transcation du contrôleur d'admission
 * 
 * @author Daniel RADEAU
 *
 */

public class AllocatedPerformanceController implements Serializable {

	private static final long serialVersionUID = -7523229239609489564L;

	public final String uri,
						pcmipURI,
						pcsipURI,
						pccrnipURI;
	
	public AllocatedPerformanceController(
			String uri,
			String pcmipURI,
			String pcsipURI,
			String pccrnipURI) throws Exception
	{
		this.uri = uri;
		this.pcmipURI = pcmipURI;
		this.pcsipURI = pcsipURI;
		this.pccrnipURI = pccrnipURI;
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( !(obj instanceof AllocatedPerformanceController) )
			return false;
		return	this.uri.equals( ((AllocatedPerformanceController) obj).uri ) &
				this.pcmipURI.equals( ((AllocatedPerformanceController) obj).pcmipURI ) &
				this.pcsipURI.equals( ((AllocatedPerformanceController) obj).pcsipURI ) &
				this.pccrnipURI.equals( ((AllocatedPerformanceController) obj).pccrnipURI );
	}
	
}
