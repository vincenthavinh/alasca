package fr.sorbonne_u.datacenter_etudiant.admissioncontroller;

import java.util.ArrayList;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.AbstractPort;
import fr.sorbonne_u.components.pre.dcc.DynamicComponentCreator;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
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
	protected ArrayList<String> computerServicesInboundPortURIs_CP;
	
	// ports appartenant au AdmissionController
	protected RequestSubmissionInboundPort requestSubmissionInboundPort_AC;
	protected RequestSubmissionOutboundPort requestSubmissionOutboundPort_AC;
	protected RequestNotificationInboundPort requestNotificationInboundPort_AC;
	protected RequestNotificationOutboundPort requestNotificationOutboundPort_AC;
	protected ComputerServicesOutboundPort computerServicesOutboundPort_AC;
	protected ApplicationVMManagementOutboundPort applicationVMManagementOutboundPort_AC;
	
	
	public AdmissionController(
			String acURI,
			String requestNotificationInboundPortURI_RG, //Request generator
			String requestSubmissionInboundPortURI_AC, //Admission controller
			ArrayList<String> computerServicesInboundPortURIs_CP //Computers
	) throws Exception {
		super(1,1);
		
		//Preconditions
		assert acURI != null;
		assert requestNotificationInboundPortURI_RG != null;
		assert requestSubmissionInboundPortURI_AC != null;
		assert computerServicesInboundPortURIs_CP != null;
		assert computerServicesInboundPortURIs_CP.size() != 0;
		
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
		
		/*Computer Service*/
		this.addRequiredInterface(ComputerServicesI.class) ;
		this.computerServicesOutboundPort_AC = new ComputerServicesOutboundPort(this) ;
		this.addPort(this.computerServicesOutboundPort_AC) ;
		this.computerServicesOutboundPort_AC.publishPort() ;
		
		/*Application VM Management*/
		this.addRequiredInterface(ApplicationVMManagementI.class) ;
		this.applicationVMManagementOutboundPort_AC = new ApplicationVMManagementOutboundPort(this) ;
		this.addPort(this.applicationVMManagementOutboundPort_AC) ;
		this.applicationVMManagementOutboundPort_AC.publishPort() ;
		
		//init des ports a connecter
		this.requestNotificationInboundPortURI_RG = requestNotificationInboundPortURI_RG;
		this.computerServicesInboundPortURIs_CP = computerServicesInboundPortURIs_CP;
		
		//Postconditions
		assert  this.requestNotificationInboundPort_AC != null && this.requestNotificationInboundPort_AC instanceof RequestNotificationI;
		assert	this.requestSubmissionOutboundPort_AC != null && this.requestSubmissionOutboundPort_AC instanceof RequestSubmissionI ;
		assert	this.requestNotificationOutboundPort_AC != null && this.requestNotificationOutboundPort_AC instanceof RequestNotificationI ;
		assert  this.computerServicesOutboundPort_AC != null && this.computerServicesOutboundPort_AC instanceof ComputerServicesI ;
		assert  this.applicationVMManagementOutboundPort_AC != null && this.applicationVMManagementOutboundPort_AC instanceof ApplicationVMManagementI;
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
		if(this.computerServicesOutboundPort_AC.connected()) {
			this.doPortDisconnection(this.computerServicesOutboundPort_AC.getPortURI()) ;
		}
		if(this.applicationVMManagementOutboundPort_AC.connected()) {
			this.doPortDisconnection(this.applicationVMManagementOutboundPort_AC.getPortURI()) ;
		}
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
			this.computerServicesOutboundPort_AC.unpublishPort() ;
			this.applicationVMManagementOutboundPort_AC.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}

		super.shutdown();
	}
	
	// -------------------------------------------------------------------------
	// Component internal services
	// -------------------------------------------------------------------------

	
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

	public void executeInComputer(String computerServicesInboundPortURI) throws Exception{
		this.doPortConnection(
				this.computerServicesOutboundPort_AC.getPortURI(),
				computerServicesInboundPortURI,
				ComputerServicesConnector.class.getCanonicalName()) ;
		
		AllocatedCore ac = this.computerServicesOutboundPort_AC.allocateCore() ;
		if(ac != null) {
			// TODO
			String avmip = AbstractPort.generatePortURI(ApplicationVMManagementI.class);
			String avmURI;
			ApplicationVM vm;
		}
		
		this.doPortDisconnection(this.computerServicesOutboundPort_AC.getPortURI()) ;
	}
	
}
