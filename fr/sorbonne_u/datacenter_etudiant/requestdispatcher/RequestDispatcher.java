package fr.sorbonne_u.datacenter_etudiant.requestdispatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.software.connectors.RequestNotificationConnector;
import fr.sorbonne_u.datacenter.software.connectors.RequestSubmissionConnector;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.ports.RequestDispatcherManagementInboundPort;

/**
 * La classe <code>RequestDispatcher</code> implémente le composant représentant un répartiteur 
 * de requêtes dans le centre de calcul
 *
 * <p><strong>Description</strong></p>
 * 
 * Le répartireur de requête s’interpose entre le générateur de requêtes et les machines virtuelles,
 * il reçoit toutes les requêtes du générateur de requêtes et les répartit entre les machine 
 * virtuelles allouées à cette application.
 * 
 * À la réception d’une requête, le répartiteur parcourt la liste des machines virtuelles dont 
 * il dispose et soumet la requête à une des instances (On soumet à l'avm qui a le moins
 * d'instructions à exécuter sous la main).
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * TODO: complete!
 * 
 * <pre>
 * invariant		rdURI != null
 * invariant		managementInboundPortURIdispatcher != null
 * invariant		requestNotificationInboundPortURIdispatcher != null
 * invariant		requestSubmissionInboundPortURIdispatcher != null
 * invariant		requestNotificationInboundPortURI != null
 * invariant		requestSubmissionInboundPortURIs != null && requestSubmissionInboundPortURIs.size() != 0
 * </pre>
 * 
 * <p>Created on : February 1, 2019</p>
 * 
 * @author	<a>Chao LIN</a>
 */
