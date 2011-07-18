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

package edu.mit.jwi.data.compare;

import java.util.Comparator;

/**
 * A string comparator that may have an associated comment detector. The
 * <code>compare</code> method of this class will throw an
 * {@link IllegalArgumentException} if the line data passed to that method is
 * ill-formed.
 * 
 * @author Mark A. Finlayson
 * @version 2.2.0
 * @since JWI 2.0.0
 */
public interface ILineComparator extends Comparator<String> {

	/**
	 * Returns the comment detector instance associated with this line
	 * comparator, or <code>null</code> if one does not exist.
	 * 
	 * @return the comment detector associated with this line comparator, or
	 *         <code>null</code> if there is none
	 * @since JWI 2.0.0
	 */
	public ICommentDetector getCommentDetector();

}