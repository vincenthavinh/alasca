package fr.sorbonne_u.datacenter_etudiant.tests;

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
/**
 * Scénario avec intervention du performance contrôleur
 * 
 * @author Chao LIN
 *
 */
public class TestAdmissionController extends AbstractCVM {

	/** static URIs **/
	//computer
	public static final String	cp0_ComputerServicesInboundPortURI = "cs0-ibp" ;
	public static final String	cp0_ComputerStaticStateDataInboundPortURI = "css0-dip" ;
	public static final String	cp0_ComputerDynamicStateDataInboundPortURI = "cds0-dip" ;
	
	public static final String	cp1_ComputerServicesInboundPortURI = "cs1-ibp" ;
	public static final String	cp1_ComputerStaticStateDataInboundPortURI = "css1-dip" ;
	public static final String	cp1_ComputerDynamicStateDataInboundPortURI = "cds1-dip" ;

	//admission controller
	public static final String ac_ApplicationSubmissionInboundPortURI = "ac-asip" ;
	public static final String ac_AdmissionControllerServicesInboundPortURI = "ac-ibp" ;
	
	//client application
	public static final String	ca0_ApplicationNotificationInboundPortURI = "ca0-anip" ;
	public static final String	ca1_ApplicationNotificationInboundPortURI = "ca1-anip" ;
	
	//dynamic component creator 
	public static final String dcc_DynamicComponentCreationInboundPortURI = AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX ;
	
	/** static components **/
	protected ComputerMonitor cm0 ;
	protected ComputerMonitor cm1 ;
	protected AdmissionController ac ;
	protected ClientApplication ca0;
	protected ClientApplication ca1;

	
	
	public TestAdmissionController() throws Exception {
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
							cp0_ComputerStaticStateDataInboundPortURI,
							cp0_ComputerDynamicStateDataInboundPortURI) ;
		this.addDeployedComponent(c0) ;
		c0.toggleTracing() ;
		c0.toggleLogging() ;
		// --------------------------------------------------------------------
		
		// --------------------------------------------------------------------
		// Computer
		// --------------------------------------------------------------------
		String computer1URI = "computer1" ;
		int cp1_numberOfProcessors = 4 ;
		int cp1_numberOfCores = 2 ;
		Computer c1 = new Computer(
							computer1URI,
							admissibleFrequencies,
							processingPower,  
							1500,		// Test scenario 1, frequency = 1,5 GHz
							// 3000,	// Test scenario 2, frequency = 3 GHz
							1500,		// max frequency gap within a processor
							cp1_numberOfProcessors,
							cp1_numberOfCores,
							cp1_ComputerServicesInboundPortURI,
							cp1_ComputerStaticStateDataInboundPortURI,
							cp1_ComputerDynamicStateDataInboundPortURI) ;
		this.addDeployedComponent(c1) ;
		c1.toggleTracing() ;
		c1.toggleLogging() ;
		// --------------------------------------------------------------------
		
		
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
		this.cm1 = new ComputerMonitor(computer1URI,
				 true,
				 cp1_ComputerStaticStateDataInboundPortURI,
				 cp1_ComputerDynamicStateDataInboundPortURI) ;
		this.addDeployedComponent(this.cm1) ;
	
		
		// --------------------------------------------------------------------
		// Create the Admission Controller component.
		// Il faut lui passer le(s) ordinateur(s) existant(s).
		// --------------------------------------------------------------------
		HashMap<String, String> csipURIs = new HashMap<String, String>();
		csipURIs.put(computer0URI, cp0_ComputerServicesInboundPortURI);
		csipURIs.put(computer1URI, cp1_ComputerServicesInboundPortURI);
		
		String ac_URI = "ac0";
		this.ac = new AdmissionController(
				ac_URI, 
				ac_ApplicationSubmissionInboundPortURI, 
				csipURIs,
				dcc_DynamicComponentCreationInboundPortURI,
				ac_AdmissionControllerServicesInboundPortURI);
		this.addDeployedComponent(this.ac);
		this.ac.toggleTracing() ;
		this.ac.toggleLogging() ;
		// --------------------------------------------------------------------
		
		// --------------------------------------------------------------------
		// Create the 2 Client Application components.
		// Il faut lui passer l'admission controller pour communiquer avec
		// --------------------------------------------------------------------
		String ca0_URI = "ca0";
		this.ca0 = new ClientApplication(
				ca0_URI, 
				ca0_ApplicationNotificationInboundPortURI, 
				ac_ApplicationSubmissionInboundPortURI, 
				2,
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
				2,
				"rg-"+ca1_URI, 
				500.0, 
				6000000000L,
				10000, // performance controller seuil inf
				12000 // performance controller seuil inf 
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
			final TestAdmissionController tac = new TestAdmissionController();
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
