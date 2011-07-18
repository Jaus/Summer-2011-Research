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
 * A verb frame as specified from the verb frames data file in the Wordnet
 * distribution
 * 
 * @author Mark A. Finlayson
 * @version 2.2.0
 * @since JWI 1.0
 */
public interface IVerbFrame {

	/**
	 * The id number of this verb frame. Should always return 1 or greater.
	 *
	 * @return the id number of the verb frame, will be positive
	 * @since JWI 1.0
	 */
	public int getNumber();

	/**
	 * The string form of the template, drawn directly from the data file.
	 * Will never return <code>null</code>
	 * @return the non-<code>null</code>, non-empty template of the verb frame
	 * @since JWI 1.0
	 */
	public String getTemplate();

	/**
	 * Takes the supplied surface form of a verb and instantiates it into the
	 * template for the verb frame. This is a convenience method; the method
	 * does no morphological processing; it does not check to see if the passed
	 * in word is actually a verb.
	 * 
	 * @param verb
	 *            the string to be substituted into the template
	 * @return the instantiated template
	 * @throws NullPointerException if the specified string is <code>null</code>
	 * @since JWI 1.0
	 */
	public String instantiateTemplate(String verb);

}