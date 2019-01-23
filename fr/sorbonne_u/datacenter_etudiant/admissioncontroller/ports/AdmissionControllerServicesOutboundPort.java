package fr.sorbonne_u.datacenter_etudiant.admissioncontroller.ports;

import java.util.Map;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM.ApplicationVMPortTypes;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.AdmissionControllerServicesI;

/**
 * The class <code>AdmissionControllerServicesOutboundPort</code> implements an outbound
 * port requiring the <code>AdmissionControllerServicesI</code> interface.
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
 */
public class				AdmissionControllerServicesOutboundPort
extends		AbstractOutboundPort
implements	AdmissionControllerServicesI
{
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public				AdmissionControllerServicesOutboundPort(
		ComponentI owner
		) throws Exception
	{
		super(AdmissionControllerServicesI.class, owner) ;
	}

	public				AdmissionControllerServicesOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, AdmissionControllerServicesI.class, owner);
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.AdmissionControllerServicesI#allocateFreeAVM()
	 */
	@Override
	public Map<ApplicationVMPortTypes, String> allocateFreeAVM() throws Exception {
		return ((AdmissionControllerServicesI)this.connector).allocateFreeAVM() ;
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.AdmissionControllerServicesI#recycleFreeAVM(String)
	 */
	@Override
	public void recycleFreeAVM(String AVMuri) throws Exception {
		((AdmissionControllerServicesI)this.connector).recycleFreeAVM(AVMuri);
	}

	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.AdmissionControllerServicesI#findComputerAndAllocateCores(int)
	 */
	@Override
	public AllocatedCore[] findComputerAndAllocateCores(int nbCore) throws Exception {
		return ((AdmissionControllerServicesI)this.connector).findComputerAndAllocateCores(nbCore);
	}
}