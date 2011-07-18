/********************************************************************************
 * MIT Java Wordnet Interface (JWI)
 * Copyright (c) 2007-2011 Massachusetts Institute of Technology
 *
 * This is the non-commercial version of JWI.  This version may *not* be used
 * for commercial purposes.
 * 
 * This program and the accompanying materials are made available by the MIT
 * Technology Licensing Office under the terms of the MIT Java Wordnet Interface 
 * Non-Commercial License.  The MIT Technology Licensing Office can be reached 
 * at 617-253-6966 for further inquiry.
 *******************************************************************************/

package edu.mit.jwi.data;

/**
 * An object that can be closed. What 'closing' means is implementation
 * specific.
 * 
 * @author Mark A. Finlayson
 * @version 2.2.0
 * @since JWI 2.2.0
 */
public interface IClosable {

	/**
	 * This closes the object by disposing of data backing objects or
	 * connections. If the object is already closed, or in the process of
	 * closing, this method does nothing (although, if the object is in the
	 * process of closing, it may block until closing is complete).
	 * 
	 * @since JWI 2.2.0
	 */
	public void close();
	
}
