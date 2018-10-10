package fr.sorbonne_u.datacenter_etudiant;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.processors.Processor;
import fr.sorbonne_u.datacenter.hardware.tests.ComputerMonitor;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;
import fr.sorbonne_u.datacenterclient.requestgenerator.RequestGenerator;
import fr.sorbonne_u.datacenterclient.tests.Integrator;


public class				TestRequestDispatcher
extends		AbstractCVM
{
	// ------------------------------------------------------------------------
	// Constants and instance variables
	// ------------------------------------------------------------------------

	// Predefined URI of the different ports visible at the component assembly
	// level.
	public static final String	ComputerServicesInboundPortURI = "cs-ibp" ;
	public static final String	ComputerStaticStateDataInboundPortURI = "css-dip" ;
	public static final String	ComputerDynamicStateDataInboundPortURI = "cds-dip" ;
	public static final String	ApplicationVMManagementInboundPortURI = "avm-ibp" ;
	public static final String	RequestSubmissionInboundPortURI = "rsibp" ;
	public static final String	RequestNotificationInboundPortURI = "rnibp" ;
	public static final String	RequestGeneratorManagementInboundPortURI = "rgmip" ;

	/** 	Computer monitor component.										*/
	protected ComputerMonitor						cm ;
	/** 	Application virtual machine component.							*/
	protected ApplicationVM							vm ;
	/** 	Request generator component.										*/
	protected RequestGenerator						rg ;
	/** Integrator component.											*/
	protected Integrator								integ ;
	
	/**		Request dispatcher component.								*/
	protected RequestDispatcher 					rd ;
	

	// ------------------------------------------------------------------------
	// Component virtual machine constructors
	// ------------------------------------------------------------------------

	public				TestRequestDispatcher()
	throws Exception
	{
		super();
	}

	// ------------------------------------------------------------------------
	// Component virtual machine methods
	// ------------------------------------------------------------------------

	@Override
	public void			deploy() throws Exception
	{
		Processor.DEBUG = true ;

		// --------------------------------------------------------------------
		// Create and deploy a computer component with its 2 processors and
		// each with 2 cores.
		// --------------------------------------------------------------------
		String computerURI = "computer0" ;
		int numberOfProcessors = 2 ;
		int numberOfCores = 2 ;
		Set<Integer> admissibleFrequencies = new HashSet<Integer>() ;
		admissibleFrequencies.add(1500) ;	// Cores can run at 1,5 GHz
		admissibleFrequencies.add(3000) ;	// and at 3 GHz
		Map<Integer,Integer> processingPower = new HashMap<Integer,Integer>() ;
		processingPower.put(1500, 1500000) ;	// 1,5 GHz executes 1,5 Mips
		processingPower.put(3000, 3000000) ;	// 3 GHz executes 3 Mips
		Computer c = new Computer(
							computerURI,
							admissibleFrequencies,
							processingPower,  
							1500,		// Test scenario 1, frequency = 1,5 GHz
							// 3000,	// Test scenario 2, frequency = 3 GHz
							1500,		// max frequency gap within a processor
							numberOfProcessors,
							numberOfCores,
							ComputerServicesInboundPortURI,
							ComputerStaticStateDataInboundPortURI,
							ComputerDynamicStateDataInboundPortURI) ;
		this.addDeployedComponent(c) ;
		c.toggleLogging() ;
		c.toggleTracing() ;
		// --------------------------------------------------------------------

		
		
		// --------------------------------------------------------------------
		// Create the computer monitor component and connect its to ports
		// with the computer component.
		// --------------------------------------------------------------------
		this.cm = new ComputerMonitor(computerURI,
									 true,
									 ComputerStaticStateDataInboundPortURI,
									 ComputerDynamicStateDataInboundPortURI) ;
		this.addDeployedComponent(this.cm) ;
		// --------------------------------------------------------------------

		
		
		// --------------------------------------------------------------------
		// Create an Application VM component
		// --------------------------------------------------------------------
		this.vm = new ApplicationVM("vm0",	// application vm component URI
								    ApplicationVMManagementInboundPortURI,
								    RequestSubmissionInboundPortURI,
								    RequestNotificationInboundPortURI) ;
		this.addDeployedComponent(this.vm) ;
		// Toggle on tracing and logging in the application virtual machine to
		// follow the execution of individual requests.
		this.vm.toggleTracing() ;
		this.vm.toggleLogging() ;
		// --------------------------------------------------------------------

		
		
		// --------------------------------------------------------------------
		// Creating the request dispatcher component.
		// --------------------------------------------------------------------
		this.rd = new RequestDispatcher(
					"rd",
					RequestSubmissionInboundPortURI,
					RequestNotificationInboundPortURI);
		
		
		// --------------------------------------------------------------------
		// Creating the request generator component.
		// --------------------------------------------------------------------
		this.rg = new RequestGenerator(
					"rg",			// generator component URI
					500.0,			// mean time between two requests
					6000000000L,	// mean number of instructions in requests
					RequestGeneratorManagementInboundPortURI,
					RequestSubmissionInboundPortURI,
					RequestNotificationInboundPortURI) ;
		this.addDeployedComponent(rg) ;

		// Toggle on tracing and logging in the request generator to
		// follow the submission and end of execution notification of
		// individual requests.
		this.rg.toggleTracing() ;
		this.rg.toggleLogging() ;
		// --------------------------------------------------------------------

		// --------------------------------------------------------------------
		// Creating the integrator component.
		// --------------------------------------------------------------------
		this.integ = new Integrator(
							ComputerServicesInboundPortURI,
							ApplicationVMManagementInboundPortURI,
							RequestGeneratorManagementInboundPortURI) ;
		this.addDeployedComponent(this.integ) ;
		// --------------------------------------------------------------------

		// complete the deployment at the component virtual machine level.
		super.deploy();
	}

	// ------------------------------------------------------------------------
	// Test scenarios and main execution.
	// ------------------------------------------------------------------------

	/**
	 * execute the test application.
	 * 
	 * @param args	command line arguments, disregarded here.
	 */
	public static void	main(String[] args)
	{
		// Uncomment next line to execute components in debug mode.
		// AbstractCVM.toggleDebugMode() ;
		try {
			final TestRequestDispatcher trg = new TestRequestDispatcher() ;
			trg.startStandardLifeCycle(10000L) ;
			// Augment the time if you want to examine the traces after
			// the exeuction of the program.
			Thread.sleep(10000L) ;
			// Exit from Java (closes all trace windows...).
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
