package fr.sorbonne_u.datacenter_etudiant.coordinator;


/**
 * Pour stocker les informations du coeur qu'on a alloué à une application
 * 
 * @author Chao LIN
 *
 */
public class ReservedCoreData {
	private int processorNo;
	private int coreNo;
	private String cpURI;
	
	public ReservedCoreData(String cpURI, int processorNo, int coreNo) {
		this.cpURI = cpURI;
		this.processorNo = processorNo;
		this.coreNo = coreNo;
	}

	public int getProcessorNo() {
		return processorNo;
	}

	public void setProcessorNo(int processorNo) {
		this.processorNo = processorNo;
	}

	public int getCoreNo() {
		return coreNo;
	}

	public void setCoreNo(int coreNo) {
		this.coreNo = coreNo;
	}

	public String getCpURI() {
		return cpURI;
	}

	public void setCpURI(String cpURI) {
		this.cpURI = cpURI;
	}
	
	
}
