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
 * A unique identifier for an index word. An index word ID is sufficient to
 * retrieve a specific index word from the Wordnet database. It consists of both
 * a lemma (root form) and part of speech.
 * 
 * @author Mark A. Finlayson
 * @version 2.2.0
 * @since JWI 1.0
 */
public interface IIndexWordID extends IHasPOS, IItemID<IIndexWord> {

	/**
	 * Returns the lemma (root form) of the index word that this ID indicates.
	 * The lemma will never be <code>null</code>, empty, or all whitespace.
	 * 
	 * @return the lemma of the index word
	 * @since JWI 1.0
	 */
	public String getLemma();

}
