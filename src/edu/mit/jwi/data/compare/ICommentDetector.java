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
 * A detector for comment lines in data resources. Objects that implement this
 * interface also serve as comparators that say how comment lines are ordered,
 * if at all.
 * 
 * @author Mark A. Finlayson
 * @version 2.2.0
 * @since JWI 1.0
 */
public interface ICommentDetector extends Comparator<String> {

	/**
	 * Returns <code>true</code> if the specified string is a comment line,
	 * <code>false</code> otherwise.
	 * 
	 * @param line
	 *            the line to be analyzed
	 * @return <code>true</code> if the specified string is a comment line,
	 *         <code>false</code> otherwise.
	 * @throws NullPointerException
	 *             if the specified line is <code>null</code>
	 * @since JWI 1.0
	 */
	public boolean isCommentLine(String line);

}