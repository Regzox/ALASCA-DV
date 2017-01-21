package fr.upmc.datacenter.providers.resources.logical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.software.ports.CoreReleasingNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;

/**
 * Système de gestion des informations d'état dynamique du fournisseur de ressources logiques.<br>
 * L'ensemble des méthodes sont synchronisées afin d'empêcher les mises en état incohérent lié au multhreading.<br>
 * Des contrats strictes ont été mis en place pour lever des {@link Exception} à chaque information incohérente détectée.<br> 
 * 
 * @author Daniel RADEAU
 *
 */

public class LogicalResourcesProviderDynamicState {

	/** 
	 * Map des {@link AllocatedCore} par {@link AllocatedApplicationVM} 
	 */
	protected Map<AllocatedApplicationVM, List<AllocatedCore>> acsmap;
	
	/**
	 * Map des {@link RequestNotificationOutboundPort} par {@link AllocatedApplicationVM}
	 */
	protected Map<AllocatedApplicationVM, RequestNotificationOutboundPort> rnopsmap;
	
	/**
	 * Map des {@link CoreReleasingNotificationOutboundPort} par {@link AllocatedApplicationVM}
	 */
	protected Map<AllocatedApplicationVM, CoreReleasingNotificationOutboundPort> crnopsmap;

	/**
	 * On initialise uniquement les maps
	 */
	
	public LogicalResourcesProviderDynamicState() {
		acsmap = new HashMap<>();
		rnopsmap = new HashMap<>();
		crnopsmap = new HashMap<>();
	}

	/**
	 * Ajoute une {@link AllocatedApplicationVM}, pour celà aucun paramètre ne doit être vide
	 * et aucun {@link AllocatedCore} ne doit être à <code>null</code>
	 * 
	 * @param aavm {@link AllocatedApplicationVM} à ajouter
	 * @param acs Tableau des cores alloués à {@link AllocatedApplicationVM}
	 * @param rnop {@link RequestNotificationOutboundPort} de l' {@link AllocatedApplicationVM}
	 * @param crnop {@link CoreReleasingNotificationOutboundPort} de l' {@link AllocatedApplicationVM}
	 */
	
	public synchronized void addAllocatedApplicationVM(
			AllocatedApplicationVM aavm, 
			AllocatedCore[] acs, 
			RequestNotificationOutboundPort rnop, 
			CoreReleasingNotificationOutboundPort crnop) throws Exception
	{
		System.out.println("BEFORE : addAllocatedApplicationVM");
		assert aavm != null;
		assert acs != null;
		assert rnop != null;
		assert crnop != null;
		assert acs.length > 0;
		for (int i = 0; i < acs.length; i++) assert acs[i] != null;
		assert acsmap.get(aavm) == null;
		assert rnopsmap.get(aavm) == null;
		assert crnopsmap.get(aavm) == null;
		
		ArrayList<AllocatedCore> list = new ArrayList<>();
		
		for (AllocatedCore ac : acs)
			list.add(ac);
		
		acsmap.put(aavm, list);
		rnopsmap.put(aavm, rnop);
		crnopsmap.put(aavm, crnop);
		
		assert acsmap.get(aavm) != null;
		assert rnopsmap.get(aavm) != null;
		assert crnopsmap.get(aavm) != null;
		assert list.size() == acs.length;
		System.out.println("AFTER : addAllocatedApplicationVM");
	}
	
	/**
	 * Supprime une {@link AllocatedApplicationVM}, pour celà l {@link AllocatedApplicationVM} doit être présente
	 * dans les toutes les {@link Map} de la classe
	 * 
	 * @param aavm {@link AllocatedApplicationVM} à supprimer
	 */
	
	public synchronized void removeAllocatedApplicationVM(AllocatedApplicationVM aavm) throws Exception {
		System.out.println("BEFORE : removeAllocatedApplicationVM");
		assert aavm != null;
		assert acsmap.containsKey(aavm);
		assert rnopsmap.containsKey(aavm);
		assert crnopsmap.containsKey(aavm);
		
		acsmap.remove(aavm);
		rnopsmap.remove(aavm);
		crnopsmap.remove(aavm);
		
		assert !acsmap.containsKey(aavm);
		assert !rnopsmap.containsKey(aavm);
		assert !crnopsmap.containsKey(aavm);
		System.out.println("AFTER : removeAllocatedApplicationVM");
	}
	
	/**
	 * Retourne la liste des {@link AllocatedCore} pour l {@link AllocatedApplicationVM} passée en paramètre
	 * @param aavm {@link AllocatedApplicationVM} dont les coeurs nous intéressent
	 * @return la {@link List} des coeurs
	 */
	
