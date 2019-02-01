package fr.sorbonne_u.datacenter_etudiant.requestdispatcher;

import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionOutboundPort;

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
	
	/** on compare deux AVMtool par leur nombre d'instruction à exécuter, pour choisir celui
	 * qui a le moins de travail en cours*/
	@Override
	public int compareTo(AVMtool o) {
		if(this.nbInstrs < o.nbInstrs) return -1;
		if(this.nbInstrs > o.nbInstrs) return 1;
		return 0;
	}
	
}
