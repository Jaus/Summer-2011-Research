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

package edu.mit.jwi.data;

/**
 * An object with a load policy. Usually objects that implement this interface
 * also implement the {@link ILoadable} interface, but not always. A load policy
 * specifies what happens when the object is instantiated or initialized.
 * 
 * @author Mark A. Finlayson
 * @version 2.2.0
 * @since JWI 2.2.0
 */
public interface ILoadPolicy {

	/**
	 * Loading behavior where the object does not load itself when instantiated,
	 * initialized, or opened.. Loading can be initiated through other means (e.g., a
	 * call to the {@link ILoadable#load(boolean)} method, if the object
	 * supports it). Value is 1 << 1.
	 * 
	 * @since JWI 2.2.0
	 */
	public static final int NO_LOAD = 1 << 1;

	/**
	 * Loading behavior where the object loads itself in the background when
	 * instantiated, initialized, or opened. Value is 1 << 2.
	 * 
	 * @since JWI 2.2.0
	 */
	public static final int BACKGROUND_LOAD = 1 << 2;

	/**
	 * Loading behavior where the object loads itself when instantiated,
	 * initialized, or opened, blocking the method. Value is 1 << 3.
	 * 
	 * @since JWI 2.2.0
	 */
	public static final int IMMEDIATE_LOAD = 1 << 3;

	/**
	 * Sets the load policy for this object. If the object is currently loaded,
	 * or in the process of loading, the load policy will not take effect until
	 * the next time objet is instantiated, initialized, or opened.
	 * 
	 * @param policy
	 *            the policy to implement; may be one of <code>NO_LOAD</code>,
	 *            <code>BACKGROUND_LOAD</code>, <code>IMMEDIATE_LOAD</code> or
	 *            an implementation-dependent value.
	 * @since JWI 2.2.0
	 */
	public void setLoadPolicy(int policy);

	/**
	 * Returns the load policy for this object, expressed as an integer.
	 * 
	 * @return the load policy for this object
	 * @since JWI 2.2.0
	 */
	public int getLoadPolicy();

}