	public synchronized List<AllocatedCore> getAllocatedCores(AllocatedApplicationVM aavm) throws Exception {
		System.out.println("BEFORE : getAllocatedCores");
		assert aavm != null;
		assert acsmap.get(aavm) != null;
		System.out.println("AFTER : getAllocatedCores");
		return acsmap.get(aavm);
	}
	
	/**
	 * Retourne le port {@link RequestNotificationOutboundPort} de {@link AllocatedApplicationVM} passée en paramètre
	 * @param aavm {@link AllocatedApplicationVM} dont le port {@link RequestNotificationOutboundPort} nous intéresse
	 * @return le port {@link RequestNotificationOutboundPort} de l {@link AllocatedApplicationVM}
	 */
	
	public synchronized RequestNotificationOutboundPort getRequestNotificationOutboundPort(AllocatedApplicationVM aavm) throws Exception {
		System.out.println("BEFORE : getRequestNotificationOutboundPort");
		assert aavm != null;
		assert rnopsmap.get(aavm) != null;
		System.out.println("AFTER : getRequestNotificationOutboundPort");
		return rnopsmap.get(aavm);
	}
	
	/**
	 * Retourne le port {@link CoreReleasingNotificationOutboundPort} de {@link AllocatedApplicationVM} passée en paramètre
	 * @param aavm {@link AllocatedApplicationVM} dont le port {@link CoreReleasingNotificationOutboundPort} nous intéresse
	 * @return le port {@link CoreReleasingNotificationOutboundPort} de l {@link AllocatedApplicationVM}
	 */
	
	public synchronized CoreReleasingNotificationOutboundPort getCoreReleasingNotificationOutboundPort(AllocatedApplicationVM aavm) throws Exception {
		System.out.println("BEFORE : getCoreReleasingNotificationOutboundPort");
		assert aavm != null;
		assert crnopsmap.get(aavm) != null;
		
		System.out.println("AFTER : getCoreReleasingNotificationOutboundPort");
		return crnopsmap.get(aavm);
	}
	
	/**
	 * Ajoute à {@link AllocatedApplicationVM} un {@link AllocatedCore}
	 * 
	 * @param aavm {@link AllocatedApplicationVM} cible
	 * @param ac {@link AllocatedCore} à ajouter
	 */
	
	public synchronized void addAllocatedCores(AllocatedApplicationVM aavm, AllocatedCore ac) throws Exception {
		System.out.println("BEFORE : addAllocatedCores");
		assert aavm != null;
		assert ac != null;
		assert acsmap.get(aavm) != null;
		assert !acsmap.containsValue(ac);
		
		int size = acsmap.get(aavm).size();
		acsmap.get(aavm).add(ac);
		
		assert acsmap.get(aavm).size() == (size + 1);
		System.out.println("AFTER : addAllocatedCores");
	}
	
	/**
	 * Ajoute à {@link AllocatedApplicationVM} un ensemble de {@link AllocatedCore}
	 * 
	 * @param aavm {@link AllocatedApplicationVM} cible
	 * @param acs tableau de {@link AllocatedCore} à ajouter
	 */
	
	public synchronized void addAllocatedCores(AllocatedApplicationVM aavm, AllocatedCore[] acs) throws Exception {
		System.out.println("BEFORE : addAllocatedCores");
		assert aavm != null;
		assert acs != null;
		assert acsmap.get(aavm) != null;
		for (int i = 0; i < acs.length; i++) {
			assert acs[i] != null;
			assert !acsmap.containsValue(acs[i]);
		}
		
		int size = acsmap.get(aavm).size();
		for (int i = 0; i < acs.length; i++)
			acsmap.get(aavm).add(acs[i]);
		
		assert acsmap.get(aavm).size() == (size + acs.length);
		System.out.println("AFTER : addAllocatedCores");
	}
	
	/**
	 * Supprime à {@link AllocatedApplicationVM} un {@link AllocatedCore}
	 * 
	 * @param aavm {@link AllocatedApplicationVM} cible
	 * @param ac {@link AllocatedCore} à supprimer
	 */
	
	public synchronized void removeAllocatedCore(AllocatedApplicationVM aavm, AllocatedCore ac) throws Exception {
		System.out.println("BEFORE : removeAllocatedCore");
		assert aavm != null;
		assert ac != null;
		assert acsmap.get(aavm) != null;
		assert acsmap.get(aavm).contains(ac);
		
		int size = acsmap.get(aavm).size();
		acsmap.get(aavm).remove(ac);
		
		assert acsmap.get(aavm).size() == (size - 1);
		System.out.println("AFTER : removeAllocatedCore");
	}
	
