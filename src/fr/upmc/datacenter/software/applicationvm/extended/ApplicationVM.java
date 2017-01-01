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
 * Il est maintenant possible de r�aliser des d�sallocation de coeurs par le biais du port {@link CoreReleasingInboundPort}.
 * Les coeurs � stopper sont plac�s dans une liste de coeurs en attente de terminaison.
 * En interne � chaque notification de requ�te les coeurs de la liste sont observ�s pour voir lequel est bien pass� en idle.
 * Les coeurs en attente de terminaison ne peuvent plus �tre utilis�s pour traiter une nouvelle requ�te.
 * Un coeur idle et dans la liste des terminaison est retir� des {@link AllocatedCore} et sera donc d�sallou� de mani�re effective.
 * Entre une demande de lib�ration de coeur et sa lib�ration effective, il s'�coule un temps d�pendant du restant d'instructions 
 * de la requ�te � ex�cuter sur ce m�me coeur au moment de la demande de lib�ration.
 * Ce temps pouvant ne pas �tre n�gligeable en cas de requ�tes lourdes, un nouveau port de notification de terminaison est ajout� � la classe {@link ApplicationVM}.
 * Le port {@link CoreReleasingNotificationOutboundPort} va permettre de d�clencher chez le d�tenteur du port {@link CoreReleasingNotificationInboundPort} l'appel � sa m�thode
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
	protected List<AllocatedCore> releasing;				// Liste des coeurs en cours de lib�ration
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
		 * V�rification que le coeur � bien �t� plac� en liste de lib�ration et est bien en �tat en attente
		 */
		
		Boolean isIdleCore = allocatedCoresIdleStatus.get(allocatedCore);
		
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
			
			/**
			 * Notification d'une lib�ration de coeur
			 */
			
			crnop.notifyCoreReleasing(this.vmURI);
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
			return;
		}

		/**
		 * Dans le cas o� tous les coeurs sont occup�s, il faut alors en choisir un au hasard pour le processus de lib�ration.
		 * Le rel�chement doit �tre effectu� moment de la terminaison de la t�che occupant le coeur.
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

}
