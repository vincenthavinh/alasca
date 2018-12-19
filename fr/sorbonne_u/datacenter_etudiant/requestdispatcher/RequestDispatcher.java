package fr.sorbonne_u.datacenter_etudiant.requestdispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM.ApplicationVMPortTypes;
import fr.sorbonne_u.datacenter.software.applicationvm.connectors.ApplicationVMIntrospectionConnector;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMIntrospectionI;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMIntrospectionOutboundPort;
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

public class RequestDispatcher 
	extends AbstractComponent
	implements RequestSubmissionHandlerI, 
			   RequestNotificationHandlerI{

	//nom de ce composant
	protected String rdURI;
	
	// liste de port des VMs
	protected ArrayList<String> requestSubmissionInboundPortsURI ; // AVMs
	protected int index; // l'avm (index%(nb AVMs)) recoit la requete
	protected ArrayList<String> introspectionInboundPortsURI ;
	
	// port de la RequestGenerator
	protected String requestNotificationInboundPortURI ; // RG
	
	// ports appartenant au dispatcher
	protected RequestSubmissionInboundPort requestSubmissionInboundPort ;
	protected HashMap<String, RequestSubmissionOutboundPort> requestSubmissionOutboundPorts ; // (inboundPort, outboundPort)
	protected RequestNotificationInboundPort requestNotificationInboundPort ;
	protected RequestNotificationOutboundPort requestNotificationOutboundPort ;
	protected ArrayList<ApplicationVMIntrospectionOutboundPort> introspectionOutBoundPorts ;
	
	protected RequestDispatcherManagementInboundPort	requestDispatcherManagementInboundPort ;

	protected Map<String, Long> timeStamp;
	
	public RequestDispatcher(
		String rdURI,
		String managementInboundPortURIdispatcher,
		String requestNotificationInboundPortURIdispatcher,
		String requestSubmissionInboundPortURIdispatcher,
		String requestNotificationInboundPortURI, //RG
		ArrayList<String> requestSubmissionInboundPortURIs /* AVMs */,
		ArrayList<String> introspectionInboundPortURIs /* AVMs introspection */) throws Exception {
		
		super(1, 1);
		
		// Preconditions
		assert	rdURI != null ;
		assert	requestSubmissionInboundPortURIdispatcher != null ;
		assert	requestNotificationInboundPortURIdispatcher != null ;
		assert	managementInboundPortURIdispatcher != null ;
		
		assert	requestNotificationInboundPortURI != null ;
		assert	requestSubmissionInboundPortURIs != null ;
		assert  requestSubmissionInboundPortURIs.size() != 0;
		assert  introspectionInboundPortURIs != null;
		assert  introspectionInboundPortURIs.size() != 0 ;
		
		//initialisation
		this.rdURI = rdURI;
		this.index = 0;
		this.timeStamp = new HashMap<String, Long>();
		
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
		
		/*Submission*/
		this.requestSubmissionOutboundPorts = new HashMap<String, RequestSubmissionOutboundPort>();
		this.addRequiredInterface(RequestSubmissionI.class) ;
		for(String requestSubmissionInboundPortURI : requestSubmissionInboundPortURIs) {
			this.requestSubmissionOutboundPorts.put(requestSubmissionInboundPortURI, new RequestSubmissionOutboundPort(this)) ;
			this.addPort(this.requestSubmissionOutboundPorts.get(requestSubmissionInboundPortURI)) ;
			this.requestSubmissionOutboundPorts.get(requestSubmissionInboundPortURI).publishPort() ;
		}
		
		/*Notification*/
		this.addRequiredInterface(RequestNotificationI.class) ;
		this.requestNotificationOutboundPort = new RequestNotificationOutboundPort(this) ;
		this.addPort(this.requestNotificationOutboundPort) ;
		this.requestNotificationOutboundPort.publishPort() ;
		
		/*Introspection*/
		this.introspectionOutBoundPorts = new ArrayList<ApplicationVMIntrospectionOutboundPort>();
		this.addRequiredInterface(ApplicationVMIntrospectionI.class);
		for(int i=0; i<introspectionInboundPortURIs.size(); i++) {
			this.introspectionOutBoundPorts.add(new ApplicationVMIntrospectionOutboundPort(this));
			this.addPort(this.introspectionOutBoundPorts.get(i));
			this.introspectionOutBoundPorts.get(i).publishPort();
		}
		
		//init des ports a connecter
		this.requestSubmissionInboundPortsURI = requestSubmissionInboundPortURIs; //aVMs
		this.requestNotificationInboundPortURI = requestNotificationInboundPortURI; //RG
		this.introspectionInboundPortsURI = introspectionInboundPortURIs; // aVMs
	
		//Postconditions check
		for(String rsip : requestSubmissionOutboundPorts.keySet()) {
			assert	requestSubmissionOutboundPorts.get(rsip) != null && requestSubmissionOutboundPorts.get(rsip) instanceof RequestSubmissionI ;
		}
		assert	this.requestNotificationOutboundPort != null && this.requestNotificationOutboundPort instanceof RequestNotificationI ;
		for(ApplicationVMIntrospectionOutboundPort avmiop : introspectionOutBoundPorts) {
			assert avmiop != null && avmiop instanceof ApplicationVMIntrospectionI ;
		}
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
		
		for(String requestSubmissionInboundPortURI : requestSubmissionInboundPortsURI) {
			this.doPortConnection(
					this.requestSubmissionOutboundPorts.get(requestSubmissionInboundPortURI).getPortURI(),
					requestSubmissionInboundPortURI,
					RequestSubmissionConnector.class.getCanonicalName()
			) ;  //Connection aVM
		}
		
		for(int i=0; i<introspectionInboundPortsURI.size(); i++) {
			this.doPortConnection(
					this.introspectionOutBoundPorts.get(i).getPortURI(),
					introspectionInboundPortsURI.get(i),
					ApplicationVMIntrospectionConnector.class.getCanonicalName()
			);
		}
	}
	
	
	@Override
	public void			finalise() throws Exception
	{	
		for(String requestSubmissionInboundPortURI : requestSubmissionInboundPortsURI) {
			this.doPortDisconnection(
				requestSubmissionOutboundPorts.get(requestSubmissionInboundPortURI).getPortURI()
			);
		}
		this.doPortDisconnection(this.requestNotificationOutboundPort.getPortURI()) ; //deconnection RG
		for(ApplicationVMIntrospectionOutboundPort avmiop : introspectionOutBoundPorts) {
			this.doPortDisconnection(avmiop.getPortURI());
		}
		super.finalise() ;
	}
	
	@Override
	public void			shutdown() throws ComponentShutdownException
	{

		try {
			this.requestDispatcherManagementInboundPort.unpublishPort();
			this.requestSubmissionInboundPort.unpublishPort() ;
			for(String requestSubmissionInboundPortURI : requestSubmissionInboundPortsURI) {
				requestSubmissionOutboundPorts.get(requestSubmissionInboundPortURI).unpublishPort();
			}
			this.requestNotificationInboundPort.unpublishPort();
			this.requestNotificationOutboundPort.unpublishPort();
			for(ApplicationVMIntrospectionOutboundPort avmiop : introspectionOutBoundPorts) {
				avmiop.unpublishPort();
			}
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}

		super.shutdown();
		
	}
	
	// -------------------------------------------------------------------------
	// Component internal services
	// -------------------------------------------------------------------------

	
	@Override
	public void	acceptRequestSubmissionAndNotify(RequestI r) throws Exception {
		this.timeStamp.put(r.getRequestURI(), System.currentTimeMillis());
		this.logMessage("ReqDisp. "+ this.rdURI+"| accept request "+ r.getRequestURI()+" submission and dispatch to AVMs.");
		this.dispatchRequest(r);
	}
	
	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
		this.logMessage("ReqDisp. "+ this.rdURI+"| accept request "+ r.getRequestURI()+" submission and dispatch to AVMs.");
		this.dispatchRequestWithOutNotification(r);
	}

	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		long time = System.currentTimeMillis() - this.timeStamp.remove(r.getRequestURI());
		this.logMessage("ReqDisp. "+ this.rdURI +
				"| is notified that request "+ r.getRequestURI() +
				" has ended with "+ time +"ms and notify the request generator.") ;
		this.requestNotificationOutboundPort.notifyRequestTermination(r);
	}
	
	// TODO changer la méthode de dispatch
	public void dispatchRequest(RequestI r) throws Exception{
		RequestSubmissionOutboundPort rsop = requestSubmissionOutboundPorts.get(getIdleAVM());
		rsop.submitRequestAndNotify(r) ;
	}
	
	public void dispatchRequestWithOutNotification(RequestI r) throws Exception{
		RequestSubmissionOutboundPort rsop = requestSubmissionOutboundPorts.get(getIdleAVM());
		rsop.submitRequest(r) ;
	}
	
	// return type: URI de requestSubmissionInboundPort d'une avm
	private String getIdleAVM() {
		// renvoie l'avm libre
		try {
			for(ApplicationVMIntrospectionOutboundPort avmiop : introspectionOutBoundPorts) {
				if(avmiop.getDynamicState().isIdle()) {
					return avmiop.getAVMPortsURI().get(ApplicationVMPortTypes.REQUEST_SUBMISSION);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// tous les avms sont occupees, renvoie une avm occupee
		return requestSubmissionInboundPortsURI.get(index++%requestSubmissionInboundPortsURI.size());
	}

	public void toggleTracingLogging() {
		System.out.println("RD "+this.rdURI+" tooggled...");
		this.toggleTracing();
		this.toggleLogging();
	}
}
