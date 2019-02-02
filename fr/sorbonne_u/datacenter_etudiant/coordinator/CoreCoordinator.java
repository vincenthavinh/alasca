package fr.sorbonne_u.datacenter_etudiant.coordinator;

import java.util.ArrayList;
import java.util.HashMap;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.CoreCoordinatorServicesI;
import fr.sorbonne_u.datacenter_etudiant.coordinator.ports.CoreCoordinatorServicesInboundPort;

/**
 * La classe <code>CoreCoordinator</code> permet de réserver un coeur avant de décider à l'utiliser
 * ou pas.
 *
 * <p><strong>Description</strong></p>
 * 
 * Le coordinateur de coeur est appelé lors des allocations de coeur. Il permet de réserver
 * d'un coeur pour une application et de laisser un temps de décision au contrôleur de performance.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * TODO: complete!
 * <pre>
 * invariant coreCoord_URI != null;
 * invariant coreCoord_services_ipURI != null;
 * invariant computers_URI != null && computers_URI.size() != 0;
 * invariant cp_computerServicesInboundPortURIs != null && cp_computerServicesInboundPortURIs.size() != 0;
 * </pre>
 * 
 * <p>Created on : February 1, 2019</p>
 * 
 * @author	<a>Chao LIN</a>
 */
public class CoreCoordinator 
extends AbstractComponent{
	protected String coreCoord_URI;
	protected CoreCoordinatorServicesInboundPort ccsip;

	/**Computer**/
	protected HashMap<String, String> computerServicesInboundPortURIs;
	protected HashMap<String, ComputerServicesOutboundPort> computerServicesOutboundPorts;
	
	/**
	 * Créer un coordinateur d'allocation de coeur
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre coreCoord_URI != null;
	 * pre coreCoord_services_ipURI != null;
	 * pre computers_URI != null && computers_URI.size() != 0;
	 * pre cp_computerServicesInboundPortURIs != null && cp_computerServicesInboundPortURIs.size() != 0;
 	 * post	true			// no postcondition.
	 * </pre>
	 * 
	 * @param coreCoord_URI
	 * @param coreCoord_services_ipURI
	 * @param computers_URI
	 * @param cp_computerServicesInboundPortURIs
	 * @throws Exception 
	 */
	public CoreCoordinator(
			String coreCoord_URI,
			String coreCoord_services_ipURI,
			ArrayList<String> computers_URI, /*Computer URI*/
			ArrayList<String> computerServicesInboundPortURIs /* computer service */) throws Exception {
		super(1,1);
		
		assert coreCoord_URI != null;
		assert coreCoord_services_ipURI != null;
		assert computers_URI != null && computers_URI.size() != 0;
		assert computerServicesInboundPortURIs != null && computerServicesInboundPortURIs.size() != 0;
		
		this.coreCoord_URI = coreCoord_URI;
		
		//initialisation des ports

		/**offered**/
		this.addOfferedInterface(CoreCoordinatorServicesI.class);
		this.ccsip = 
				new CoreCoordinatorServicesInboundPort(
						coreCoord_services_ipURI, this);
		this.addPort(ccsip);
		this.ccsip.publishPort();
		
		/**Ordinateur*/
		// n'utilise pas HashMap directement dans le constructeur en raison du DynamicCreator ne supporte pas les hashMap comme paramètre
		this.computerServicesInboundPortURIs = new HashMap<String, String>();
		for(int i=0; i<computers_URI.size(); i++) {
			this.computerServicesInboundPortURIs.put(computers_URI.get(i), computerServicesInboundPortURIs.get(i));
		}
		
		/**required**/
		//ComputerServices
		this.computerServicesOutboundPorts = new HashMap<String, ComputerServicesOutboundPort>();
		this.addRequiredInterface(ComputerServicesI.class) ;
		for(String cpuri : this.computerServicesInboundPortURIs.keySet()) {
			this.computerServicesOutboundPorts.put(cpuri, new ComputerServicesOutboundPort(this));
			this.addPort(this.computerServicesOutboundPorts.get(cpuri)) ;
			this.computerServicesOutboundPorts.get(cpuri).publishPort();
		}
	}
	
	// Component life cycle
	
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;
		try {
			for(String cpuri : this.computerServicesOutboundPorts.keySet()){
				this.doPortConnection(
						this.computerServicesOutboundPorts.get(cpuri).getPortURI(),
						this.computerServicesInboundPortURIs.get(cpuri),
						ComputerServicesConnector.class.getCanonicalName()) ;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{	
		for(String cpuri : this.computerServicesOutboundPorts.keySet()){
			this.doPortConnection(
					this.computerServicesOutboundPorts.get(cpuri).getPortURI(),
					this.computerServicesInboundPortURIs.get(cpuri),
					ComputerServicesConnector.class.getCanonicalName()) ;
		}
	}
	
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			this.ccsip.unpublishPort();
			for(String cpuri : this.computerServicesOutboundPorts.keySet()){
				this.computerServicesOutboundPorts.get(cpuri).unpublishPort() ;
			}
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}

		super.shutdown();
	}
	
	//--------------------------------------------------------------------
	// METHODS
	//--------------------------------------------------------------------
	
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.CoreCoordinatorServicesI#allocateCore(String)
	 */
	public AllocatedCore allocateCore(String cpuri) throws Exception {
		AllocatedCore[] allocatedCores = new AllocatedCore[0];
		ComputerServicesOutboundPort csop = this.computerServicesOutboundPorts.get(cpuri);
		allocatedCores = csop.allocateCores(1) ;
		if(allocatedCores.length == 1) {
			this.logMessage("CoreCoord "+this.coreCoord_URI + " | alloue un coeur.");
			return allocatedCores[0];
		}
		else {
			csop.releaseCores(allocatedCores);
		}
		return null;
	}

	/**
	 * @throws Exception 
	 * @see fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.CoreCoordinatorServicesI#releaseCore(AllocatedCore)
	 */
	public void releaseCore(AllocatedCore ac) throws Exception {
		String cpuri =  ac.processorURI.split("-")[0];
		ComputerServicesOutboundPort csop = this.computerServicesOutboundPorts.get(cpuri);
		csop.releaseCore(ac);
		this.logMessage("CoreCoord "+this.coreCoord_URI + " | déalloue un coeur.");
	}
	
	/**
	 * @throws Exception 
	 * @see fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.CoreCoordinatorServicesI#findComputerAndAllocateCores(int)
	 */
	// TODO ne pas parcourir tous les ordinateurs
	public AllocatedCore[] findComputerAndAllocateCores(int nbCore) throws Exception {
		AllocatedCore[] allocatedCores = new AllocatedCore[0];

		for(String cpURI : this.computerServicesOutboundPorts.keySet()) {
			ComputerServicesOutboundPort csop = this.computerServicesOutboundPorts.get(cpURI);
			allocatedCores = csop.allocateCores(nbCore) ;
			if(allocatedCores.length == nbCore) {
				this.logMessage("CoreCoord "+this.coreCoord_URI + " | alloue "+nbCore+" coeur.");
				return allocatedCores;
			}
			else {
				csop.releaseCores(allocatedCores);
			}
		}
		this.logMessage("CoreCoord "+this.coreCoord_URI + " | n'a pas pu alloué "+nbCore+".");
		return allocatedCores;
	}
}
