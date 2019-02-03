package fr.sorbonne_u.datacenter_etudiant.coordinator.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.CoreCoordinatorServicesI;

/**
 * La classe <code>CoreCoordinatorServicesConnector</code> implémente un connecteur pour
 * l'échange des ports à travers l'interface <code>AdmissionControllerServicesI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : January 16, 2019</p>
 * 
 * @author	<a>Chao LIN</a>
 */
public class CoreCoordinatorServicesConnector 
extends		AbstractConnector
implements	CoreCoordinatorServicesI{

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.CoreCoordinatorServicesI#reserveCore(String, String)
	 */
	@Override
	public boolean reserveCore(String cpuri, String pcuri) throws Exception{
		return ((CoreCoordinatorServicesI)this.offering).reserveCore(cpuri, pcuri);
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.CoreCoordinatorServicesI#makeChoice(String, boolean)
	 */
	@Override
	public AllocatedCore makeChoice(String pcuri, boolean choice) throws Exception {
		return ((CoreCoordinatorServicesI)this.offering).makeChoice(pcuri, choice);
	}

	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.CoreCoordinatorServicesI#releaseCore(AllocatedCore)
	 */
	@Override
	public void releaseCore(AllocatedCore ac)  throws Exception {
		((CoreCoordinatorServicesI)this.offering).releaseCore(ac);
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.CoreCoordinatorServicesI#findComputerAndAllocateCores(int)
	 */
	@Override
	public AllocatedCore[] findComputerAndAllocateCores(int nbCore)  throws Exception {
		return ((CoreCoordinatorServicesI)this.offering).findComputerAndAllocateCores(nbCore);
	}
}
