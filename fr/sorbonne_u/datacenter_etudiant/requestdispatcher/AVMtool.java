package fr.sorbonne_u.datacenter_etudiant.requestdispatcher;

import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionOutboundPort;

class AVMtool {

	final String rsipURI;
	RequestSubmissionOutboundPort rsop;
	int nbInstrs = 0;
	
	AVMtool(String rsipURI) {
		this.rsipURI = rsipURI;
	}
	
}
