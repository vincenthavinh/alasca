package fr.sorbonne_u.datacenter_etudiant.admissioncontroller.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces.ApplicationSubmissionI;

public class ApplicationSubmissionConnector 
extends AbstractConnector 
implements ApplicationSubmissionI {

	@Override
	public void submitApplicationAndNotify() throws Exception {
		((ApplicationSubmissionI)this.offering).submitApplicationAndNotify() ;
	}

}
