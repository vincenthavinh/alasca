package fr.sorbonne_u.datacenter_etudiant.performanceController.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter_etudiant.performanceController.interfaces.PerformanceControllerManagementI;

/**
 * La classe <code>PerformanceControllerManagementOutboundPort</code> implémente le
 * inbound port requis par l'interface <code>PerformanceControllerManagementI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : February 1, 2019</p>
 * 
 * @author	<a>Chao LIN</a>
 */
public class PerformanceControllerManagementOutboundPort 
	extends AbstractOutboundPort
	implements PerformanceControllerManagementI{

	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------
	public PerformanceControllerManagementOutboundPort(ComponentI owner) 
			throws Exception {
		super(PerformanceControllerManagementI.class, owner);
	}
	
	public PerformanceControllerManagementOutboundPort(String uri, ComponentI owner) 
			throws Exception {
		super(uri, PerformanceControllerManagementI.class, owner);
	}
	
	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.performanceController.interfaces.PerformanceControllerManagementI#connectOutboundPorts()
	 */
	@Override
	public void connectOutboundPorts() throws Exception {
		((PerformanceControllerManagementI)this.connector).connectOutboundPorts();
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.performanceController.interfaces.PerformanceControllerManagementI#toggleTracingLogging()
	 */
	@Override
	public void toggleTracingLogging() throws Exception {
		((PerformanceControllerManagementI)this.connector).toggleTracingLogging();
	}

	@Override
	public void orderIncreaseCoresFrequencyOf(String procURI) throws Exception {
		((PerformanceControllerManagementI)this.connector).orderIncreaseCoresFrequencyOf(procURI);
	}
	
}
