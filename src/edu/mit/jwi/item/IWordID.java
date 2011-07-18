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
 * A unique identifier sufficient to retrieve a particular word from the Wordnet
 * database. Consists of a synset id, sense number, and lemma.
 * 
 * @author Mark A. Finlayson
 * @version 2.2.0
 * @since JWI 1.0
 */
public interface IWordID extends IHasPOS, IItemID<IWord> {

	/**
	 * Returns the synset id object associated with this word.
	 * 
	 * @return the synset id for this word; never <code>null</code>
	 * @since JWI 1.0
	 */
	public ISynsetID getSynsetID();

	/**
	 * Returns the word number, which is a number from 1 to 255 that indicates
	 * the order this word is listed in the Wordnet data files.
	 * 
	 * @return an integer between 1 and 255, inclusive.
	 * @since JWI 1.0
	 */
	public int getWordNumber();

	/**
	 * Returns the lemma (word root) associated with this word.
	 * 
	 * @return the lemma (word root) associated with this word.
	 * @since JWI 1.0
	 */
	public String getLemma();
}
