package fr.upmc.datacenter.software.controllers.performance;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;
import fr.upmc.datacenter.software.controllers.performance.AllocatedDispatcherDataStore.DataType;

/**
 * Données d'un ensemble de répartiteur de requêtes. </br></br>
 * Il comprend pour chaque répartiteur de requêtes : </br>
 * &nbsp - Une map de clef {@link DataType} pour les générateurs de requêtes.
 * &nbsp - Une map de clef {@link DataType} pour les machines virtuelles. </br></br>
 * 
 * MODE MONODISPATCHER
 * 
 * @author Daniel RADEAU
 *
 */

public class AllocatedDispatcherDataStore extends HashMap<AllocatedDispatcher, Map<DataType, Object>> {
	
	public AllocatedDispatcher adsp = null;
	
	public enum DataType {
		ALLOCATED_REQUEST_GENERATOR,
		ALLOCATED_APPLICATION_VM
	}
	
	private static final long serialVersionUID = -7931889640820516332L;
	
	/**
	 * Retourne le générateur de requêtes ou null
	 * 
	 * @param adsp
	 * @return
	 * @throws Exception
	 */
	
	public AllocatedRequestGenerator getRequestGenerator() throws Exception {
		
		if ( this.get(adsp) == null )
			throw new Exception("No key for : " + adsp.dspURI);
		
		return (AllocatedRequestGenerator) this.get(adsp).get(DataType.ALLOCATED_REQUEST_GENERATOR);
	}
	
	/**
	 * Place un générateur de requêtes dans la map. (Ecrasement du précédent)
	 * 
	 * @param adsp
	 * @param argn
	 */
	
	public void setRequestGenerator(AllocatedRequestGenerator argn) {
		
		Map<DataType, Object> dataMap = new HashMap<>();
		
		dataMap.put(DataType.ALLOCATED_REQUEST_GENERATOR, argn);
		this.put(adsp, dataMap);	
		
	}
	
	/**
	 * Retourne l'ensemble des {@link AllocatedApplicationVM}
	 * 
	 * @param adsp
	 * @return
	 * @throws Exception
	 */
	
	@SuppressWarnings("unchecked")
	public Set<AllocatedApplicationVM> getAllocatedApplicationVMs() throws Exception {
		
		if ( this.get(adsp) == null )
			throw new Exception("No key for : " + adsp.dspURI);
		
		if ( this.get(adsp).get(DataType.ALLOCATED_REQUEST_GENERATOR) == null)
			throw new Exception("No map ALLOCATED_REQUEST_GENERATOR for : " + adsp.dspURI);
		
		return ((Map<AllocatedApplicationVM, Integer>) this.get(adsp).get(DataType.ALLOCATED_APPLICATION_VM)).keySet();
		
	}
	
	/**
	 * Met à jour le nombre de coeurs de l' {@link AllocatedApplicationVM}
	 * 
	 * @param adsp
	 * @param aavm
	 * @param coreCount
	 * @throws Exception
	 */
	
	@SuppressWarnings("unchecked")
	public void updateAllocatedApplicationVMCoreCount(AllocatedApplicationVM aavm, Integer coreCount) throws Exception {
		
		Map<DataType, Object> dataMap = new HashMap<>();
		Map<AllocatedApplicationVM, Integer> aavmMap = new HashMap<>();
		
		aavmMap.put(aavm, coreCount);
		dataMap.put(DataType.ALLOCATED_APPLICATION_VM, aavmMap);
		
		if ( this.get(adsp) == null )
			throw new Exception("No key for : " + adsp.dspURI);
		
		if ( this.get(adsp).get(DataType.ALLOCATED_APPLICATION_VM) == null ) {
			this.get(adsp).put(DataType.ALLOCATED_APPLICATION_VM, aavmMap);
		} else {
			 ( (Map<AllocatedApplicationVM, Integer>) this.get(adsp).get(DataType.ALLOCATED_APPLICATION_VM)).put(aavm, coreCount);
		}
	}
	
	/**
	 * Retourne le nombre de coeurs de l'{@link AllocatedApplicationVM}
	 * 
	 * @param adsp
	 * @param aavm
	 * @return
	 * @throws Exception
	 */
	
	@SuppressWarnings("unchecked")
	public Integer getAllocatedApplicationVMCoreCount(AllocatedApplicationVM aavm) throws Exception {
		
		if ( this.get(adsp) == null )
			throw new Exception("No key for : " + adsp.dspURI);
		
		if ( this.get(adsp).get(DataType.ALLOCATED_APPLICATION_VM) == null ) {
			throw new Exception("No map ALLOCATED_APPLICATION_VM for : " + adsp.dspURI);
		}
		
		return  ( (Map<AllocatedApplicationVM, Integer>) this.get(adsp).get(DataType.ALLOCATED_APPLICATION_VM)).get(aavm);
	}
	
	/**
	 * Supprimer une {@link AllocatedApplicationVM}
	 * 
	 * @param aavm
	 * @throws Exception
	 */
	
	@SuppressWarnings("unchecked")
	public void removeAllocatedApplicationVM(AllocatedApplicationVM aavm) throws Exception {
		
		if ( this.get(adsp) == null )
			throw new Exception("No key for : " + adsp.dspURI);
		
		if ( this.get(adsp).get(DataType.ALLOCATED_APPLICATION_VM) == null ) {
			throw new Exception("No map ALLOCATED_APPLICATION_VM for : " + adsp.dspURI);
		}
		
		( (Map<AllocatedApplicationVM, Integer>) this.get(adsp).get(DataType.ALLOCATED_APPLICATION_VM)).remove(aavm);
		
	}
	
}
