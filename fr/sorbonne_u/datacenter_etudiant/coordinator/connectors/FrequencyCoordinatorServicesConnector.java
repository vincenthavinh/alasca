package fr.sorbonne_u.datacenter_etudiant.coordinator.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.CoreCoordinatorServicesI;
import fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.FrequencyCoordinatorServicesI;

public class FrequencyCoordinatorServicesConnector 
extends		AbstractConnector
implements	FrequencyCoordinatorServicesI{

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------
		
	@Override
	public void increaseFrequencyOutOfGap(String procURI, int coreNo, String perfContManInboundPort) throws Exception {
		((FrequencyCoordinatorServicesI)this.offering).increaseFrequencyOutOfGap(procURI, coreNo, perfContManInboundPort);		
	}

	@Override
	public void notifyAddProc(String pcmip_URI, String procURI) throws Exception {
		((FrequencyCoordinatorServicesI)this.offering).notifyAddProc(pcmip_URI, procURI);	
	}

	@Override
	public void notifyRemoveProc(String pcmip_URI, String procURI) throws Exception {
		((FrequencyCoordinatorServicesI)this.offering).notifyRemoveProc(pcmip_URI, procURI);
	}

}
