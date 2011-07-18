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

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Default implementation {@code IExceptionEntryProxy}l
 * 
 * @author Mark A. Finlayson
 * @version 2.2.0
 * @since JWI 1.0
 */
public class ExceptionEntryProxy implements IExceptionEntryProxy {

	// immutable instance fields
	private final String surfaceForm;
	private final List<String> rootForms;

	/**
	 * Constructs a new proxy that is a copy of the specified proxy
	 * 
	 * @param proxy
	 *            the proxy to be copied
	 * @throws NullPointerException
	 *             if the specified proxy is <code>null</code>
	 * @since JWI 1.0
	 */
	public ExceptionEntryProxy(IExceptionEntryProxy proxy) {
		if(proxy == null)
			throw new NullPointerException();
		this.surfaceForm = proxy.getSurfaceForm();
		this.rootForms = proxy.getRootForms();
	}

	/** 
	 * Constructs a new proxy with the specified field values.
	 * 
	 * @param surfaceForm the surface form for the entry; may not be <code>null</code>, empty, or all whitespace
	 * @param rootForms the root forms for the entry; may not contain <code>null</code>, empty, or all whitespace strings
	 * @since JWI 1.0
	 */
	public ExceptionEntryProxy(String surfaceForm, String ... rootForms) {
		if (surfaceForm == null) 
			throw new NullPointerException();
		for(int i = 0; i < rootForms.length; i++){
			if(rootForms[i] == null)
				throw new NullPointerException();
			rootForms[i] = rootForms[i].trim();
			if(rootForms[i].length() == 0)
				throw new IllegalArgumentException();
		}
		
		this.surfaceForm = surfaceForm;
		this.rootForms = Collections.unmodifiableList(Arrays.asList(rootForms));
	}

	/* 
	 * (non-Javadoc) 
	 *
	 * @see edu.mit.jwi.item.IExceptionEntryProxy#getSurfaceForm()
	 */
	public String getSurfaceForm() {
		return surfaceForm;
	}

	/* 
	 * (non-Javadoc) 
	 *
	 * @see edu.mit.jwi.item.IExceptionEntryProxy#getRootForms()
	 */
	public List<String> getRootForms() {
		return rootForms;
	}

	/* 
	 * (non-Javadoc) 
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("EXC-");
		sb.append(surfaceForm);
		sb.append('[');
		for(Iterator<String> i = rootForms.iterator(); i.hasNext();) {
			sb.append(i.next());
			if (i.hasNext()) 
				sb.append(',');
		}
		sb.append(']');
		return sb.toString();
	}
}
