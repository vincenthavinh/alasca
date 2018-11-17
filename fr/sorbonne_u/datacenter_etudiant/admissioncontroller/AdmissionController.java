package fr.sorbonne_u.datacenter_etudiant.admissioncontroller;

import java.util.ArrayList;
import java.util.HashMap;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.AbstractPort;
import fr.sorbonne_u.components.pre.dcc.connectors.DynamicComponentCreationConnector;
import fr.sorbonne_u.components.pre.dcc.interfaces.DynamicComponentCreationI;
import fr.sorbonne_u.components.pre.dcc.ports.DynamicComponentCreationOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementInboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationHostingHandlerI;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationHostingI;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.ports.ApplicationHostingInboundPort;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.RequestDispatcher;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.connectors.RequestDispatcherManagementConnector;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.ports.RequestDispatcherManagementInboundPort;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.ports.RequestDispatcherManagementOutboundPort;

public class AdmissionController 
	extends AbstractComponent
	implements ApplicationHostingHandlerI {
	
	/**URI de ce composant**/
	protected String ac_URI;

	/**RequestDispatchers*/
	//outboundports
	/* clefs: RequestNotificationInboundPortURI du la Client Application.
	 * valeurs : le management port RequestDispatcher cree dynamiquement 
	 * 			 pour gerer les requetes de cette application				*/
	protected HashMap<String, RequestDispatcherManagementOutboundPort> ac_RequestDispatcherManagementOutboundPorts;
	
	/**ApplicationVMs**/
	//outboundports
	//protected ArrayList<ApplicationVMManagementOutboundPort> ac_ApplicationVMManagementOutboundPorts;
	/* clefs: RequestNotificationInboundPortURI du la Client Application.
	 * valeurs : les Submission ports des ApplicationVMs creees dynamiquement 
	 * 			 pour gerer les requetes de cette application				*/
	protected HashMap<String, ArrayList<ApplicationVM>> ac_ApplicationVMs;
	
	/**Client**/
	//inboundport
	protected ApplicationHostingInboundPort ac_ApplicationSubmissionInboundPort;
	
	/**Computer**/
	//outboundport
	protected ArrayList<String> cp_ComputerServicesInboundPortURIs;
	protected ArrayList<ComputerServicesOutboundPort> ac_ComputerServicesOutboundPorts;
	
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
			ArrayList<String> cp_computerServicesInboundPortURIs,
			String dcc_DynamicComponentCreationInboundPortURI
	) throws Exception {
		super(1,1);
		
		//Preconditions
		assert ac_URI != null;
		assert ac_ApplicationSubmissionInboundPortURI != null;
		assert dcc_DynamicComponentCreationInboundPortURI != null;
		assert cp_ComputerServicesInboundPortURIs != null;
		assert cp_computerServicesInboundPortURIs.size() != 0;
		
		//initilisation
		
		this.ac_URI = ac_URI;
		this.cp_ComputerServicesInboundPortURIs = cp_computerServicesInboundPortURIs;
		this.dcc_DynamicComponentCreationInboundPortURI = dcc_DynamicComponentCreationInboundPortURI;
		
		this.ac_RequestDispatcherManagementOutboundPorts = new HashMap<String, RequestDispatcherManagementOutboundPort>();
		this.ac_ApplicationVMs = new HashMap<String, ArrayList<ApplicationVM>>();
		
		//initialisation des ports
		
		/**offered**/
		//ApplicationSubmission
		this.addOfferedInterface(ApplicationHostingI.class) ;
		this.ac_ApplicationSubmissionInboundPort = new ApplicationHostingInboundPort(ac_ApplicationSubmissionInboundPortURI, this);
		this.addPort(this.ac_ApplicationSubmissionInboundPort) ;
		this.ac_ApplicationSubmissionInboundPort.publishPort() ;
		
		/**required**/
		//ComputerServices
		this.ac_ComputerServicesOutboundPorts = new ArrayList<ComputerServicesOutboundPort>();
		for(int i=0; i<this.cp_ComputerServicesInboundPortURIs.size(); i++) {
			this.addRequiredInterface(ComputerServicesI.class) ;
			this.ac_ComputerServicesOutboundPorts.add(new ComputerServicesOutboundPort(this));
			this.addPort(this.ac_ComputerServicesOutboundPorts.get(i)) ;
			this.ac_ComputerServicesOutboundPorts.get(i).publishPort() ;
		}
		//DynamicComponentCreation
		this.addRequiredInterface(DynamicComponentCreationI.class);
		this.ac_DynamicComponentCreationOutboundPort = new DynamicComponentCreationOutboundPort(this);
		this.addPort(this.ac_DynamicComponentCreationOutboundPort);
		this.ac_DynamicComponentCreationOutboundPort.publishPort();

		//Postconditions
		assert  this.ac_ComputerServicesOutboundPorts != null ;//&& this.ac_ComputerServicesOutboundPorts instanceof ComputerServicesI ;
//		assert  this.applicationVMManagementOutboundPort_AC != null && this.applicationVMManagementOutboundPort_AC instanceof ApplicationVMManagementI;
		assert  this.ac_DynamicComponentCreationOutboundPort != null && this.ac_DynamicComponentCreationOutboundPort instanceof DynamicComponentCreationI;
	}

	// Component life cycle
	
	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;

		try {
			for(int i=0; i<this.ac_ComputerServicesOutboundPorts.size(); i++){
				this.doPortConnection(
						this.ac_ComputerServicesOutboundPorts.get(i).getPortURI(),
						this.cp_ComputerServicesInboundPortURIs.get(i),
						ComputerServicesConnector.class.getCanonicalName()) ;
			}
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
		for(ComputerServicesOutboundPort csop : this.ac_ComputerServicesOutboundPorts){
			this.doPortDisconnection(csop.getPortURI()) ;
		}
		
		this.doPortDisconnection(this.ac_DynamicComponentCreationOutboundPort.getPortURI()) ;
		
		for(RequestDispatcherManagementOutboundPort rdmop : this.ac_RequestDispatcherManagementOutboundPorts.values()) {
			this.doPortDisconnection(rdmop.getPortURI());
		}
		
		//TODO ajouter la deco des ports des appsVMs quand elle es seront dynamiquement creees
		
		super.finalise() ;
	}
	
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			this.ac_ApplicationSubmissionInboundPort.unpublishPort() ;
			for(ComputerServicesOutboundPort csop : this.ac_ComputerServicesOutboundPorts){
				csop.unpublishPort() ;
			}
			this.ac_DynamicComponentCreationOutboundPort.unpublishPort() ;
			for(RequestDispatcherManagementOutboundPort rdmop : this.ac_RequestDispatcherManagementOutboundPorts.values()) {
				rdmop.unpublishPort();
			}
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}

		super.shutdown();
	}

	
	@Override
	public String processAskHosting(String requestNotificationInboundPortURI) throws Exception {
		this.logMessage(this.ac_URI + " accepts an application submission and notify." /*+ r.getRequestURI()*/);
		
		/**Try hosting application**/
		AllocatedCore[] allocatedCores = this.findComputerAndAllocateCores();

		if(allocatedCores.length == 0) {
			return null;
		}else {
			/*generation des URIs des composants dynamiques*/
			
			//ports du RequestDispatcher
			String rd_rsipURI = AbstractPort.generatePortURI(RequestSubmissionInboundPort.class);
			String rd_rnipURI = AbstractPort.generatePortURI(RequestNotificationInboundPort.class);
			String rd_rdmipURI = AbstractPort.generatePortURI(RequestDispatcherManagementInboundPort.class);
			
			//ports des AppVMs
			String avm_rsipURI = AbstractPort.generatePortURI(RequestSubmissionInboundPort.class);
			String avm_amipURI = AbstractPort.generatePortURI(ApplicationVMManagementInboundPort.class);
			
			//stockage dans des listes.
			ArrayList<String> avms_rsipURIs = new ArrayList<String>();
			avms_rsipURIs.add(avm_rsipURI);
			ArrayList<String> avms_amipURIs = new ArrayList<String>();
			avms_amipURIs.add(avm_amipURI);
			
			
			/*Creation des composants dynamiques*/
			
//			logMessage("Creation d'un Request Dispatcher.");
//			RequestDispatcher rd0 = new RequestDispatcher(
//					"rd0",
//					"a",
//					rd_rnipURI, 
//					rd_rsipURI, 
//					requestNotificationInboundPortURI, 
//					avms_rsipURIs);
//			rd0.toggleTracing();
//			rd0.toggleLogging();
			
			//RequestDispatcher
			this.ac_DynamicComponentCreationOutboundPort.createComponent(
					RequestDispatcher.class.getCanonicalName(), 
					new Object[]{
							"rd0",
							rd_rdmipURI,
							rd_rnipURI, 
							rd_rsipURI, 
							requestNotificationInboundPortURI, 
							avms_rsipURIs});
			
			
			//ApplicationVM(s)
			logMessage("Creation d'une ApplicatinVM.");
			ApplicationVM vm0 = new ApplicationVM(
					"vm0", 
					avm_amipURI, 
					avm_rsipURI, 
					rd_rnipURI);
			vm0.toggleTracing();
			vm0.toggleLogging();
			
			
			/*Connexion de l'AdmissionController avec les composants dynamiques*/
	
			//RequestDispatcherManagementOutboundPort de l'AC
			RequestDispatcherManagementOutboundPort rdmop = new RequestDispatcherManagementOutboundPort(this);
			
			this.addRequiredInterface(RequestDispatcherManagementI.class);
			this.ac_RequestDispatcherManagementOutboundPorts.put(requestNotificationInboundPortURI, rdmop);
			this.addPort(rdmop);
			rdmop.publishPort();
			
			this.doPortConnection(
					rdmop.getPortURI(),
					rd_rdmipURI,
					RequestDispatcherManagementConnector.class.getCanonicalName()) ;
			
			rdmop.toggleTracingLogging();
			
			//ApplicationVMManagementOutboundPorts de l'AC
//			this.addRequiredInterface(ApplicationVMManagementI.class) ;
//			this.applicationVMManagementOutboundPort_AC = new ApplicationVMManagementOutboundPort(this) ;
//			this.addPort(this.applicationVMManagementOutboundPort_AC) ;
//			this.applicationVMManagementOutboundPort_AC.publishPort() ;
			//TODO
			ArrayList<ApplicationVM> appVMs = new ArrayList<ApplicationVM>();
			appVMs.add(vm0);
			this.ac_ApplicationVMs.put(requestNotificationInboundPortURI, appVMs);
		
			
			//allouer les coeurs reserves aux aVMs
			vm0.allocateCores(allocatedCores);
			
			//retourne la RequestSubmissionInboundPortURI du Request Dispatcher.
			return rd_rsipURI;
		}
		
	}

	@Override
	public Boolean processAskHostToConnect(String requestNotificationInboundPortURI) {
		try {
			//connexion des OutboundPorts du RequestDispatcher
			this.ac_RequestDispatcherManagementOutboundPorts.
				get(requestNotificationInboundPortURI).
				connectOutboundPorts();
			
			//connexion des OutboundPorts des ApplicationVMs
			//TODO changer applicationVM -> apvmManageementOutboundPort
			for(ApplicationVM appVM : this.ac_ApplicationVMs.get(requestNotificationInboundPortURI)){
				appVM.start();
			}
			
		} catch (ComponentStartException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			//si le connectOutboundPorts() de RequestDispatcher echoue.
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	private AllocatedCore[] findComputerAndAllocateCores() throws Exception {
		AllocatedCore[] allocatedCores = new AllocatedCore[0];

		for(ComputerServicesOutboundPort csop : this.ac_ComputerServicesOutboundPorts) {
			allocatedCores = csop.allocateCores(2) ;
			if(allocatedCores.length > 0) {
				logMessage(allocatedCores.length + " coeur(s) alloué(s) depuis " + csop.getServerPortURI());
				return allocatedCores;
			}	
		}
		logMessage("Aucun coeur n'a pu etre alloue.");
		return allocatedCores;
	}
}
