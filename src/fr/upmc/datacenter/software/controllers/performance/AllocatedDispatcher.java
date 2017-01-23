package fr.upmc.datacenter.software.controllers.performance;

import java.io.Serializable;

/**
 * Jeton d'allocation du répartiteur de requêtes
 * 
 * @author Daniel RADEAU
 *
 */

public class AllocatedDispatcher implements Serializable {

	private static final long serialVersionUID = 4005788762743118140L;
	
	public String 	dspURI,
					dspmipURI,
					avmrnopURI,
					dspdsdipURI;
	
	public String	dsprsipURI,
					dsprnipURI;
	
	public AllocatedDispatcher(
			String dspURI,
			String dspmipURI,
			String avmrnop,
			String dspdsdip) 
	{
		this.dspURI = dspURI;
		this.dspmipURI = dspmipURI;
		this.avmrnopURI = avmrnop;
		this.dspdsdipURI = dspdsdip;
		
		dsprsipURI = null;
		dsprnipURI = null;
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( !(obj instanceof AllocatedDispatcher) )
			return false;
		AllocatedDispatcher adsp = (AllocatedDispatcher) obj;
		return	dspURI.equals(adsp.dspURI) &&
				dspmipURI.equals(adsp.dspmipURI) &&
				avmrnopURI.equals(adsp.avmrnopURI) &&
				dspdsdipURI.equals(adsp.dspdsdipURI);
	}

}
