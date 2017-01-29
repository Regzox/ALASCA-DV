package fr.upmc.datacenter.software.applicationvm.extended;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorServicesNotificationInboundPort;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorServicesOutboundPort;
import fr.upmc.datacenter.software.applicationvm.extended.interfaces.ApplicationVMCoreReleasingI;
import fr.upmc.datacenter.software.applicationvm.extended.ports.ApplicationVMCoreReleasingInboundPort;
import fr.upmc.datacenter.software.applicationvm.interfaces.TaskI;
import fr.upmc.datacenter.software.interfaces.CoreReleasingNotificationI;
import fr.upmc.datacenter.software.ports.CoreReleasingNotificationInboundPort;
import fr.upmc.datacenter.software.ports.CoreReleasingNotificationOutboundPort;

/**
 * Extension de la classe {@link fr.upmc.datacenter.software.applicationvm.ApplicationVM}.
 * Il est maintenant possible de r�aliser des d�sallocation de coeurs par le biais du port {@link ApplicationVMCoreReleasingInboundPort}.
 * Les coeurs � stopper sont plac�s dans une liste de coeurs en attente de terminaison.
 * En interne � chaque notification de requ�te, les coeurs de la liste sont observ�s pour voir lesquels sont bien pass�s en idle.
 * Les coeurs en attente de terminaison ne peuvent plus �tre utilis�s pour traiter une nouvelle requ�te.
 * Un coeur idle et dans la liste des terminaisons est retir� des {@link AllocatedCore} et sera donc d�sallou� de mani�re effective.
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
ApplicationVMCoreReleasingI
{
	protected List<AllocatedCore> releasing;				// Liste des coeurs en cours de lib�ration
	protected CoreReleasingNotificationOutboundPort crnop;	// Port de notification des terminaison de coeurs
	protected ApplicationVMCoreReleasingInboundPort crip;				// Port de demande de terminaison de coeurs

	private static Object LOCK = new Object();

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

		if (!offeredInterfaces.contains(ApplicationVMCoreReleasingI.class))
			offeredInterfaces.add(ApplicationVMCoreReleasingI.class);

		crip = new ApplicationVMCoreReleasingInboundPort(	coreReleasingInboundPortURI, 
				ApplicationVMCoreReleasingI.class, 
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

		if ( releasing == null ) {
			throw new Exception("releasing is null");
		}

		synchronized (LOCK) {
			
			if (releasing.size() > 0) {
				allocatedCore = releasing.remove(0);

				if ( allocatedCore == null ) {
					System.out.println("releasing size : " + releasing.size());
					for ( AllocatedCore ac : releasing) {
						System.out.print(ac);
						if (ac != null)
							System.out.println(" -> " + ac.processorURI);
					}
					throw new Exception("allocatedCore is null");
				}

				/**
				 * V�rification que le coeur � bien �t� plac� en liste de lib�ration et est bien en �tat en attente
				 */

				Boolean isIdleCore = allocatedCoresIdleStatus.get(allocatedCore);

				if ( isIdleCore == null ) {
					System.out.println(allocatedCore);
					if (allocatedCore != null)
						System.out.println(allocatedCore.processorURI + "(" + allocatedCore.coreNo + ")");
					System.out.println("allocatedCoresIdleStatus : ");
					for (AllocatedCore ac : allocatedCoresIdleStatus.keySet()) {
						System.out.println("\t" + ac.processorURI + "(" + ac.coreNo + ")" + " : " + allocatedCoresIdleStatus.get(ac) + " " + ac);
					}
					System.out.println("releasing : ");
					for (AllocatedCore ac : releasing) {
						System.out.println("\t" + ac.processorURI + "(" + ac.coreNo + ")" + " : " + allocatedCoresIdleStatus.get(ac) + " " + ac);
					}
					System.exit(-100); // TODO
					throw new Exception("isIdleCore is null");
				}		

				if ( !isIdleCore )
					releasing.add(allocatedCore);
				else {

					try {
						assert allocatedCoresIdleStatus.size() > 0;
					
						int size = allocatedCoresIdleStatus.size();
						boolean removed = allocatedCoresIdleStatus.remove(allocatedCore);
					
						assert allocatedCoresIdleStatus.size() < size;
						assert removed == true;
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(-1); // TODO
					}
					
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
						ProcessorServicesNotificationInboundPort psnip = processorNotificationInboundPorts.remove(allocatedCore.processorURI);

						psop.doDisconnection();

						/**
						 * Destruction des ports d�tach�s de l'AVM
						 */

						psop.destroyPort();
						psnip.destroyPort();

					}

					/**
					 * Notification d'une lib�ration de coeur
					 */

					logMessage("The allocated core [" + allocatedCore + "] is released and ready to be notified to the LRP");
					
					crnop.notifyCoreReleasing(this.vmURI, allocatedCore);
					
				}
			}
		}
		
	}

	@Override
	public synchronized void releaseCore() throws Exception {
		AllocatedCore allocatedCore = findIdleCore();

		/** 
		 * Tentative compl�te de d�sallocation d'un coeur en status en attente
		 */

		if (allocatedCore != null) {
			if ( !releasing.contains(allocatedCore) )
				releasing.add(allocatedCore);
			else
				logMessage("Releasing list already contains allocated core : " + allocatedCore);
			tryPerformCoreRelease();
		} else {

			/**
			 * Dans le cas o� tous les coeurs sont occup�s, il faut alors en choisir un au hasard pour le processus de lib�ration.
			 * Le rel�chement doit �tre effectu� moment de la terminaison de la t�che occupant le coeur.
			 */
	
			assert !releasing.contains(null);
			assert !releasing.contains(allocatedCore);
			
			Random random = new Random(System.nanoTime());
			
			while (releasing.contains(allocatedCore) || (allocatedCore == null) && !releasing.containsAll(allocatedCoresIdleStatus.keySet())) {
				Integer index = random.nextInt(allocatedCoresIdleStatus.size());
				int i = 0;
				
				for (AllocatedCore ac : allocatedCoresIdleStatus.keySet()) {
					if (i++ == index) {
						allocatedCore = ac;
						break;
					}
				}
		
				
			}
			
			if (allocatedCore == null) {
				logMessage("All cores are already on way the be released");
			}
			else
				releasing.add(allocatedCore);
		}
	}

	@Override
	public void releaseCores(int cores) throws Exception {
		assert cores >= 0;
		
		if (cores > allocatedCoresIdleStatus.size())
			throw new Exception("To many cores wanted for releasing (" + cores + "/" + allocatedCoresIdleStatus.size() + ")");
		for (int i = 0; i < cores; i++) {
			releaseCore();
		}

	}

	@Override
	public void releaseMaximumCores() throws Exception {
		Integer allocatedCoreCount = allocatedCoresIdleStatus.size();

		while (allocatedCoreCount > 0) {
			releaseCore();
			allocatedCoreCount--;
		}

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
	
	@Override
	public void allocateCores(AllocatedCore[] allocatedCores) throws Exception {
	
		/**
		 * Le m�canisme mis en place ne doit pas produire l'allocation de coeurs d�j� allou�s.
		 * Si c'est le cas nous somme alors dans une sorte d'�tat incoh�rent vis � vis des
		 * composants g�rant les ressources physiques et logiques. 
		 */
		
		for (AllocatedCore ac :  allocatedCores) {
			System.out.println("->" + ac.processorURI + "\t" + ac.coreNo);
			for (AllocatedCore acd : allocatedCoresIdleStatus.keySet() )
				if ( ac.processorURI.equals(acd.processorURI) && (ac.coreNo == acd.coreNo) ) {
					StringBuilder sb = new StringBuilder();
					
					sb
					.append("FATAL ERROR : Already allocated core is allocated ... doublons\n\n")
					.append(ac).append("\t")
					.append(ac.processorURI).append("\t")
					.append(ac.coreNo).append("\n")
					.append("are same\n")
					.append(acd).append("\t")
					.append(acd.processorURI).append("\t")
					.append(acd.coreNo).append("\n\n");
					
					sb.append("Between : \n");
					
					for ( AllocatedCore elt : allocatedCores ) {
						sb
						.append(elt).append("\t")
						.append(elt.processorURI).append("\t")
						.append(elt.coreNo).append("\t\n");
					}
					
					sb.append("and : \n");
					
					for ( AllocatedCore elt : allocatedCoresIdleStatus.keySet() ) {
						sb
						.append(elt).append("\t")
						.append(elt.processorURI).append("\t")
						.append(elt.coreNo).append("\t\n");
					}
					
					System.err.println(sb.toString());
					System.exit(-2);
				}
		}
		
		super.allocateCores(allocatedCores);
	}

}
