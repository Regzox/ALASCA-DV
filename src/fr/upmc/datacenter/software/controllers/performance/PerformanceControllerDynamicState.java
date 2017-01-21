package fr.upmc.datacenter.software.controllers.performance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.providers.resources.logical.AllocatedApplicationVM;
import fr.upmc.datacenter.providers.resources.logical.LogicalResourceProvider;
import fr.upmc.datacenter.providers.resources.logical.LogicalResourcesProviderDynamicState;
import fr.upmc.datacenter.software.dispatcher.Dispatcher;

/**
 * Etat dynamique du {@link PerformanceController}.
 * 
 * De la m�me mani�re que {@link LogicalResourcesProviderDynamicState} on assure les op�rations
 * par la mise en place de synchrnisations et de contrats.
 * 
 * @author Daniel RADEAU
 *
 */

public class PerformanceControllerDynamicState {

	protected Map<AllocatedDispatcher, AllocatedRequestGenerator> argMap;
	protected Map<AllocatedDispatcher, List<AllocatedApplicationVM>> aavmsMap;
	
	protected Map<AllocatedApplicationVM, Integer> aavmsCoreCountMap;
	
	protected List<AllocatedApplicationVMStateChanging> stateChanging;
	
	/**
	 * Mat�rialise le changement d'�tat d'un {@link AllocatedApplicationVM} au moment de la variation de son nombre {@link AllocatedCore}
	 * Tant que des la variation de coeurs n'est pas nulle alors on r�alise une �tape de variation.
	 * Le but �tant de pouvoir synchroniser l'�tat du {@link PerformanceController} et celui du {@link LogicalResourceProvider}
	 * lors de op�rations du genre variation du nombre de coeurs d'un {@link AllocatedApplicationVM}.
	 * {@link AllocatedApplicationVMStateChanging} trouve plut�t son utilit� dans les op�rations de r�duction de coeurs,
	 * pouvant prendre un d�lai relativement important d'une demande � l'autre.
	 * Le principe serait n'autoriser les op�rations de variation de coeurs que lorsque aucun 
	 * {@link AllocatedApplicationVMStateChanging} correspondant � une {@link AllocatedApplicationVM} 
	 * n'est pr�sent dans la liste du {@link PerformanceControllerDynamicState} 
	 * 
	 * @author Daniel RADEAU
	 *
	 */
	
	class AllocatedApplicationVMStateChanging {
		protected AllocatedApplicationVM aavm;
		protected Integer coreVariation;
		
		public AllocatedApplicationVMStateChanging(AllocatedApplicationVM aavm, Integer coreVariation) throws Exception {
			assert aavm != null;
			assert coreVariation != null;
			
			this.aavm = aavm;
			this.coreVariation = coreVariation;
		}
		
		/**
		 * Si l'�tat de {@link AllocatedApplicationVM} est consid�r� comme chang�
		 * 
		 * @return <b>vrai</b> si nous avons termin� la variation, sinon <b>faux</b>
		 */
		
		public boolean isChanged() throws Exception {
			assert coreVariation != null;
			return coreVariation == 0;
		}
		
		public AllocatedApplicationVM getAllocatedApplicationVM() throws Exception {
			assert aavm != null;
			return aavm;
		}
		
		public Integer getCoreVariation() throws Exception {
			assert coreVariation != null;
			return coreVariation;
		}
		
		/**
		 * Effectue une �tape de d�crementation ou d'incr�mentation, le but �tant de d'atteindre 0
		 * 
		 * @return <b>vrai</b> si � l'issue de l'�tape nous avons termin� la variation, sinon <b>faux</b>
		 */
		
		public synchronized boolean step() throws Exception {
			assert aavm != null;
			assert coreVariation != null;
			
			if (coreVariation < 0)
				coreVariation++;
			else if (coreVariation > 0)
				coreVariation--;
			return isChanged();
		}
		
