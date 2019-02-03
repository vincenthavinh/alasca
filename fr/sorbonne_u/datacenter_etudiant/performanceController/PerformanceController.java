package fr.sorbonne_u.datacenter_etudiant.performanceController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.TimeManagement;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.processors.Processor.ProcessorPortTypes;
import fr.sorbonne_u.datacenter.hardware.processors.connectors.ProcessorIntrospectionConnector;
import fr.sorbonne_u.datacenter.hardware.processors.connectors.ProcessorManagementConnector;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorIntrospectionOutboundPort;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorManagementOutboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM.ApplicationVMPortTypes;
import fr.sorbonne_u.datacenter.software.applicationvm.connectors.ApplicationVMIntrospectionConnector;
import fr.sorbonne_u.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMIntrospectionI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMIntrospectionOutboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.connectors.AdmissionControllerServicesConnector;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.AdmissionControllerServicesI;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.ports.AdmissionControllerServicesOutboundPort;
import fr.sorbonne_u.datacenter_etudiant.coordinator.connectors.CoreCoordinatorServicesConnector;
import fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.CoreCoordinatorServicesI;
import fr.sorbonne_u.datacenter_etudiant.coordinator.ports.CoreCoordinatorServicesOutboundPort;
import fr.sorbonne_u.datacenter_etudiant.performanceController.interfaces.PerformanceControllerManagementI;
import fr.sorbonne_u.datacenter_etudiant.performanceController.ports.PerformanceControllerManagementInboundPort;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.connectors.RequestDispatcherManagementConnector;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.ports.RequestDispatcherManagementOutboundPort;

/**
 * La classe <code>PerformanceController</code> manipule les ressources des ordinateurs et des
 * avm pour contrôler la performance souhaitée par l'application.
 *
 * <p><strong>Description</strong></p>
 * 
 * Le contrôleur de performance varie les fréquences des coeurs alloués aux avms, attribue ou
 * reprend des coeurs aux avms, ajoute ou retire des avms libres à une application pour que le
 * temps moyen d'exécution d'une requête soit dans les seuils demandés par le client lors de 
 * l'hébergement de l'application.
 * 
 * <p><strong>Invariant</strong></p>
 * TODO: complete!
 * <pre>
 * invariant		pcURI != null
 * invariant		pc_management_ipURI != null
 * invariant		rd_management_ipURI != null
 * invariant		rd_request_notification_ipURI != null
 * invariant		avms_URI != null && avms_URI.size() != 0
 * invariant		avmsIntrospectionInboundPortURIs != null && avmsIntrospectionInboundPortURIs.size() != 0
 * invariant		avmsManagementInboundPortURIs != null && avmsManagementInboundPortURIs.size() != 0
 * invariant		computers_URI != null && computers_URI.size() != 0
 * invariant		cp_computerServicesInboundPortURIs != null && cp_computerServicesInboundPortURIs.size() != 0
 * invariant		admissionControllerServicesInboundPortURI != null
 * invariant		pc_seuil_inf <= pc_seuil_sup
 * invariant		coreCoord_services_ipURI != null
 * </pre>
 * 
 * <p>Created on : February 1, 2019</p>
 * 
 * @author	<a>Chao LIN</a>
 */
