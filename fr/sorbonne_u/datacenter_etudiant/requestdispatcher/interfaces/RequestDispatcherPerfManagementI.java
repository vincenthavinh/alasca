package fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface RequestDispatcherPerfManagementI 
extends OfferedI,
		RequiredI {

	public void	connectOutboundPorts() throws Exception ;
	
	public void toggleTracingLogging() throws Exception;
}
