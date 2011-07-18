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
 * A description of a Wordnet lexical file. This interface does not give access
 * to the actual lexicographer's file, but rather is a description, giving the
 * name of the file, it's assigned number, and a brief description.
 * 
 * @author Mark A. Finlayson
 * @version 2.2.0
 * @since JWI 2.1.0
 */
public interface ILexFile extends IHasPOS {

	/**
	 * Returns the number of the lexicographer file. This is used in sense keys
	 * and the data files. A lexical file number is always in the closed range
	 * [0, 99].
	 * 
	 * @return the lexicograph file number, between 0 and 99, inclusive.
	 * @since JWI 2.1.0
	 */
	public int getNumber();

	/**
	 * Returns the name of the lexicographer file. The string will not be
	 * <code>null</code>, empty, or all whitespace.
	 * 
	 * @return the lexicographer file name
	 * @since JWI 2.1.0
	 */
	public String getName();

	/**
	 * Returns a description of the lexicographer file contents. The string will
	 * not be <code>null</code>, empty, or all whitespace.
	 * 
	 * @return a description of the lexicographer file contents
	 * @since JWI 2.1.0
	 */
	public String getDescription();

}
