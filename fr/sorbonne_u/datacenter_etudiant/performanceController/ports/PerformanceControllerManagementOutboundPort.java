package fr.sorbonne_u.datacenter_etudiant.performanceController.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter_etudiant.performanceController.interfaces.PerformanceControllerManagementI;

public class PerformanceControllerManagementOutboundPort 
extends AbstractOutboundPort
implements PerformanceControllerManagementI{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PerformanceControllerManagementOutboundPort(ComponentI owner) 
			throws Exception {
		super(PerformanceControllerManagementI.class, owner);
	}
	
	public PerformanceControllerManagementOutboundPort(String uri, ComponentI owner) 
			throws Exception {
		super(uri, PerformanceControllerManagementI.class, owner);
	}
	
	@Override
	public void connectOutboundPorts() throws Exception {
		((PerformanceControllerManagementI)this.connector).connectOutboundPorts();
	}
	
	@Override
	public void toggleTracingLogging() throws Exception {
		((PerformanceControllerManagementI)this.connector).toggleTracingLogging();
	}
	
	@Override
	public void checkPerformance() throws Exception {
		((PerformanceControllerManagementI)this.connector).checkPerformance();
	}
}
