package fr.sorbonne_u.datacenter_etudiant.admissioncontroller.connectors;

import java.util.Map;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM.ApplicationVMPortTypes;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.AdmissionControllerServicesI;

/**
 * The class <code>AdmissionControllerServicesConnector</code> implements a connector for
 * ports exchanging through the interface <code>AdmissionControllerServicesI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 16 January, 2019</p>
 * 
 */
public class				AdmissionControllerServicesConnector
extends		AbstractConnector
implements	AdmissionControllerServicesI
{
	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.AdmissionControllerServicesI#allocateFreeAVM()
	 */
	@Override
	public Map<ApplicationVMPortTypes, String> allocateFreeAVM() throws Exception {
		return ((AdmissionControllerServicesI)this.offering).allocateFreeAVM();
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.AdmissionControllerServicesI#recycleFreeAVM(String)
	 */
	@Override
	public void recycleFreeAVM(String AVMuri) throws Exception {
		((AdmissionControllerServicesI)this.offering).recycleFreeAVM(AVMuri);
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.AdmissionControllerServicesI#findComputerAndAllocateCores(int)
	 */
	@Override
	public AllocatedCore[] findComputerAndAllocateCores(int nbCore) throws Exception {
		return ((AdmissionControllerServicesI)this.offering).findComputerAndAllocateCores(nbCore);
	}
}