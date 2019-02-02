package fr.sorbonne_u.datacenter_etudiant.coordinator.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.CoreCoordinatorServicesI;

/**
 * La classe <code>CoreCoordinatorServicesOutboundPort</code> implémente un outbound
 * port requierant l'interface <code>CoreCoordinatorServicesI</code>.
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
public class CoreCoordinatorServicesOutboundPort 
extends		AbstractOutboundPort
implements	CoreCoordinatorServicesI{
	
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public				CoreCoordinatorServicesOutboundPort(
		ComponentI owner
		) throws Exception
	{
		super(CoreCoordinatorServicesI.class, owner) ;
	}

	public				CoreCoordinatorServicesOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, CoreCoordinatorServicesI.class, owner);
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.CoreCoordinatorServicesI#allocateCore(String)
	 */
	@Override
	public AllocatedCore allocateCore(String cpuri) throws Exception {
		return ((CoreCoordinatorServicesI)this.connector).allocateCore(cpuri) ;
	}

	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.CoreCoordinatorServicesI#releaseCore(AllocatedCore)
	 */
	@Override
	public void releaseCore(AllocatedCore ac) throws Exception {
		((CoreCoordinatorServicesI)this.connector).releaseCore(ac) ;
	}

	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.CoreCoordinatorServicesI#findComputerAndAllocateCores(int)
	 */
	@Override
	public AllocatedCore[] findComputerAndAllocateCores(int nbCore) throws Exception {
		return ((CoreCoordinatorServicesI)this.connector).findComputerAndAllocateCores(nbCore);
	}
}
