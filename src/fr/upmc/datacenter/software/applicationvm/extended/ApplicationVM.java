package fr.upmc.datacenter.software.applicationvm.extended;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorServicesNotificationInboundPort;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorServicesOutboundPort;
import fr.upmc.datacenter.software.applicationvm.extended.interfaces.CoreReleasingI;
import fr.upmc.datacenter.software.applicationvm.extended.ports.CoreReleasingInboundPort;
import fr.upmc.datacenter.software.applicationvm.interfaces.TaskI;
import fr.upmc.datacenter.software.interfaces.CoreReleasingNotificationI;
import fr.upmc.datacenter.software.ports.CoreReleasingNotificationInboundPort;
import fr.upmc.datacenter.software.ports.CoreReleasingNotificationOutboundPort;

/**
 * Extension de la classe {@link fr.upmc.datacenter.software.applicationvm.ApplicationVM}.
 * Il est maintenant possible de réaliser des désallocation de coeurs par le biais du port {@link CoreReleasingInboundPort}.
 * Les coeurs à stopper sont placés dans une liste de coeurs en attente de terminaison.
 * En interne à chaque notification de requête les coeurs de la liste sont observés pour voir lequel est bien passé en idle.
 * Les coeurs en attente de terminaison ne peuvent plus être utilisés pour traiter une nouvelle requête.
 * Un coeur idle et dans la liste des terminaison est retiré des {@link AllocatedCore} et sera donc désalloué de manière effective.
 * Entre une demande de libération de coeur et sa libération effective, il s'écoule un temps dépendant du restant d'instructions 
 * de la requête à exécuter sur ce même coeur au moment de la demande de libération.
 * Ce temps pouvant ne pas être négligeable en cas de requêtes lourdes, un nouveau port de notification de terminaison est ajouté à la classe {@link ApplicationVM}.
 * Le port {@link CoreReleasingNotificationOutboundPort} va permettre de déclencher chez le détenteur du port {@link CoreReleasingNotificationInboundPort} l'appel à sa méthode
 * de gestion au moment d'un terminaison de coeur.
 * 
 * @author Daniel RADEAU
 *
 */

public class ApplicationVM 
	extends 
		fr.upmc.datacenter.software.applicationvm.ApplicationVM
	implements 
		CoreReleasingI
{
	protected List<AllocatedCore> releasing;				// Liste des coeurs en cours de libération
	protected CoreReleasingNotificationOutboundPort crnop;	// Port de notification des terminaison de coeurs
	protected CoreReleasingInboundPort crip;				// Port de demande de terminaison de coeurs

	public ApplicationVM(	String vmURI,
							String applicationVMManagementInboundPortURI,
							String requestSubmissionInboundPortURI, 
							String requestNotificationOutboundPortURI,
							String coreReleasingInboundPortURI,
							String coreReleasingNotificationOutboundPortURI) throws Exception 
	{
		super(	vmURI, 
				applicationVMManagementInboundPortURI, 
				requestSubmissionInboundPortURI,
				requestNotificationOutboundPortURI );
		releasing = new ArrayList<>();
		
		if (!offeredInterfaces.contains(CoreReleasingI.class))
			offeredInterfaces.add(CoreReleasingI.class);
		
		crip = new CoreReleasingInboundPort(	coreReleasingInboundPortURI, 
												CoreReleasingI.class, 
												this);
		addPort(crip);
		crip.publishPort();
		
		if (!requiredInterfaces.contains(CoreReleasingNotificationI.class))
			requiredInterfaces.add(CoreReleasingNotificationI.class);
		
		crnop = new CoreReleasingNotificationOutboundPort(	coreReleasingNotificationOutboundPortURI, 
															CoreReleasingNotificationI.class, 
															this);
		addPort(crnop);
		crnop.publishPort();
		
		
	}

	protected void tryPerformCoreRelease() throws Exception {
		
		AllocatedCore allocatedCore = null;
		
		if (releasing.size() != 0)
			allocatedCore = releasing.get(0);
		else
			return;
		
		/**
		 * Vérification que le coeur à bien été placé en liste de libération et est bien en état en attente
		 */
		
		Boolean isIdleCore = allocatedCoresIdleStatus.get(allocatedCore);
		
		if (releasing.contains(allocatedCore) && isIdleCore) {
			
			allocatedCoresIdleStatus.remove(allocatedCore);

			/**
			 * Parcours des coeurs alloués à la recherche d'un coeur alloué appartenant au même processeur
			 */

			boolean hasAllocatedCoreOnTheSameProcessor = false;
			for (AllocatedCore ac : allocatedCoresIdleStatus.keySet()) {
				if (allocatedCore.processorURI.equals(ac.processorURI))
					hasAllocatedCoreOnTheSameProcessor = true;
			}

			/**
			 * Suppression des ports processeurs si il n'y a plus de coeurs alloués sur ce même processeur
			 */

			if (!hasAllocatedCoreOnTheSameProcessor) {

				/**
				 * Pop des ports processeurs qui ne sont plus utilisés par l'AVM
				 */
				ProcessorServicesOutboundPort psop = processorServicesPorts.remove(allocatedCore.processorURI);
				ProcessorServicesNotificationInboundPort psnop = processorNotificationInboundPorts.remove(allocatedCore.processorURI);

				/**
				 * Dépublication des ports qui ne seront plus utilisés
				 */

				psop.unpublishPort();
				psnop.unpublishPort();

				/**
				 * Suppression des ports de l'AVM
				 */

				this.removePort(psop);
				this.removePort(psnop);

				/**
				 * Destruction des ports détachés de l'AVM
				 */

				psop.destroyPort();
				psnop.destroyPort();
			}

			/**
			 * Le coeurs est maintenant désalloué, il est supprimer de la liste des coeurs à libérer
			 */

			releasing.remove(allocatedCore);
			
			/**
			 * Notification d'une libération de coeur
			 */
			
			crnop.notifyCoreReleasing(this.vmURI);
		}
		
	}

	@Override
	public synchronized void releaseCore() throws Exception {
		AllocatedCore allocatedCore = findIdleCore();

		/** 
		 * Tentative complète de désallocation d'un coeur en status en attente
		 */

		if (allocatedCore != null) {
			releasing.add(allocatedCore);
			tryPerformCoreRelease();
			return;
		}

		/**
		 * Dans le cas où tous les coeurs sont occupés, il faut alors en choisir un au hasard pour le processus de libération.
		 * Le relâchement doit être effectué moment de la terminaison de la tâche occupant le coeur.
		 */

		Random random = new Random(System.nanoTime());
		Integer index = random.nextInt(allocatedCoresIdleStatus.size());
		int i = 0;
		
		for (AllocatedCore ac : allocatedCoresIdleStatus.keySet()) {
			if (i == index) {
				allocatedCore = ac;
				break;
			}
		}

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
		 * Injection du processus de libération
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
	 * Trouve un coeur en attente qui n'est pas un coeur à libérer
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
		 * Injection d'une modification dans le processus de sélection d'un coeur à libérer 
		 * pour limiter le risque de libération d'un coeur qui traite une tâche en cours
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

}
