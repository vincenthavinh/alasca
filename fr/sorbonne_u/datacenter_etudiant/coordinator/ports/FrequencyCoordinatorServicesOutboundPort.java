package fr.sorbonne_u.datacenter_etudiant.coordinator.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.CoreCoordinatorServicesI;
import fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.FrequencyCoordinatorServicesI;

public class FrequencyCoordinatorServicesOutboundPort 
extends		AbstractOutboundPort
implements	FrequencyCoordinatorServicesI {

	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public FrequencyCoordinatorServicesOutboundPort(ComponentI owner) throws Exception {
		super(FrequencyCoordinatorServicesI.class, owner) ;
	}

	public FrequencyCoordinatorServicesOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, FrequencyCoordinatorServicesI.class, owner);
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	@Override
	public void increaseFrequencyOutOfGap(String procURI, int coreNo, String perfContManInboundPort) throws Exception {
		((FrequencyCoordinatorServicesI)this.connector).increaseFrequencyOutOfGap(procURI, coreNo, perfContManInboundPort);		
	}

	@Override
	public void notifyAddProc(String pcmip_URI, String procURI) throws Exception {
		((FrequencyCoordinatorServicesI)this.connector).notifyAddProc(pcmip_URI, procURI);	
	}

	@Override
	public void notifyRemoveProc(String pcmip_URI, String procURI) throws Exception {
		((FrequencyCoordinatorServicesI)this.connector).notifyRemoveProc(pcmip_URI, procURI);	
	}
}
