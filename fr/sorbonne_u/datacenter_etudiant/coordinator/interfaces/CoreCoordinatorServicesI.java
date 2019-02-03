package fr.sorbonne_u.datacenter_etudiant.coordinator.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;

/**
 * L'interface <code>CoreCoordinatorServicesI</code> définie les services offerts par
 * le composant <code>CoreCoordinator</code>.
 *
 * <p><strong>Description</strong></p>
 * Permet de préallouer un coeur d'un ordinateur
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
public interface CoreCoordinatorServicesI 
extends		OfferedI,
			RequiredI{
	
	/**
	 * réserve un coeur à une application sur un ordinateur précis
	 *
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	cpuri != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param cpuri			URI de l'ordinateur dont on veut préallouer un coeur
	 * @param pcuri			URI du contrôleur de performance 
	 * @throws Exception	<i>todo.</i>
	 */
	public boolean reserveCore(String cpuri, String pcuri) throws Exception;
	
	/**
	 * fait le choix d'utiliser le coeur réservé ou pas
	 *
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	cpuri != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param pcuri			URI du contrôleur de performance 
	 * @param choice		utiliser le coeur réservé ou pas
	 * @throws Exception	<i>todo.</i>
	 */
	public AllocatedCore makeChoice(String pcuri, boolean choice) throws Exception;
	
	/**
	 * déalloue un coeur qui a été alloué
	 *
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	cpuri != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param cpuri			URI de l'ordinateur dont on veut préallouer un coeur
	 * @throws Exception	<i>todo.</i>
	 */
	public void releaseCore(AllocatedCore ac)  throws Exception ;
	
	/**
	 * find an idle cores 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	nbCore != 0
	 * post	true			// no postcondition.
	 * </pre>
	 * 
	 * @param nbCore		number of cores requested
	 * @return a cores allocated from a computer
	 * @throws Exception
	 */
	public AllocatedCore[] findComputerAndAllocateCores(int nbCore) throws Exception;
}
