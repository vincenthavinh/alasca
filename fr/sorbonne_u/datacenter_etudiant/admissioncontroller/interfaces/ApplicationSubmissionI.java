package fr.sorbonne_u.datacenter_etudiant.admissioncontroller.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface ApplicationSubmissionI 
extends OfferedI, 
		RequiredI{

	public void submitApplicationAndNotify() throws Exception;
}
