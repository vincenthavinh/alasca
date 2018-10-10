package fr.sorbonne_u.datacenter.hardware.tests;

//Copyright Jacques Malenfant, Sorbonne Universite.
//
//Jacques.Malenfant@lip6.fr
//
//This software is a computer program whose purpose is to provide a
//basic component programming model to program with components
//distributed applications in the Java programming language.
//
//This software is governed by the CeCILL-C license under French law and
//abiding by the rules of distribution of free software.  You can use,
//modify and/ or redistribute the software under the terms of the
//CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
//URL "http://www.cecill.info".
//
//As a counterpart to the access to the source code and  rights to copy,
//modify and redistribute granted by the license, users are provided only
//with a limited warranty  and the software's author,  the holder of the
//economic rights,  and the successive licensors  have only  limited
//liability. 
//
//In this respect, the user's attention is drawn to the risks associated
//with loading,  using,  modifying and/or developing or reproducing the
//software by the user in light of its specific status of free software,
//that may mean  that it is complicated to manipulate,  and  that  also
//therefore means  that it is reserved for developers  and  experienced
//professionals having in-depth computer knowledge. Users are therefore
//encouraged to load and test the software's suitability as regards their
//requirements in conditions enabling the security of their systems and/or 
//data to be ensured and,  more generally, to use and operate it in the 
//same conditions as regards security. 
//
//The fact that you are presently reading this means that you have had
//knowledge of the CeCILL-C license and that you accept its terms.

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.connectors.DataConnector;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.datacenter.connectors.ControlledDataConnector;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerStateDataConsumerI;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerStaticStateI;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerStaticStateDataOutboundPort;
import fr.sorbonne_u.datacenter.interfaces.ControlledDataRequiredI;

/**
 * The class <code>ComputerMonitor</code> is a component used in the test to
 * act as a receiver for state data notifications coming from a computer.
 *
 * <p><strong>Description</strong></p>
 * 
 * The component class simply implements the necessary methods to process the
 * notifications without paying attention to do that in a really safe component
 * programming way. More or less quick and dirty...
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : April 24, 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class				ComputerMonitor
extends		AbstractComponent
implements	ComputerStateDataConsumerI
{
	// -------------------------------------------------------------------------
	// Constants and instance variables
	// -------------------------------------------------------------------------

	protected boolean					active ;
	protected String						computerStaticStateDataInboundPortURI ;
	protected String						computerDynamicStateDataInboundPortURI ;
	protected ComputerStaticStateDataOutboundPort		cssPort ;
	protected ComputerDynamicStateDataOutboundPort		cdsPort ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				ComputerMonitor(
		String computerURI,
		boolean active,
		String computerStaticStateDataInboundPortURI,
		String computerDynamicStateDataInboundPortURI
		) throws Exception
	{
		super(1, 0) ;

		assert	computerStaticStateDataInboundPortURI != null ;
		assert	computerDynamicStateDataInboundPortURI != null ;

		this.active = active ;
		this.computerDynamicStateDataInboundPortURI =
									computerDynamicStateDataInboundPortURI ;
		this.computerStaticStateDataInboundPortURI =
									computerStaticStateDataInboundPortURI ;

		this.addOfferedInterface(DataRequiredI.PushI.class) ;
		this.addRequiredInterface(DataRequiredI.PullI.class) ;
		this.cssPort = new ComputerStaticStateDataOutboundPort(this,
															  computerURI) ;
		this.addPort(cssPort) ;
		this.cssPort.publishPort() ;

		this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class) ;
		this.cdsPort = new ComputerDynamicStateDataOutboundPort(this,
															   computerURI) ;
		this.addPort(cdsPort) ;
		this.cdsPort.publishPort() ;
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;

		// start the pushing of dynamic state information from the computer;
		// here only one push of information is planned after one second.
		try {
			this.doPortConnection(
					this.cssPort.getPortURI(),
					this.computerStaticStateDataInboundPortURI,
					DataConnector.class.getCanonicalName());
			this.doPortConnection(
					this.cdsPort.getPortURI(),
					this.computerDynamicStateDataInboundPortURI,
					ControlledDataConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(
							"Unable to start the pushing of dynamic data from"
							+ " the comoter component.", e) ;
		}
	}

	@Override
	public void			execute() throws Exception
	{
		super.execute() ;

		this.cdsPort.startLimitedPushing(1000, 25) ;
	}

	@Override
	public void			finalise() throws Exception
	{
		try {
			if (this.cssPort.connected()) {
				this.cssPort.doDisconnection() ;
			}
			if (this.cdsPort.connected()) {
				this.cdsPort.doDisconnection() ;
			}
		} catch (Exception e) {
			throw new ComponentShutdownException("port disconnection error", e) ;
		}
		super.finalise() ;
	}

	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			this.cssPort.unpublishPort() ;
			this.cdsPort.unpublishPort() ;
		} catch (Exception e) {
			throw new ComponentShutdownException("port unpublishing error", e) ;
		}

		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component internal services
	// -------------------------------------------------------------------------

	@Override
	public void		acceptComputerStaticData(
		String computerURI,
		ComputerStaticStateI ss
		) throws Exception
	{
		if (this.active) {
			StringBuffer sb = new StringBuffer() ;
			sb.append("Accepting static data from " + computerURI + "\n") ;
			sb.append("  timestamp                      : " +
			   							ss.getTimeStamp() + "\n") ;
			sb.append("  timestamper id                 : " +
										ss.getTimeStamperId() + "\n") ;
			sb.append("  number of processors           : " +
										ss.getNumberOfProcessors() + "\n") ;
			sb.append("  number of cores per processors : " +
										ss.getNumberOfCoresPerProcessor() + "\n") ;
			for (int p = 0 ; p < ss.getNumberOfProcessors() ; p++) {
				if (p == 0) {
					sb.append("  processor URIs                 : ") ;
				} else {
					sb.append("                                 : ") ;
				}
				sb.append(p + "  " + ss.getProcessorURIs().get(p) + "\n") ;
			}
			sb.append("  processor port URIs            : " + "\n") ;
			sb.append(Computer.printProcessorsInboundPortURI(
						10, ss.getNumberOfProcessors(),
						ss.getProcessorURIs(), ss.getProcessorPortMap())) ;
			this.logMessage(sb.toString()) ;
		}
	}

	@Override
	public void		acceptComputerDynamicData(
		String computerURI,
		ComputerDynamicStateI cds
		) throws Exception
	{
		if (this.active) {
			StringBuffer sb = new StringBuffer() ;
			sb.append("Accepting dynamic data from " + computerURI + "\n") ;
			sb.append("  timestamp                : " +
											cds.getTimeStamp() + "\n") ;
			sb.append("  timestamper id           : " +
											cds.getTimeStamperId() + "\n") ;

			boolean[][] reservedCores = cds.getCurrentCoreReservations() ;
			for (int p = 0 ; p < reservedCores.length ; p++) {
				if (p == 0) {
					sb.append("  reserved cores           : ") ;
				} else {
					sb.append("                             ") ;
				}
				for (int c = 0 ; c < reservedCores[p].length ; c++) {
					if (reservedCores[p][c]) {
						sb.append("t ") ;
					} else {
						sb.append("f ") ;
					}
				}
			}
			this.logMessage(sb.toString()) ;
		}
	}
}
