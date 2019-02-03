package fr.sorbonne_u.datacenter_etudiant.performanceController.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter_etudiant.performanceController.PerformanceController;
import fr.sorbonne_u.datacenter_etudiant.performanceController.interfaces.PerformanceControllerManagementI;

/**
 * La classe <code>PerformanceControllerManagementInboundPort</code> implémente le
 * inbound port offrant l'interface <code>PerformanceControllerManagementI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		owner instanceof PerformanceControllerManagementI
 * </pre>
 * 
 * <p>Created on : February 1, 2019</p>
 * 
 * @author	<a>Chao LIN</a>
 */
public class PerformanceControllerManagementInboundPort 
	extends		AbstractInboundPort
	implements PerformanceControllerManagementI{

	private static final long serialVersionUID = 1L;
	
	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	/**
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	owner instanceof PerformanceControllerManagementI
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param owner			owner component.
	 * @throws Exception		<i>todo.</i>
	 */
	public PerformanceControllerManagementInboundPort(ComponentI owner) throws Exception {
		super(PerformanceControllerManagementI.class, owner);
	}
	
	/**
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null and owner instanceof PerformanceControllerManagementI
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri			uri of the port.
	 * @param owner			owner component.
	 * @throws Exception		<i>todo.</i>
	 */
	public PerformanceControllerManagementInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, PerformanceControllerManagementI.class, owner);
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.performanceController.interfaces.PerformanceControllerManagementI#connectOutboundPorts()
	 */
	@Override
	public void connectOutboundPorts() throws Exception {
		this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((PerformanceController)this.getOwner()).
								connectOutboundPorts();
						return null;
					}
				}) ;
	}

	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.performanceController.interfaces.PerformanceControllerManagementI#toggleTracingLogging()
	 */
	@Override
	public void toggleTracingLogging() throws Exception {
		this.getOwner().handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((PerformanceController)this.getOwner()).
								toggleTracingLogging();
						return null;
					}
				}) ;
	}

	@Override
	public void orderIncreaseCoresFrequencyOf(String procURI) throws Exception {
		this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((PerformanceController)this.getOwner()).
							orderIncreaseCoresFrequencyOf(procURI);
						return null;
					}
				}) ;
	}
	
}
