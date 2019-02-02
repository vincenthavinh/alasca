package fr.sorbonne_u.datacenter_etudiant.requestdispatcher.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * L'interface <code>RequestDispatcherManagementI</code> définie les méthodes
 * de gestion d'un répartiteur de requête.
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
public interface RequestDispatcherManagementI 
extends OfferedI,
		RequiredI {

	/**
	 * Connecte les outboundports du RD
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
	
	/**
	 * Ajoute une avm au répartiteur de requête.
	 * On donne le requestSubmissionInboundPort de l'avm car le répartiteur identifie les avm
	 * par leur port de submission.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	reqSubURI != null 
	 * post	true			// no postcondition.
	 * </pre>
	 * 
	 * @param reqSubURI		URI du requestSubmissionInboundPort de l'avm
	 * @throws Exception
	 */
	public void addAVM(String reqSubURI) throws Exception ;
	
	/**
	 * Retire une avm du répartiteur de requête.
	 * On donne le requestSubmissionInboundPort de l'avm car le répartiteur identifie les avm
	 * par leur port de submission.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	avm_rsipURI != null 
	 * post	true			// no postcondition.
	 * </pre>
	 * 
	 * @param avm_rsipURI		URI du requestSubmissionInboundPort de l'avm
	 * @throws Exception
	 */
	public void removeAVM(String avm_rsipURI) throws Exception ;
	
	/**
	 * Renvoie la moyenne des temps d'exécution des n derniers requêtes passant par ce répartiteur.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true 			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 * 
	 * @throws Exception
	 */
	public long getAverageReqDuration() throws Exception;
}
