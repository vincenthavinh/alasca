package fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * L'interface <code>ApplicationHostingI</code> définie les méthodes de demande d'une application
 * vers l'admission controlleur.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : February 2, 2019</p>
 * 
 * @author	<a>Chao LIN</a>
 */
public interface ApplicationHostingI 
extends OfferedI, 
		RequiredI{

	/**
	 * Demande l'hebergement d'une application.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	requestNotificationInboundPortURI !=null
	 * pre	nbCores !=null
	 * pre	seuil_inf <= seuil_sup
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param requestNotificationInboundPortURI		URI du inbound port de notification du générateur de requête
	 * @param nbCores								Nombre de coeurs estimé pour chaque avm attribué à cet application
	 * @param seuil_inf								Seuil inférieur du temps moyen d'exécution souhaité
	 * @param seuil_sup								Seuil supérieur du temps moyen d'exécution souhaité
	 * @return										URI du request submission inbound port du répartiteur de requête attribué
	 * @throws Exception	<i>todo.</i>
	 */
	public String askHosting(String requestNotificationInboundPortURI, int nbCores, int seuil_inf, int seuil_sup) throws Exception;

	/**
	 * Demande les connections des composants attribués à cet application
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	requestNotificationInboundPortURI !=null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param requestNotificationInboundPortURI			URI du générateur de requête (on identifie l'appartenance des composants par cet URI)
	 * @return											retourne true si la connection a bien ete faite, false sinon.
	 */
	public Boolean askHostToConnect(String requestNotificationInboundPortURI) throws Exception ;
}
