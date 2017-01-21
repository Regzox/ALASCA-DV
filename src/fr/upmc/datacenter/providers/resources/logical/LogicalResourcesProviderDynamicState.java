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
 * Syst�me de gestion des informations d'�tat dynamique du fournisseur de ressources logiques.<br>
 * L'ensemble des m�thodes sont synchronis�es afin d'emp�cher les mises en �tat incoh�rent li� au multhreading.<br>
 * Des contrats strictes ont �t� mis en place pour lever des {@link Exception} � chaque information incoh�rente d�tect�e.<br> 
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
	 * Ajoute une {@link AllocatedApplicationVM}, pour cel� aucun param�tre ne doit �tre vide
	 * et aucun {@link AllocatedCore} ne doit �tre � <code>null</code>
	 * 
	 * @param aavm {@link AllocatedApplicationVM} � ajouter
	 * @param acs Tableau des cores allou�s � {@link AllocatedApplicationVM}
	 * @param rnop {@link RequestNotificationOutboundPort} de l' {@link AllocatedApplicationVM}
	 * @param crnop {@link CoreReleasingNotificationOutboundPort} de l' {@link AllocatedApplicationVM}
	 */
	
	public synchronized void addAllocatedApplicationVM(
			AllocatedApplicationVM aavm, 
			AllocatedCore[] acs, 
			RequestNotificationOutboundPort rnop, 
			CoreReleasingNotificationOutboundPort crnop) throws Exception
	{
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
	}
	
	/**
	 * Supprime une {@link AllocatedApplicationVM}, pour cel� l {@link AllocatedApplicationVM} doit �tre pr�sente
	 * dans les toutes les {@link Map} de la classe
	 * 
	 * @param aavm {@link AllocatedApplicationVM} � supprimer
	 */
	
	public synchronized void removeAllocatedApplicationVM(AllocatedApplicationVM aavm) throws Exception {
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
	}
	
	/**
	 * Retourne la liste des {@link AllocatedCore} pour l {@link AllocatedApplicationVM} pass�e en param�tre
	 * @param aavm {@link AllocatedApplicationVM} dont les coeurs nous int�ressent
	 * @return la {@link List} des coeurs
	 */
	
	public synchronized List<AllocatedCore> getAllocatedCores(AllocatedApplicationVM aavm) throws Exception {
		assert aavm != null;
		assert acsmap.get(aavm) != null;
		return acsmap.get(aavm);
	}
	
	/**
	 * Retourne le port {@link RequestNotificationOutboundPort} de {@link AllocatedApplicationVM} pass�e en param�tre
	 * @param aavm {@link AllocatedApplicationVM} dont le port {@link RequestNotificationOutboundPort} nous int�resse
	 * @return le port {@link RequestNotificationOutboundPort} de l {@link AllocatedApplicationVM}
	 */
	
	public synchronized RequestNotificationOutboundPort getRequestNotificationOutboundPort(AllocatedApplicationVM aavm) throws Exception {
		assert aavm != null;
		assert rnopsmap.get(aavm) != null;
		return rnopsmap.get(aavm);
	}
	
	/**
	 * Retourne le port {@link CoreReleasingNotificationOutboundPort} de {@link AllocatedApplicationVM} pass�e en param�tre
	 * @param aavm {@link AllocatedApplicationVM} dont le port {@link CoreReleasingNotificationOutboundPort} nous int�resse
	 * @return le port {@link CoreReleasingNotificationOutboundPort} de l {@link AllocatedApplicationVM}
	 */
	
	public synchronized CoreReleasingNotificationOutboundPort getCoreReleasingNotificationOutboundPort(AllocatedApplicationVM aavm) throws Exception {
		assert aavm != null;
		assert crnopsmap.get(aavm) != null;
		
		return crnopsmap.get(aavm);
	}
	
	/**
	 * Ajoute � {@link AllocatedApplicationVM} un {@link AllocatedCore}
	 * 
	 * @param aavm {@link AllocatedApplicationVM} cible
	 * @param ac {@link AllocatedCore} � ajouter
	 */
	
	public synchronized void addAllocatedCores(AllocatedApplicationVM aavm, AllocatedCore ac) throws Exception {
		assert aavm != null;
		assert ac != null;
		assert acsmap.get(aavm) != null;
		assert !acsmap.containsValue(ac);
		
		int size = acsmap.get(aavm).size();
		acsmap.get(aavm).add(ac);
		
		assert acsmap.get(aavm).size() == (size + 1);
	}
	
	/**
	 * Ajoute � {@link AllocatedApplicationVM} un ensemble de {@link AllocatedCore}
	 * 
	 * @param aavm {@link AllocatedApplicationVM} cible
	 * @param acs tableau de {@link AllocatedCore} � ajouter
	 */
	
	public synchronized void addAllocatedCores(AllocatedApplicationVM aavm, AllocatedCore[] acs) throws Exception {
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
	 * Supprime � {@link AllocatedApplicationVM} un {@link AllocatedCore}
	 * 
	 * @param aavm {@link AllocatedApplicationVM} cible
	 * @param ac {@link AllocatedCore} � supprimer
	 */
	
	public synchronized void removeAllocatedCore(AllocatedApplicationVM aavm, AllocatedCore ac) throws Exception {
		assert aavm != null;
		assert ac != null;
		assert acsmap.get(aavm) != null;
		assert acsmap.get(aavm).contains(ac);
		
		int size = acsmap.get(aavm).size();
		acsmap.get(aavm).remove(ac);
		
		assert acsmap.get(aavm).size() == (size - 1);
	}
	
	/**
	 * Supprime � {@link AllocatedApplicationVM} un ensemble de {@link AllocatedCore}
	 * 
	 * @param aavm {@link AllocatedApplicationVM} cible
	 * @param acs tableau de {@link AllocatedCore} � supprimer
	 */
	
	public synchronized void removeAllocatedCores(AllocatedApplicationVM aavm, AllocatedCore[] acs) throws Exception {
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
	}
	
	/**
	 * Supprime � {@link AllocatedApplicationVM} le port {@link RequestNotificationOutboundPort}
	 * 
	 * @param aavm {@link AllocatedApplicationVM} cible
	 * @param rnop {@link RequestNotificationOutboundPort} port � supprimer
	 */
	
	public synchronized void removeRequestNotificationOutboundPort(AllocatedApplicationVM aavm, RequestNotificationOutboundPort rnop) throws Exception {
		assert aavm != null;
		assert rnop != null;
		assert rnopsmap.get(aavm) != null;
		assert rnopsmap.containsValue(rnop);
		
		rnopsmap.remove(aavm);
	}
	
	/**
	 * Supprime � {@link AllocatedApplicationVM} le port {@link CoreReleasingNotificationOutboundPort}
	 * 
	 * @param aavm {@link AllocatedApplicationVM} cible
	 * @param crnop {@link CoreReleasingNotificationOutboundPort} � supprimer
	 */
	
	public synchronized void removeCoreReleasingNotificationOutboundPort(AllocatedApplicationVM aavm, CoreReleasingNotificationOutboundPort crnop) throws Exception {
		assert aavm != null;
		assert crnop != null;
		assert crnopsmap.get(aavm) != null;
		assert crnopsmap.containsValue(crnop);
		
		crnopsmap.remove(aavm);
	}
	
	/**
	 * Definit � {@link AllocatedApplicationVM} le port {@link RequestNotificationOutboundPort}
	 * 
	 * @param aavm {@link AllocatedApplicationVM} cible
	 * @param rnop {@link RequestNotificationOutboundPort} � d�finir
	 */
	
	public synchronized void setRequestNotificationOutboundPort(AllocatedApplicationVM aavm, RequestNotificationOutboundPort rnop) throws Exception {
		assert aavm != null;
		assert rnopsmap.get(aavm) == null;
		
		rnopsmap.put(aavm, rnop);
	}
	
	/**
	 * Definit � {@link AllocatedApplicationVM} le port {@link CoreReleasingNotificationOutboundPort}
	 * 
	 * @param aavm {@link AllocatedApplicationVM} cible
	 * @param rnop {@link CoreReleasingNotificationOutboundPort} � d�finir
	 */
	
	public synchronized void setCoreReleasingNotificationOutboundPort(AllocatedApplicationVM aavm, CoreReleasingNotificationOutboundPort crnop) throws Exception {
		assert aavm != null;
		assert crnop != null;
		assert crnopsmap.get(aavm) == null;
		
		crnopsmap.put(aavm, crnop);
	}
	
	/**
	 * Retourne l'ensemble des {@link AllocatedApplicationVM}
	 * @return l'ensemble des {@link AllocatedApplicationVM}
	 */
	
	public synchronized Set<AllocatedApplicationVM> getAllocatedApplicationVMSet() throws Exception {
		Set<AllocatedApplicationVM> aavms = acsmap.keySet();
		
		assert rnopsmap.keySet().containsAll(aavms);
		assert crnopsmap.keySet().containsAll(aavms);
		
		return aavms;
	}
}
