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
 * Represents an entry in an exception file (e.g., verb.exc or exc.vrb). Most of
 * the functionality of this interface is inherited from
 * <code>IExceptionEntryProxy</code>.
 * 
 * @author Mark A. Finlayson
 * @version 2.2.0
 * @since JWI 1.0
 */
public interface IExceptionEntry extends IExceptionEntryProxy, IHasPOS, IItem<IExceptionEntryID> {

}
