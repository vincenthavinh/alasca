package fr.sorbonne_u.datacenter_etudiant.coordinator;

import java.util.ArrayList;
import java.util.HashMap;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.computers.connectors.ComputerIntrospectionConnector;
import fr.sorbonne_u.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerIntrospectionI;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerIntrospectionOutboundPort;
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
 * invariant computerServicesInboundPortURIs != null && computerServicesInboundPortURIs.size() != 0;
 * invariant computerIntrospectionInboundPortURIs != null && computerIntrospectionInboundPortURIs.size() != 0;
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
	protected HashMap<String, ReservedCoreData> pc_reservedCore;

	/**Computer**/
	protected HashMap<String, String> computerServicesInboundPortURIs;
	protected HashMap<String, ComputerServicesOutboundPort> computerServicesOutboundPorts;
	protected HashMap<String, String> computerIntrospectionInboundPortURIs;
	protected HashMap<String, ComputerIntrospectionOutboundPort> computerIntrospectionOutboundPorts;
	protected HashMap<String, boolean[][]> computerReservedCores ;
	
	/**
	 * Créer un coordinateur d'allocation de coeur
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre coreCoord_URI != null;
	 * pre coreCoord_services_ipURI != null;
	 * pre computers_URI != null && computers_URI.size() != 0;
	 * pre computerServicesInboundPortURIs != null && computerServicesInboundPortURIs.size() != 0;
 	 * pre computerIntrospectionInboundPortURIs != null && computerIntrospectionInboundPortURIs.size() != 0;
 	 * post	true			// no postcondition.
	 * </pre>
	 * 
	 * @param coreCoord_URI								URI du coordinateur de coeur
	 * @param coreCoord_services_ipURI					URI du port de services du coordinateur de coeur
	 * @param computers_URI								URI de tous les ordinateurs
	 * @param computerServicesInboundPortURIs			URI des ports de services des ordinateurs
	 * @param computerIntrospectionInboundPortURIs		URI des prots de introspection des ordinateurs
	 * @throws Exception 
	 */
	public CoreCoordinator(
			String coreCoord_URI,
			String coreCoord_services_ipURI,
			ArrayList<String> computers_URI, /*Computer URI*/
			ArrayList<String> computerServicesInboundPortURIs, /* computer service */
			ArrayList<String> computerIntrospectionInboundPortURIs) throws Exception {
		super(1,1);
		
		assert coreCoord_URI != null;
		assert coreCoord_services_ipURI != null;
		assert computers_URI != null && computers_URI.size() != 0;
		assert computerServicesInboundPortURIs != null && computerServicesInboundPortURIs.size() != 0;
		
		this.coreCoord_URI = coreCoord_URI;
		this.computerReservedCores = new HashMap<String, boolean[][]>();
		this.pc_reservedCore = new HashMap<String, ReservedCoreData>();
		
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
		this.computerIntrospectionInboundPortURIs = new HashMap<String, String>();
		/**required**/
		//ComputerServices
		this.computerServicesOutboundPorts = new HashMap<String, ComputerServicesOutboundPort>();
		this.addRequiredInterface(ComputerServicesI.class) ;
		
		this.computerIntrospectionOutboundPorts = new HashMap<String, ComputerIntrospectionOutboundPort>();
		this.addRequiredInterface(ComputerIntrospectionI.class) ;
		
		for(int i=0; i<computers_URI.size(); i++) {
			this.computerServicesInboundPortURIs.put(computers_URI.get(i), computerServicesInboundPortURIs.get(i));
			this.computerIntrospectionInboundPortURIs.put(computers_URI.get(i), computerIntrospectionInboundPortURIs.get(i));
			
			this.computerServicesOutboundPorts.put(computers_URI.get(i), new ComputerServicesOutboundPort(this));
			this.addPort(this.computerServicesOutboundPorts.get(computers_URI.get(i))) ;
			this.computerServicesOutboundPorts.get(computers_URI.get(i)).publishPort();
			
			this.computerIntrospectionOutboundPorts.put(computers_URI.get(i), new ComputerIntrospectionOutboundPort(this));
			this.addPort(this.computerIntrospectionOutboundPorts.get(computers_URI.get(i))) ;
			this.computerIntrospectionOutboundPorts.get(computers_URI.get(i)).publishPort();
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
				this.doPortConnection(
						this.computerIntrospectionOutboundPorts.get(cpuri).getPortURI(),
						this.computerIntrospectionInboundPortURIs.get(cpuri),
						ComputerIntrospectionConnector.class.getCanonicalName()) ;
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
			this.doPortConnection(
					this.computerIntrospectionOutboundPorts.get(cpuri).getPortURI(),
					this.computerIntrospectionInboundPortURIs.get(cpuri),
					ComputerIntrospectionConnector.class.getCanonicalName()) ;
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
				this.computerIntrospectionOutboundPorts.get(cpuri).unpublishPort() ;
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
	 * @see fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.CoreCoordinatorServicesI#reserveCore(String, String)
	 */
	public boolean reserveCore(String cpuri, String pcuri) throws Exception{
		if(this.computerReservedCores.isEmpty()) {
			this.updateData();
		}
		boolean[][] reservedCores = this.computerReservedCores.get(cpuri);
		for(int i=0; i<reservedCores.length; i++) {
			for(int j=0; j<reservedCores[i].length; j++) {
				if(reservedCores[i][j] == false) {
					reservedCores[i][j] = true;
					this.pc_reservedCore.put(pcuri, new ReservedCoreData(cpuri, i, j));
					this.logMessage("CoreCoord "+this.coreCoord_URI + " | reserve le coeur"+"("+i+","+j+")"+" à "+pcuri+" de l'ordinateur "+cpuri+".");
					return true;
				}
			}
		}
		this.logMessage("CoreCoord "+this.coreCoord_URI + " | n'a pas pu reserver de coeur à "+pcuri+" sur l'ordinateur "+cpuri+".");
		return false;
	}
	
	/**
	 * @see fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.CoreCoordinatorServicesI#makeChoice(String, boolean)
	 */
	public AllocatedCore makeChoice(String pcuri, boolean choice) throws Exception{
		if(this.computerReservedCores.isEmpty()) {
			this.updateData();
		}
		ReservedCoreData core = this.pc_reservedCore.remove(pcuri);
		if(core == null) {
			this.logMessage("CoreCoord "+this.coreCoord_URI + " | n'a pas réservé de coeur à "+pcuri+".");
			return null;
		}
		if(choice) {
			this.logMessage("CoreCoord "+this.coreCoord_URI + " | "+pcuri+" utilise le coeur réservé sur "+core.getCpURI()+".");
			ComputerServicesOutboundPort csop = this.computerServicesOutboundPorts.get(core.getCpURI());
			return csop.allocateCore(core.getProcessorNo(), core.getCoreNo()) ;
		}
		else {
			this.logMessage("CoreCoord "+this.coreCoord_URI + " | "+pcuri+" abandonne le coeur réservé sur "+core.getCpURI()+".");
			this.computerReservedCores.get(core.getCpURI())[core.getProcessorNo()][core.getCoreNo()] = false;
			return null;
		}
	}
	
	/**
	 * @throws Exception 
	 * @see fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.CoreCoordinatorServicesI#releaseCore(AllocatedCore)
	 */
	public void releaseCore(AllocatedCore ac) throws Exception {
		if(this.computerReservedCores.isEmpty()) {
			this.updateData();
		}
		
		String cpuri =  ac.processorURI.split("-")[0];
		ComputerServicesOutboundPort csop = this.computerServicesOutboundPorts.get(cpuri);
		csop.releaseCore(ac);
		this.computerReservedCores.get(cpuri)[ac.processorNo][ac.coreNo] = false;
		this.logMessage("CoreCoord "+this.coreCoord_URI + " | déalloue un coeur.");
	}
	
	/**
	 * @throws Exception 
	 * @see fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces.CoreCoordinatorServicesI#findComputerAndAllocateCores(int)
	 */
	// TODO ne pas parcourir tous les ordinateurs
	public AllocatedCore[] findComputerAndAllocateCores(int nbCore) throws Exception {
		if(this.computerReservedCores.isEmpty()) {
			this.updateData();
		}
		ArrayList<ReservedCoreData> list_reservedCores = new ArrayList<ReservedCoreData>();

		for(String cpURI : this.computerReservedCores.keySet()) {
			boolean[][] reservedCores = this.computerReservedCores.get(cpURI);
			int nb_cores_reserved = 0;
			for(int i=0; i<reservedCores.length; i++) {
				for(int j=0; j<reservedCores[i].length; j++) {
					if(reservedCores[i][j] == false) {
						reservedCores[i][j] = true;
						list_reservedCores.add(new ReservedCoreData(cpURI, i, j));
						nb_cores_reserved++;
						if(nb_cores_reserved == nbCore) {
							break;
						}
					}
				}
				if(nb_cores_reserved == nbCore) {
					break;
				}
			}
			if(nb_cores_reserved == nbCore) {
				break;
			}
			else {
				list_reservedCores.clear();
			}
		}
		
		if(list_reservedCores.size() != nbCore) {
			this.logMessage("CoreCoord "+this.coreCoord_URI + " | n'a pas "+nbCore+" coeurs libres.");
			return new AllocatedCore[0];
		}

		AllocatedCore[] allocatedCores = new AllocatedCore[nbCore];
		
		for(int i=0; i<nbCore; i++) {
			ReservedCoreData rc = list_reservedCores.get(i);
			ComputerServicesOutboundPort csop = this.computerServicesOutboundPorts.get(rc.getCpURI());
			allocatedCores[i] = csop.allocateCore(rc.getProcessorNo(), rc.getCoreNo()) ;
		}
		this.logMessage("CoreCoord "+this.coreCoord_URI + " | alloue "+nbCore+" coeurs.");
		return allocatedCores;
	}
	
	
	/**
	 * Met à jour les données des coeurs des ordinateurs 
	 * @throws Exception 
	 */
	private void updateData() throws Exception {
		for(String cpuri : this.computerIntrospectionOutboundPorts.keySet()) {
			this.computerReservedCores.put(cpuri, this.computerIntrospectionOutboundPorts.get(cpuri).getDynamicState().getCurrentCoreReservations());
		}
	}
}
