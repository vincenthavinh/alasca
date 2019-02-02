package fr.sorbonne_u.datacenter_etudiant.performanceController.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter_etudiant.performanceController.interfaces.PerformanceControllerManagementI;

/**
 * La classe <code>PerformanceControllerManagementConnector</code> implémente un
 * connecteur pour l'échange des ports à travers l'interface
 * <code>PerformanceControllerManagementI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : February 1, 2019</p>
 * 
 * @author	<a>Chao LIN</a>
 */
public class PerformanceControllerManagementConnector 
extends		AbstractConnector
implements PerformanceControllerManagementI{
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.performanceController.interfaces.PerformanceControllerManagementI#connectOutboundPorts()
	 */
	@Override
	public void connectOutboundPorts() throws Exception {
		((PerformanceControllerManagementI)this.offering).connectOutboundPorts();
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.performanceController.interfaces.PerformanceControllerManagementI#toggleTracingLogging()
	 */
	@Override
	public void toggleTracingLogging() throws Exception {
		((PerformanceControllerManagementI)this.offering).toggleTracingLogging();
	}
}
