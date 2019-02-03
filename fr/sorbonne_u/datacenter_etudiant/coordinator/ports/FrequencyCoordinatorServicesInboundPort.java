package fr.sorbonne_u.datacenter_etudiant.coordinator.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter_etudiant.coordinator.CoreCoordinator;
import fr.sorbonne_u.datacenter_etudiant.coordinator.FrequencyCoordinator;
import fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.FrequencyCoordinatorServicesI;

public class FrequencyCoordinatorServicesInboundPort 
extends		AbstractInboundPort
implements	FrequencyCoordinatorServicesI{

	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public FrequencyCoordinatorServicesInboundPort(ComponentI owner) throws Exception {
		super(FrequencyCoordinatorServicesI.class, owner) ;
		assert owner instanceof FrequencyCoordinator ;
	}

	public FrequencyCoordinatorServicesInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, FrequencyCoordinatorServicesI.class, owner);
		assert owner instanceof FrequencyCoordinator ;
	}
	
	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------
	
	@Override
	public void increaseFrequencyOutOfGap(String procURI, int coreNo, String perfContManInboundPort) throws Exception {
		this.getOwner().handleRequestSync(
			new AbstractComponent.AbstractService<Void>() {
				@Override
				public Void call() throws Exception {
					((FrequencyCoordinator)this.getOwner()).
						increaseFrequencyOutOfGap(procURI, coreNo, perfContManInboundPort);
					return null;
				}
			});
	}

	@Override
	public void notifyAddProc(String pcmip_URI, String procURI) throws Exception {
		this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((FrequencyCoordinator)this.getOwner()).
						notifyAddProc(pcmip_URI, procURI);
						return null;
					}
				});		
	}

	@Override
	public synchronized void notifyRemoveProc(String pcmip_URI, String procURI) throws Exception {
		this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((FrequencyCoordinator)this.getOwner()).
						notifyRemoveProc(pcmip_URI, procURI);
						return null;
					}
				});		
	}
}
