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

package edu.mit.jwi.item;

/**
 * An object that potentially has an associated version.
 * 
 * @author Mark A. Finlayson
 * @version 2.2.0
 * @since JWI 2.1.0
 */
public interface IHasVersion {

	/**
	 * Returns the associated version for this object. If this object is not
	 * associated with any particular version, this method may return
	 * <code>null</code>.
	 * 
	 * @return The associated version, or <code>null</code> if none.
	 * @since JWI 2.1.0
	 */
	public IVersion getVersion();

}