	/**
	 * Supprime à {@link AllocatedApplicationVM} un ensemble de {@link AllocatedCore}
	 * 
	 * @param aavm {@link AllocatedApplicationVM} cible
	 * @param acs tableau de {@link AllocatedCore} à supprimer
	 */
	
	public synchronized void removeAllocatedCores(AllocatedApplicationVM aavm, AllocatedCore[] acs) throws Exception {
		System.out.println("BEFORE : removeAllocatedCores");
		assert aavm != null;
		assert acs != null;
		assert acsmap.get(aavm) != null;
		for (int i = 0; i < acs.length; i++) {
			assert acs[i] != null;
			assert acsmap.get(aavm).contains(acs[i]);
		}
		int size = acsmap.get(aavm).size();
		for (int i = 0; i < acs.length; i++)
			acsmap.get(aavm).remove(acs[i]);
		assert acsmap.get(aavm).size() == (size - acs.length);
		System.out.println("AFTER : removeAllocatedCores");
	}
	
	/**
	 * Supprime à {@link AllocatedApplicationVM} le port {@link RequestNotificationOutboundPort}
	 * 
	 * @param aavm {@link AllocatedApplicationVM} cible
	 * @param rnop {@link RequestNotificationOutboundPort} port à supprimer
	 */
	
	public synchronized void removeRequestNotificationOutboundPort(AllocatedApplicationVM aavm, RequestNotificationOutboundPort rnop) throws Exception {
		System.out.println("BEFORE : removeRequestNotificationOutboundPort");
		assert aavm != null;
		assert rnop != null;
		assert rnopsmap.get(aavm) != null;
		assert rnopsmap.containsValue(rnop);
		
		rnopsmap.remove(aavm);
		System.out.println("AFTER : removeRequestNotificationOutboundPort");
	}
	
	/**
	 * Supprime à {@link AllocatedApplicationVM} le port {@link CoreReleasingNotificationOutboundPort}
	 * 
	 * @param aavm {@link AllocatedApplicationVM} cible
	 * @param crnop {@link CoreReleasingNotificationOutboundPort} à supprimer
	 */
	
	public synchronized void removeCoreReleasingNotificationOutboundPort(AllocatedApplicationVM aavm, CoreReleasingNotificationOutboundPort crnop) throws Exception {
		System.out.println("BEFORE : removeCoreReleasingNotificationOutboundPort");
		assert aavm != null;
		assert crnop != null;
		assert crnopsmap.get(aavm) != null;
		assert crnopsmap.containsValue(crnop);
		
		crnopsmap.remove(aavm);
		System.out.println("AFTER : removeCoreReleasingNotificationOutboundPort");
	}
	
	/**
	 * Definit à {@link AllocatedApplicationVM} le port {@link RequestNotificationOutboundPort}
	 * 
	 * @param aavm {@link AllocatedApplicationVM} cible
	 * @param rnop {@link RequestNotificationOutboundPort} à définir
	 */
	
	public synchronized void setRequestNotificationOutboundPort(AllocatedApplicationVM aavm, RequestNotificationOutboundPort rnop) throws Exception {
		System.out.println("BEFORE : setRequestNotificationOutboundPort");
		assert aavm != null;
		assert rnopsmap.get(aavm) == null;
		
		rnopsmap.put(aavm, rnop);
		System.out.println("AFTER : setRequestNotificationOutboundPort");
	}
	
	/**
	 * Definit à {@link AllocatedApplicationVM} le port {@link CoreReleasingNotificationOutboundPort}
	 * 
	 * @param aavm {@link AllocatedApplicationVM} cible
	 * @param rnop {@link CoreReleasingNotificationOutboundPort} à définir
	 */
	
	public synchronized void setCoreReleasingNotificationOutboundPort(AllocatedApplicationVM aavm, CoreReleasingNotificationOutboundPort crnop) throws Exception {
		System.out.println("BEFORE : setCoreReleasingNotificationOutboundPort");
		assert aavm != null;
		assert crnop != null;
		assert crnopsmap.get(aavm) == null;
		
		crnopsmap.put(aavm, crnop);
		System.out.println("AFTER : setCoreReleasingNotificationOutboundPort");
	}
	
	/**
	 * Retourne l'ensemble des {@link AllocatedApplicationVM}
	 * @return l'ensemble des {@link AllocatedApplicationVM}
	 */
	
	public synchronized Set<AllocatedApplicationVM> getAllocatedApplicationVMSet() throws Exception {
		System.out.println("BEFORE : getAllocatedApplicationVMSet");
		Set<AllocatedApplicationVM> aavms = acsmap.keySet();
		
		assert rnopsmap.keySet().containsAll(aavms);
		assert crnopsmap.keySet().containsAll(aavms);
		
		System.out.println("AFTER : getAllocatedApplicationVMSet");
		return aavms;
	}
}
