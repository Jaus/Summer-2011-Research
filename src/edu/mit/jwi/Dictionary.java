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

import java.io.File;
import java.net.URL;

import edu.mit.jwi.data.FileProvider;

/**
 * Basic {@code IDictionary} implementation that mounts files on disk and has
 * caching. A file URL to the directory containing the Wordnet dictionary files
 * must be provided.  This implementation has adjustable caching.
 * 
 * @author Mark A. Finlayson
 * @version 2.2.0
 * @since JWI 1.0
 */
public class Dictionary extends CachingDictionary {

	/**
	 * Constructs a new dictionary that uses the Wordnet files located in a
	 * directory pointed to by the specified url
	 * 
	 * @param wordnetDir
	 *            a url pointing to a directory containing the wordnet data
	 *            files on the filesystem
	 * @throws NullPointerException
	 *             if the specified url is <code>null</code>
	 * @since JWI 1.0
	 */
	public Dictionary(URL wordnetDir) {
		super(new DataSourceDictionary(new FileProvider(wordnetDir)));
	}

	/**
	 * Constructs a new dictionary that uses the Wordnet files located in a
	 * directory pointed to by the specified file
	 * 
	 * @param wordnetDir
	 *            a file pointing to a directory containing the wordnet data files on the filesystem
	 * @throws NullPointerException
	 *             if the specified file is <code>null</code>
	 * @since JWI 1.0
	 */
	public Dictionary(File wordnetDir) {
		super(new DataSourceDictionary(new FileProvider(wordnetDir)));
	}
	
}
