package fr.sorbonne_u.datacenter_etudiant.admissioncontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.ports.AbstractPort;
import fr.sorbonne_u.components.pre.dcc.connectors.DynamicComponentCreationConnector;
import fr.sorbonne_u.components.pre.dcc.interfaces.DynamicComponentCreationI;
import fr.sorbonne_u.components.pre.dcc.ports.DynamicComponentCreationOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM.ApplicationVMPortTypes;
import fr.sorbonne_u.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMIntrospectionInboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementInboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.AdmissionControllerServicesI;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationHostingHandlerI;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationHostingI;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.ports.AdmissionControllerServicesInboundPort;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.ports.ApplicationHostingInboundPort;
import fr.sorbonne_u.datacenter_etudiant.coordinator.connectors.CoreCoordinatorServicesConnector;
import fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.CoreCoordinatorServicesI;
import fr.sorbonne_u.datacenter_etudiant.coordinator.ports.CoreCoordinatorServicesOutboundPort;
import fr.sorbonne_u.datacenter_etudiant.performanceController.PerformanceController;
import fr.sorbonne_u.datacenter_etudiant.performanceController.connectors.PerformanceControllerManagementConnector;
import fr.sorbonne_u.datacenter_etudiant.performanceController.interfaces.PerformanceControllerManagementI;
import fr.sorbonne_u.datacenter_etudiant.performanceController.ports.PerformanceControllerManagementInboundPort;
import fr.sorbonne_u.datacenter_etudiant.performanceController.ports.PerformanceControllerManagementOutboundPort;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.RequestDispatcher;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.connectors.RequestDispatcherManagementConnector;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.ports.RequestDispatcherManagementInboundPort;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.ports.RequestDispatcherManagementOutboundPort;

/**
 * La classe <code>AdmissionController</code> controle l'hébergement des applications en examinant
 * les ressources qu'il possède.
 *
 * <p><strong>Description</strong></p>
 * 
 * Le contrôleur d'admission est un composant qui est connecté aux ordinateurs pour gérer les
 * resssources. Il refuse les demandes d'hébergement lorsqu'il n'a plus alloué de coeurs suffisant
 * pour une application. Il gère la création de la plupart des composants.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * TODO: complete!
 * <pre>
 * invariant		ac_URI != null
 * invariant		ac_ApplicationSubmissionInboundPortURI != null
 * invariant		ac_AdmissionControllerServicesInboundPortURI != null
 * invariant		coreCoord_services_ipURI != null
 * </pre>
 * 
 * <p>Created on : February 1, 2019</p>
 * 
 * @author	<a>Chao LIN</a>
 */
