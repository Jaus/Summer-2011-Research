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
 * A Wordnet sense entry object, represented in the Wordnet files as a line in the
 * sense entry.
 * 
 * @author Mark A. Finlayson
 * @version 2.2.0
 * @since JWI 2.1.0
 */
public interface ISenseEntry extends IHasPOS {

	/**
	 * Returns the sense key for this sense entry.  Will not return <code>null</code>.
	 *
	 * @return The non-<code>null</code> sense key for this sense entry.
	 * @since JWI 2.1.0
	 */
	public ISenseKey getSenseKey();

	/**
	 * Returns the synset offset for this sense entry, a non-negative integer.
	 * 
	 * @return the non-negative synset offset for this entry
	 * @since JWI 2.1.0
	 */
	public int getOffset();

	/**
	 * Returns the sense number for the word indicated by this entry. A sense
	 * number is a positive integer.
	 * 
	 * @return the non-negative sense number for the word indicated by this entry.
	 * @since JWI 2.1.0
	 */
	public int getSenseNumber();

	/**
	 * Returns the tag count for the sense entry. A tag count is a non-negative
	 * integer that represents the number of times the sense is tagged in
	 * various semantic concordance texts. A count of 0 indicates that the sense
	 * has not been semantically tagged.
	 * 
	 * @return the non-negative tag count for this entry
	 * @since JWI 2.1.0
	 */
	public int getTagCount();

}
