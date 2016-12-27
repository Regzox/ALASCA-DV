	@Override
	public void submitApplication(
			AbstractApplication application,
			Class<?> submissionInterface) throws Exception 
	{
		/**
		 * On suppose que le client voulant soumettre son application possède déjà un composant de type générateur de requêtes
		 * lui permettant de soumettre via son interface requise (implémentée par son ...submissionOutboundPort) 
		 */
		
		/**
		 * Recherche parmis les ordinateurs disponibles du premier candidat possèdant les ressources suffisantes
		 * à l'allocation d'une AVM de taille arbitraire.
		 * Dans un premier temps nous allons salement allouer un processeur entier par AVM.
		 * 
		 */

		/**
		 * Parcours de tous les ordinateurs mis à disposition à la recherche d'un processeur à allouer
		 */

		String computerURI = null;
		int numberOfCores = 0;
		
		for (String cdsdopURI : computerDynamicStateDataOutboundPortMap.values()) {
			ComputerDynamicStateDataOutboundPort cdsdop = (ComputerDynamicStateDataOutboundPort) findPortFromURI(cdsdopURI);
			ComputerDynamicState data = (ComputerDynamicState) cdsdop.request();
			boolean[][] processorMap = data.getCurrentCoreReservations();

			/**
			 * Parcours de tous les processeurs de l'ordinateur en cours
			 */
			
			for (int proccessorIndex = 0; proccessorIndex < processorMap.length; proccessorIndex++) {
				
				/**
				 * Parcours de tous les coeurs du processeur en cours
				 */
				
				for (boolean coreAllocated : processorMap[proccessorIndex]) {
					
					/**
					 * A la détection du premier coeur non alloué, nous tenons notre machine pour l'allocation
					 */
					
					if (!coreAllocated) {
						computerURI = data.getComputerURI();
						numberOfCores =  processorMap[proccessorIndex].length;
					}
				}
			}
		}
		
		/**
		 * Dans la cas où nous n'avons pas trouvé de d'ordinateurs de libres, alors computerURI est toujours nulle
		 * et nous ne pouvons pas donner suite à la demande d'hebergement d'application
		 */
		
		if (computerURI == null && numberOfCores == 0) {
			logMessage("Ressources leak, impossible to welcome the application");
			throw new Exception("Ressources leak, impossible to welcome the application");
		}
		
		logMessage("Ressources found, " + numberOfCores +  " cores on computer : " + computerURI);
		
		/**
		 * Déclaration d'une nouvelle applicationVM
		 */
		
		final String applicationVMURI = generateURI(Tag.APPLICATION_VM);
		final String applicationVMManagementInboundPortURI = generateURI(Tag.APPLICATION_VM_MANAGEMENT_INBOUND_PORT);
		final String applicationVMRequestSubmissionInboundPortURI = generateURI(Tag.REQUEST_SUBMISSION_INBOUND_PORT);
		final String applicationVMRequestNotificationOutboundPortURI = generateURI(Tag.REQUEST_NOTIFICATION_OUTBOUND_PORT);
		
		ApplicationVM applicationVM = new ApplicationVM(
				applicationVMURI,
				applicationVMManagementInboundPortURI,
				applicationVMRequestSubmissionInboundPortURI,
				applicationVMRequestNotificationOutboundPortURI
				);
		AbstractCVM.theCVM.addDeployedComponent(applicationVM);
		
		if (LOGGING_ALL | LOGGING_APPLICATION_VM) {
			applicationVM.toggleLogging();
			applicationVM.toggleTracing();
		}
		
		/**
		 * Déclaration d'un nouveau dispatcher
		 */
		
		final String dispatcherURI = generateURI(Tag.DISPATCHER);
		final String dispatcherManagementInboundPortURI = generateURI(Tag.DISPATCHER_MANAGEMENT_INBOUND_PORT);
		
		Dispatcher dispatcher = new Dispatcher(
				dispatcherURI,
				dispatcherManagementInboundPortURI
				);
		AbstractCVM.theCVM.addDeployedComponent(dispatcher);
		
		if (LOGGING_ALL | LOGGING_DISPATCHER) {
			dispatcher.toggleLogging();
			dispatcher.toggleTracing();
		}
		
		dispatcher.start();
		
		/**
		 * Tentative d'allocation du nombre de coeurs voulu pour l'applicationVM
		 */
				
		ComputerServicesOutboundPort csop = (ComputerServicesOutboundPort) findPortFromURI(computerServicesOutboundPortMap.get(computerURI));
		AllocatedCore[] cores = csop.allocateCores(numberOfCores);
		
		if (cores.length == numberOfCores)
			logMessage("(" + cores.length + "/" + numberOfCores + ") Amount of wanted cores successfully found on " + computerURI);
		else
			logMessage("(" + cores.length + "/" + numberOfCores + ") Amount of wanted cores unfortunately not found on " + computerURI);
		
		/**
		 * Si à ce stade aucun coeurs n'est disponible alors nous nous trouvons dans un état incohérent 
		 * et la soumission ne peut donc donner suite
		 */
		
		if (cores.length == 0) {
			logMessage("GRAVE : no core busyless found on the selected computer " + computerURI);
//			AbstractCVM.theCVM.removeDeployedComponent(requestGenerator);
			AbstractCVM.theCVM.removeDeployedComponent(applicationVM);
			AbstractCVM.theCVM.removeDeployedComponent(dispatcher);
			logMessage("Submission aborted due to incoherent an computer status on " + computerURI);
			throw new Exception("Submission aborted due to incoherent an computer status on " + computerURI);
		}
		
		applicationVM.allocateCores(cores);
		
		logMessage(cores.length + " cores are successfully allocated for the applicationVM " + applicationVMURI);
		
		/**
		 * Création d'un port de contrôle pour la gestion du dispatcher
		 */
		
		if (!requiredInterfaces.contains(DispatcherManagementI.class))
			addRequiredInterface(DispatcherManagementI.class);
		
		DispatcherManagementOutboundPort dmop = new DispatcherManagementOutboundPort(DispatcherManagementI.class, this);
		addPort(dmop);
		dmop.publishPort();
		dmop.doConnection(dispatcherManagementInboundPortURI, DispatcherManagementConnector.class.getCanonicalName());
		
		/**
		 * Connexion de l'application au dispatcher
		 */

		System.out.println(application.findPortURIsFromInterface(RequestSubmissionI.class)[0]);
		System.out.println(application.findPortURIsFromInterface(RequestNotificationI.class)[0]);
		
		String rsipURI = dmop.connectToRequestGenerator(application.findPortURIsFromInterface(RequestNotificationI.class)[0]);
		RequestSubmissionOutboundPort rsop = (RequestSubmissionOutboundPort) application.findPortFromURI(application.findPortURIsFromInterface(RequestSubmissionI.class)[0]);
		rsop.doConnection(rsipURI, DynamicConnectorFactory.createConnector(RequestSubmissionI.class, RequestSubmissionI.class).getCanonicalName());
		 
		logMessage(rsop.getOwner().toString() + " connected to " + dispatcherURI);
		
		/**
		 * Connexion de l'ApplicationVM au dispatcher
		 */
		
		String rnipURI = dmop.connectToApplicationVM(applicationVMRequestSubmissionInboundPortURI);
		RequestNotificationOutboundPort rnop = (RequestNotificationOutboundPort) applicationVM.findPortFromURI(applicationVMRequestNotificationOutboundPortURI);
		rnop.doConnection(rnipURI, RequestNotificationConnector.class.getCanonicalName());
		
		logMessage(applicationVMURI + " connected to " + dispatcherURI);
		
		/**
		 * Lancement de l'applicationVM
		 */
		
		applicationVM.start();
		
		logMessage(applicationVMURI + " launched and ready to receive requests");
				
	}