public class AdmissionController 
	extends AbstractComponent
	implements ApplicationHostingHandlerI {
	
	/**URI de ce composant**/
	protected String ac_URI;
	/** admission controller inbound port through which management methods are called.	*/
	protected AdmissionControllerServicesInboundPort admissionControllerServicesInboundPort;
	
	/**RequestDispatchers*/
	//outboundports
	/* clefs: RequestNotificationInboundPortURI du la Client Application.
	 * valeurs : le management port RequestDispatcher cree dynamiquement 
	 * 			 pour gerer les requetes de cette application				*/
	protected HashMap<String, RequestDispatcherManagementOutboundPort> ac_RequestDispatcherManagementOutboundPorts;
	protected int rd_number;
	
	/**ApplicationVMs**/
	//outboundports
	//protected ArrayList<ApplicationVMManagementOutboundPort> ac_ApplicationVMManagementOutboundPorts;
	/* clefs: RequestNotificationInboundPortURI du la Client Application.
	 * valeurs : les Submission ports des ApplicationVMs creees dynamiquement 
	 * 			 pour gerer les requetes de cette application				*/
	protected HashMap<String, ArrayList<ApplicationVMManagementOutboundPort>> ac_ApplicationVMManagementOutboundPorts;
	
	/**Clefs: URI de l'avm
	 * Valeurs: Les différents ports des avms*/
	protected HashMap<String, Map<ApplicationVMPortTypes, String>> avms_created;
	
	/**PerformanceControllers**/
	protected HashMap<String, PerformanceControllerManagementOutboundPort> ac_PerformanceControllerManagementOutboundPorts;
	protected HashMap<String, PerformanceController> ac_PerformanceController;
	
	/**Client**/
	//inboundport
	protected ApplicationHostingInboundPort ac_ApplicationSubmissionInboundPort;
	
	/**DynamicComponentCreator**/
	//outboundport
	protected String dcc_DynamicComponentCreationInboundPortURI;
	protected DynamicComponentCreationOutboundPort ac_DynamicComponentCreationOutboundPort;

	/**AVM libre*/
	protected HashMap<String, ApplicationVM> avms_libre;
	// on recycle les avms libre qu'on nous a rendu
	protected ArrayList<Map<ApplicationVMPortTypes, String>> avms_libre_recyclees;
	// index pour les uris des avms libre
	protected int index_free_avm;
	
	/**Coordinateur de coeur*/
	protected String coreCoord_services_ipURI;
	protected CoreCoordinatorServicesOutboundPort coreCoord_services_op;
	
	/**Coordinateur de frequence*/
	protected String freqCoord_services_ipURI;
	
	/**
	 * Créer un contrôleur d'admission en donnant son URI et les inbound ports
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre		ac_URI != null
	 * pre		ac_ApplicationSubmissionInboundPortURI != null
	 * pre		dcc_DynamicComponentCreationInboundPortURI != 0
	 * pre		ac_AdmissionControllerServicesInboundPortURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param ac_URI										URI du contrôleur d'admission
	 * @param ac_ApplicationSubmissionInboundPortURI		URI du application submission inbound port du contrôleur d'admission
	 * @param dcc_DynamicComponentCreationInboundPortURI	URI du dynamic component creator
	 * @param ac_AdmissionControllerServicesInboundPortURI	URI du port de service du contrôleur d'admission
	 * @throws Exception
	 */
	public AdmissionController(
			String ac_URI,
			String ac_ApplicationSubmissionInboundPortURI,
			String dcc_DynamicComponentCreationInboundPortURI,
			String ac_AdmissionControllerServicesInboundPortURI,
			String coreCoord_services_ipURI,
			String freqCoord_services_ipURI
	) throws Exception {
		super(1,1);
		
		//Preconditions
		assert ac_URI != null;
		assert ac_ApplicationSubmissionInboundPortURI != null;
		assert dcc_DynamicComponentCreationInboundPortURI != null;
		assert ac_AdmissionControllerServicesInboundPortURI != null;
		assert coreCoord_services_ipURI != null;
		assert freqCoord_services_ipURI != null;
		
		//initilisation
		
		this.ac_URI = ac_URI;
		this.dcc_DynamicComponentCreationInboundPortURI = dcc_DynamicComponentCreationInboundPortURI;
		this.coreCoord_services_ipURI = coreCoord_services_ipURI;
		this.freqCoord_services_ipURI = freqCoord_services_ipURI;
		
		this.ac_RequestDispatcherManagementOutboundPorts = new HashMap<String, RequestDispatcherManagementOutboundPort>();
		this.ac_ApplicationVMManagementOutboundPorts = new HashMap<String, ArrayList<ApplicationVMManagementOutboundPort>>();
		this.ac_PerformanceControllerManagementOutboundPorts = new HashMap<String, PerformanceControllerManagementOutboundPort>();
		this.ac_PerformanceController = new HashMap<String, PerformanceController>();
		
		this.avms_libre = new HashMap<String, ApplicationVM>();
		this.index_free_avm = 0;
		this.rd_number = 0;
		
		this.avms_libre_recyclees = new ArrayList<Map<ApplicationVMPortTypes, String>>();
		this.avms_created = new HashMap<String, Map<ApplicationVMPortTypes, String>>();
		//initialisation des ports
		
		/**offered**/
		//ApplicationSubmission
		this.addOfferedInterface(ApplicationHostingI.class) ;
		this.ac_ApplicationSubmissionInboundPort = new ApplicationHostingInboundPort(ac_ApplicationSubmissionInboundPortURI, this);
		this.addPort(this.ac_ApplicationSubmissionInboundPort) ;
		this.ac_ApplicationSubmissionInboundPort.publishPort() ;
		
		// Adding admission controller interfaces, creating and publishing the related ports
		this.addOfferedInterface(AdmissionControllerServicesI.class);
		this.admissionControllerServicesInboundPort = 
				new AdmissionControllerServicesInboundPort(
						ac_AdmissionControllerServicesInboundPortURI, this);
		this.addPort(admissionControllerServicesInboundPort);
		this.admissionControllerServicesInboundPort.publishPort();
		
		/**required**/
		//DynamicComponentCreation
		this.addRequiredInterface(DynamicComponentCreationI.class);
		this.ac_DynamicComponentCreationOutboundPort = new DynamicComponentCreationOutboundPort(this);
		this.addPort(this.ac_DynamicComponentCreationOutboundPort);
		this.ac_DynamicComponentCreationOutboundPort.publishPort();
		
		/**Coordinateur de coeur*/
		this.addRequiredInterface(CoreCoordinatorServicesI.class);
		this.coreCoord_services_op = new CoreCoordinatorServicesOutboundPort(this);
		this.addPort(coreCoord_services_op);
		this.coreCoord_services_op.publishPort();

		//Postconditions
		assert  this.ac_DynamicComponentCreationOutboundPort != null && this.ac_DynamicComponentCreationOutboundPort instanceof DynamicComponentCreationI;
	}

	// Component life cycle
	
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;

		try {
			this.doPortConnection(
					this.ac_DynamicComponentCreationOutboundPort.getPortURI(),
					this.dcc_DynamicComponentCreationInboundPortURI,
					DynamicComponentCreationConnector.class.getCanonicalName()) ;
			this.doPortConnection(
					this.coreCoord_services_op.getPortURI(),
					this.coreCoord_services_ipURI,
					CoreCoordinatorServicesConnector.class.getCanonicalName() );
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}
	
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{	
		this.doPortDisconnection(this.ac_DynamicComponentCreationOutboundPort.getPortURI()) ;
		
		for(RequestDispatcherManagementOutboundPort rdmop : this.ac_RequestDispatcherManagementOutboundPorts.values()) {
			this.doPortDisconnection(rdmop.getPortURI());
		}
		
		for(ArrayList<ApplicationVMManagementOutboundPort> avmmops : this.ac_ApplicationVMManagementOutboundPorts.values()) {
			for(ApplicationVMManagementOutboundPort avmmop : avmmops) {
				this.doPortDisconnection(avmmop.getPortURI());
			}
		}
		
		for(String rnibp : this.ac_PerformanceControllerManagementOutboundPorts.keySet()) {
			this.doPortDisconnection(this.ac_PerformanceControllerManagementOutboundPorts.get(rnibp).getPortURI());
			this.ac_PerformanceController.get(rnibp).finalise();
		}
		
		for(ApplicationVM avm : this.avms_libre.values()) {
			avm.finalise();
		}
		this.doPortDisconnection(this.coreCoord_services_op.getPortURI());
		super.finalise() ;
	}
	
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			this.ac_ApplicationSubmissionInboundPort.unpublishPort() ;
			this.ac_DynamicComponentCreationOutboundPort.unpublishPort() ;
			for(RequestDispatcherManagementOutboundPort rdmop : this.ac_RequestDispatcherManagementOutboundPorts.values()) {
				rdmop.unpublishPort();
			}
			for(ArrayList<ApplicationVMManagementOutboundPort> avmmops : this.ac_ApplicationVMManagementOutboundPorts.values()) {
				for(ApplicationVMManagementOutboundPort avmmop : avmmops) {
					avmmop.unpublishPort();
				}
			}
			this.admissionControllerServicesInboundPort.unpublishPort();
			
			for(String rnibp : this.ac_PerformanceControllerManagementOutboundPorts.keySet()) {
				this.ac_PerformanceControllerManagementOutboundPorts.get(rnibp).unpublishPort();
				this.ac_PerformanceController.get(rnibp).shutdown();
			}
			
			for(ApplicationVM avm : this.avms_libre.values()) {
				avm.shutdown();
			}
			this.coreCoord_services_op.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}

		super.shutdown();
	}

	//--------------------------------------------------------------------
	//METHODS
	//--------------------------------------------------------------------
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationHostingHandlerI#processAskHosting(String, int, int, int)
	 */
	@Override
	public String processAskHosting(String requestNotificationInboundPortURI, int nbCoresByAVM, int seul_inf, int seul_sup) throws Exception {
		this.logMessage("AdContr. "+ this.ac_URI+"| reçu askHosting() d'un applicationClient.");
		
		int nbAVMsByApp = 2;
		
		/**Try hosting application**/
		// choix arbitraire pour le moment
		// argument variable selon la demande de l application dans le futur
		AllocatedCore[] allocatedCores = this.coreCoord_services_op.findComputerAndAllocateCores(nbCoresByAVM*nbAVMsByApp);
		
		if(allocatedCores.length == 0) {
			return null;
		}
		
		/*generation des URIs des composants dynamiques*/
		
		//ports du RequestDispatcher
		String rd_URI = "rd"+ rd_number++;
		String rd_rsipURI = AbstractPort.generatePortURI(RequestSubmissionInboundPort.class);
		String rd_rnipURI = AbstractPort.generatePortURI(RequestNotificationInboundPort.class);
		String rd_rdmipURI = AbstractPort.generatePortURI(RequestDispatcherManagementInboundPort.class);
		
		//listes de stockage des URIs des avms.
		ArrayList<String> avms_URI = new ArrayList<String>();
		ArrayList<String> avms_rsipURIs = new ArrayList<String>();
		ArrayList<String> avms_amipURIs = new ArrayList<String>();
		ArrayList<String> avms_iipURIs = new ArrayList<String>();

		for(int i=0; i<nbAVMsByApp; i++) {
			//ports des AppVMs
			String avm_URI = "vm"+ i +"-"+ rd_URI;
			String avm_rsipURI = AbstractPort.generatePortURI(RequestSubmissionInboundPort.class);
			String avm_amipURI = AbstractPort.generatePortURI(ApplicationVMManagementInboundPort.class);
			String avm_iipURI = AbstractPort.generatePortURI(ApplicationVMIntrospectionInboundPort.class);
			avms_URI.add(avm_URI);
			avms_rsipURIs.add(avm_rsipURI);
			avms_amipURIs.add(avm_amipURI);
			avms_iipURIs.add(avm_iipURI);
			Map<ApplicationVMPortTypes, String> avm_ports = new HashMap<ApplicationVMPortTypes, String>();
			avm_ports.put(ApplicationVMPortTypes.REQUEST_SUBMISSION, avm_rsipURI);
			avm_ports.put(ApplicationVMPortTypes.MANAGEMENT, avm_amipURI);
			avm_ports.put(ApplicationVMPortTypes.INTROSPECTION, avm_iipURI);
			this.avms_created.put(avm_URI, avm_ports);
		}
		
		//port du performance controller
		String pc_management_ipURI = AbstractPort.generatePortURI(PerformanceControllerManagementInboundPort.class);
		
		/*Creation des composants dynamiques*/
		
		//RequestDispatcher
		this.ac_DynamicComponentCreationOutboundPort.createComponent(
				RequestDispatcher.class.getCanonicalName(), 
				new Object[]{
						rd_URI,
						rd_rdmipURI,
						rd_rnipURI, 
						rd_rsipURI, 
						requestNotificationInboundPortURI, 
						avms_rsipURIs});
		
		this.logMessage("AdContr. "+ this.ac_URI+"| creation dynamique de "+rd_URI);
		
		//ApplicationVM(s)
		for(int i=0; i<nbAVMsByApp; i++) {
			this.ac_DynamicComponentCreationOutboundPort.createComponent(
					ApplicationVM.class.getCanonicalName(),
					new Object[] {
							"vm"+ i +"-"+ rd_URI,
							avms_amipURIs.get(i),
							avms_iipURIs.get(i),
							avms_rsipURIs.get(i),
							rd_rnipURI
					}
			);
			
			this.logMessage("AdContr. "+ this.ac_URI+"| creation dynamique de "+"vm"+ i +"-"+ rd_URI);
		}
		PerformanceController pc = new PerformanceController(
				"pc-"+rd_URI,
				pc_management_ipURI,
				rd_rdmipURI, 
				rd_rnipURI,
				avms_URI, 
				avms_iipURIs, 
				avms_amipURIs,
				seul_inf,
				seul_sup,
				1,
				admissionControllerServicesInboundPort.getPortURI(),
				coreCoord_services_ipURI,
				freqCoord_services_ipURI
		);
		ac_PerformanceController.put(requestNotificationInboundPortURI, pc);
		//Performance controller
		// pb pour le creer avec dynamic component, la création échoue en donnant 
		// les mêmes configurations qu'au-dessus (n'entre pas dans le constructeur du PerformanceController)
//		this.ac_DynamicComponentCreationOutboundPort.createComponent(
//				PerformanceController.class.getCanonicalName(), 
//				new Object[]{
//						"pc-"+rd_URI,
//						pc_management_ipURI,
//						rd_rdmipURI, 
//						avms_URI, 
//						avms_iipURIs, 
//						avms_amipURIs,
//						computers_URI,
//						computersServices_URI,
//						seul_inf,
//						seul_sup,
//						admissionControllerServicesInboundPort.getPortURI()
//				}
//		);
//		
		this.logMessage("AdContr. "+ this.ac_URI+"| creation de "+"pc-"+rd_URI);

		/*Connexion de l'AdmissionController avec les composants dynamiques*/

		//RequestDispatcherManagementOutboundPort de l'AC
		this.addRequiredInterface(RequestDispatcherManagementI.class);
		RequestDispatcherManagementOutboundPort rdmop = new RequestDispatcherManagementOutboundPort(this);
		this.ac_RequestDispatcherManagementOutboundPorts.put(requestNotificationInboundPortURI, rdmop);
		this.addPort(rdmop);
		rdmop.publishPort();
		
		this.doPortConnection(
				rdmop.getPortURI(),
				rd_rdmipURI,
				RequestDispatcherManagementConnector.class.getCanonicalName()) ;
		rdmop.toggleTracingLogging();
		
		//ApplicationVMManagementOutboundPorts de l'AC
		this.addRequiredInterface(ApplicationVMManagementI.class) ;
		ArrayList<ApplicationVMManagementOutboundPort> avmmop_list = new ArrayList<ApplicationVMManagementOutboundPort>();
		for(int i=0; i<nbAVMsByApp; i++) {
			ApplicationVMManagementOutboundPort avmmop = new ApplicationVMManagementOutboundPort(this);
			avmmop_list.add(avmmop);
			this.addPort(avmmop) ;
			avmmop.publishPort() ;
			this.doPortConnection(
					avmmop.getPortURI(),
					avms_amipURIs.get(i),
					ApplicationVMManagementConnector.class.getCanonicalName());
			avmmop.toggleTracingLogging();
			
			//allouer les coeurs reserves aux aVMs
			AllocatedCore[] aVMCores = new AllocatedCore[nbCoresByAVM];
			for(int j=0; j<nbCoresByAVM; j++) {
				aVMCores[j] = allocatedCores[i*nbCoresByAVM + j];
			}
			avmmop.allocateCores(aVMCores);
		}
		this.ac_ApplicationVMManagementOutboundPorts.put(requestNotificationInboundPortURI, avmmop_list);
		
		//PerformanceControllerOutboundPorts de l'AC
		this.addRequiredInterface(PerformanceControllerManagementI.class);
		PerformanceControllerManagementOutboundPort pcmop = new PerformanceControllerManagementOutboundPort(this);
		this.addPort(pcmop);
		pcmop.publishPort();
		this.doPortConnection(
				pcmop.getPortURI(), 
				pc_management_ipURI, 
				PerformanceControllerManagementConnector.class.getCanonicalName()
		);
		pcmop.toggleTracingLogging();
		this.ac_PerformanceControllerManagementOutboundPorts.put(requestNotificationInboundPortURI, pcmop);
		//retourne la RequestSubmissionInboundPortURI du Request Dispatcher.
		return rd_rsipURI;
	}

	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationHostingHandlerI#processAskHostToConnect(String)
	 */
	@Override
	public Boolean processAskHostToConnect(String requestNotificationInboundPortURI) {
		try {
			logMessage("AdContr. "+ this.ac_URI+"| reçu askHostToConnect() d'un applicationClient.");
			//connexion des OutboundPorts du RequestDispatcher
			this.ac_RequestDispatcherManagementOutboundPorts.
				get(requestNotificationInboundPortURI).
				connectOutboundPorts();
			
			//connexion des OutboundPorts des ApplicationVMs
			for(ApplicationVMManagementOutboundPort avmmop : this.ac_ApplicationVMManagementOutboundPorts.get(requestNotificationInboundPortURI)) {
				avmmop.connectOutboundPorts();
			}
			
			//connexion des outboundports du performanceController
			this.ac_PerformanceControllerManagementOutboundPorts.get(requestNotificationInboundPortURI).connectOutboundPorts();
			ac_PerformanceController.get(requestNotificationInboundPortURI).execute();
			logMessage("AdContr. "+ this.ac_URI+"| ReqDisp dynamique et ReqGen du client connectés.");
			
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
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.AdmissionControllerServicesI#recycleFreeAVM(String)
	 */
	public void recycleFreeAVM(String AVMuri) throws Exception{
		ApplicationVM avm = this.avms_libre.get(AVMuri);
		if(avm != null) {
			this.avms_libre_recyclees.add(avm.getAVMPortsURI());
			return;
		}
		this.avms_libre_recyclees.add(this.avms_created.get(AVMuri));
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.AdmissionControllerServicesI#allocateFreeAVM()
	 */
	public Map<ApplicationVMPortTypes, String> allocateFreeAVM() throws Exception{
		if(!this.avms_libre_recyclees.isEmpty()) {
			return this.avms_libre_recyclees.remove(0);
		}
		if(this.avms_libre.size() >= 5) {
			return null;
		}
		String avm_rsipURI = AbstractPort.generatePortURI(RequestSubmissionInboundPort.class);
		String avm_amipURI = AbstractPort.generatePortURI(ApplicationVMManagementInboundPort.class);
		String avm_iipURI = AbstractPort.generatePortURI(ApplicationVMIntrospectionInboundPort.class);
		String avm_uri = "free_vm" + this.index_free_avm++;
		
		// le dynamicComponentCreator n'accepte pas de paramètre null
//		this.ac_DynamicComponentCreationOutboundPort.createComponent(
//				ApplicationVM.class.getCanonicalName(),
//				new Object[] {
//						avm_uri,
//						avm_amipURI,
//						avm_iipURI,
//						avm_rsipURI,
//						null
//				}
//		);
		ApplicationVM avm = new ApplicationVM(
						avm_uri,
						avm_amipURI,
						avm_iipURI,
						avm_rsipURI,
						null
		);
		this.avms_libre.put(avm_uri, avm);
		
		this.logMessage("AdContr. "+ this.ac_URI+"| creation de "+avm_uri);
		
		Map<ApplicationVMPortTypes, String> free_avm_ports = new HashMap<ApplicationVMPortTypes, String>();
		free_avm_ports.put(ApplicationVMPortTypes.REQUEST_SUBMISSION, avm_rsipURI);
		free_avm_ports.put(ApplicationVMPortTypes.MANAGEMENT, avm_amipURI);
		free_avm_ports.put(ApplicationVMPortTypes.INTROSPECTION, avm_iipURI);
		
		avm.toggleTracingLogging();
		
		return free_avm_ports;
	}
}
