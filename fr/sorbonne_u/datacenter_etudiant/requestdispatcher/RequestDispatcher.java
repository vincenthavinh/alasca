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
	
	public RequestDispatcher(
		String rdURI,
		String requestNotificationInboundPortURIdispatcher,
		String requestSubmissionInboundPortURIdispatcher,
		String requestNotificationInboundPortURI, //RG
		ArrayList<String> requestSubmissionInboundPortsURI /* AVMs */) throws Exception {
		//String requestSubmissionInboundPortURI /* AVM */) throws Exception {
		
		super(1, 1);
		
		// Preconditions
		assert	rdURI != null ;
		assert	requestSubmissionInboundPortURIdispatcher != null ;
		assert	requestNotificationInboundPortURIdispatcher != null ;
		
		assert	requestNotificationInboundPortURI != null ;
		assert	requestSubmissionInboundPortsURI != null ;
		assert  requestSubmissionInboundPortsURI.size() != 0;
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
		
		//required
		
		/*Submission*/
		this.requestSubmissionOutboundPorts = new ArrayList<RequestSubmissionOutboundPort>();
		this.addRequiredInterface(RequestSubmissionI.class) ;
		for(int i=0; i<requestSubmissionInboundPortsURI.size(); i++) {
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
		this.requestSubmissionInboundPortsURI = requestSubmissionInboundPortsURI; //aVMs
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

		try {
			
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
			
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}
	
	
	@Override
	public void			finalise() throws Exception
	{	
		for(RequestSubmissionOutboundPort rsop : requestSubmissionOutboundPorts) {
			if(rsop.connected()) {
				this.doPortDisconnection(rsop.getPortURI());
			}
		}
		//this.doPortDisconnection(this.requestSubmissionOutboundPort.getPortURI()); //deconnection aVMs
		this.doPortDisconnection(this.requestNotificationOutboundPort.getPortURI()) ; //deconnection RG

		super.finalise() ;
	}
	
	@Override
	public void			shutdown() throws ComponentShutdownException
	{

		try {
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
//		String aVMuri = this.requestSubmissionInboundPortsURI.get(index++%this.requestSubmissionInboundPortsURI.size());
//		this.doPortConnection(
//			this.requestSubmissionOutboundPort.getPortURI(),
//			aVMuri,
//			RequestSubmissionConnector.class.getCanonicalName()) ;  //Connection aVM
		
		rsop.submitRequestAndNotify(r) ;
		
	//	this.doPortDisconnection(this.requestSubmissionOutboundPort.getPortURI());
	}
	
	public void dispatchRequestWithOutNotification(RequestI r) throws Exception{
		RequestSubmissionOutboundPort rsop = requestSubmissionOutboundPorts.get(index++%this.requestSubmissionInboundPortsURI.size());
//		String aVMuri = this.requestSubmissionInboundPortsURI.get(index++%this.requestSubmissionInboundPortsURI.size());
//		this.doPortConnection(
//			this.requestSubmissionOutboundPort.getPortURI(),
//			aVMuri,
//			RequestSubmissionConnector.class.getCanonicalName()) ;  //Connection aVM
//		
		rsop.submitRequest(r) ;
		
		//this.doPortDisconnection(this.requestSubmissionOutboundPort.getPortURI());
	}
}
