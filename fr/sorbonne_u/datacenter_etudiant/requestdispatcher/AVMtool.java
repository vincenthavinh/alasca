package fr.sorbonne_u.datacenter_etudiant.requestdispatcher;

import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionOutboundPort;

public class AVMtool implements Comparable<AVMtool>{

	public int local_ID;
	public final String rsipURI;
	public RequestSubmissionOutboundPort rsop;
	public long nbInstrs = 0;
	
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
