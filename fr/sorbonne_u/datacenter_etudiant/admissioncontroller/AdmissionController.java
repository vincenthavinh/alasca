package fr.sorbonne_u.datacenter_etudiant.admissioncontroller;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.pre.dcc.connectors.DynamicComponentCreationConnector;
import fr.sorbonne_u.components.pre.dcc.interfaces.DynamicComponentCreationI;
import fr.sorbonne_u.components.pre.dcc.ports.DynamicComponentCreationOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.connectors.ApplicationNotificationConnector;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationNotificationI;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationSubmissionHandlerI;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationSubmissionI;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.ports.ApplicationNotificationOutboundPort;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.ports.ApplicationSubmissionInboundPort;

public class AdmissionController 
	extends AbstractComponent
	implements ApplicationSubmissionHandlerI {
	
	/**URI de ce composant**/
	protected String ac_URI;
	
	/**Client Application**/
	//inboundport
	protected ApplicationSubmissionInboundPort ac_ApplicationSubmissionInboundPort;
	//outboundport
	protected String ca_ApplicationNotificationInboundPortURI;
	protected ApplicationNotificationOutboundPort ac_ApplicationNotificationOutboundPort;
	
	/**Computer**/
	//outboundport
	protected String cp_ComputerServicesInboundPortURI;
	protected ComputerServicesOutboundPort ac_ComputerServicesOutboundPort;
	
	/**DynamicComponentCreator**/
	//outboundport
	protected String dcc_DynamicComponentCreationInboundPortURI;
	protected DynamicComponentCreationOutboundPort ac_DynamicComponentCreationOutboundPort;

	
	//--------------------------------------------------------------------
	//METHODS
	//--------------------------------------------------------------------
	
	public AdmissionController(
			String ac_URI,
			String ac_ApplicationSubmissionInboundPortURI,
			String ca_ApplicationNotificationInboundPortURI,
			String cp_computerServicesInboundPortURI,
			String dcc_DynamicComponentCreationInboundPortURI
	) throws Exception {
		super(1,1);
		
		//Preconditions
		assert ac_URI != null;
		assert ac_ApplicationSubmissionInboundPortURI != null;
//		assert dcc_dynamicComponentCreationInboundPortURI != null;
//		assert cp_computerServicesInboundPortURIs != null;
//		assert cp_computerServicesInboundPortURIs.size() != 0;
		
		//initilisation
		
		this.ac_URI = ac_URI;
		this.ca_ApplicationNotificationInboundPortURI = ca_ApplicationNotificationInboundPortURI;
		this.cp_ComputerServicesInboundPortURI = cp_computerServicesInboundPortURI;
		this.dcc_DynamicComponentCreationInboundPortURI = dcc_DynamicComponentCreationInboundPortURI;
		
		//initialisation des ports
		
		/**offered**/
		//ApplicationSubmission
		this.addOfferedInterface(ApplicationSubmissionI.class) ;
		this.ac_ApplicationSubmissionInboundPort = new ApplicationSubmissionInboundPort(ac_ApplicationSubmissionInboundPortURI, this);
		this.addPort(this.ac_ApplicationSubmissionInboundPort) ;
		this.ac_ApplicationSubmissionInboundPort.publishPort() ;
		/*Notification*/
//		this.addOfferedInterface(RequestNotificationI.class) ;
//		this.requestNotificationInboundPort_AC = new RequestNotificationInboundPort(this);
//		this.addPort(this.requestNotificationInboundPort_AC) ;
//		this.requestNotificationInboundPort_AC.publishPort() ;
		
		/**required**/
		//Notification
		this.addRequiredInterface(ApplicationNotificationI.class) ;
		this.ac_ApplicationNotificationOutboundPort = new ApplicationNotificationOutboundPort(this) ;
		this.addPort(this.ac_ApplicationNotificationOutboundPort) ;
		this.ac_ApplicationNotificationOutboundPort.publishPort() ;
		//ComputerServices
		this.addRequiredInterface(ComputerServicesI.class) ;
		this.ac_ComputerServicesOutboundPort = new ComputerServicesOutboundPort(this) ;
		this.addPort(this.ac_ComputerServicesOutboundPort) ;
		this.ac_ComputerServicesOutboundPort.publishPort() ;
		//DynamicComponentCreation
		this.addRequiredInterface(DynamicComponentCreationI.class);
		this.ac_DynamicComponentCreationOutboundPort = new DynamicComponentCreationOutboundPort(this);
		this.addPort(this.ac_DynamicComponentCreationOutboundPort);
		this.ac_DynamicComponentCreationOutboundPort.publishPort();
		
//		//Submission
//		this.addRequiredInterface(RequestSubmissionI.class) ;
//		this.requestSubmissionOutboundPort_AC = new RequestSubmissionOutboundPort(this) ;
//		this.addPort(this.requestSubmissionOutboundPort_AC) ;
//		this.requestSubmissionOutboundPort_AC.publishPort() ;
//		
//		
//		/*Application VM Management*/
//		this.addRequiredInterface(ApplicationVMManagementI.class) ;
//		this.applicationVMManagementOutboundPort_AC = new ApplicationVMManagementOutboundPort(this) ;
//		this.addPort(this.applicationVMManagementOutboundPort_AC) ;
//		this.applicationVMManagementOutboundPort_AC.publishPort() ;
//		
//		
//		//init des ports a connecter
//		this.requestNotificationInboundPortURI_RG = requestNotificationInboundPortURI_RG;
//		this.computerServicesInboundPortURIs_CP = computerServicesInboundPortURIs_CP;
//		
//		//Postconditions
//		assert  this.requestNotificationInboundPort_AC != null && this.requestNotificationInboundPort_AC instanceof RequestNotificationI;
//		assert	this.requestSubmissionOutboundPort_AC != null && this.requestSubmissionOutboundPort_AC instanceof RequestSubmissionI ;
//		assert	this.requestNotificationOutboundPort_AC != null && this.requestNotificationOutboundPort_AC instanceof RequestNotificationI ;
//		assert  this.computerServicesOutboundPort_AC != null && this.computerServicesOutboundPort_AC instanceof ComputerServicesI ;
//		assert  this.applicationVMManagementOutboundPort_AC != null && this.applicationVMManagementOutboundPort_AC instanceof ApplicationVMManagementI;
//		assert  this.dynamicComponentCreationOutboundPort_AC != null && this.dynamicComponentCreationOutboundPort_AC instanceof DynamicComponentCreationI;
	}

	// Component life cycle
	
	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;

		try {
			this.doPortConnection(
					this.ac_ApplicationNotificationOutboundPort.getPortURI(),
					this.ca_ApplicationNotificationInboundPortURI,
					ApplicationNotificationConnector.class.getCanonicalName()) ;
			this.doPortConnection(
					this.ac_ComputerServicesOutboundPort.getPortURI(),
					this.cp_ComputerServicesInboundPortURI,
					ComputerServicesConnector.class.getCanonicalName()) ;
			this.doPortConnection(
					this.ac_DynamicComponentCreationOutboundPort.getPortURI(),
					this.dcc_DynamicComponentCreationInboundPortURI,
					DynamicComponentCreationConnector.class.getCanonicalName()) ;
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}
	
	@Override
	public void			finalise() throws Exception
	{	
		this.doPortDisconnection(this.ac_ApplicationNotificationOutboundPort.getPortURI()) ;
		this.doPortDisconnection(this.ac_ComputerServicesOutboundPort.getPortURI()) ;
		this.doPortDisconnection(this.ac_DynamicComponentCreationOutboundPort.getPortURI()) ;
//		if(this.requestSubmissionOutboundPort_AC.connected()) {
//			this.doPortDisconnection(this.requestSubmissionOutboundPort_AC.getPortURI());
//		}
//		if(this.applicationVMManagementOutboundPort_AC.connected()) {
//			this.doPortDisconnection(this.applicationVMManagementOutboundPort_AC.getPortURI()) ;
//		}
		super.finalise() ;
	}
	
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			this.ac_ApplicationNotificationOutboundPort.unpublishPort();
			this.ac_ApplicationSubmissionInboundPort.unpublishPort() ;
			this.ac_ComputerServicesOutboundPort.unpublishPort() ;
			this.ac_DynamicComponentCreationOutboundPort.unpublishPort() ;
//			this.requestSubmissionInboundPort_AC.unpublishPort() ;
//			this.requestSubmissionOutboundPort_AC.unpublishPort() ;
//			this.applicationVMManagementOutboundPort_AC.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}

		super.shutdown();
	}

	
	@Override
	public void acceptApplicationSubmissionAndNotify() throws Exception {
		// TODO Auto-generated method stub
		this.logMessage(this.ac_URI + " accepts an application submission and notify." /*+ r.getRequestURI()*/);
		
		/**Try hosting application**/
		AllocatedCore[] allocatedCores = this.ac_ComputerServicesOutboundPort.allocateCores(2) ;
		logMessage(allocatedCores.length + " coeur(s) alloué(s) depuis " +
				this.ac_ComputerServicesOutboundPort.getServerPortURI());

		if(allocatedCores.length == 0) {
			this.ac_ApplicationNotificationOutboundPort.notifyApplicationRejected();
		}else {
//			this.ac_DynamicComponentCreationOutboundPort.createComponent(
//					ApplicationVM.class.getCanonicalName(), 
//					new Object[]{PROVIDER_COMPONENT_URI,
//							 this.providerInboundPortURI});
		}
		
	}
}
