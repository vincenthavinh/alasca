package fr.sorbonne_u.datacenter_etudiant;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionOutboundPort;

public class RequestDispatcher extends AbstractComponent {

	//nom de ce composant
	protected String rdURI;
	
	
	//ports fournis
	protected RequestSubmissionInboundPort requestSubmissionInboundPort ;
	protected RequestNotificationInboundPort requestNotificationInboundPort ;
	
	//ports requis
	protected RequestSubmissionOutboundPort requestSubmissionOutboundPort ;
	protected RequestNotificationOutboundPort requestNotificationOutboundPort ;
	
	
	
	public RequestDispatcher(
		String rdURI,
		String requestSubmissionInboundPortURI,
		String requestNotificationInboundPortURI) throws Exception {
		
		super(1, 1);
		
		// Preconditions
		assert	rdURI != null ;
		assert	requestSubmissionInboundPortURI != null ;
		assert	requestNotificationInboundPortURI != null ;
		
		
		this.rdURI = rdURI;
		
		//init des ports fournisseurs
		this.addOfferedInterface(RequestSubmissionI.class) ;
		this.requestSubmissionInboundPort = new RequestSubmissionInboundPort(requestSubmissionInboundPortURI, this);
		this.addPort(this.requestSubmissionInboundPort) ;
		this.requestSubmissionInboundPort.publishPort() ;
		
		this.addOfferedInterface(RequestNotificationI.class) ;
		this.requestNotificationInboundPort = new RequestNotificationInboundPort(requestNotificationInboundPortURI, this);
		this.addPort(this.requestNotificationInboundPort) ;
		this.requestNotificationInboundPort.publishPort() ;
		
		//init des ports requis
		this.addRequiredInterface(RequestSubmissionI.class) ;
		this.requestSubmissionOutboundPort = new RequestSubmissionOutboundPort(this) ;
		this.addPort(this.requestSubmissionOutboundPort) ;
		this.requestSubmissionOutboundPort.publishPort() ;
		
		this.addRequiredInterface(RequestNotificationI.class) ;
		this.requestNotificationOutboundPort = new RequestNotificationOutboundPort(this) ;
		this.addPort(this.requestNotificationOutboundPort) ;
		this.requestNotificationOutboundPort.publishPort() ;
			
	
		//Postconditions check
		assert	this.requestSubmissionOutboundPort != null && this.requestSubmissionOutboundPort instanceof RequestSubmissionI ;
		assert	this.requestNotificationOutboundPort != null && this.requestNotificationOutboundPort instanceof RequestNotificationI ;
		
		
	}
/*
	public void			start() throws ComponentStartException
	{
		super.start() ;

		try {
			this.doPortConnection(
					this.requestSubmissionOutboundPort.getPortURI(),
					requestSubmissionInboundPortURI,
					RequestSubmissionConnector.class.getCanonicalName()) ;
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}
	*/
	
}
