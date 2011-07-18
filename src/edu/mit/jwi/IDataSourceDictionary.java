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

package edu.mit.jwi;

import edu.mit.jwi.data.IDataProvider;

/**
 * A type of {@code IDictionary} which uses an instance of an
 * {@code IDataProvider} to obtain its data.
 * 
 * @author Mark A. Finlayson
 * @version 2.2.0
 * @since JWI 2.0.0
 */
public interface IDataSourceDictionary extends IDictionary {

	/**
	 * Returns the data provider for this dictionary. Should never return
	 * <code>null</code>.
	 * 
	 * @return the data provider for this dictionary
	 * @since JWI 2.0.0
	 */
	public IDataProvider getDataProvider();

}