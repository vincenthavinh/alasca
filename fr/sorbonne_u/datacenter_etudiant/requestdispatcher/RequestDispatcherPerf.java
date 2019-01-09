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
import fr.sorbonne_u.datacenter_etudiant.performanceController.connectors.PerformanceControllerManagementConnector;
import fr.sorbonne_u.datacenter_etudiant.performanceController.interfaces.PerformanceControllerManagementI;
import fr.sorbonne_u.datacenter_etudiant.performanceController.ports.PerformanceControllerManagementOutboundPort;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherPerfManagementI;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.ports.RequestDispatcherPerfManagementInboundPort;

public class RequestDispatcherPerf 
	extends AbstractComponent
	implements RequestSubmissionHandlerI, 
			   RequestNotificationHandlerI{

	//nom de ce composant
	protected String rdURI;
	
	// temps des 10 dernieres requetes
	ArrayBlockingQueue<Long> last_req_durations;
	protected Map<String, Long> req_startTimes;
	
	// liens avec les AVMs
	protected Map<String, AVMtool> reqURIs_avms;
	protected ArrayList<AVMtool> avms;
	protected int avm_local_ID = 0;
	protected int index = 0;
	protected int nb_req = 0;
	
	// lien avec le RequestGenerator
	protected String requestNotificationInboundPortURI ; // RG
	protected RequestNotificationOutboundPort requestNotificationOutboundPort ;
	
	// lien avec le PerformanceController
	protected String pc_managementInboundPortURI; // PC
	protected PerformanceControllerManagementOutboundPort pc_managementOutboundPort; 
	
	// InboundPorts appartenant au dispatcher
	protected RequestSubmissionInboundPort requestSubmissionInboundPort ;
	protected RequestNotificationInboundPort requestNotificationInboundPort ;
	protected RequestDispatcherPerfManagementInboundPort	requestDispatcherManagementInboundPort ;

	
	public RequestDispatcherPerf(
		String rdURI,
		String managementInboundPortURIdispatcher,
		String requestNotificationInboundPortURIdispatcher,
		String requestSubmissionInboundPortURIdispatcher,
		String requestNotificationInboundPortURI, //RG
		String managementInboundPortURIPerformanceController,
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
		this.last_req_durations = new ArrayBlockingQueue<Long>(10);
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
		this.addOfferedInterface(RequestDispatcherPerfManagementI.class);
		this.requestDispatcherManagementInboundPort = new RequestDispatcherPerfManagementInboundPort(managementInboundPortURIdispatcher, this);
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
		
		/*Performance Controller*/
		this.pc_managementInboundPortURI = managementInboundPortURIPerformanceController;
		this.addRequiredInterface(PerformanceControllerManagementI.class);
		this.pc_managementOutboundPort = new PerformanceControllerManagementOutboundPort(this);
		this.addPort(this.pc_managementOutboundPort);
		this.pc_managementOutboundPort.publishPort();

		//init des ports a connecter
		this.requestNotificationInboundPortURI = requestNotificationInboundPortURI; //RG

		//Postconditions check
		for(AVMtool avm : this.avms) {
			assert avm.rsop != null && avm.rsop instanceof RequestSubmissionI ;
		}
		assert	this.requestNotificationOutboundPort != null && this.requestNotificationOutboundPort instanceof RequestNotificationI ;
	}
	
	
	
	// Component life cycle
	
	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;
	}
	
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
		this.doPortConnection(
				this.pc_managementOutboundPort.getPortURI(),
				this.pc_managementInboundPortURI,
				PerformanceControllerManagementConnector.class.getCanonicalName()
			); // connection PC
	}
	
	@Override
	public void			finalise() throws Exception
	{	
		for(AVMtool avm : this.avms) {
			this.doPortDisconnection(avm.rsop.getPortURI());
		}
		this.doPortDisconnection(this.requestNotificationOutboundPort.getPortURI()) ; //deconnection RG
		this.doPortDisconnection(this.pc_managementOutboundPort.getPortURI()); // deconnection PC
		super.finalise() ;
	}
	
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
			this.pc_managementOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}

		super.shutdown();
		
	}
	
	// -------------------------------------------------------------------------
	// Component internal services
	// -------------------------------------------------------------------------
	
	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
		// not used
	}

	
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
		this.logMessage("mean: "+ mean +", nb: "+ 
				this.last_req_durations.size());
		this.nb_req++;
		if(nb_req%5 == 0) {
			this.pc_managementOutboundPort.checkPerformance(mean);
		}
		this.requestNotificationOutboundPort.notifyRequestTermination(r);
	}

	public void toggleTracingLogging() {
		this.toggleTracing();
		this.toggleLogging();
	}
	
	
	private long getAverageReqDuration() {
		long sum_durations = 0;
		for(Long duration : this.last_req_durations) {
			sum_durations += duration;
		}
		
		return sum_durations / (long) this.last_req_durations.size();
	}
}