public class PerformanceController 
extends AbstractComponent
implements PerformanceControllerManagementI{	
	protected String pcURI;
	
	/** attributs pour stoker les informations des coeurs, des processeurs et des avms/avms libres */
	protected final int SEUIL_INF;
	protected final int SEUIL_SUP;
	protected ArrayList<String> avmURIs;
	protected HashMap<String, Integer> number_of_cores_of_avm;
	protected HashMap<AllocatedCore, Integer> allocatedCoreStates;
	protected HashMap<AllocatedCore, ArrayList<Integer>> allocatedCoreAdmissibleFrequencies;
	protected HashMap<AllocatedCore, ProcessorManagementOutboundPort> processorManagementOutboundPorts;
	protected HashMap<String, ProcessorIntrospectionOutboundPort> processorIntrospectionOutboundPorts;
	protected HashMap<String, ProcessorManagementOutboundPort> pmip_processorManagementOutboundPorts;
	/** Liste des avms libres qu'on a alloué depuis admissionController*/
	protected ArrayList<String> listAVMs_libre;
	/** Nombre d'avms qu'on peut allouer/désallouer*/
	protected int limite_nb_avms;
	/** Nombre d'avms qu'on a alloué/désalloué*/
	protected int variance_avms;
	
	/**Computer**/
	protected HashMap<String, ArrayList<String>> computer_list_avm; 
	
	/**AVMs introspections**/
	protected HashMap<String, String> avmsIntrospectionInboundPortURIs; // avmURI, introspectionIPURI
	protected HashMap<String, ApplicationVMIntrospectionOutboundPort> avmsIntrospectionOutboundPorts;// avmURI, introspectionOPURI
	
	/**AVMs management**/
	protected HashMap<String, String> avmsManagementInboundPortURIs; // avmURI, managementIPURI
	protected HashMap<String, ApplicationVMManagementOutboundPort> avmsManagementOutboundPorts; // avmURI, managementOPURI
	
	protected PerformanceControllerManagementInboundPort pcmip;
	
	/**Request Dispatcher**/
	protected String rdmipURI;
	protected String rd_rnipURI;
	protected RequestDispatcherManagementOutboundPort rdmop;
	
	/**Admission Controller*/
	protected String admissionControllerServicesInboundPortURI;
	protected AdmissionControllerServicesOutboundPort acsop;
	
	/**Coordinateur de coeur*/
	protected String coreCoord_services_ipURI;
	protected CoreCoordinatorServicesOutboundPort coreCoord_services_op;
	
	/**
	 * Créer un contrôleur de performance en donnant son URI et les inbound ports
	 * On utilise que des ArrayList dans le constructeur car le dynamicComponentCreator ne supporte
	 * pas de créer un component avec des HashMap dans les paramètres
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre		pcURI != null
	 * pre		pc_management_ipURI != null
	 * pre		rd_management_ipURI != null
	 * pre		rd_request_notification_ipURI != null
	 * pre		avms_URI != null && avms_URI.size() != 0
	 * pre		avmsIntrospectionInboundPortURIs != null && avmsIntrospectionInboundPortURIs.size() != 0
	 * pre		avmsManagementInboundPortURIs != null && avmsManagementInboundPortURIs.size() != 0
	 * pre		computers_URI != null && computers_URI.size() != 0
	 * pre		cp_computerServicesInboundPortURIs != null && cp_computerServicesInboundPortURIs.size() != 0
	 * pre		admissionControllerServicesInboundPortURI != null
	 * pre		pc_seuil_inf <= pc_seuil_sup
	 * post	true			// no postcondition.
	 * </pre>
	 * 
	 * @param pcURI										URI du contrôleur de performance
	 * @param pc_management_ipURI						URI du management inbound port du contrôleur de performance
	 * @param rd_management_ipURI						URI du management inbound port du répartiteur de requête
	 * @param rd_request_notification_ipURI				URI du notification de requête du répartiteur de requête (connection aux avms libre)
	 * @param avms_URI									URIs des avms attribué à l'application
	 * @param avmsIntrospectionInboundPortURIs			URIs du introspection inbound port des avms
	 * @param avmsManagementInboundPortURIs				URIs du management inbound port des avms
	 * @param computers_URI								URIs des ordinateurs
	 * @param cp_computerServicesInboundPortURIs		URIs du services inbound port des ordinateurs
	 * @param seuil_inf									Seuil inférieur du temps moyen d'exécution souhaité
	 * @param seuil_sup									Seuil supérieur du temps moyen d'exécution souhaité
	 * @param limite_nb_avms							Nombre d'avms qu'on peut allouer/déallouer
	 * @param admissionControllerServicesInboundPortURI	URI du services inbound port du contrôleur d'admission 
	 * @param coreCoord_services_ipURI					URI du coordinateur de coeurs
	 */
	public PerformanceController(
			String pcURI,
			String pc_management_ipURI,
			String rd_management_ipURI,
			String rd_request_notification_ipURI, // pour le passer aux avms libres
			ArrayList<String> avms_URI, /* AVMs URI */
			ArrayList<String> avmsIntrospectionInboundPortURIs /* AVMs introspection */,
			ArrayList<String> avmsManagementInboundPortURIs /* AVMs management */,
			int seuil_inf,
			int seuil_sup,
			int limite_nb_avms,
			String admissionControllerServicesInboundPortURI,
			String coreCoord_services_ipURI
	) throws Exception {
		
		super(1,1);
		//Preconditions
		assert pcURI != null;
		assert pc_management_ipURI != null;
		assert rd_management_ipURI != null;
		assert rd_request_notification_ipURI != null;
		assert avms_URI != null && avms_URI.size() != 0;
		assert avmsIntrospectionInboundPortURIs != null && avmsIntrospectionInboundPortURIs.size() != 0;
		assert avmsManagementInboundPortURIs != null && avmsManagementInboundPortURIs.size() != 0;
		assert admissionControllerServicesInboundPortURI != null;
		assert seuil_inf <= seuil_sup;
		assert coreCoord_services_ipURI != null;
		assert limite_nb_avms < avms_URI.size();
		
		/**Variables*/
		this.pcURI = pcURI;
		this.SEUIL_INF = seuil_inf;
		this.SEUIL_SUP = seuil_sup;
		this.rdmipURI = rd_management_ipURI;
		this.rd_rnipURI = rd_request_notification_ipURI;
		this.computer_list_avm = new HashMap<String, ArrayList<String>>();
		this.admissionControllerServicesInboundPortURI = admissionControllerServicesInboundPortURI;
		this.avmURIs = avms_URI;
		this.listAVMs_libre = new ArrayList<String>();
		this.number_of_cores_of_avm = new HashMap<String, Integer>();
		this.allocatedCoreAdmissibleFrequencies = new HashMap<AllocatedCore, ArrayList<Integer>>();
		this.allocatedCoreStates = new HashMap<AllocatedCore, Integer>();
		this.coreCoord_services_ipURI = coreCoord_services_ipURI;
		this.limite_nb_avms = limite_nb_avms;
		this.variance_avms = 0;
		
		/**AVMs*/
		this.avmsManagementInboundPortURIs = new HashMap<String, String>();
		this.avmsIntrospectionInboundPortURIs = new HashMap<String, String>();
		for(int i=0; i<avms_URI.size(); i++) {
			this.avmsManagementInboundPortURIs.put(avms_URI.get(i), avmsManagementInboundPortURIs.get(i));
			this.avmsIntrospectionInboundPortURIs.put(avms_URI.get(i), avmsIntrospectionInboundPortURIs.get(i));
		}
		
		/**Processor*/
		this.addOfferedInterface(ProcessorIntrospectionI.class);
		this.processorIntrospectionOutboundPorts = new HashMap<String, ProcessorIntrospectionOutboundPort>();
		this.processorManagementOutboundPorts = new HashMap<AllocatedCore, ProcessorManagementOutboundPort>();
		this.pmip_processorManagementOutboundPorts = new HashMap<String, ProcessorManagementOutboundPort>();
		
		/*Management*/
		this.addOfferedInterface(PerformanceControllerManagementI.class);
		this.pcmip = new PerformanceControllerManagementInboundPort(pc_management_ipURI, this);
		this.addPort(this.pcmip);
		this.pcmip.publishPort();
		
		
		/**required**/
		
		//AVMsIntrospection
		this.avmsIntrospectionOutboundPorts = new HashMap<String, ApplicationVMIntrospectionOutboundPort>();
		this.addRequiredInterface(ApplicationVMIntrospectionI.class) ;
		for(String avmuri : this.avmsIntrospectionInboundPortURIs.keySet()) {
			this.avmsIntrospectionOutboundPorts.put(avmuri, new ApplicationVMIntrospectionOutboundPort(this));
			this.addPort(this.avmsIntrospectionOutboundPorts.get(avmuri)) ;
			this.avmsIntrospectionOutboundPorts.get(avmuri).publishPort() ;
		}
		
		//AVMsManagement
		this.avmsManagementOutboundPorts = new HashMap<String, ApplicationVMManagementOutboundPort>();
		this.addRequiredInterface(ApplicationVMManagementI.class) ;
		for(String avmuri : this.avmsManagementInboundPortURIs.keySet()) {
			this.avmsManagementOutboundPorts.put(avmuri, new ApplicationVMManagementOutboundPort(this));
			this.addPort(this.avmsManagementOutboundPorts.get(avmuri)) ;
			this.avmsManagementOutboundPorts.get(avmuri).publishPort() ;
		}
		
		//ReqDisp Management
		this.addRequiredInterface(RequestDispatcherManagementI.class);
		this.rdmop = new RequestDispatcherManagementOutboundPort(this);
		this.addPort(this.rdmop);
		this.rdmop.publishPort();
		
		//Admission Controller Services
		this.addRequiredInterface(AdmissionControllerServicesI.class);
		this.acsop = new AdmissionControllerServicesOutboundPort(this);
		this.addPort(this.acsop);
		this.acsop.publishPort();
		
		/**Coordinateur de coeur*/
		this.addRequiredInterface(CoreCoordinatorServicesI.class);
		this.coreCoord_services_op = new CoreCoordinatorServicesOutboundPort(this);
		this.addPort(coreCoord_services_op);
		this.coreCoord_services_op.publishPort();
	}
	
	// Component life cycle
	
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.performanceController.interfaces.PerformanceControllerManagementI#connectOutboundPorts()
	 */
	@Override
	public void connectOutboundPorts() throws Exception {
		this.doPortConnection(
				this.rdmop.getPortURI(), 
				this.rdmipURI, 
				RequestDispatcherManagementConnector.class.getCanonicalName());
		
		
		for(String avmuri : this.avmsIntrospectionOutboundPorts.keySet()) {
			this.doPortConnection(
					this.avmsIntrospectionOutboundPorts.get(avmuri).getPortURI(),
					this.avmsIntrospectionInboundPortURIs.get(avmuri),
					ApplicationVMIntrospectionConnector.class.getCanonicalName()) ;
		}
		
		for(String avmuri : this.avmsManagementOutboundPorts.keySet()) {
			this.doPortConnection(
					this.avmsManagementOutboundPorts.get(avmuri).getPortURI(),
					this.avmsManagementInboundPortURIs.get(avmuri),
					ApplicationVMManagementConnector.class.getCanonicalName()) ;
		}
		this.doPortConnection(
				this.acsop.getPortURI(),
				this.admissionControllerServicesInboundPortURI,
				AdmissionControllerServicesConnector.class.getCanonicalName() );
		this.doPortConnection(
				this.coreCoord_services_op.getPortURI(),
				this.coreCoord_services_ipURI,
				CoreCoordinatorServicesConnector.class.getCanonicalName() );
	}
	
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public void execute() throws Exception {
		super.execute();
		controlAverageReqDuration(3000);
	}
	
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{	
		this.doPortDisconnection(this.rdmop.getPortURI());
		
		for(String avmuri : this.avmsIntrospectionOutboundPorts.keySet()) {
			this.doPortDisconnection(this.avmsIntrospectionOutboundPorts.get(avmuri).getPortURI()) ;
		}
		for(String avmuri : this.avmsManagementOutboundPorts.keySet()) {
			this.doPortDisconnection(this.avmsManagementOutboundPorts.get(avmuri).getPortURI()) ;
		}
		for(String processor : this.processorIntrospectionOutboundPorts.keySet()) {
			this.doPortDisconnection(this.processorIntrospectionOutboundPorts.get(processor).getPortURI());
		}
		for(String processor : this.pmip_processorManagementOutboundPorts.keySet()) {
			this.doPortDisconnection(this.pmip_processorManagementOutboundPorts.get(processor).getPortURI());
		}
		this.doPortDisconnection(this.acsop.getPortURI());
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
			
			this.rdmop.unpublishPort();
			this.pcmip.unpublishPort();
			for(String avmuri : this.avmsIntrospectionOutboundPorts.keySet()) {
				this.avmsIntrospectionOutboundPorts.get(avmuri).unpublishPort() ;
			}
			for(String avmuri : this.avmsManagementOutboundPorts.keySet()) {
				this.avmsManagementOutboundPorts.get(avmuri).unpublishPort() ;
			}
			for(String processor : this.processorIntrospectionOutboundPorts.keySet()) {
				this.processorIntrospectionOutboundPorts.get(processor).unpublishPort();
			}
			for(AllocatedCore ac : this.processorManagementOutboundPorts.keySet()) {
				this.processorManagementOutboundPorts.get(ac).unpublishPort();
			}
			this.acsop.unpublishPort();
			this.coreCoord_services_op.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdown();
		
	}

	//--------------------------------------------------------------------
	// METHODS
	//--------------------------------------------------------------------
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.performanceController.interfaces.PerformanceControllerManagementI#toggleTracingLogging()
	 */
	@Override
	public void toggleTracingLogging() {
		this.toggleTracing();
		this.toggleLogging();
		this.logMessage( "PC " +this.pcURI +" start");
	}
	
	/**
	 * Cette méthode appel à checkPerformance tous les interval milliseconds pour interroger sur
	 * la performance
	 * 
	 * @param interval		Intervalle de temps où on inspecte le temps moyen d'exécution des requêtes
	 * @throws Exception
	 */
	private void controlAverageReqDuration(int interval) throws Exception {
		this.scheduleTaskAtFixedRate(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
							((PerformanceController)this.getOwner()).checkPerformance();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				},
				TimeManagement.acceleratedDelay(interval),
				TimeManagement.acceleratedDelay(interval),
				TimeUnit.MILLISECONDS
		) ;
	}
	
	/**
	 * Récupère de répartiteur de requête le temps moyen d'exécution et varie la performance
	 * selon exigence demandé.
	 * 
	 * @throws Exception
	 */
	private void checkPerformance() throws Exception {

		// On récupère les données sur les coeurs attribués aux avms si on ne les a pas
		// on considère par la suite que ces données sont manipulées dans le contrôleur de performance
		if(this.allocatedCoreStates.isEmpty()) {
			this.updateProcessorData();
		}
		if(this.number_of_cores_of_avm.isEmpty()) {
			this.updateNbCoresOfAVMs();
		}
		boolean canReserve = false;
		String avm = null;
		for(String cpuri : this.computer_list_avm.keySet()) {
			canReserve = this.coreCoord_services_op.reserveCore(cpuri, this.pcURI);
			if(canReserve) {
				avm = this.computer_list_avm.get(cpuri).get(0);
				break;
			}
		}
		
		// Utilise sleep pour bien voir la coordination des coeurs.
		//Thread.sleep(4000);
		
		long moyenne = this.rdmop.getAverageReqDuration();
		this.logMessage("PerfControl. "+ this.pcURI+"| temps moyenne "+moyenne+".");
		if(moyenne > SEUIL_SUP) {
			// incrémente la fréquence
			if(!this.increaseFrequency()) {
				if(canReserve) {
					// augmente de nombre de coeur 
					this.findAVMAndAddAllocateCore(avm);
				}
				else {
					// ajoute une avm en cherchant un coeur sur d'autres ordinateurs
					this.addAVM();
				}
			}
			else {
				if(canReserve) {
					this.coreCoord_services_op.makeChoice(this.pcURI, false);
				}
			}
		}
		else {
			if(canReserve) {
				this.coreCoord_services_op.makeChoice(this.pcURI, false);
			}
		}
		if(moyenne < SEUIL_INF) {
			// décrémente la fréquence
			if(!this.decreaseFrequency()) {
				// si on ne plus décrémenter la fréquence, diminue le nombre de coeur
				if(!this.findAVMAndRemoveAllocateCore()) {
					// si tous les avms ne possèdent qu'un seul coeur, on commence à retirer des avms
					this.removeAVM();
				}
			}
		}
	}
	
	/**
	 * Incrémente la fréquence du coeur d'un cran supérieur 
	 * (on possède un liste de coeurs qui sont alloués pour cette application)
	 * @return si on a réussi à incrémenter la fréquence d'un coeur ou pas
	 * @throws Exception
	 */
	private boolean increaseFrequency() throws Exception {
		for(AllocatedCore ac : this.allocatedCoreStates.keySet()) {
			int index = this.allocatedCoreAdmissibleFrequencies.get(ac).lastIndexOf(this.allocatedCoreStates.get(ac));
			if(index < (this.allocatedCoreAdmissibleFrequencies.get(ac).size()-1)) {
				int freq = this.allocatedCoreAdmissibleFrequencies.get(ac).get(index+1);
				this.processorManagementOutboundPorts.get(ac).setCoreFrequency(ac.coreNo, freq);
				this.logMessage("PerfControl. "+ this.pcURI + "| coeur "+ac.processorURI+"-"+ac.coreNo+" passe de "+this.allocatedCoreStates.get(ac)+"Hz à "+freq+"Hz.");
				this.allocatedCoreStates.replace(ac, freq);
				return true;
			}
		}
		this.logMessage("PerfControl. "+ this.pcURI + "| ne peut plus augmenter la fréquence des coeurs.");
		return false;
	}
	
	/**
	 * Décrémente la fréquence du coeur d'un cran inférieur 
	 * 
	 * @return si on a réussi à décrémenter la fréquence d'un coeur ou pas
	 * @throws Exception
	 */
	private boolean decreaseFrequency() throws Exception {
		for(AllocatedCore ac : this.allocatedCoreStates.keySet()) {
			int index = this.allocatedCoreAdmissibleFrequencies.get(ac).indexOf(this.allocatedCoreStates.get(ac));
			if(index > 0) {
				int freq = this.allocatedCoreAdmissibleFrequencies.get(ac).get(index-1);
				this.processorManagementOutboundPorts.get(ac).setCoreFrequency(ac.coreNo, freq);
				this.logMessage("PerfControl. "+ this.pcURI + "| coeur "+ac.processorURI+"-"+ac.coreNo+" passe de "+this.allocatedCoreStates.get(ac)+"Hz à "+freq+"Hz.");
				this.allocatedCoreStates.replace(ac, freq);
				return true;
			}
		}
		this.logMessage("PerfControl. "+ this.pcURI + "| ne peut plus diminuer la fréquence des coeurs.");
		return false;
	}
	
	/**
	 * Trouve un avm dans lequel l'ordinateur qu'il héberge possède encore des coeurs libres
	 * (on possède une liste des ordinateurs dans lesquels nos avms s'est hébergé)
	 * 
	 * @return si on a réussi à trouver des coeurs libres ou pas sur les ordinateurs hébergés par nos avms
	 * @throws Exception
	 */
	private boolean findAVMAndAddAllocateCore(String avm) throws Exception {
		AllocatedCore ac = this.coreCoord_services_op.makeChoice(this.pcURI, true);
		if(ac != null && avm != null) {
			ApplicationVMManagementOutboundPort avmmop = avmsManagementOutboundPorts.get(avm);
			avmmop.addAllocateCore(ac);
			this.connectProcessorPorts(ac);
			this.number_of_cores_of_avm.put(avm, this.number_of_cores_of_avm.get(avm)+1);
			logMessage("PerfControl. "+ this.pcURI+"| ajoute un coeur à l'avm "+ avm +".");
			return true;
		}
		else {
			logMessage("PerfControl. "+ this.pcURI + "| ne peut plus ajouter de coeur.");
			return false;
		}
	}
	
	/**
	 * Retire un coeur à l'avm qui possède le plus de coeur
	 * 
	 * @return si on a réussi à retirer un coeur à une avm, on ne retire pas de coeur au avm qui n'en possède qu'un
	 * @throws Exception
	 */
	private boolean findAVMAndRemoveAllocateCore() throws Exception {
		String avm = Collections.max(this.number_of_cores_of_avm.entrySet(),
									  Comparator.comparingInt(Map.Entry::getValue)).getKey();
		if(this.number_of_cores_of_avm.get(avm) == 1) {
			logMessage("PerfControl. "+ this.pcURI+"| ne peut plus retirer de coeur.");
			return false;
		}
		AllocatedCore ac = this.avmsManagementOutboundPorts.get(avm).removeAllocateCore();
		this.coreCoord_services_op.releaseCore(ac);
		this.number_of_cores_of_avm.put(avm, this.number_of_cores_of_avm.get(avm)-1);
		this.allocatedCoreStates.remove(ac);
		this.allocatedCoreAdmissibleFrequencies.remove(ac);
		logMessage("PerfControl. "+ this.pcURI+"| retire un coeur à l'avm "+ avm +".");
		return true;
	}
	
	/**
	 * Essaye d'abord de trouver un coeur libre sur tous les ordinateurs que possède le contrôleur
	 * d'admission, si on trouve un, on ajoute cet avm dans la liste des avms attribués à l'application
	 * 
	 * @throws Exception
	 */
	private void addAVM() throws Exception {
		if(this.variance_avms >= this.limite_nb_avms) {
			logMessage("PerfControl. "+ this.pcURI + "| atteint le nombre limite d'avms que cet application peut allouer.");
			logMessage("PerfControl. "+ this.pcURI + "| aucune augmentation de la performance n'a pu être effectuée.");
			return;
		}
		
		AllocatedCore[] ac = this.coreCoord_services_op.findComputerAndAllocateCores(1);
		if(ac.length == 0) {
			logMessage("PerfControl. "+ this.pcURI + "| plus de coeur libre parmi tous les ordinateurs que nous disposons.");
			logMessage("PerfControl. "+ this.pcURI + "| aucune augmentation de la performance n'a pu être effectuée.");
			return;
		}
		
		Map<ApplicationVMPortTypes, String> freeAVM = this.acsop.allocateFreeAVM();
		if( freeAVM != null) {
			String avm_reqSub = freeAVM.get(ApplicationVMPortTypes.REQUEST_SUBMISSION);
			String avm_intro = freeAVM.get(ApplicationVMPortTypes.INTROSPECTION);
			String avm_manage = freeAVM.get(ApplicationVMPortTypes.MANAGEMENT);
			
			ApplicationVMIntrospectionOutboundPort avmiop = new ApplicationVMIntrospectionOutboundPort(this);
			this.addPort(avmiop) ;
			avmiop.publishPort() ;
			this.doPortConnection(
					avmiop.getPortURI(),
					avm_intro,
					ApplicationVMIntrospectionConnector.class.getCanonicalName()) ;
			String avmURI = avmiop.getDynamicState().getApplicationVMURI();
			this.avmsIntrospectionInboundPortURIs.put(avmURI, avm_intro);
			this.avmsIntrospectionOutboundPorts.put(avmURI, avmiop);
			
			this.avmsManagementInboundPortURIs.put(avmURI, avm_manage);
			this.avmsManagementOutboundPorts.put(avmURI, new ApplicationVMManagementOutboundPort(this));
			this.addPort(this.avmsManagementOutboundPorts.get(avmURI)) ;
			this.avmsManagementOutboundPorts.get(avmURI).publishPort() ;
			this.doPortConnection(
					this.avmsManagementOutboundPorts.get(avmURI).getPortURI(),
					this.avmsManagementInboundPortURIs.get(avmURI),
					ApplicationVMManagementConnector.class.getCanonicalName()) ;
			
			this.avmsManagementOutboundPorts.get(avmURI).connectOutboundPorts(this.rd_rnipURI);
			this.avmsManagementOutboundPorts.get(avmURI).allocateCores(ac);
			this.avmURIs.add(avmURI);
			
			connectProcessorPorts(ac[0]);
			rdmop.addAVM(avm_reqSub);
			
			listAVMs_libre.add(avmURI);
			this.updateNbCoresOfAVMs();
			this.updateProcessorData();
			this.variance_avms++;
			logMessage("PerfControl. "+ this.pcURI + "| ajoute une avm.");
		}
		else {
			logMessage("PerfControl. "+ this.pcURI + "| plus d'avm libre.");
			logMessage("PerfControl. "+ this.pcURI + "| aucune augmentation de la performance n'a pu être effectuée.");
		}
	}
	
	/**
	 * Retire une avm à l'application
	 * 
	 * @throws Exception
	 */
	private void removeAVM() throws Exception {
		if(this.variance_avms <= -(this.limite_nb_avms)) {
			logMessage("PerfControl. "+ this.pcURI + "| atteint le nombre limite d'avms que cet application peut déallouer.");
			logMessage("PerfControl. "+ this.pcURI + "| aucune augmentation de la performance n'a pu être effectuée.");
			return;
		}
		// c'est un appel interne dans le cas où tous les AVMs ne restent plus qu'un seul coeur
		String avmURI = null;
		if(!this.listAVMs_libre.isEmpty()) {
			avmURI = this.listAVMs_libre.remove(0);
		}
		else {
			avmURI = this.avmURIs.remove(0);
		}
		this.variance_avms--;
		
		this.rdmop.removeAVM(this.avmsIntrospectionOutboundPorts.get(avmURI).getAVMPortsURI().get(ApplicationVMPortTypes.REQUEST_SUBMISSION));
		
		AllocatedCore ac = this.avmsManagementOutboundPorts.get(avmURI).removeAllocateCore();
		this.coreCoord_services_op.releaseCore(ac);
		this.number_of_cores_of_avm.put(avmURI, this.number_of_cores_of_avm.get(avmURI)-1);
		this.allocatedCoreStates.remove(ac);
		this.allocatedCoreAdmissibleFrequencies.remove(ac);
		this.processorManagementOutboundPorts.remove(ac);
		
		this.avmsIntrospectionInboundPortURIs.remove(avmURI);
		ApplicationVMIntrospectionOutboundPort avmiop = this.avmsIntrospectionOutboundPorts.remove(avmURI);
		String cpuri = avmiop.getDynamicState().getComputerURI();
		this.computer_list_avm.get(cpuri).remove(avmURI);
		this.doPortDisconnection(avmiop.getPortURI());
		avmiop.unpublishPort();
		this.removePort(avmiop);
		

		this.avmsManagementInboundPortURIs.remove(avmURI);
		ApplicationVMManagementOutboundPort avmmop = this.avmsManagementOutboundPorts.remove(avmURI);
		avmmop.disconnectOutboundPorts();
		this.doPortDisconnection(avmmop.getPortURI());
		avmmop.unpublishPort();
		this.removePort(avmmop);
		
		
		
		this.avmURIs.remove(avmURI);
		
		this.acsop.recycleFreeAVM(avmURI);
	}
	
	/**
	 * Met à jour les informations sur le nombre de coeurs attribués aux avms
	 * @throws Exception
	 */
	private void updateNbCoresOfAVMs() throws Exception {
		for(String avmuri : this.avmsIntrospectionOutboundPorts.keySet()) {
			ApplicationVMIntrospectionOutboundPort avmiop = avmsIntrospectionOutboundPorts.get(avmuri);
			
			int nb_cores = avmiop.getDynamicState().getCoresStatus().size();
			this.number_of_cores_of_avm.put(avmuri, nb_cores);
			
			String cpuri = avmiop.getDynamicState().getComputerURI();
			if(this.computer_list_avm.containsKey(cpuri)) {
				this.computer_list_avm.get(cpuri).add(avmuri);
			}
			else {
				ArrayList<String> list_avm_of_cp = new ArrayList<String>();
				list_avm_of_cp.add(avmuri);
				this.computer_list_avm.put(cpuri, list_avm_of_cp);
			}
			
		}
	}
	
	/**
	 * Met à jour les informations sur les données des processeurs qui sont utilisé pour notre application
	 * @throws Exception
	 */
	private void updateProcessorData() throws Exception {
		for(String avmuri : avmsIntrospectionOutboundPorts.keySet()) {
			for(AllocatedCore ac : this.avmsIntrospectionOutboundPorts.get(avmuri).getDynamicState().getCoresStatus()) {
				this.connectProcessorPorts(ac);
			}
		}
	}
	
	/**
	 * Connecte les ports des processeurs lorsqu'on alloue un nouveau coeur, pour savoir les différents données du processeur
	 * (Fréquences acceptés et la demande de manipulation des fréquences)
	 * @throws Exception
	 */
	private void connectProcessorPorts(AllocatedCore ac) throws Exception {
		String piip = ac.processorInboundPortURI.get(ProcessorPortTypes.INTROSPECTION);
		String pmip = ac.processorInboundPortURI.get(ProcessorPortTypes.MANAGEMENT);
		if(!processorIntrospectionOutboundPorts.keySet().contains(piip)) {
			ProcessorIntrospectionOutboundPort p = new ProcessorIntrospectionOutboundPort(this);
			this.addPort(p);
			p.publishPort() ;
			this.doPortConnection(
					p.getPortURI(), 
					piip,
					ProcessorIntrospectionConnector.class.getCanonicalName()) ;
			processorIntrospectionOutboundPorts.put(piip, p);
			
			ProcessorManagementOutboundPort pm = new ProcessorManagementOutboundPort(this);
			this.addPort(pm);
			pm.publishPort();
			this.doPortConnection(
					pm.getPortURI(), 
					pmip, 
					ProcessorManagementConnector.class.getCanonicalName()) ;
			pmip_processorManagementOutboundPorts.put(pmip, pm);
		}
		allocatedCoreStates.put(ac, processorIntrospectionOutboundPorts.get(piip).getDynamicState().getCurrentCoreFrequency(ac.coreNo));
		ArrayList<Integer> sortedList = new ArrayList<Integer>(processorIntrospectionOutboundPorts.get(piip).getStaticState().getAdmissibleFrequencies());
		Collections.sort(sortedList);
		allocatedCoreAdmissibleFrequencies.put(ac, sortedList);
		processorManagementOutboundPorts.put(ac, this.pmip_processorManagementOutboundPorts.get(pmip));
	}
}
