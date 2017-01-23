package fr.upmc.datacenter.providers.resources.logical;

import java.io.Serializable;

/**
 * Jeton d'allocation de machine virtuelle
 * 
 * @author Daniel RADEAU
 *
 */

public class AllocatedApplicationVM implements Serializable {

	private static final long serialVersionUID = 6067423750124007482L;

	public final String	avmURI,
						avmmipURI,
						avmrsipURI,
						avmrnopURI,
						avmcripURI,
						avmcrnopURI;
	
	public final String	lrpURI;
	
	public AllocatedApplicationVM(
			String avmURI, 
			String lrpURI,
			String avmmipURI,
			String avmrsipURI,
			String avmrnopURI,
			String avmcripURI,
			String avmcrnopURI) {
		assert avmURI != null;
		assert lrpURI != null;
		assert avmmipURI != null;
		assert avmrsipURI != null;
		assert avmrnopURI != null;
		assert avmcripURI != null;
		assert avmcrnopURI != null;
		
		this.avmURI = avmURI;
		this.lrpURI = lrpURI;
		this.avmmipURI = avmmipURI;
		this.avmrsipURI = avmrsipURI;
		this.avmrnopURI = avmrnopURI;
		this.avmcripURI = avmcripURI;
		this.avmcrnopURI = avmcrnopURI;
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( !(obj instanceof AllocatedApplicationVM) )
			return false;
		AllocatedApplicationVM aavm = (AllocatedApplicationVM) obj;
		return	avmURI.equals(aavm.avmURI) &&
				lrpURI.equals(aavm.lrpURI) &&
				avmmipURI.equals(aavm.avmmipURI) &&
				avmrsipURI.equals(aavm.avmrsipURI) &&
				avmrnopURI.equals(aavm.avmrnopURI) &&
				avmcripURI.equals(aavm.avmcripURI) &&
				avmcrnopURI.equals(aavm.avmcrnopURI);
	}	
}