public class RequestDispatcher 
	extends AbstractComponent
	implements RequestSubmissionHandlerI, 
			   RequestNotificationHandlerI,
			   RequestDispatcherManagementI{

	//nom de ce composant
	protected String rdURI;
	
	// stock le temps d'exécution que prend les n dernières requêtes
	protected ArrayBlockingQueue<Long> last_req_durations;
	// stock le temps de début des requêtes
	protected Map<String, Long> req_startTimes;
	
	// liens avec les AVMs
	/**
	 * Key: rsipURI de l'avm
	 * Value: fr.sorbonne_u.datacenter_etudiant.requestdispatcher.AVMtool
	 * AVMtool contient des informations des ports concernant cet avm
	 */
	protected Map<String, AVMtool> reqURIs_avms;
	/** Liste des avms */
	protected ArrayList<AVMtool> avms;
	/** On donne un id local à chaque avm de notre répartiteur pour les traces d'exécution*/
	protected int avm_local_ID = 0;
	
	// lien avec le RequestGenerator
	protected String requestNotificationInboundPortURI ; // RG
	protected RequestNotificationOutboundPort requestNotificationOutboundPort ; // Connect to RG
	
	// InboundPorts appartenant au dispatcher
	protected RequestSubmissionInboundPort requestSubmissionInboundPort ;
	protected RequestNotificationInboundPort requestNotificationInboundPort ;
	protected RequestDispatcherManagementInboundPort	requestDispatcherManagementInboundPort ;

	/**
	 * Créer un répartiteur de requête en donnant son URI et les inbound ports
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	rdURI != null
	 * pre	managementInboundPortURIdispatcher != null
	 * pre	requestNotificationInboundPortURIdispatcher != null
	 * pre	requestSubmissionInboundPortURIdispatcher != null
	 * pre	requestNotificationInboundPortURI != null
	 * pre	requestSubmissionInboundPortURIs != null && requestSubmissionInboundPortURIs.size() != 0
	 * 
	 * post	true			// no postcondition.
	 * </pre>
	 * 
	 * @param rdURI											URI of newly create RD
	 * @param managementInboundPortURIdispatcher			URI of the RD management inbound port.
	 * @param requestNotificationInboundPortURIdispatcher	URI of the request notification inbound port.
	 * @param requestSubmissionInboundPortURIdispatcher		URI of the request submission inbound port.
	 * @param requestNotificationInboundPortURI				URI of the request notification inbound port of request generator.
	 * @param requestSubmissionInboundPortURIs				URI of the request submission inbound ports of VMs.
	 * @throws Exception
	 */
	public RequestDispatcher(
		String rdURI,
		String managementInboundPortURIdispatcher,
		String requestNotificationInboundPortURIdispatcher,
		String requestSubmissionInboundPortURIdispatcher,
		String requestNotificationInboundPortURI, //RG
		ArrayList<String> requestSubmissionInboundPortURIs /* AVMs */) throws Exception {
		
		super(1, 1);
		
		// Preconditions
		assert	rdURI != null ;
		assert	requestSubmissionInboundPortURIdispatcher != null ;
		assert	requestNotificationInboundPortURIdispatcher != null ;
		assert	managementInboundPortURIdispatcher != null ;
		
		assert	requestNotificationInboundPortURI != null ;
		assert	requestSubmissionInboundPortURIs != null ;
		assert  requestSubmissionInboundPortURIs.size() != 0;
		
		//initialisation
		this.rdURI = rdURI;
		this.req_startTimes = new HashMap<String, Long>();
		this.last_req_durations = new ArrayBlockingQueue<Long>(5);
		this.avms = new ArrayList<AVMtool>();
		this.reqURIs_avms = new HashMap<String, AVMtool>();
		
		//init des ports dont dispatcher est le owner
		
		//offered
		/*Submission*/
		this.addOfferedInterface(RequestSubmissionI.class) ;
		this.requestSubmissionInboundPort = new RequestSubmissionInboundPort(requestSubmissionInboundPortURIdispatcher, this);
		this.addPort(this.requestSubmissionInboundPort) ;
		this.requestSubmissionInboundPort.publishPort() ;
		
		/*Notification*/
		this.addOfferedInterface(RequestNotificationI.class) ;
		this.requestNotificationInboundPort = new RequestNotificationInboundPort(requestNotificationInboundPortURIdispatcher, this);
		this.addPort(this.requestNotificationInboundPort) ;
		this.requestNotificationInboundPort.publishPort() ;
		
		/*Management*/
		this.addOfferedInterface(RequestDispatcherManagementI.class);
		this.requestDispatcherManagementInboundPort = new RequestDispatcherManagementInboundPort(managementInboundPortURIdispatcher, this);
		this.addPort(this.requestDispatcherManagementInboundPort);
		this.requestDispatcherManagementInboundPort.publishPort();
		
		//required
		
		/*Notification*/
		this.addRequiredInterface(RequestNotificationI.class) ;
		this.requestNotificationOutboundPort = new RequestNotificationOutboundPort(this) ;
		this.addPort(this.requestNotificationOutboundPort) ;
		this.requestNotificationOutboundPort.publishPort() ;
		
		//pour les AVMs:
		
		/*Submission*/
		this.addRequiredInterface(RequestSubmissionI.class) ;
		for(String requestSubmissionInboundPortURI : requestSubmissionInboundPortURIs) {
			AVMtool tmp = new AVMtool(requestSubmissionInboundPortURI);
			tmp.rsop = new RequestSubmissionOutboundPort(this);
			this.addPort(tmp.rsop);
			tmp.rsop.publishPort() ;
			tmp.local_ID = this.avm_local_ID++;
			
			this.avms.add(tmp);
		}

		//init des ports a connecter
		this.requestNotificationInboundPortURI = requestNotificationInboundPortURI; //RG

		//Postconditions check
		for(AVMtool avm : this.avms) {
			assert avm.rsop != null && avm.rsop instanceof RequestSubmissionI ;
		}
		assert	this.requestNotificationOutboundPort != null && this.requestNotificationOutboundPort instanceof RequestNotificationI ;
	}
	
	
	
	// Component life cycle
	
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI#connectOutboundPorts()
	 */
	@Override
	public void connectOutboundPorts() throws Exception {

		this.doPortConnection(
				this.requestNotificationOutboundPort.getPortURI(),
				requestNotificationInboundPortURI,
				RequestNotificationConnector.class.getCanonicalName()
		) ;  //Connection RG
		for(AVMtool avm : this.avms) {
			this.doPortConnection(
					avm.rsop.getPortURI(), 
					avm.rsipURI, 
					RequestSubmissionConnector.class.getCanonicalName()
			);
		}
	}
	
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{	
		for(AVMtool avm : this.avms) {
			this.doPortDisconnection(avm.rsop.getPortURI());
		}
		this.doPortDisconnection(this.requestNotificationOutboundPort.getPortURI()) ; //deconnection RG
		super.finalise() ;
	}
	
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public void			shutdown() throws ComponentShutdownException
	{

		try {
			this.requestDispatcherManagementInboundPort.unpublishPort();
			this.requestSubmissionInboundPort.unpublishPort() ;
			for(AVMtool avm : this.avms) {
				avm.rsop.unpublishPort();
			}
			this.requestNotificationInboundPort.unpublishPort();
			this.requestNotificationOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}

		super.shutdown();
		
	}
	
	// -------------------------------------------------------------------------
	// Component internal services
	// -------------------------------------------------------------------------
	
	/**
	 * @see fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionHandlerI#acceptRequestSubmission(fr.sorbonne_u.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
		// not used
	}

	/**
	 * @see fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionHandlerI#acceptRequestSubmissionAndNotify(fr.sorbonne_u.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void	acceptRequestSubmissionAndNotify(RequestI r) throws Exception {
		for(AVMtool avm : this.avms) {
			this.logMessage("avm-"+ avm.local_ID +" nbInstrs: "+ avm.nbInstrs);
		}
		
		AVMtool chosen_avm = Collections.min(this.avms);
		RequestSubmissionOutboundPort rsop = chosen_avm.rsop;
		
		chosen_avm.nbInstrs += r.getPredictedNumberOfInstructions();
		this.reqURIs_avms.put(r.getRequestURI(), chosen_avm);

		this.logMessage("ReqDisp. "+ this.rdURI+"| submits ["
				+ r.getRequestURI() +"] to  [avm-"+ chosen_avm.local_ID +"].");
		this.req_startTimes.put(r.getRequestURI(), System.currentTimeMillis());
		rsop.submitRequestAndNotify(r) ;
	}

	/**
	 * @see fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationHandlerI#acceptRequestTerminationNotification(fr.sorbonne_u.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		long duration = System.currentTimeMillis() - this.req_startTimes.remove(r.getRequestURI());
		while(this.last_req_durations.offer(duration) == false) {
			this.last_req_durations.remove();
		}
		
		AVMtool avm_that_sent = this.reqURIs_avms.remove(r.getRequestURI());
		avm_that_sent.nbInstrs -= r.getPredictedNumberOfInstructions();

		this.logMessage("ReqDisp. "+ this.rdURI + "| receives ["
				+ r.getRequestURI() +"] from [avm-"+ avm_that_sent.local_ID +
				"] in "+ duration +"ms") ;
		long mean = this.getAverageReqDuration();
		this.logMessage("mean: "+ mean +", nb: "+ this.last_req_durations.size());
		this.requestNotificationOutboundPort.notifyRequestTermination(r);
	}

	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI#toggleTracingLogging()
	 */
	@Override
	public void toggleTracingLogging() {
		this.toggleTracing();
		this.toggleLogging();
		this.logMessage( "RD " +this.rdURI +" start");
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI#getAverageReqDuration()
	 */
	@Override
	public long getAverageReqDuration() {
		long sum_durations = 0;
		for(Long duration : this.last_req_durations) {
			sum_durations += duration;
		}
		if(sum_durations == 0) {
			return 0;
		}
		return sum_durations / (long) this.last_req_durations.size();
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI#addAVM(String)
	 */
	@Override
	public void addAVM(String avm_reqSubURI) throws Exception {
		AVMtool tmp = new AVMtool(avm_reqSubURI);
		tmp.rsop = new RequestSubmissionOutboundPort(this);
		this.addPort(tmp.rsop);
		tmp.rsop.publishPort() ;
		tmp.local_ID = this.avm_local_ID++;
		this.avms.add(tmp);
		
		this.doPortConnection(
				tmp.rsop.getPortURI(), 
				tmp.rsipURI, 
				RequestSubmissionConnector.class.getCanonicalName()
		);
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI#removeAVM(String)
	 */
	@Override
	public void removeAVM(String avm_rsipURI) throws Exception {
		AVMtool avm_toremove = null;
		for(AVMtool avm : avms) {
			if(avm.rsipURI.equals(avm_rsipURI)) {
				avm_toremove = avm;
				break;
			}
		}
		if(avm_toremove != null) {
			this.doPortDisconnection(avm_toremove.rsop.getPortURI());
			this.avms.remove(avm_toremove);
			avm_toremove.rsop.unpublishPort();
			this.removePort(avm_toremove.rsop);
		}
	}
}
