package fr.sorbonne_u.datacenter_etudiant.admissioncontroller.ports;

import java.util.Map;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM.ApplicationVMPortTypes;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.AdmissionController;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.AdmissionControllerServicesI;

/**
 * The class <code>AdmissionControllerServicesInboundPort</code> implements an inbound
 * port offering the <code>AdmissionControllerServicesI</code> interface.
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
public class				AdmissionControllerServicesInboundPort
extends		AbstractInboundPort
implements	AdmissionControllerServicesI
{
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public				AdmissionControllerServicesInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(AdmissionControllerServicesI.class, owner) ;

		assert owner instanceof AdmissionController ;
	}

	public				AdmissionControllerServicesInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, AdmissionControllerServicesI.class, owner);

		assert owner instanceof AdmissionController ;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.AdmissionControllerServicesI#allocateFreeAVM()
	 */
	@Override
	public Map<ApplicationVMPortTypes, String> allocateFreeAVM() throws Exception {
		return this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<Map<ApplicationVMPortTypes, String>>() {
					@Override
					public Map<ApplicationVMPortTypes, String> call() throws Exception {
						return ((AdmissionController)this.getOwner()).
									allocateFreeAVM() ;
					}
				}) ;
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.AdmissionControllerServicesI#recycleFreeAVM(String)
	 */
	@Override
	public void recycleFreeAVM(String AVMuri) throws Exception {
		this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((AdmissionController)this.getOwner()).recycleFreeAVM(AVMuri);
						return null;
					}
				}) ;
	}

}
