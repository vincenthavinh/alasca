package fr.sorbonne_u.datacenter_etudiant.coordinator.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter_etudiant.coordinator.CoreCoordinator;
import fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.CoreCoordinatorServicesI;

/**
 * La classe <code>CoreCoordinatorServicesOutboundPort</code> implémente un inbound
 * port offrant l'interface <code>CoreCoordinatorServicesI</code>.
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
public class CoreCoordinatorServicesInboundPort 
	extends		AbstractInboundPort
	implements	CoreCoordinatorServicesI{
	
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public				CoreCoordinatorServicesInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(CoreCoordinatorServicesI.class, owner) ;

		assert owner instanceof CoreCoordinator ;
	}

	public				CoreCoordinatorServicesInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, CoreCoordinatorServicesI.class, owner);

		assert owner instanceof CoreCoordinator ;
	}
	

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.CoreCoordinatorServicesI#reserveCore(String, String)
	 */
	@Override
	public boolean reserveCore(String cpuri, String pcuri) throws Exception{
		return this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ((CoreCoordinator)this.getOwner()).
								reserveCore(cpuri, pcuri) ;
					}
				});
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.CoreCoordinatorServicesI#makeChoice(String, boolean)
	 */
	@Override
	public AllocatedCore makeChoice(String pcuri, boolean choice) throws Exception {
		return this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<AllocatedCore>() {
					@Override
					public AllocatedCore call() throws Exception {
						return ((CoreCoordinator)this.getOwner()).
								makeChoice(pcuri, choice) ;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.CoreCoordinatorServicesI#releaseCore(AllocatedCore)
	 */
	@Override
	public void releaseCore(AllocatedCore ac) throws Exception {
		this.getOwner().handleRequestSync(
			new AbstractComponent.AbstractService<Void>() {
				@Override
				public Void call() throws Exception {
					((CoreCoordinator)this.getOwner()).
								releaseCore(ac);
					return null;
				}
			});
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.CoreCoordinatorServicesI#allocateCore(int)
	 */
	@Override
	public AllocatedCore[] findComputerAndAllocateCores(int nbCore) throws Exception {
		return this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<AllocatedCore[]>() {
					@Override
					public AllocatedCore[] call() throws Exception {
						return ((CoreCoordinator)this.getOwner()).
									findComputerAndAllocateCores(nbCore) ;
					}
				});
	}
}