		/**
		 * Sont consid�r�s les m�mes si leurs {@link AllocatedApplicationVM} sont identiques
		 */
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof AllocatedApplicationVMStateChanging))
				return false;
			return this.aavm.equals(((AllocatedApplicationVMStateChanging) obj).aavm);
		}
		
	}
	
	public PerformanceControllerDynamicState() {
		argMap = new HashMap<>();
		aavmsMap = new HashMap<>();
		aavmsCoreCountMap = new HashMap<>();
		stateChanging = new ArrayList<>();
	}
	
	/**
	 * Ajout d'un {@link Dispatcher} avec ses conditions initiales
	 * 
	 * @param adsp {@link AllocatedDispatcher} � ajouter
	 * @param arg {@link AllocatedRequestGenerator} li� au {@link AllocatedDispatcher}
	 * @param aavms tableau des {@link AllocatedApplicationVM} li�s au {@link AllocatedDispatcher}
	 * @param coreCounts tableau des nombres de {@link AllocatedCore} associ�s aux {@link AllocatedApplicationVM}
	 * @throws Exception
	 */
	
	public synchronized void addAllocatedDispatcher(
			AllocatedDispatcher adsp,
			AllocatedRequestGenerator arg,
			AllocatedApplicationVM[] aavms,
			Integer[] coreCounts
			) throws Exception 
	{
		assert adsp != null;
		assert arg != null;
		assert aavms != null;
		assert coreCounts != null;
		assert aavms.length > 0;
		assert argMap.get(adsp) == null;
		assert aavmsMap.get(adsp) == null;
		for (int i = 0; i < aavms.length; i++) {
			assert aavms[i] != null;
			assert aavmsCoreCountMap.get(aavms[i]) == null;
		}
		
		ArrayList<AllocatedApplicationVM> list = new ArrayList<>();

		for (int i = 0; i < aavms.length; i++) {
			list.add(aavms[i]);
			aavmsCoreCountMap.put(aavms[i], coreCounts[i]);
		}
		
		argMap.put(adsp, arg);
		aavmsMap.put(adsp, list);
		
		assert argMap.get(adsp) != null;
		assert aavmsMap.get(adsp) != null; 
		assert aavmsMap.get(adsp).size() == aavms.length;
		for (int i = 0; i < aavms.length; i++) {
			assert aavmsCoreCountMap.get(aavms[i]) != null;
		}
	}
	
	/**
	 * Supression d'un {@link AllocatedDispatcher} pr�sent dans le {@link PerformanceControllerDynamicState}
	 * 
	 * @param adsp {@link AllocatedDispatcher} cible
	 * @throws Exception
	 */
	
	public synchronized void removeAllocatedDispatcher(AllocatedDispatcher adsp) throws Exception {
		assert adsp != null;
		assert argMap.get(adsp) != null;
		assert aavmsMap.get(adsp) != null;
		
		argMap.remove(adsp);
		aavmsMap.remove(adsp);
		
		assert argMap.get(adsp) == null;
		assert aavmsMap.get(adsp) == null;
	}
	
	/**
	 * Ajoute un {@link AllocatedApplicationVM} non pr�sent dans le {@link PerformanceControllerDynamicState}
	 * 
	 * @param adsp adsp {@link AllocatedDispatcher} cible
	 * @param aavm {@link AllocatedApplicationVM} � ajouter
	 * @throws Exception
	 */
	
	public synchronized void addAllocatedApplicationVM(AllocatedDispatcher adsp, AllocatedApplicationVM aavm, Integer coreCount) throws Exception {
		assert adsp != null;
		assert aavm != null;
		assert aavmsMap.get(adsp) != null;
		assert !aavmsMap.get(adsp).contains(aavm);
		assert aavmsCoreCountMap != null;
		assert aavmsCoreCountMap.get(aavm) == null;
		
		int size = aavmsMap.get(adsp).size();
		aavmsMap.get(adsp).add(aavm);
		aavmsCoreCountMap.put(aavm, coreCount);
		
		assert aavmsMap.get(adsp).size() == (size + 1);
	}
	
	/**
	 * Supprime un {@link AllocatedApplicationVM} pr�sent dans le {@link PerformanceControllerDynamicState}
	 * 
	 * @param adsp {@link AllocatedDispatcher} cible
	 * @param aavm {@link AllocatedApplicationVM} � supprimer
	 * @throws Exception
	 */
	
	public synchronized void removeAllocatedApplicationVM(AllocatedDispatcher adsp, AllocatedApplicationVM aavm) throws Exception {
		assert aavm != null;
		assert aavmsMap.get(adsp) != null;
		assert aavmsMap.get(adsp).contains(aavm);
		assert aavmsCoreCountMap.get(aavm) != null;
		
		int size = aavmsMap.get(adsp).size();
		aavmsMap.get(adsp).remove(aavm);
		aavmsCoreCountMap.remove(aavm);
		
		AllocatedApplicationVMStateChanging result = null;
		for (AllocatedApplicationVMStateChanging aavmsc : stateChanging)
			if ( aavmsc.getAllocatedApplicationVM().equals(aavm) )
				result = aavmsc;
		if ( result != null )
			stateChanging.remove(result);
		
		assert aavmsMap.get(adsp).size() == (size - 1);
		assert !aavmsMap.get(adsp).contains(aavm);
		assert aavmsCoreCountMap.get(aavm) == null;
		assert !stateChanging.contains(result);
	}
	
	/**
	 * Augmente le nombre de coeurs recens�s pour la {@link AllocatedApplicationVM} en question
	 * 
	 * @param aavm {@link AllocatedApplicationVM} cible
	 * @param coreNumber nombre de coeurs � ajouter
	 * @throws Exception
	 */
	
	public synchronized void increaseAllocatedCoreCount(AllocatedApplicationVM aavm, Integer coreNumber) throws Exception {
		assert aavm != null;
		assert coreNumber != null;
		assert coreNumber > 0;
		assert aavmsCoreCountMap.get(aavm) != null;
		
		Integer coreCount = aavmsCoreCountMap.get(aavm);
		coreCount += coreNumber;
		aavmsCoreCountMap.put(aavm, coreCount);
	}
	
	/**
	 * R�duit le nombre de coeurs recens�s pour la {@link AllocatedApplicationVM} en question
	 * 
	 * @param aavm {@link AllocatedApplicationVM} cible
	 * @param coreNumber nombre de coeurs � retirer
	 * @throws Exception
	 */
	
	public synchronized void decreaseAllocatedCoreCount(AllocatedApplicationVM aavm, Integer coreNumber) throws Exception {
		assert aavm != null;
		assert coreNumber != null;
		assert coreNumber > 0;
		assert aavmsCoreCountMap.get(aavm) != null;
		assert (aavmsCoreCountMap.get(aavm) - coreNumber) >= 0;
		
		Integer coreCount = aavmsCoreCountMap.get(aavm);
		coreCount -= coreNumber;
		aavmsCoreCountMap.put(aavm, coreCount);
	}
	
	/**
	 * Ajoute un {@link AllocatedApplicationVMStateChanging} dans la liste des �tats changeant
	 * 
	 * @param aavm {@link AllocatedApplicationVM} concern�e
	 * @param coreVariation Variation � op�rer
	 * @throws Exception
	 */
	
	public synchronized void pushAllocatedApplicationVMChangingState(AllocatedApplicationVM aavm, Integer coreVariation) throws Exception {
		assert aavm != null;
		assert coreVariation != null;
		assert stateChanging != null;

		int size = stateChanging.size();
		AllocatedApplicationVMStateChanging aavmsc = new AllocatedApplicationVMStateChanging(aavm, coreVariation);
		
		assert !stateChanging.contains(aavmsc);
		
		stateChanging.add(aavmsc);
		
		assert stateChanging.size() == (size + 1); 
	}
	
	/**
	 * Effectue un pas changement d'�tat pour la {@link AllocatedApplicationVM} cible.
	 * Un pas met � jour toute la structure de {@link PerformanceControllerDynamicState}
	 * 
	 * @param aavm {@link AllocatedApplicationVM} cible
	 * @return vrai si la cible � fini sa transition (allocation ou lib�ration compl�te de coeurs)
	 * @throws Exception
	 */
	
	public synchronized boolean step(AllocatedApplicationVM aavm) throws Exception {
		assert aavm != null;
		assert stateChanging != null;
		assert !stateChanging.isEmpty();
		
		Boolean finished = null;
		AllocatedApplicationVMStateChanging result = null;
		
		for ( AllocatedApplicationVMStateChanging aavmsc : stateChanging ) {
			System.out.println("IN STEPPING : " + aavmsc.getAllocatedApplicationVM().avmURI + " " + aavmsc.getCoreVariation());
			if ( aavmsc.getAllocatedApplicationVM().equals(aavm) ) {
				int cv = aavmsc.getCoreVariation();
				finished = aavmsc.step();
				if ( finished ) {
					result = aavmsc;
				}
				if (aavmsc.getCoreVariation() < cv) {
					increaseAllocatedCoreCount(aavm, 1);
				} else if (aavmsc.getCoreVariation() > cv) {
					decreaseAllocatedCoreCount(aavm, 1);
				}
			}
		}
		
		if ( finished == null )
			throw new Exception("step failure : AllocatedApplicationVM not found");
		if ( result != null )
			assert stateChanging.remove(result);
		
		return finished;
	}
	
	/**
	 * Retourne l'�tat courant de changement de la {@link AllocatedApplicationVM}
	 * @param aavm {@link AllocatedApplicationVM} cible
	 * @return vrai si la cible � fini sa transition (allocation ou lib�ration compl�te de coeurs)
	 * @throws Exception
	 */
	
	public synchronized boolean isChanged(AllocatedApplicationVM aavm) throws Exception {
		assert aavm != null;
		assert stateChanging != null;
		assert !stateChanging.isEmpty();
		
		Boolean result = null;
		
		for ( AllocatedApplicationVMStateChanging aavmsc : stateChanging ) {
			if ( aavmsc.getAllocatedApplicationVM().equals(aavm) )
				result = aavmsc.isChanged();
		}
		
		assert result != null;
		return result; 
	}
	
	public synchronized boolean isChanging(AllocatedApplicationVM aavm) throws Exception {
		boolean result = false;
		for ( AllocatedApplicationVMStateChanging aavmsc : stateChanging ) {
			System.out.println("\tstateChanging\t" +aavmsc.getAllocatedApplicationVM().avmURI);
			System.out.println("\tstateChanging\t" +aavmsc.getCoreVariation());
			if ( aavmsc.getAllocatedApplicationVM().equals(aavm) )
				result = true;
		}
		return result;
	}
	
	public synchronized List<AllocatedApplicationVM> getChangingAllocatedApplicationVM() throws Exception {
		ArrayList<AllocatedApplicationVM> list = new ArrayList<>();
		
		for ( AllocatedApplicationVMStateChanging aavmsc : stateChanging ) {
			list.add(aavmsc.getAllocatedApplicationVM());
		}
		return list;
	}
	
	public synchronized List<AllocatedApplicationVM> getAllocatedApplicationVMs(AllocatedDispatcher adsp) {
		assert adsp != null;
		assert aavmsMap != null;
		return aavmsMap.get(adsp);
	}
	
	public synchronized List<AllocatedApplicationVM> getAllocatedApplicationVMs() {
		assert aavmsMap != null;
		assert aavmsMap.size() == 1;
		return aavmsMap.get(new ArrayList<>(aavmsMap.keySet()).get(0));
	}
	
	public synchronized Integer getAllocatedApplicationVMCoreCount(AllocatedApplicationVM aavm) {
		assert aavm != null;
		assert aavmsCoreCountMap.containsKey(aavm);
		return aavmsCoreCountMap.get(aavm);
	}
	
	public synchronized AllocatedDispatcher getSingleAllocatedDispatcher() {
		assert aavmsMap != null;
		assert aavmsMap.size() == 1;
		return new ArrayList<>(aavmsMap.keySet()).get(0);
	}
	
	public synchronized void removeAllocatedApplicationVM(AllocatedApplicationVM aavm) throws Exception {
		removeAllocatedApplicationVM(getSingleAllocatedDispatcher(), aavm);
	}

}
