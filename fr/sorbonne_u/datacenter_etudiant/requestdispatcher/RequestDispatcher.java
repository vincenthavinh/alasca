package fr.sorbonne_u.datacenter_etudiant.requestdispatcher;

import java.util.ArrayList;

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

public class RequestDispatcher 
	extends AbstractComponent
	implements RequestSubmissionHandlerI, 
			   RequestNotificationHandlerI{

	//nom de ce composant
	protected String rdURI;
	
	// liste de port des VMs
	protected ArrayList<String> requestSubmissionInboundPortsURI ; // AVMs
	protected int index; // l'avm (index%(nb AVMs)) recoit la requete
	//protected String requestSubmissionInboundPortURI ; // AVM
	
	// port de la RequestGenerator
	protected String requestNotificationInboundPortURI ; // RG
	
	// ports appartenant au dispatcher
	protected RequestSubmissionInboundPort requestSubmissionInboundPort ;
	protected ArrayList<RequestSubmissionOutboundPort> requestSubmissionOutboundPorts ;
	protected RequestNotificationInboundPort requestNotificationInboundPort ;
	protected RequestNotificationOutboundPort requestNotificationOutboundPort ;
	
	protected RequestDispatcherManagementInboundPort	requestDispatcherManagementInboundPort ;
	
	public RequestDispatcher(
		String rdURI,
		String managementInboundPortURIdispatcher,
		String requestNotificationInboundPortURIdispatcher,
		String requestSubmissionInboundPortURIdispatcher,
		String requestNotificationInboundPortURI, //RG
		ArrayList<String> requestSubmissionInboundPortURIs /* AVMs */) throws Exception {
		//String requestSubmissionInboundPortURI /* AVM */) throws Exception {
		
		super(1, 1);
		
		// Preconditions
		assert	rdURI != null ;
		assert	requestSubmissionInboundPortURIdispatcher != null ;
		assert	requestNotificationInboundPortURIdispatcher != null ;
		assert	managementInboundPortURIdispatcher != null ;
		
		assert	requestNotificationInboundPortURI != null ;
		assert	requestSubmissionInboundPortURIs != null ;
		assert  requestSubmissionInboundPortURIs.size() != 0;
		//assert	requestSubmissionInboundPortURI != null ;
		
		//initialisation
		this.rdURI = rdURI;
		this.index = 0;
		
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
		this.requestSubmissionOutboundPorts = new ArrayList<RequestSubmissionOutboundPort>();
		this.addRequiredInterface(RequestSubmissionI.class) ;
		for(int i=0; i<requestSubmissionInboundPortURIs.size(); i++) {
			this.requestSubmissionOutboundPorts.add(new RequestSubmissionOutboundPort(this)) ;
			this.addPort(this.requestSubmissionOutboundPorts.get(i)) ;
			this.requestSubmissionOutboundPorts.get(i).publishPort() ;
		}
		
		/*Notification*/
		this.addRequiredInterface(RequestNotificationI.class) ;
		this.requestNotificationOutboundPort = new RequestNotificationOutboundPort(this) ;
		this.addPort(this.requestNotificationOutboundPort) ;
		this.requestNotificationOutboundPort.publishPort() ;
		
		//init des ports a connecter
		this.requestSubmissionInboundPortsURI = requestSubmissionInboundPortURIs; //aVMs
		//this.requestSubmissionInboundPortURI = requestSubmissionInboundPortURI; //aVM
		this.requestNotificationInboundPortURI = requestNotificationInboundPortURI; //RG
	
		//Postconditions check
		for(RequestSubmissionOutboundPort rsop : requestSubmissionOutboundPorts) {
			assert	rsop != null && rsop instanceof RequestSubmissionI ;
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
				RequestNotificationConnector.class.getCanonicalName()) ;  //Connection RG
		
		for(int i=0; i<requestSubmissionInboundPortsURI.size(); i++) {
			this.doPortConnection(
					this.requestSubmissionOutboundPorts.get(i).getPortURI(),
					requestSubmissionInboundPortsURI.get(i),
					RequestSubmissionConnector.class.getCanonicalName()) ;  //Connection aVM
		}
	}
	
	
	@Override
	public void			finalise() throws Exception
	{	
		for(RequestSubmissionOutboundPort rsop : requestSubmissionOutboundPorts) {
				this.doPortDisconnection(rsop.getPortURI());
		}
		//this.doPortDisconnection(this.requestSubmissionOutboundPort.getPortURI()); //deconnection aVMs
		this.doPortDisconnection(this.requestNotificationOutboundPort.getPortURI()) ; //deconnection RG
		super.finalise() ;
	}
	
	@Override
	public void			shutdown() throws ComponentShutdownException
	{

		try {
			this.requestDispatcherManagementInboundPort.unpublishPort();
			this.requestSubmissionInboundPort.unpublishPort() ;
			for(RequestSubmissionOutboundPort rsop : requestSubmissionOutboundPorts) {
				rsop.unpublishPort() ;
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

	
	@Override
	public void	acceptRequestSubmissionAndNotify(RequestI r) throws Exception {
		this.logMessage(
				"Request dispatcher "+this.rdURI+" accept request "+ r.getRequestURI()+" submission and dispatch to AVMs.");
		this.dispatchRequest(r);
	}
	
	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
		this.logMessage(
				"Request dispatcher "+this.rdURI+" accept request "+ r.getRequestURI()+" submission and dispatch to AVMs.");
		this.dispatchRequestWithOutNotification(r);
	}

	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		this.logMessage("Request dispatcher " + this.rdURI +
				" is notified that request "+ r.getRequestURI() +
				" has ended and notify the request generator.") ;
		this.requestNotificationOutboundPort.notifyRequestTermination(r);
	}
	
	public void dispatchRequest(RequestI r) throws Exception{
		RequestSubmissionOutboundPort rsop = requestSubmissionOutboundPorts.get(index++%this.requestSubmissionInboundPortsURI.size());
		rsop.submitRequestAndNotify(r) ;
	}
	
	public void dispatchRequestWithOutNotification(RequestI r) throws Exception{
		RequestSubmissionOutboundPort rsop = requestSubmissionOutboundPorts.get(index++%this.requestSubmissionInboundPortsURI.size());
		rsop.submitRequest(r) ;
	}



	public void toggleTracingLogging() {
		System.out.println("RD "+this.rdURI+" tooggled...");
		this.toggleTracing();
		this.toggleLogging();
	}
}
