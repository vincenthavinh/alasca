package fr.sorbonne_u.datacenter_etudiant.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.processors.Processor;
import fr.sorbonne_u.datacenter.hardware.tests.ComputerMonitor;
import fr.sorbonne_u.datacenter_etudiant.admissioncontroller.AdmissionController;
import fr.sorbonne_u.datacenter_etudiant.clientapplication.ClientApplication;
import fr.sorbonne_u.datacenter_etudiant.coordinator.CoreCoordinator;
/**
 * Scénario avec intervention du performance contrôleur (varie les fréquences, les allocations
 * de coeurs et allocation d'avm libre)
 * Il faut décommenter le Thread.sleep(4000) dans le PerformanceController au niveau du checkPerformance
 * pour bien voir le fonctionnement du coordinateur de coeur.
 * 
 * @author Chao LIN
 *
 */
public class TestAdmissionControllerWithOneComputer extends AbstractCVM {

	/** static URIs **/
	//computer
	public static final String	cp0_ComputerServicesInboundPortURI = "cs0-ibp" ;
	public static final String	cp0_ComputerIntrospectionInboundPortURI = "ci0-ibp" ;
	public static final String	cp0_ComputerStaticStateDataInboundPortURI = "css0-dip" ;
	public static final String	cp0_ComputerDynamicStateDataInboundPortURI = "cds0-dip" ;

	//admission controller
	public static final String ac_ApplicationSubmissionInboundPortURI = "ac-asip" ;
	public static final String ac_AdmissionControllerServicesInboundPortURI = "acs-ibp" ;
	
	//client application
	public static final String	ca0_ApplicationNotificationInboundPortURI = "ca0-anip" ;
	public static final String	ca1_ApplicationNotificationInboundPortURI = "ca1-anip" ;
	
	//dynamic component creator 
	public static final String dcc_DynamicComponentCreationInboundPortURI = AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX ;
	
	//core coordinator
	public static final String cc_CoreCoordinatorServicesInboundPortURI = "ccs-ibp";
	
	/** static components **/
	protected ComputerMonitor cm0 ;
	protected AdmissionController ac ;
	protected CoreCoordinator cc;
	protected ClientApplication ca0;
	protected ClientApplication ca1;
	
	public TestAdmissionControllerWithOneComputer() throws Exception {
		super();
	}
	
