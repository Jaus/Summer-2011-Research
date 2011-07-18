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
 * A pointer is a marker object that represents different types of relationships
 * between items in a Wordnet dictionary.
 * 
 * @author Mark A. Finlayson
 * @version 2.2.0
 * @since JWI 2.0.0
 */
public interface IPointer {

	/**
	 * The symbol in the Wordnet data files that is used to indicate this
	 * pointer type. Will not be <code>null</code>, empty, or all whitespace.
	 * 
	 * @return the symbol for this pointer
	 * @since JWI 2.0.0
	 */
	public String getSymbol();

	/**
	 * Returns a user-friendly name of this pointer type for identification
	 * purposes. Will not be <code>null</code>, empty, or all whitespace.
	 * 
	 * @return the user-friendly name of this pointer
	 * @since JWI 2.0.0
	 */
	public String getName();

}
