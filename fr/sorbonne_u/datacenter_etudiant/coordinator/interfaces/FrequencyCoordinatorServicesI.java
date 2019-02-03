package fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface FrequencyCoordinatorServicesI 
extends OfferedI,
		RequiredI {

	public void increaseFrequencyOutOfGap(String procURI, int coreNo, String perfContManInboundPort) throws Exception;

	public void notifyAddProc(String pcmip_URI, String procURI) throws Exception;
	
	public void notifyRemoveProc(String pcmip_URI, String procURI) throws Exception;

}
