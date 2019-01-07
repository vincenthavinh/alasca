package fr.sorbonne_u.datacenter_etudiant.performanceController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.sorbonne_u.datacenter.hardware.processors.Processor.ProcessorPortTypes;
import fr.sorbonne_u.datacenter.hardware.processors.connectors.ProcessorIntrospectionConnector;
import fr.sorbonne_u.datacenter.hardware.processors.connectors.ProcessorServicesNotificationConnector;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorIntrospectionOutboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.connectors.ApplicationVMIntrospectionConnector;
import fr.sorbonne_u.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMIntrospectionI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMIntrospectionOutboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.sorbonne_u.datacenter_etudiant.performanceController.interfaces.PerformanceControllerManagementI;
import fr.sorbonne_u.datacenter_etudiant.performanceController.ports.PerformanceControllerManagementInboundPort;

public class PerformanceController 
extends AbstractComponent{	
	protected String pcURI;
	
	//var
	protected final int SEUIL_INF;
	protected final int SEUIL_SUP;
	protected final int NB_AVMS_MANIPULABLES;
	protected long tmp_moyenne;
	protected int qt_req;
	protected int index_avm;
	protected ArrayList<String> avmURIs;
	protected HashMap<String, Integer> number_of_cores_of_avm;
	protected HashMap<AllocatedCore, Integer> allocatedCoreStates;
	protected HashMap<AllocatedCore, Set<Integer>> allocatedCoreAdmissibleFrequencies;
	protected HashMap<String, ProcessorIntrospectionOutboundPort> processorIntrospectionOutboundPorts;
	
	/**Computer**/
	protected HashMap<String, String> cp_ComputerServicesInboundPortURIs;
	protected HashMap<String, ComputerServicesOutboundPort> pc_ComputerServicesOutboundPorts;
	
	/**AVMs introspections**/
	protected HashMap<String, String> avmsIntrospectionInboundPortURIs; // avmURI, introspectionIPURI
	protected HashMap<String, ApplicationVMIntrospectionOutboundPort> avmsIntrospectionOutboundPorts;// avmURI, introspectionOPURI
	
	/**AVMs management**/
	protected HashMap<String, String> avmsManagementInboundPortURIs; // avmURI, managementIPURI
	protected HashMap<String, ApplicationVMManagementOutboundPort> avmsManagementOutboundPorts; // avmURI, managementOPURI
	
	protected PerformanceControllerManagementInboundPort pcmip;
	
	public PerformanceController(
			String pcURI,
			String pc_management_ipURI,
			HashMap<String,String> avmsIntrospectionInboundPortURIs /* AVMs introspection */,
			HashMap<String,String> avmsManagementInboundPortURIs /* AVMs management */,
			HashMap<String,String> cp_computerServicesInboundPortURIs /* computer service */,
			int seuil_inf,
			int seuil_sup,
			int nb_avms_manipulables) throws Exception {
		
		super(1,1);
		//Preconditions
		assert cp_computerServicesInboundPortURIs != null && cp_computerServicesInboundPortURIs.size() != 0;
		assert avmsManagementInboundPortURIs != null && avmsManagementInboundPortURIs.size() != 0;
		assert avmsIntrospectionInboundPortURIs != null && avmsIntrospectionInboundPortURIs.size() != 0;
		assert pcURI != null && pc_management_ipURI != null;
		
		this.pcURI = pcURI;
		this.SEUIL_INF = seuil_inf;
		this.SEUIL_SUP = seuil_sup;
		this.NB_AVMS_MANIPULABLES = nb_avms_manipulables;
		this.cp_ComputerServicesInboundPortURIs = cp_computerServicesInboundPortURIs;
		this.avmsManagementInboundPortURIs = avmsManagementInboundPortURIs;
		this.avmsIntrospectionInboundPortURIs = avmsIntrospectionInboundPortURIs;
		this.index_avm = 0;
		this.avmURIs = new ArrayList<String>();
		for(String avmURI : avmsIntrospectionInboundPortURIs.keySet()) {
			avmURIs.add(avmURI);
		}
		this.number_of_cores_of_avm = new HashMap<String, Integer>();
		this.allocatedCoreAdmissibleFrequencies = new HashMap<AllocatedCore, Set<Integer>>();
		this.allocatedCoreStates = new HashMap<AllocatedCore, Integer>();
		this.processorIntrospectionOutboundPorts = new HashMap<String, ProcessorIntrospectionOutboundPort>();
		
		/*Management*/
		this.addOfferedInterface(PerformanceControllerManagementI.class);
		this.pcmip = new PerformanceControllerManagementInboundPort(pc_management_ipURI, this);
		this.addPort(this.pcmip);
		this.pcmip.publishPort();
		
		/**required**/
		//ComputerServices
		this.pc_ComputerServicesOutboundPorts = new HashMap<String, ComputerServicesOutboundPort>();
		this.addRequiredInterface(ComputerServicesI.class) ;
		for(String cpuri : this.cp_ComputerServicesInboundPortURIs.keySet()) {
			this.pc_ComputerServicesOutboundPorts.put(cpuri, new ComputerServicesOutboundPort(this));
			this.addPort(this.pc_ComputerServicesOutboundPorts.get(cpuri)) ;
			this.pc_ComputerServicesOutboundPorts.get(cpuri).publishPort();
		}
		
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
	}
	
	// Component life cycle
	
	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;
		try {
			for(String cpuri : this.pc_ComputerServicesOutboundPorts.keySet()){
				this.doPortConnection(
						this.pc_ComputerServicesOutboundPorts.get(cpuri).getPortURI(),
						this.cp_ComputerServicesInboundPortURIs.get(cpuri),
						ComputerServicesConnector.class.getCanonicalName()) ;
			}
			
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
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}
	
	@Override
	public void			finalise() throws Exception
	{	
		for(String cpuri : this.pc_ComputerServicesOutboundPorts.keySet()){
			this.doPortDisconnection(this.pc_ComputerServicesOutboundPorts.get(cpuri).getPortURI()) ;
		}
		for(String avmuri : this.avmsIntrospectionOutboundPorts.keySet()) {
			this.doPortDisconnection(this.avmsIntrospectionOutboundPorts.get(avmuri).getPortURI()) ;
		}
		for(String avmuri : this.avmsManagementOutboundPorts.keySet()) {
			this.doPortDisconnection(this.avmsManagementOutboundPorts.get(avmuri).getPortURI()) ;
		}
		for(String processor : this.processorIntrospectionOutboundPorts.keySet()) {
			this.doPortDisconnection(this.processorIntrospectionOutboundPorts.get(processor).getPortURI());
		}
		super.finalise() ;
	}
	
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			this.pcmip.unpublishPort();
			for(String cpuri : this.pc_ComputerServicesOutboundPorts.keySet()){
				this.pc_ComputerServicesOutboundPorts.get(cpuri).unpublishPort() ;
			}
			for(String avmuri : this.avmsIntrospectionOutboundPorts.keySet()) {
				this.avmsIntrospectionOutboundPorts.get(avmuri).unpublishPort() ;
			}
			for(String avmuri : this.avmsManagementOutboundPorts.keySet()) {
				this.avmsManagementOutboundPorts.get(avmuri).unpublishPort() ;
			}
			for(String processor : this.processorIntrospectionOutboundPorts.keySet()) {
				this.processorIntrospectionOutboundPorts.get(processor).unpublishPort();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdown();
		
	}

	public void connectOutboundPorts() {
		
	}
	
	public void toggleTracingLogging() {
		System.out.println("PC "+this.pcURI+" tooggled...");
		this.toggleTracing();
		this.toggleLogging();
	}
	
	// -------------------------------------------------------------------------
	// Component internal services
	// -------------------------------------------------------------------------
	
	public void receiveData(long temps_requete) throws Exception {
		long total = tmp_moyenne * qt_req; 
		qt_req++;
		tmp_moyenne = (total + temps_requete) / qt_req;
		logMessage("PerfControl. "+ this.pcURI+"| temps moyenne "+tmp_moyenne+".");
//		if(tmp_moyenne < SEUIL_INF) {
//			this.findAVMAndAddAllocateCore();
//		}
//		if(tmp_moyenne > SEUIL_SUP) {
//			this.findAVMAndRemoveAllocateCore();
//		}
	}
	
	private void findAVMAndAddAllocateCore() throws Exception {
		String avm = avmURIs.get(index_avm++%avmURIs.size());
		ApplicationVMManagementOutboundPort avmmop = avmsManagementOutboundPorts.get(avm);
		AllocatedCore ac = findComputerAndAllocateCore();
		if(ac != null) {
			avmmop.addAllocateCore(ac);
		}
		logMessage("PerfControl. "+ this.pcURI+"| ajoute un coeur à l'avm "+ avm +".");
	}
	
	private void findAVMAndRemoveAllocateCore() throws Exception {
		if(this.number_of_cores_of_avm.isEmpty()) {
			this.updateNbCoresOfAVMs();
		}
		String avm = Collections.max(this.number_of_cores_of_avm.entrySet(),
									  Comparator.comparingInt(Map.Entry::getValue)).getKey();
		AllocatedCore ac = this.avmsManagementOutboundPorts.get(avm).removeAllocateCore();
		String computer_uri = ac.processorURI.split("-")[0];
		this.pc_ComputerServicesOutboundPorts.get(computer_uri).releaseCore(ac);
		logMessage("PerfControl. "+ this.pcURI+"| retire un coeur à l'avm "+ avm +".");
	}
	
	// TODO ne pas parcourir tous les ordinateurs
	private AllocatedCore findComputerAndAllocateCore() throws Exception { //default 1
		AllocatedCore[] allocatedCores = new AllocatedCore[0];

		for(String cpuri : this.pc_ComputerServicesOutboundPorts.keySet()) {
			ComputerServicesOutboundPort csop = this.pc_ComputerServicesOutboundPorts.get(cpuri);
			allocatedCores = csop.allocateCores(1) ;
			if(allocatedCores.length == 1) {
				logMessage("PerfControl. "+ this.pcURI+"| "+ allocatedCores.length + " coeur alloué depuis " + csop.getServerPortURI());
				return allocatedCores[0];
			}
			else {
				csop.releaseCores(allocatedCores);
			}
		}
		logMessage("PerfControl. "+ this.pcURI+"| Aucun coeur n'a pu être alloué.");
		return null;
	}
	
	private void updateNbCoresOfAVMs() throws Exception {
		for(String avmuri : this.avmsIntrospectionOutboundPorts.keySet()) {
			int nb_cores = this.avmsIntrospectionOutboundPorts.get(avmuri).getDynamicState().getCoresStatus().size();
			this.number_of_cores_of_avm.put(avmuri, nb_cores);
		}
	}
	
	private void updateProcessorData() throws Exception {
		for(String avmuri : avmsIntrospectionOutboundPorts.keySet()) {
			for(AllocatedCore ac : this.avmsIntrospectionOutboundPorts.get(avmuri).getDynamicState().getCoresStatus()) {
				String piip = ac.processorInboundPortURI.get(ProcessorPortTypes.INTROSPECTION);
				if(!processorIntrospectionOutboundPorts.keySet().contains(piip)) {
					ProcessorIntrospectionOutboundPort p = new ProcessorIntrospectionOutboundPort(this);
					this.addPort(p);
					p.publishPort() ;
					this.doPortConnection(
							p.getPortURI(), 
							piip,
							ProcessorIntrospectionConnector.class.getCanonicalName()) ;
					processorIntrospectionOutboundPorts.put(piip, p);
				}
				allocatedCoreStates.put(ac, processorIntrospectionOutboundPorts.get(piip).getDynamicState().getCurrentCoreFrequency(ac.coreNo));
				allocatedCoreAdmissibleFrequencies.put(ac, processorIntrospectionOutboundPorts.get(piip).getStaticState().getAdmissibleFrequencies());
			}
		}
	}
}