	@Override
	public void			deploy() throws Exception
	{
		Processor.DEBUG = true ;

		// --------------------------------------------------------------------
		// Computer
		// --------------------------------------------------------------------
		String computer0URI = "computer0" ;
		int cp0_numberOfProcessors = 4 ;
		int cp0_numberOfCores = 2 ;
		Set<Integer> admissibleFrequencies = new HashSet<Integer>() ;
		admissibleFrequencies.add(1500) ;	// Cores can run at 1,5 GHz
		admissibleFrequencies.add(3000) ;	// and at 3 GHz
		admissibleFrequencies.add(4500) ;	// and at 4,5 GHz
		Map<Integer,Integer> processingPower = new HashMap<Integer,Integer>() ;
		processingPower.put(1500, 1500000) ;	// 1,5 GHz executes 1,5 Mips
		processingPower.put(3000, 3000000) ;	// 3 GHz executes 3 Mips
		processingPower.put(4500, 4500000) ;	// 3 GHz executes 4,5 Mips
		Computer c0 = new Computer(
							computer0URI,
							admissibleFrequencies,
							processingPower,  
							1500,		// Test scenario 1, frequency = 1,5 GHz
							// 3000,	// Test scenario 2, frequency = 3 GHz
							1500,		// max frequency gap within a processor
							cp0_numberOfProcessors,
							cp0_numberOfCores,
							cp0_ComputerServicesInboundPortURI,
							cp0_ComputerIntrospectionInboundPortURI,
							cp0_ComputerStaticStateDataInboundPortURI,
							cp0_ComputerDynamicStateDataInboundPortURI) ;
		this.addDeployedComponent(c0) ;
		c0.toggleTracing() ;
		c0.toggleLogging() ;
		// --------------------------------------------------------------------
		// Create the computer monitor component and connect its to ports
		// with the computer component.
		// --------------------------------------------------------------------
		this.cm0 = new ComputerMonitor(computer0URI,
									 true,
									 cp0_ComputerStaticStateDataInboundPortURI,
									 cp0_ComputerDynamicStateDataInboundPortURI) ;
		this.addDeployedComponent(this.cm0) ;
		// --------------------------------------------------------------------
		ArrayList<String> cpURI = new ArrayList<String>();
		cpURI.add(computer0URI);
		ArrayList<String> cp_services_URI = new ArrayList<String>();
		cp_services_URI.add(cp0_ComputerServicesInboundPortURI);
		ArrayList<String> cp_intro_URI = new ArrayList<String>();
		cp_intro_URI.add(cp0_ComputerIntrospectionInboundPortURI);
		// --------------------------------------------------------------------
		// Create the Admission Controller component.
		// Il faut lui passer le(s) ordinateur(s) existant(s).
		// --------------------------------------------------------------------
		String cc_URI = "cc0";
		this.cc = new CoreCoordinator(
				cc_URI,
				cc_CoreCoordinatorServicesInboundPortURI,
				cpURI,
				cp_services_URI,
				cp_intro_URI);
		this.addDeployedComponent(this.cc) ;
		this.cc.toggleTracing() ;
		this.cc.toggleLogging() ;
		// --------------------------------------------------------------------
		// Create the Admission Controller component.
		// Il faut lui passer le(s) ordinateur(s) existant(s).
		// --------------------------------------------------------------------
		String ac_URI = "ac0";
		this.ac = new AdmissionController(
				ac_URI, 
				ac_ApplicationSubmissionInboundPortURI, 
				dcc_DynamicComponentCreationInboundPortURI,
				ac_AdmissionControllerServicesInboundPortURI,
				cc_CoreCoordinatorServicesInboundPortURI);
		this.addDeployedComponent(this.ac) ;
		this.ac.toggleTracing() ;
		this.ac.toggleLogging() ;
		
		// --------------------------------------------------------------------
		
		// Create the 2 Client Application components.
		// Il faut lui passer l'admission controller pour communiquer avec
		// --------------------------------------------------------------------
		String ca0_URI = "ca0";
		this.ca0 = new ClientApplication(
				ca0_URI, 
				ca0_ApplicationNotificationInboundPortURI, 
				ac_ApplicationSubmissionInboundPortURI, 
				1,
				"rg-"+ca0_URI,
				500.0,
				6000000000L,
				2000, // performance controller seuil inf
				3000 // performance controller seuil inf 
		);
		this.addDeployedComponent(this.ca0);
		this.ca0.toggleTracing() ;
		this.ca0.toggleLogging() ;
		
		
		String ca1_URI = "ca1";
		this.ca1 = new ClientApplication(
				ca1_URI, 
				ca1_ApplicationNotificationInboundPortURI, 
				ac_ApplicationSubmissionInboundPortURI, 
				1,
				"rg-"+ca1_URI, 
				500.0, 
				6000000000L,
				1000, // performance controller seuil inf
				24000 // performance controller seuil inf 
		);
		this.addDeployedComponent(this.ca1);
		this.ca1.toggleTracing() ;
		this.ca1.toggleLogging() ;
		
		// --------------------------------------------------------------------

		// complete the deployment at the component virtual machine level.
		super.deploy();
	}
	
	
	/**
	 * execute the test application.
	 * 
	 * @param args	command line arguments, disregarded here.
	 */
	public static void	main(String[] args)
	{
		// Uncomment next line to execute components in debug mode.
		//AbstractCVM.toggleDebugMode() ;
		try {
			final TestAdmissionControllerWithOneComputer tac = new TestAdmissionControllerWithOneComputer();
			tac.startStandardLifeCycle(100000L) ;
			// Augment the time if you want to examine the traces after
			// the execution of the program.
			Thread.sleep(100000000L) ;
			// Exit from Java (closes all trace windows...).
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
	
}
