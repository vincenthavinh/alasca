package fr.sorbonne_u.datacenter_etudiant.admissioncontroller;

import java.util.ArrayList;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.software.connectors.RequestNotificationConnector;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionOutboundPort;

public class AdmissionController 
	extends AbstractComponent
	implements RequestSubmissionHandlerI, 
			   RequestNotificationHandlerI{
	
	//nom de ce composant
	protected String acURI;
	
	//port de la RequestGenerator
	protected String requestNotificationInboundPortURI_RG;
	
	//ports des Computers
	protected ArrayList<String> computerServicesInboundPortURI_CP;
	
	// ports appartenant au AdmissionController
	protected RequestSubmissionInboundPort requestSubmissionInboundPort_AC;
	protected RequestSubmissionOutboundPort requestSubmissionOutboundPort_AC;
	protected RequestNotificationInboundPort requestNotificationInboundPort_AC;
	protected RequestNotificationOutboundPort requestNotificationOutboundPort_AC;
	
	public AdmissionController(
			String acURI,
			String requestNotificationInboundPortURI_RG, //Request generator
			String requestSubmissionInboundPortURI_AC, //Admission controller
			ArrayList<String> computerServicesInboundPortURI_CP //Computers
	) throws Exception {
		super(1,1);
		
		//Preconditions
		assert acURI != null;
		assert requestNotificationInboundPortURI_RG != null;
		assert requestSubmissionInboundPortURI_AC != null;
		assert computerServicesInboundPortURI_CP != null;
		assert computerServicesInboundPortURI_CP.size() != 0;
		
		//Initilisation
		this.acURI = acURI;
		
		//init des ports dont admission controller est le owner
		
		//offered
		
		/*Submission*/
		this.addOfferedInterface(RequestSubmissionI.class) ;
		this.requestSubmissionInboundPort_AC = new RequestSubmissionInboundPort(requestSubmissionInboundPortURI_AC, this);
		this.addPort(this.requestSubmissionInboundPort_AC) ;
		this.requestSubmissionInboundPort_AC.publishPort() ;
		
		/*Notification*/
		this.addOfferedInterface(RequestNotificationI.class) ;
		this.requestNotificationInboundPort_AC = new RequestNotificationInboundPort(this);
		this.addPort(this.requestNotificationInboundPort_AC) ;
		this.requestNotificationInboundPort_AC.publishPort() ;
		
		//required
		
		/*Submission*/
		this.addRequiredInterface(RequestSubmissionI.class) ;
		this.requestSubmissionOutboundPort_AC = new RequestSubmissionOutboundPort(this) ;
		this.addPort(this.requestSubmissionOutboundPort_AC) ;
		this.requestSubmissionOutboundPort_AC.publishPort() ;
		
		/*Notification*/
		this.addRequiredInterface(RequestNotificationI.class) ;
		this.requestNotificationOutboundPort_AC = new RequestNotificationOutboundPort(this) ;
		this.addPort(this.requestNotificationOutboundPort_AC) ;
		this.requestNotificationOutboundPort_AC.publishPort() ;
		
		//init des ports a connecter
		this.requestNotificationInboundPortURI_RG = requestNotificationInboundPortURI_RG;
		this.computerServicesInboundPortURI_CP = computerServicesInboundPortURI_CP;
		
		//Postconditions
		assert  this.requestNotificationInboundPort_AC != null && this.requestNotificationInboundPort_AC instanceof RequestNotificationI;
		assert	this.requestSubmissionOutboundPort_AC != null && this.requestSubmissionOutboundPort_AC instanceof RequestSubmissionI ;
		assert	this.requestNotificationOutboundPort_AC != null && this.requestNotificationOutboundPort_AC instanceof RequestNotificationI ;
	}

	// Component life cycle
	
	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;

		try {
			this.doPortConnection(
					this.requestNotificationOutboundPort_AC.getPortURI(),
					requestNotificationInboundPortURI_RG,
					RequestNotificationConnector.class.getCanonicalName()) ;  //Connection RG
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}
	
	@Override
	public void			finalise() throws Exception
	{	
		if(this.requestSubmissionOutboundPort_AC.connected()) {
			this.doPortDisconnection(this.requestNotificationOutboundPort_AC.getPortURI());
		}
		this.doPortDisconnection(this.requestNotificationOutboundPort_AC.getPortURI()) ; //deconnection RG

		super.finalise() ;
	}
	
	@Override
	public void			shutdown() throws ComponentShutdownException
	{

		try {
			this.requestSubmissionInboundPort_AC.unpublishPort() ;
			this.requestSubmissionOutboundPort_AC.unpublishPort() ;
			this.requestNotificationInboundPort_AC.unpublishPort();
			this.requestNotificationOutboundPort_AC.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}

		super.shutdown();
		
	}
	
	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		this.logMessage("Admission controller " + this.acURI +
				" is notified that request "+ r.getRequestURI() +
				" has ended and notify the request generator.") ;
		this.requestNotificationOutboundPort_AC.notifyRequestTermination(r);
	}

	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void acceptRequestSubmissionAndNotify(RequestI r) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
