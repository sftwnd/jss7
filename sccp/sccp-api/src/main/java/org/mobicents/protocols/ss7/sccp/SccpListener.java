/*
 * The Java Call Control API for CAMEL 2
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */

package org.mobicents.protocols.ss7.sccp;

import org.mobicents.protocols.ss7.mtp.ActionReference;
import org.mobicents.protocols.ss7.sccp.parameter.SccpAddress;

/**
 * 
 * @author Oleg Kulikov
 * @author baranowb
 */
public interface SccpListener {
	/**
	 * Called when proper data is received, it is partially decoded. This method is called with message payload.
	 * @param calledPartyAddress - destination address
	 * @param callingPartyAddress - originating address
	 * @param data - payload of data unit
	 * @param backReference - reference to be passed to provider send method.
	 */
	public void onMessage(SccpAddress calledPartyAddress, SccpAddress callingPartyAddress, byte[] data, ActionReference backReference);
	/**
	 * Called when underlying link goes down.
	 */
	public void linkDown();
	/**
	 * Called when underlying link goes up.
	 */
	public void linkUp();

}
