package fr.sorbonne_u.datacenter_etudiant.performanceController.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter_etudiant.performanceController.interfaces.PerformanceControllerManagementI;

public class PerformanceControllerManagementConnector 
extends		AbstractConnector
implements PerformanceControllerManagementI{
	@Override
	public void connectOutboundPorts() throws Exception {
		((PerformanceControllerManagementI)this.offering).connectOutboundPorts();
	}

	@Override
	public void toggleTracingLogging() throws Exception {
		((PerformanceControllerManagementI)this.offering).toggleTracingLogging();
	}
}
