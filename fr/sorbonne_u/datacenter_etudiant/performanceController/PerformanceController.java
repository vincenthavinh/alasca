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
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.sorbonne_u.datacenter.hardware.processors.Processor.ProcessorPortTypes;
import fr.sorbonne_u.datacenter.hardware.processors.connectors.ProcessorIntrospectionConnector;
import fr.sorbonne_u.datacenter.hardware.processors.connectors.ProcessorManagementConnector;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorIntrospectionOutboundPort;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorManagementOutboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.connectors.ApplicationVMIntrospectionConnector;
import fr.sorbonne_u.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMIntrospectionI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMIntrospectionOutboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.sorbonne_u.datacenter_etudiant.performanceController.interfaces.PerformanceControllerManagementI;
import fr.sorbonne_u.datacenter_etudiant.performanceController.ports.PerformanceControllerManagementInboundPort;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.connectors.RequestDispatcherPerfManagementConnector;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherManagementI;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces.RequestDispatcherPerfManagementI;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.ports.RequestDispatcherManagementOutboundPort;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.ports.RequestDispatcherPerfManagementOutboundPort;

public class PerformanceController 
extends AbstractComponent{	
	protected String pcURI;
	
	//var
	protected final int SEUIL_INF;
	protected final int SEUIL_SUP;
	protected final int NB_AVMS_MANIPULABLES;
	protected int index_avm;
	protected ArrayList<String> avmURIs;
	protected HashMap<String, Integer> number_of_cores_of_avm;
	protected HashMap<AllocatedCore, Integer> allocatedCoreStates;
	protected HashMap<AllocatedCore, ArrayList<Integer>> allocatedCoreAdmissibleFrequencies;
	protected HashMap<AllocatedCore, ProcessorManagementOutboundPort> processorManagementOutboundPorts;
	protected HashMap<String, ProcessorIntrospectionOutboundPort> processorIntrospectionOutboundPorts;
	protected HashMap<String, ProcessorManagementOutboundPort> pmip_processorManagementOutboundPorts;
	
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
	
	/**Request Dispatcher**/
	protected String rdmipURI;
	protected RequestDispatcherPerfManagementOutboundPort rdmop;
	
	public PerformanceController(
			String pcURI,
			String pc_management_ipURI,
			String rd_management_ipURI,
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
		this.rdmipURI = rd_management_ipURI;
		this.cp_ComputerServicesInboundPortURIs = cp_computerServicesInboundPortURIs;
		this.avmsManagementInboundPortURIs = avmsManagementInboundPortURIs;
		this.avmsIntrospectionInboundPortURIs = avmsIntrospectionInboundPortURIs;
		this.index_avm = 0;
		this.avmURIs = new ArrayList<String>();
		for(String avmURI : avmsIntrospectionInboundPortURIs.keySet()) {
			avmURIs.add(avmURI);
		}
		this.number_of_cores_of_avm = new HashMap<String, Integer>();
		this.allocatedCoreAdmissibleFrequencies = new HashMap<AllocatedCore, ArrayList<Integer>>();
		this.allocatedCoreStates = new HashMap<AllocatedCore, Integer>();
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
		
		//ReqDisp Management
		this.addRequiredInterface(RequestDispatcherPerfManagementI.class);
		this.rdmop = new RequestDispatcherPerfManagementOutboundPort(this);
		this.addPort(this.rdmop);
		this.rdmop.publishPort();
		
	}
	
	// Component life cycle
	
	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;
		try{
			this.connectOutboundPorts();
		}catch(Exception e) {
			throw new ComponentStartException();
		}
	}
	
	public void connectOutboundPorts() throws Exception {
		this.doPortConnection(
				this.rdmop.getPortURI(), 
				this.rdmipURI, 
				RequestDispatcherPerfManagementConnector.class.getCanonicalName());
		
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
	}
	
	@Override
	public void			finalise() throws Exception
	{	
		this.doPortDisconnection(this.rdmop.getPortURI());
		
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
		for(String processor : this.pmip_processorManagementOutboundPorts.keySet()) {
			this.doPortDisconnection(this.pmip_processorManagementOutboundPorts.get(processor).getPortURI());
		}
		super.finalise() ;
	}
	
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			
			this.rdmop.unpublishPort();
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
			for(AllocatedCore ac : this.processorManagementOutboundPorts.keySet()) {
				this.processorManagementOutboundPorts.get(ac).unpublishPort();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdown();
		
	}

	public void toggleTracingLogging() {
		this.toggleTracing();
		this.toggleLogging();
	}
	
	@Override
	public void execute() throws Exception {
		super.execute();
		
		periodicCheckMean(3);
	}
	
	// -------------------------------------------------------------------------
	// Component internal services
	// -------------------------------------------------------------------------
	
	public void periodicCheckMean(int interval) throws Exception {
		while(true) {
			TimeUnit.SECONDS.sleep(interval);
			long moyenne = this.rdmop.getAverageReqDuration();
			checkPerformance(moyenne);
		}
	}

	public void checkPerformance(long moyenne) throws Exception {
		if(this.allocatedCoreStates.isEmpty()) {
			this.updateProcessorData();
		}
		if(this.number_of_cores_of_avm.isEmpty()) {
			this.updateNbCoresOfAVMs();
		}
		this.logMessage("PerfControl. "+ this.pcURI+"| temps moyenne "+moyenne+".");
		if(moyenne < SEUIL_INF) {
			this.findCoreAndDecreaseFrequency();
		}
		if(moyenne > SEUIL_SUP) {
			this.findCoreAndIncreaseFrequency();
		}
	}
	
	private void findCoreAndIncreaseFrequency() throws Exception {
		boolean nothingChange = true;
		for(AllocatedCore ac : this.allocatedCoreStates.keySet()) {
			int index = this.allocatedCoreAdmissibleFrequencies.get(ac).lastIndexOf(this.allocatedCoreStates.get(ac));
			if(index < (this.allocatedCoreAdmissibleFrequencies.get(ac).size()-1)) {
				int freq = this.allocatedCoreAdmissibleFrequencies.get(ac).get(index+1);
				this.processorManagementOutboundPorts.get(ac).setCoreFrequency(ac.coreNo, freq);
				this.logMessage("PerfControl. "+ this.pcURI + "| coeur "+ac.processorURI+"-"+ac.coreNo+" passe de "+this.allocatedCoreStates.get(ac)+"Hz à "+freq+"Hz.");
				this.allocatedCoreStates.replace(ac, freq);
				nothingChange = false;
				break;
			}
		}
		if(nothingChange) {
			this.logMessage("PerfControl. "+ this.pcURI + "| ne peut plus augmenter la fréquence des coeurs.");
			this.findAVMAndAddAllocateCore();
		}
	}
	
	private void findCoreAndDecreaseFrequency() throws Exception {
		boolean nothingChange = true;
		for(AllocatedCore ac : this.allocatedCoreStates.keySet()) {
			int index = this.allocatedCoreAdmissibleFrequencies.get(ac).indexOf(this.allocatedCoreStates.get(ac));
			if(index > 0) {
				int freq = this.allocatedCoreAdmissibleFrequencies.get(ac).get(index-1);
				this.processorManagementOutboundPorts.get(ac).setCoreFrequency(ac.coreNo, freq);
				this.logMessage("PerfControl. "+ this.pcURI + "| coeur "+ac.processorURI+"-"+ac.coreNo+" passe de "+this.allocatedCoreStates.get(ac)+"Hz à "+freq+"Hz.");
				this.allocatedCoreStates.replace(ac, freq);
				nothingChange = false;
				break;
			}
		}
		if(nothingChange) {
			this.logMessage("PerfControl. "+ this.pcURI + "| ne peut plus diminuer la fréquence des coeurs.");
			this.findAVMAndRemoveAllocateCore();
		}
	}
	
	private void findAVMAndAddAllocateCore() throws Exception {
		String avm = avmURIs.get(index_avm++%avmURIs.size());
		ApplicationVMManagementOutboundPort avmmop = avmsManagementOutboundPorts.get(avm);
		ApplicationVMIntrospectionOutboundPort avmiop = avmsIntrospectionOutboundPorts.get(avm);
		String cpuri = avmiop.getDynamicState().getComputerURI();
		AllocatedCore ac = allocateCore(cpuri);
		if(ac != null) {
			avmmop.addAllocateCore(ac);
			this.connectProcessorPorts(ac);
			this.number_of_cores_of_avm.put(avm, this.number_of_cores_of_avm.get(avm)+1);
			logMessage("PerfControl. "+ this.pcURI+"| ajoute un coeur à l'avm "+ avm +".");
		}
		else {
			// ajoutes nb AVM
			logMessage("PerfControl. "+ this.pcURI + "| aucune augmentation de la performance n'a pu être effectuée.");
		}
	}
	
	private void findAVMAndRemoveAllocateCore() throws Exception {
		String avm = Collections.max(this.number_of_cores_of_avm.entrySet(),
									  Comparator.comparingInt(Map.Entry::getValue)).getKey();
		if(this.number_of_cores_of_avm.get(avm) == 1) {
			logMessage("PerfControl. "+ this.pcURI+"| aucune diminution de la performance n'a pu être effectuée.");
			// retire AVM
			return;
		}
		AllocatedCore ac = this.avmsManagementOutboundPorts.get(avm).removeAllocateCore();
		String computer_uri = ac.processorURI.split("-")[0];
		this.pc_ComputerServicesOutboundPorts.get(computer_uri).releaseCore(ac);
		this.number_of_cores_of_avm.put(avm, this.number_of_cores_of_avm.get(avm)-1);
		this.allocatedCoreStates.remove(ac);
		this.allocatedCoreAdmissibleFrequencies.remove(ac);
		logMessage("PerfControl. "+ this.pcURI+"| retire un coeur à l'avm "+ avm +".");
	}
	
	private AllocatedCore allocateCore(String cpuri) throws Exception { //default 1
		AllocatedCore[] allocatedCores = new AllocatedCore[0];
		ComputerServicesOutboundPort csop = this.pc_ComputerServicesOutboundPorts.get(cpuri);
		allocatedCores = csop.allocateCores(1) ;
		if(allocatedCores.length == 1) {
			logMessage("PerfControl. "+ this.pcURI+"| "+ allocatedCores.length + " coeur alloué depuis " + csop.getServerPortURI());
			return allocatedCores[0];
		}
		else {
			csop.releaseCores(allocatedCores);
		}
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
				this.connectProcessorPorts(ac);
			}
		}
	}
	
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
