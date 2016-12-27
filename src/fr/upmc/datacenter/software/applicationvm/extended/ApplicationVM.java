package fr.upmc.datacenter.software.applicationvm.extended;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorServicesNotificationInboundPort;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorServicesOutboundPort;
import fr.upmc.datacenter.software.applicationvm.extended.interfaces.ApplicationVMManagementI;
import fr.upmc.datacenter.software.applicationvm.interfaces.TaskI;

public class ApplicationVM 
extends fr.upmc.datacenter.software.applicationvm.ApplicationVM
implements ApplicationVMManagementI
{
	/**
	 * Liste des coeurs en cours de lib�ration
	 */

	protected List<AllocatedCore> releasing;

	public ApplicationVM(	String vmURI,
			String applicationVMManagementInboundPortURI,
			String requestSubmissionInboundPortURI, 
			String requestNotificationOutboundPortURI) throws Exception 
	{
		super(	vmURI, 
				applicationVMManagementInboundPortURI, 
				requestSubmissionInboundPortURI,
				requestNotificationOutboundPortURI );
		releasing = new ArrayList<>();
	}

	protected void tryPerformCoreRelease() throws Exception {
		
		AllocatedCore allocatedCore = null;
		
		if (releasing.size() != 0)
			allocatedCore = releasing.get(0);
		else
			return;
		
		/**
		 * V�rification que le coeur � bien �t� plac� en liste de lib�ration et est bien en �tat en attente
		 */

		boolean isIdleCore = allocatedCoresIdleStatus.get(allocatedCore);

		if (releasing.contains(allocatedCore) && isIdleCore) {
			
			allocatedCoresIdleStatus.remove(allocatedCore);

			/**
			 * Parcours des coeurs allou�s � la recherche d'un coeur allou� appartenant au m�me processeur
			 */

			boolean hasAllocatedCoreOnTheSameProcessor = false;
			for (AllocatedCore ac : allocatedCoresIdleStatus.keySet()) {
				if (allocatedCore.processorURI.equals(ac.processorURI))
					hasAllocatedCoreOnTheSameProcessor = true;
			}

			/**
			 * Suppression des ports processeurs si il n'y a plus de coeurs allou�s sur ce m�me processeur
			 */

			if (!hasAllocatedCoreOnTheSameProcessor) {

				/**
				 * Pop des ports processeurs qui ne sont plus utilis�s par l'AVM
				 */
				ProcessorServicesOutboundPort psop = processorServicesPorts.remove(allocatedCore.processorURI);
				ProcessorServicesNotificationInboundPort psnop = processorNotificationInboundPorts.remove(allocatedCore.processorURI);

				/**
				 * D�publication des ports qui ne seront plus utilis�s
				 */

				psop.unpublishPort();
				psnop.unpublishPort();

				/**
				 * Suppression des ports de l'AVM
				 */

				this.removePort(psop);
				this.removePort(psnop);

				/**
				 * Destruction des ports d�tach�s de l'AVM
				 */

				psop.destroyPort();
				psnop.destroyPort();
			}

			/**
			 * Le coeurs est maintenant d�sallou�, il est supprimer de la liste des coeurs � lib�rer
			 */

			releasing.remove(allocatedCore);

		}
		
	}

	@Override
	public synchronized void releaseCore() throws Exception {
		AllocatedCore allocatedCore = findIdleCore();

		/** 
		 * Tentative compl�te de d�sallocation d'un coeur en status en attente
		 */

		if (allocatedCore != null) {
			releasing.add(allocatedCore);
			tryPerformCoreRelease();
		}

		/**
		 * Dans le cas o� tous les coeurs sont occup�s, il faut alors en choisir un au hasard pour le processus de lib�ration.
		 * Le rel�chement doit �tre effectu� moment de la terminaison de la t�che occupant le coeur.
		 */

		Random random = new Random(System.nanoTime());
		Integer index = random.nextInt(allocatedCoresIdleStatus.size());
		allocatedCore = ((AllocatedCore[]) allocatedCoresIdleStatus.keySet().toArray())[index];

		releasing.add(allocatedCore);

	}

	@Override
	public void releaseCores(int cores) throws Exception {

		if (cores >= allocatedCoresIdleStatus.size())
			throw new Exception("To many cores wanted for releasing");
		for (int i = 0; i < cores; i++) {
			releaseCore();
		}

	}

	@Override
	public void releaseMaximumCores() throws Exception {
		Integer allocatedCoreCount = allocatedCoresIdleStatus.size();

		while (allocatedCoreCount > 1)
			releaseCore();
	}

	@Override
	public void endTask(TaskI t) throws Exception {
		assert	t != null && this.isRunningTask(t) ;
		
		this.logMessage(this.vmURI + " terminates request " +
				t.getRequest().getRequestURI()) ;
		AllocatedCore ac = this.runningTasks.remove(t.getTaskURI()) ;
		this.allocatedCoresIdleStatus.remove(ac) ;
		this.allocatedCoresIdleStatus.put(ac, true) ;

		/**
		 * Injection du processus de lib�ration
		 */

		tryPerformCoreRelease();

		if (this.tasksToNotify.contains(t.getTaskURI())) {
			this.tasksToNotify.remove(t.getTaskURI()) ;
			this.requestNotificationOutboundPort.
			notifyRequestTermination(t.getRequest()) ;
		}
		
		if (!this.taskQueue.isEmpty()) {
			this.startTask() ;
		}
	}

	/**
	 * Trouve un coeur en attente qui n'est pas un coeur � lib�rer
	 * @return
	 */

	public synchronized AllocatedCore findNonReleasingIdleCore() {
		AllocatedCore ret = null ;
		for (AllocatedCore ac : this.allocatedCoresIdleStatus.keySet()) {
			if (this.allocatedCoresIdleStatus.get(ac) && !releasing.contains(ac)) {
				ret = ac ;
				break ;
			}
		}
		return ret ;
	}

	@Override
	public void			startTask() throws Exception
	{
		assert	!this.taskQueue.isEmpty() ;

		/**
		 * Injection d'une modification dans le processus de s�lection d'un coeur � lib�rer 
		 * pour limiter le risque de lib�ration d'un coeur qui traite une t�che en cours
		 */

		AllocatedCore ac = this.findNonReleasingIdleCore() ;

		if (ac != null) {
			this.allocatedCoresIdleStatus.remove(ac) ;
			this.allocatedCoresIdleStatus.put(ac, false) ;
			TaskI t = this.taskQueue.remove() ;
			this.logMessage(this.vmURI + " starts request " +
					t.getRequest().getRequestURI()) ;
			this.runningTasks.put(t.getTaskURI(), ac) ;
			ProcessorServicesOutboundPort p =
					this.processorServicesPorts.get(ac.processorURI) ;
			ProcessorServicesNotificationInboundPort np =
					this.processorNotificationInboundPorts.get(ac.processorURI) ;
			p.executeTaskOnCoreAndNotify(t, ac.coreNo, np.getPortURI()) ;
		}
	}

	/**
	 * Ajouter un vecteur de coeurs en terminaison
	 * Ajouter des m�thodes pour g�rer la suppression d'un coeur
	 * Inserer dans endTask l'op�ration de d�sallocation du coeur cible (dispatcher like)
	 */

}
