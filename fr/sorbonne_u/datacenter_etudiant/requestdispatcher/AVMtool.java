package fr.sorbonne_u.datacenter_etudiant.requestdispatcher;

import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionOutboundPort;

public class AVMtool implements Comparable<AVMtool>{

	int local_ID;
	final String rsipURI;
	RequestSubmissionOutboundPort rsop;
	long nbInstrs = 0;
	
	public AVMtool(String rsipURI) {
		this.rsipURI = rsipURI;
	}

	@Override
	public int compareTo(AVMtool o) {
		if(this.nbInstrs < o.nbInstrs) return -1;
		if(this.nbInstrs > o.nbInstrs) return 1;
		return 0;
	}
	
}
