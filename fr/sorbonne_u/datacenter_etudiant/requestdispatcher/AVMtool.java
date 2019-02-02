package fr.sorbonne_u.datacenter_etudiant.requestdispatcher;

import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionOutboundPort;

/**
 * La classe <code>AVMtool</code> contient les données d'une avm et permet de comparer
 * les avms entre eux par le nombre d'instructions qu'il sont en train de traiter.
 *
 * <p><strong>Description</strong></p>
 * 
 * Aide à la selection de l'avm qui a le moins d'instructions à traiter
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * TODO: complete!
 * 
 * <pre>
 * invariant		rsipURI != null
 * </pre>
 * 
 * <p>Created on : February 1, 2019</p>
 * 
 * @author	<a>Chao LIN</a>
 */
public class AVMtool implements Comparable<AVMtool>{
	
	/** un id local au répartiteur pour l'affichage*/
	int local_ID;
	
	/** le request submission inbound port URI de l'avm*/
	final String rsipURI;
	
	/** le request submission outbound port connecté à cet avm*/
	RequestSubmissionOutboundPort rsop;
	
	/** le nombre d'instruction que cet avm est en train de traiter*/
	long nbInstrs = 0;
	
	public AVMtool(String rsipURI) {
		this.rsipURI = rsipURI;
	}
	
	/** on compare deux AVMtool par leur nombre d'instruction à exécuter, pour selectionner celui
	 * qui a le moins de travail en cours*/
	@Override
	public int compareTo(AVMtool o) {
		if(this.nbInstrs < o.nbInstrs) return -1;
		if(this.nbInstrs > o.nbInstrs) return 1;
		return 0;
	}
	
}
