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

import java.util.List;

/**
 * A Wordnet index word object, represented in the Wordnet files as a line in an
 * index file.
 * 
 * @author Mark A. Finlayson
 * @version 2.2.0
 * @since JWI 1.0
 */
public interface IIndexWord extends IHasPOS, IItem<IIndexWordID> {

	/**
	 * @return the lemma (word root) associated with this index word.
	 * @return the lemma (word root) for this index word, never
	 *         <code>null</code>, empty, or all whitespace.
	 * @since JWI 1.0
	 */
	public String getLemma();

	/**
	 * Returns an immutable list of word id objects, that point to the words for
	 * this root form and part of speech combination. The list will neither be
	 * <code>null</code> or empty, or contain <code>null</code>.
	 * 
	 * @return an immutable list of word id objects, that point to the words for
	 *         this root form and part of speech combination.
	 * @since JWI 2.0
	 */
	public List<IWordID> getWordIDs();
	
	/**
	 * Returns the number of senses of lemma that are ranked according to their
	 * frequency of occurrence in semantic concordance texts. This will be a
	 * non-negative number.
	 * 
	 * @return the number of senses of lemma that are ranked according to their
	 *         frequency of occurrence in semantic concordance texts.
	 * @since JWI 2.1.2
	 */
	public int getTagSenseCount();

}
