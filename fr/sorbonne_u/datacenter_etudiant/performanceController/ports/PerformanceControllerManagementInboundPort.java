package fr.sorbonne_u.datacenter_etudiant.performanceController.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter_etudiant.performanceController.PerformanceController;
import fr.sorbonne_u.datacenter_etudiant.performanceController.interfaces.PerformanceControllerManagementI;

public class PerformanceControllerManagementInboundPort 
extends		AbstractInboundPort
implements PerformanceControllerManagementI{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public PerformanceControllerManagementInboundPort(ComponentI owner) throws Exception {
		super(PerformanceControllerManagementI.class, owner);
	}
	
	public PerformanceControllerManagementInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, PerformanceControllerManagementI.class, owner);
	}
	
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
	public void checkPerformance() throws Exception {
		this.getOwner().handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((PerformanceController)this.getOwner()).
							checkPerformance();
						return null;
					}
				}) ;
	}
}
