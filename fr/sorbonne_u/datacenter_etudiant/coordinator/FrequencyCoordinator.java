package fr.sorbonne_u.datacenter_etudiant.coordinator;

import java.util.ArrayList;
import java.util.HashMap;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.datacenter.hardware.processors.connectors.ProcessorManagementConnector;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorIntrospectionOutboundPort;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorManagementOutboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementInboundPort;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.AdmissionControllerServicesI;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.ports.AdmissionControllerServicesOutboundPort;
import fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.FrequencyCoordinatorServicesI;
import fr.sorbonne_u.datacenter_etudiant.coordinator.ports.FrequencyCoordinatorServicesInboundPort;
import fr.sorbonne_u.datacenter_etudiant.performanceController.connectors.PerformanceControllerManagementConnector;
import fr.sorbonne_u.datacenter_etudiant.performanceController.interfaces.PerformanceControllerManagementI;
import fr.sorbonne_u.datacenter_etudiant.performanceController.ports.PerformanceControllerManagementOutboundPort;
import fr.sorbonne_u.datacenter_etudiant.requestdispatcher.connectors.RequestDispatcherManagementConnector;

public class FrequencyCoordinator 
extends AbstractComponent 
implements FrequencyCoordinatorServicesI {
	
	protected String freqCoor_URI;
	protected FrequencyCoordinatorServicesInboundPort fcsip;

	protected HashMap<String, PerformanceControllerManagementOutboundPort> pcmops;
	protected HashMap<String, ArrayList<String>> procURI_pcmipURIs;
	
	public FrequencyCoordinator(
			String freqCoor_URI,
			String fcsip_URI
			) throws Exception {
		super(1,1);
		
		assert freqCoor_URI != null;
		assert fcsip_URI != null;
		
		/*init simple attributes*/
		this.freqCoor_URI = freqCoor_URI;
		this.pcmops = new HashMap<String, PerformanceControllerManagementOutboundPort>();
		this.procURI_pcmipURIs = new HashMap<String, ArrayList<String>>();
		
		/*Offered*/
		this.addOfferedInterface(FrequencyCoordinatorServicesI.class) ;
		this.fcsip = new FrequencyCoordinatorServicesInboundPort(fcsip_URI, this) ;
		this.addPort(this.fcsip) ;
		this.fcsip.publishPort() ;
	}

	@Override
	public synchronized void increaseFrequencyOutOfGap(String procURI, int coreNo, String pcmip_URI) throws Exception {
		ArrayList<String> all_pc_to_coordinate = this.procURI_pcmipURIs.get(procURI);
		for(String pc : all_pc_to_coordinate) {
			this.pcmops.get(pc).orderIncreaseCoresFrequencyOf(procURI);
		}
	}

	@Override
	public synchronized void notifyAddProc(String pcmip_URI, String procURI) throws Exception {
		if(!this.pcmops.keySet().contains(pcmip_URI)) {
			createAndConnect(pcmip_URI);
		}
		
		if(!procURI_pcmipURIs.keySet().contains(procURI)) {
			procURI_pcmipURIs.put(procURI, new ArrayList<String>());
		}
		
		if(!procURI_pcmipURIs.get(procURI).contains(pcmip_URI)) {
			procURI_pcmipURIs.get(procURI).add(pcmip_URI);
		}
	}

	@Override
	public synchronized void notifyRemoveProc(String pcmip_URI, String procURI) throws Exception {
		procURI_pcmipURIs.get(procURI).remove(pcmip_URI);
	}
	
	private synchronized void createAndConnect(String pcmip_URI) throws Exception {
		/*creation pcmop*/
		this.addRequiredInterface(PerformanceControllerManagementI.class);
		PerformanceControllerManagementOutboundPort pcmop = new PerformanceControllerManagementOutboundPort(this);
		this.addPort(pcmop);
		pcmop.publishPort();
		
		/*ajout dans la hashmap*/
		this.pcmops.put(pcmip_URI, pcmop);
		
		/*connection*/
		this.doPortConnection(
				pcmop.getPortURI(), 
				pcmip_URI, 
				PerformanceControllerManagementConnector.class.getCanonicalName());
	}
}
