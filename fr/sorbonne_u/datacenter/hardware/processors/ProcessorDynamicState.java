package fr.sorbonne_u.datacenter.hardware.processors;

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

import fr.sorbonne_u.datacenter.data.AbstractTimeStampedData;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI;

/**
 * The class <code>ProcessorDynamicState</code> implements objects representing
 * a snapshot of the dynamic state of a processor component to be pulled or
 * pushed through the dynamic state data interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		coresIdleStatus != null and currentCoreFrequencies != null
 * </pre>
 * 
 * <p>Created on : April 7, 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class				ProcessorDynamicState
extends		AbstractTimeStampedData
implements	ProcessorDynamicStateI
{
	// ------------------------------------------------------------------------
	// Instance variables and constants
	// ------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L ;
	/** execution status of the processor cores.							*/
	protected final boolean[]	coresIdleStatus ;
	/** current frequencies of the processor cores.							*/
	protected final int[]		currentCoreFrequencies ;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	/**
	 * create a snapshot of the dynamic state of a processor component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	coresIdleStatus != null and currentCoreFrequencies != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param coresIdleStatus		execution status of the processor cores.
	 * @param currentCoreFrequencies	current frequencies of the processor cores.
	 * @throws Exception				<i>todo.</i>
	 */
	public				ProcessorDynamicState(
		boolean[] coresIdleStatus,
		int[] currentCoreFrequencies
		) throws Exception
	{
		super() ;

		assert	coresIdleStatus != null && currentCoreFrequencies != null ;

		this.coresIdleStatus = new boolean[currentCoreFrequencies.length] ;
		this.currentCoreFrequencies = new int[currentCoreFrequencies.length] ;
		for (int i = 0 ; i < currentCoreFrequencies.length ; i++) {
			this.coresIdleStatus[i] = coresIdleStatus[i] ;
			this.currentCoreFrequencies[i] = currentCoreFrequencies[i] ;
		}
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI#getCoresIdleStatus()
	 */
	@Override
	public boolean[]		getCoresIdleStatus()
	{
		return this.coresIdleStatus ;
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI#getCoreIdleStatus(int)
	 */
	@Override
	public boolean		getCoreIdleStatus(int coreNo)
	{
		return this.coresIdleStatus[coreNo] ;
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI#getCurrentCoreFrequencies()
	 */
	@Override
	public int[]			getCurrentCoreFrequencies() {
		return this.currentCoreFrequencies ;
	}

	/**
	 * @see fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI#getCurrentCoreFrequency(int)
	 */
	@Override
	public int			getCurrentCoreFrequency(int coreNo)
	{
		return this.currentCoreFrequencies[coreNo] ;
	}
}
