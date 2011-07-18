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
 * The data that can be obtained from a line in an exception entry file. Because
 * each exception entry does not specify its associated part of speech, this object
 * is just a proxy and must be supplemented by the part of speech at some
 * point to make a full {@code IExceptionEntry} object.
 * 
 * @author Mark A. Finlayson
 * @version 2.2.0
 * @since JWI 1.0
 */
public interface IExceptionEntryProxy {

	/**
	 * Returns the surface form (i.e., not the root form) of the word for this
	 * exception entry. Because all surface forms in the exception files are
	 * lower case, the string returned by this call is also lower case.
	 * 
	 * @return the lowercase surface form of the exception entry
	 * @since JWI 1.0
	 */
	public String getSurfaceForm();

	/**
	 * Returns an unmodifiable list of cceptable root forms for the surface
	 * form.
	 * 
	 * @return A non-null, non-empty, unmodifiable list of root forms
	 * @since JWI 2.0.0
	 */
	public List<String> getRootForms();

}