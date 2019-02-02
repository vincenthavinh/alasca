package fr.sorbonne_u.datacenter_etudiant.performanceController.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * L'interface <code>PerformanceControllerManagementI</code> définie les méthodes
 * de gestion d'un contrôleur de performance.
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
public interface PerformanceControllerManagementI 
	extends OfferedI,
	RequiredI {

	/**
	 * Connecte les outboundports du contrôleur de performance
	 * On utilise cette méthode pour éviter de faire la connection aux inbound ports qui 
	 * n'existe pas en créant le répartiteur dynamiquement.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition
	 * post	true			// no postcondition.
	 * </pre>
	 * 
	 * @throws Exception
	 */
	public void	connectOutboundPorts() throws Exception ;
	
	/**
	 * Pour ouvrir la fenêtre contenant les traces d'exécution plus rapidement
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 * 
	 * @throws Exception
	 */
	public void toggleTracingLogging() throws Exception;
}
