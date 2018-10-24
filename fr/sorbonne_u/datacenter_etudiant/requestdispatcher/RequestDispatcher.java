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
	protected int index;
	//protected String requestSubmissionInboundPortURI ; // AVM
	
	protected String requestNotificationInboundPortURI ; // RG
	
	// ports appartenant au dispatcher
	protected RequestSubmissionInboundPort requestSubmissionInboundPort ;
	protected RequestSubmissionOutboundPort requestSubmissionOutboundPort ;
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
		this.addRequiredInterface(RequestSubmissionI.class) ;
		this.requestSubmissionOutboundPort = new RequestSubmissionOutboundPort(this) ;
		this.addPort(this.requestSubmissionOutboundPort) ;
		this.requestSubmissionOutboundPort.publishPort() ;
		
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
		assert	this.requestSubmissionOutboundPort != null && this.requestSubmissionOutboundPort instanceof RequestSubmissionI ;
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
			
//			this.doPortConnection(
//					this.requestSubmissionOutboundPort.getPortURI(),
//					requestSubmissionInboundPortURI,
//					RequestSubmissionConnector.class.getCanonicalName()) ;  //Connection aVM
			
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}
	
	
	@Override
	public void			finalise() throws Exception
	{	
		if(this.requestSubmissionOutboundPort.connected()) {
			this.doPortDisconnection(this.requestNotificationOutboundPort.getPortURI());
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
			this.requestSubmissionOutboundPort.unpublishPort() ;
			this.requestNotificationInboundPort.unpublishPort();
			this.requestNotificationOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}

		super.shutdown();
		
	}
	
	@Override
	public void	acceptRequestSubmissionAndNotify(RequestI r) throws Exception {
		this.logMessage(
				"Request dispatcher "+this.rdURI+" accept request "+ r.getRequestURI()+" submission and dispatch to AVMs.");
		this.dispatchRequest(r);
	}
	
	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
		this.requestSubmissionOutboundPort.submitRequest(r) ;
	}

	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		this.logMessage("Request dispatcher " + this.rdURI +
				" is notified that request "+ r.getRequestURI() +
				" has ended and notify request generator.") ;
		this.requestNotificationOutboundPort.notifyRequestTermination(r);
	}
	
	public void dispatchRequest(RequestI r) throws Exception{
		String aVMuri = this.requestSubmissionInboundPortsURI.get(index++%this.requestSubmissionInboundPortsURI.size());
		this.doPortConnection(
			this.requestSubmissionOutboundPort.getPortURI(),
			aVMuri,
			RequestSubmissionConnector.class.getCanonicalName()) ;  //Connection aVM
		
		this.requestSubmissionOutboundPort.submitRequestAndNotify(r) ;
		
		this.doPortDisconnection(this.requestSubmissionOutboundPort.getPortURI());
	}
}
