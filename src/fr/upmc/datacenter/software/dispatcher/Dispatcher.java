package fr.upmc.datacenter.software.dispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ComponentI;
import fr.upmc.datacenter.interfaces.ControlledDataOfferedI;
import fr.upmc.datacenter.interfaces.PushModeControllerI;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.dispatcher.interfaces.DispatcherI;
import fr.upmc.datacenter.software.dispatcher.interfaces.DispatcherManagementI;
import fr.upmc.datacenter.software.dispatcher.ports.DispatcherDynamicStateDataInboundPort;
import fr.upmc.datacenter.software.dispatcher.ports.DispatcherManagementInboundPort;
import fr.upmc.datacenter.software.dispatcher.statistics.ExponentialAverage;
import fr.upmc.datacenter.software.dispatcher.time.Chronometer;
import fr.upmc.datacenter.software.dispatcher.time.interfaces.ChronometerI;
import fr.upmc.datacenter.software.dispatcher.time.interfaces.DurationI;
import fr.upmc.datacenter.software.enumerations.Tag;
import fr.upmc.datacenter.software.interfaces.ApplicationVMReleasingNotificationI;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;
import fr.upmc.datacenter.software.ports.ApplicationVMReleasingNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestNotificationInboundPort;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;

public class Dispatcher extends AbstractComponent
	implements 
	DispatcherI,
	DispatcherManagementI,
	RequestSubmissionHandlerI,
	RequestNotificationHandlerI,
	PushModeControllerI
{	
	public static boolean DYNAMIC_STATE_DATA_DISPLAY = false;
		
	protected Map<String, List<String>> pendings = new HashMap<>();
	protected Map<String, Integer> performed = new HashMap<>();
	protected Vector<String> terminating = new Vector<>();
	
	protected ScheduledFuture<?> pushingTask = null;
	
	/**
	 * Système de moyennage par calcul d'une moyenne exponentielle de coefficient alpha.
	 * Pour évaluer cette moyenne une map associant les uris des requêtes à des chronomètres est créé
	 * et chaque chrnomètre est lancé au moment de sa création dans la méthode dispatch (arrivée de requêtes) puis ensuite
	 * stoppé dans la méthode forward (arrivée de notification).
	 */
	
	protected double alpha = 0.33; // Donne un poids de 1/3 au nouveau résultat contre 2/3 pour la moyenne précédente
	protected Map<String, ExponentialAverage> exponentialAverages = new HashMap<>();
	protected Map<String, ChronometerI> requestChronometers = new HashMap<>();
	
	protected String uri;		
	
	protected String rsipURI, rnopURI, avmrnopURI, ddsdipURI;
	protected List<String> rnipURIs, rsopURIs;
	
	protected DispatcherManagementInboundPort dmip;
	protected ApplicationVMReleasingNotificationOutboundPort avmrnop;
	protected DispatcherDynamicStateDataInboundPort ddsdip;
	
	/**
	 * Dispatcher constructor takes an uri his name,
	 * the request submission in bound port where plug a request generator,
	 * the request notification out to plug into request generator.
	 * 
	 * @param uri
	 * @param requestSubmissionInbundPort
	 * @param requestNotificationOutboundPort
	 * @throws Exception
	 */
	
	public Dispatcher(	
			String uri,
			String dispatcherManagementInboundPort,
			String applicationVMReleasingNotificationOutboundPort,
			String dispatcherDynamicStateDataInboundPort
			)
					throws Exception 
	{
		super(1, 1);
		this.uri = uri;
		rnipURIs = new ArrayList<>();
		rsopURIs = new ArrayList<>();
		
		addOfferedInterface(DispatcherManagementI.class);
		dmip = new DispatcherManagementInboundPort(dispatcherManagementInboundPort, DispatcherManagementI.class, this);
		addPort(dmip);
		dmip.publishPort();
		
		addRequiredInterface(ApplicationVMReleasingNotificationI.class);
		avmrnopURI = applicationVMReleasingNotificationOutboundPort;
		avmrnop = new ApplicationVMReleasingNotificationOutboundPort(avmrnopURI, ApplicationVMReleasingNotificationI.class, this);
		addPort(avmrnop);
		avmrnop.publishPort();
		
		addOfferedInterface(ControlledDataOfferedI.ControlledPullI.class);
		ddsdipURI = dispatcherDynamicStateDataInboundPort;
		ddsdip = new DispatcherDynamicStateDataInboundPort(ddsdipURI, this);
		addPort(ddsdip);
		ddsdip.publishPort();
	}
	
	@Override
	public void acceptRequestTerminationNotification(RequestI request) throws Exception {
		forward(request);
	}

	@Override
	public void acceptRequestSubmission(RequestI request) throws Exception {
		dispatch(request, false);
	}

	@Override
	public void acceptRequestSubmissionAndNotify(RequestI request) throws Exception {
		dispatch(request, true);
	}
	
	@Override
	public String connectToRequestGenerator(String rnipURI) throws Exception {
		String[] rnopsURI = findOutboundPortURIsFromInterface(RequestNotificationI.class);
		
		if (rnopsURI != null)
			if (rnopsURI.length > 0)
				throw new Exception("A RequestGenerator is already connected to this Dispatcher");

		if (!requiredInterfaces.contains(RequestNotificationI.class))
			addRequiredInterface(RequestNotificationI.class);
		
		rnopURI = generateURI(Tag.REQUEST_NOTIFICATION_OUTBOUND_PORT);
		RequestNotificationOutboundPort rnop = new RequestNotificationOutboundPort(rnopURI, this);
		addPort(rnop);
		rnop.publishPort();
		rnop.doConnection(rnipURI, RequestNotificationConnector.class.getCanonicalName());
		
		if (!offeredInterfaces.contains(RequestSubmissionI.class))
			addOfferedInterface(RequestSubmissionI.class);
		
		rsipURI = generateURI(Tag.REQUEST_SUBMISSION_INBOUND_PORT);
		RequestSubmissionInboundPort rsip = new RequestSubmissionInboundPort(rsipURI, this);
		addPort(rsip);
		rsip.publishPort();
		
		return rsipURI;
	}

	@Override
	public void disconnectFromRequestGenerator() throws Exception {
		String[] rnopsURI = findOutboundPortURIsFromInterface(RequestNotificationI.class);
		String[] rsipsURI = findInboundPortURIsFromInterface(RequestSubmissionI.class);
		
		if (rnopsURI != null)
			if (rnopsURI.length == 0)
				throw new Exception("No RequestGenerator connected to this Dispatcher");
			else {
				if (requiredInterfaces.contains(RequestNotificationI.class))
					removeRequiredInterface(RequestNotificationI.class);
				String rnopURI = rnopsURI[0];
				RequestNotificationOutboundPort rnop = (RequestNotificationOutboundPort) findPortFromURI(rnopURI);
				rnop.doDisconnection();
				rnop.unpublishPort();
				removePort(rnop);
				rnop.destroyPort();
				
				String rsipURI = rsipsURI[0];
				RequestSubmissionInboundPort rsip = (RequestSubmissionInboundPort) findPortFromURI(rsipURI);
				rsip.doDisconnection();
				rsip.unpublishPort();
				removePort(rsip);
				rsip.destroyPort();
			}
	}
	
	@Override
	public String connectToApplicationVM(String rsipURI) throws Exception {	
		if (!requiredInterfaces.contains(RequestSubmissionI.class))
			addRequiredInterface(RequestSubmissionI.class);
		
		String rsopURI = generateURI(Tag.REQUEST_SUBMISSION_OUTBOUND_PORT);
		RequestSubmissionOutboundPort rsop = new RequestSubmissionOutboundPort(rsopURI, this);
		addPort(rsop);
		rsop.publishPort();
		rsop.doConnection(rsipURI, RequestSubmissionConnector.class.getCanonicalName());
	
		rsopURIs.add(rsopURI);
		System.out.println(rsopURI);
		
		String rnipURI = generateURI(Tag.REQUEST_NOTIFICATION_INBOUND_PORT);
		RequestNotificationInboundPort rnip = new RequestNotificationInboundPort(rnipURI, this);
		addPort(rnip);
		rnip.publishPort();
		
		rnipURIs.add(rnipURI);
		
		pendings.put(rsopURI, new ArrayList<String>());
		performed.put(rsopURI, 0);
		
		exponentialAverages.put(rsopURI, new ExponentialAverage(alpha));
		
		return rnipURI;
	}

	@Override
	public void disconnectFromApplicationVM() throws Exception {
		String rsopURI = lessBusyRequestSubmissionOutboundPortURI();
		terminating.addElement(rsopURI);
		tryToPerformApplicationVMDisconnection();
	}

	@Override
	public void dispatch(RequestI request, Boolean forward) throws Exception {
		String rsopURI = lessBusyRequestSubmissionOutboundPortURI();
		RequestSubmissionOutboundPort rsop = (RequestSubmissionOutboundPort) findPortFromURI(rsopURI);
		
		logMessage("DISPATCHING " + request.getRequestURI() + " TO : " + rsopURI);
		
		if (forward) {
			pendings.get(rsopURI).add(request.getRequestURI());
			
			try {
				Chronometer chronometer = new Chronometer();
				
				requestChronometers.put(request.getRequestURI(), chronometer);
				chronometer.start();				
			} catch (Exception e) { e.printStackTrace(); }
			
			rsop.submitRequestAndNotify(request);
		} else {
			rsop.submitRequest(request);
		}
		
		if (DYNAMIC_STATE_DATA_DISPLAY) {
			System.out.println("@AFTER DISPATCHING");
			System.out.println(dynamicStateToString());
		}
	}

	@Override
	public void forward(RequestI request) throws Exception {
		String notifiedRsopURI = null;
		String[] rnopsURI = findOutboundPortURIsFromInterface(RequestNotificationI.class);		
		
		for (String rsopURI : pendings.keySet()) {
			if (pendings.get(rsopURI).contains(request.getRequestURI())) {
				notifiedRsopURI = rsopURI;
				pendings.get(rsopURI).remove(request.getRequestURI());
				
				try {
					ChronometerI chronometer = requestChronometers.get(request.getRequestURI());
										
					if (chronometer == null) {
						System.out.println("GRAVE : CHRONOMETER IS NULL !");
						System.exit(-1);
					}
					
					DurationI duration = requestChronometers.get(request.getRequestURI()).stop();
					exponentialAverages.get(rsopURI).push(duration);
					requestChronometers.remove(rsopURI);
					
				} catch (Exception e) { e.printStackTrace(); }
				
				break;
			}
		}
		
		logMessage("FORWARDING " + request.getRequestURI() + " FROM : " + notifiedRsopURI + " OWNER");
		
		performed.replace(notifiedRsopURI, performed.get(notifiedRsopURI) + 1);
		
		if (DYNAMIC_STATE_DATA_DISPLAY) {
			System.out.println("@DURING FORWARDING");
			System.out.println(dynamicStateToString());
			System.out.println(notifiedRsopURI + " average performance time : " + exponentialAverages.get(notifiedRsopURI).getValue().getMilliseconds() + "ms");
		}
		
		tryToPerformApplicationVMDisconnection();
		if(rnopsURI.length <= 0)
			throw new Exception("Attempt forwarding to nothing");
		else {
			for (String rnopURI : rnopsURI) {
				RequestNotificationOutboundPort rnop = (RequestNotificationOutboundPort) findPortFromURI(rnopURI);
				rnop.notifyRequestTermination(request);
			}
		}
	}

	@Override
	public String generateURI(Object tag) {
		return uri + '_' + tag + '_' + Math.abs(new Random().nextInt());
	}

	@Override
	public String lessBusyRequestSubmissionOutboundPortURI() {
		String lessBusyRequestSubmissionOutboundURI = null;
		Integer lessPendingScore = null;
		
		for (String rsopURI : pendings.keySet()) {
			if (terminating.contains(rsopURI))
				continue;
			if ((lessBusyRequestSubmissionOutboundURI == null && lessPendingScore == null) || pendings.get(rsopURI).size() < lessPendingScore) {
				lessBusyRequestSubmissionOutboundURI = rsopURI;
				lessPendingScore = pendings.get(lessBusyRequestSubmissionOutboundURI).size();
			}
		}
		
		return lessBusyRequestSubmissionOutboundURI;
	}

	@Override
	public String mostBusyRequestSubmissionOutboundPortURI() {
		String mostBusyRequestSubmissionOutboundURI = null;
		Integer mostPendingScore = null;
		
		for (String rsopURI : pendings.keySet()) {
			if (terminating.contains(rsopURI))
				continue;
			if ((mostBusyRequestSubmissionOutboundURI == null && mostPendingScore == null) || pendings.get(rsopURI).size() > mostPendingScore) {
				mostBusyRequestSubmissionOutboundURI = rsopURI;
				mostPendingScore = pendings.get(mostBusyRequestSubmissionOutboundURI).size();
			}
		}
		
		return mostBusyRequestSubmissionOutboundURI;
	}

	@Override
	public Integer lessBusyRequestSubmissionOutboundPortScore() {
		return pendings.get(lessBusyRequestSubmissionOutboundPortURI()).size();
	}

	@Override
	public Integer mostBusyRequestSubmissionOutboundPortScore() {
		return pendings.get(mostBusyRequestSubmissionOutboundPortURI()).size();
	}

	@Override
	public void tryToPerformApplicationVMDisconnection() throws Exception {
		Vector<String> terminated = new Vector<>();
		
		for (String rsopURI : terminating) {
			if (pendings.get(rsopURI).size() == 0) {
				terminated.add(rsopURI);
			}
		}
		
		for (String rsopURI : terminated) {
			RequestSubmissionOutboundPort rsop = (RequestSubmissionOutboundPort) findPortFromURI(rsopURI);
			
			pendings.remove(rsopURI);
			performed.remove(rsopURI);
			terminating.remove(rsopURI);
			exponentialAverages.remove(rsopURI);
			rsop.doDisconnection();
			rsop.unpublishPort();
			removePort(rsop);
			rsop.destroyPort();
			
			int index = rsopURIs.indexOf(rsopURI);
			rsopURIs.remove(index);			
			
			avmrnop.notifyApplicationVMReleasing(uri, rsopURI, rnipURIs.remove(index));
		}	
	}
	
	@Override
	public String dynamicStateToString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DYNAMIC_STATE_DATA_DISPLAY : \n");
		sb.append("PENDINGS : \n");
		for (String rsopURI : pendings.keySet()) {
			sb.append("\t");
			sb.append(rsopURI);
			sb.append(" pendings : ");
			sb.append(pendings.get(rsopURI).size());
			sb.append("\n");
		}
		
		sb.append("PERFORMED : \n");
		for (String rsopURI : performed.keySet()) {
			sb.append("\t");
			sb.append(rsopURI);
			sb.append(" performed : ");
			sb.append(performed.get(rsopURI));
			sb.append("\n");
		}
		sb.append("\n");
		
		return sb.toString();
	}

	@Override
	public String getURI() {
		return uri;
	}

	@Override
	public String getRequestSubmissionInboundPortURI() {
		return rsipURI;
	}

	@Override
	public List<String> getRequestSubmissionOutboundPortURIs() {
		return rsopURIs;
	}

	@Override
	public List<String> getRequestNotificationInboundPortURIs() {
		return rnipURIs;
	}

	@Override
	public String getRequestNotificationOutboundPortURI() {
		return rnopURI;
	}

	@Override
	public String getApplicationVMReleasingNotificationOutboundPortURI() {
		return avmrnopURI;
	}

	@Override
	public void startUnlimitedPushing(int interval) throws Exception {
			
		final Dispatcher dsp = this;
		pushingTask = this.scheduleTaskAtFixedRate(
				new ComponentI.ComponentTask() {
					@Override
					public void run() {
						try {
							dsp.sendDynamicState() ;
						} catch (Exception e) {
							throw new RuntimeException(e) ;
						}
					}
				}, 
				interval,
				interval,
				TimeUnit.MILLISECONDS);
		
	}

	@Override
	public void startLimitedPushing(int interval, int n) throws Exception {
		assert	n > 0 ;

		final Dispatcher dsp = this ;
		this.pushingTask =
			this.scheduleTask(
					new ComponentI.ComponentTask() {
						@Override
						public void run() {
							try {
								dsp.sendDynamicState(interval, n) ;
							} catch (Exception e) {
								throw new RuntimeException(e) ;
							}
						}
					}, interval, TimeUnit.MILLISECONDS) ;
	}

	@Override
	public void stopPushing() throws Exception {
		if (this.pushingTask != null &&
				!(this.pushingTask.isCancelled() ||
									this.pushingTask.isDone())) {
			this.pushingTask.cancel(false) ;
		}
	}
	
	public DispatcherDynamicState getDynamicState() {
		return new DispatcherDynamicState(uri, exponentialAverages, pendings, performed);
	}
	
	public void sendDynamicState() throws Exception {
		if (this.ddsdip.connected())
			ddsdip.send(getDynamicState());
	}
	
	public void sendDynamicState(
			final int interval,
			int numberOfRemainingPushes) throws Exception
	{
		this.sendDynamicState() ;
		final int fNumberOfRemainingPushes = numberOfRemainingPushes - 1 ;
		if (fNumberOfRemainingPushes > 0) {
			final Dispatcher dsp = this ;
			this.pushingTask =
					this.scheduleTask(
							new ComponentI.ComponentTask() {
								@Override
								public void run() {
									try {
										dsp.sendDynamicState(
												interval,
												fNumberOfRemainingPushes) ;
									} catch (Exception e) {
										throw new RuntimeException(e) ;
									}
								}
							}, interval, TimeUnit.MILLISECONDS) ;
			}
		}
	
}
